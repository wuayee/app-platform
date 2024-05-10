/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/13 16:35
 */

#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/util/context/context.hpp>
#include <fit/internal/util/context/context_wrapper.hpp>
#include "allocator_impl.hpp"

namespace Fit {
namespace Context {
ContextObj NewContext()
{
    return NewContextDefault();
}

ContextObj NewContext(AllocFunctor alloc, FreeFunctor free)
{
    if (alloc == nullptr || free == nullptr) {
        return nullptr;
    }

    return new ContextWrapper(AllocatorImpl::CreateAllocatorImpl(
        std::move(alloc), std::move(free)));
}

int32_t ContextLinkObj(ContextObj ctx, void *obj, void *realMem,
    DeconstructWrapper deconstruct)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_FAIL;
    }

    return wrapper->context.LinkObj(CacheObjDetail {
        realMem,
        obj,
        std::move(deconstruct)
    });
}

void SetGenericableId(ContextObj ctx, const Fit::string& genericableId)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.SetGerericableId(genericableId);
}

Fit::string GetGenericableId(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return Fit::string("");
    }
    return wrapper->context.GetGenericableId();
}

// context address
FitCode ContextSetTargetAddress(ContextObj ctx, const TargetAddress* targetAddressPtr)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_FAIL;
    }
    return wrapper->context.SetTargetAddress(targetAddressPtr);
}

TargetAddress* ContextGetTargetAddress(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    TargetAddress* nulladdress = {nullptr};
    if (wrapper == nullptr) {
        return nulladdress;
    }
    return (wrapper->context.GetTargetAddress()).get();
}

// context route

namespace {
constexpr const char *ROUTE_KEY_PREFIX = "fit.route.";
constexpr int32_t ROUTE_KEY_PREFIX_LEN = 10;
}

/**
* 向route context中设置一组值
* @param ctx 上下文对象句柄
* @param key
* @param value
* @return 是否设置成功
*/
bool PutRouteContext(ContextObj ctx, const Fit::string &key, const Fit::string &value)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    Fit::string routeKey = ROUTE_KEY_PREFIX + key;
    return wrapper->context.PutGlobalContext(routeKey, value);
}

/**
 * 获取routeContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return 缓存数据
 */
Fit::map<Fit::string, Fit::string> GetAllRouteContext(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return Fit::map<Fit::string, Fit::string>();
    }

    auto allContext = wrapper->context.GetAllGlobalContext();
    Fit::map<Fit::string, Fit::string> res;
    for (const auto &item : allContext) {
        if (item.first.find(ROUTE_KEY_PREFIX) == 0) {
            res[item.first.substr(ROUTE_KEY_PREFIX_LEN)] = item.second;
        }
    }
    return res;
}

namespace Global {
/**
* 向global context中设置一组值
* @param ctx 上下文对象句柄
* @param key
* @param value
* @return 是否设置成功
*/
bool PutGlobalContext(ContextObj ctx, const Fit::string &key, const Fit::string &value)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.PutGlobalContext(key, value);
}

/**
 * 删除globalContext中key对应的值
 * @param ctx 上下文对象句柄
 * @param key
 * @return 是否成功
 */
bool RemoveGlobalContext(ContextObj ctx, const Fit::string &key)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.RemoveGlobalContext(key);
}

/**
* 获取globalContext中key对应的值
* @param ctx 上下文对象句柄
* @param key
* @return value，不存在时为空值
*/
Fit::string GetGlobalContext(ContextObj ctx, const Fit::string &key)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return "";
    }
    return wrapper->context.GetGlobalContext(key);
}

/**
 * 获取globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return 缓存数据
 */
Fit::map<Fit::string, Fit::string> GetAllGlobalContext(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return Fit::map<Fit::string, Fit::string>();
    }
    return wrapper->context.GetAllGlobalContext();
}

/**
 * 设置globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @param data 数据
 * @return 是否成功
 */
bool RestoreGlobalContext(ContextObj ctx, const Fit::map<Fit::string, Fit::string>& data)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.RestoreGlobalContext(data);
}

bool HasGlobalContext(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    return wrapper != nullptr && wrapper->context.HasGlobalContext();
}

FitCode GlobalContextSerialize(ContextObj ctx, Fit::string& result)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_PARAM;
    }
    return wrapper->context.SerializeGlobalContext(result);
}

FitCode GlobalContextDeserialize(ContextObj ctx, const Fit::string &data)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_PARAM;
    }
    return wrapper->context.DeserializeGlobalContext(data);
}
}

namespace Exception {
bool PutExceptionContext(ContextObj ctx, const Fit::string& key, const Fit::string& value)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.PutExceptionContext(key, value);
}

bool RemoveExceptionContext(ContextObj ctx, const Fit::string& key)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return false;
    }
    return wrapper->context.RemoveExceptionContext(key);
}

Fit::string GetExceptionContext(ContextObj ctx, const Fit::string& key)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return "";
    }
    return wrapper->context.GetExceptionContext(key);
}

Fit::map<Fit::string, Fit::string> GetAllExceptionContext(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return Fit::map<Fit::string, Fit::string>();
    }
    return wrapper->context.GetAllExceptionContext();
}

bool HasExceptionContext(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    return wrapper != nullptr && wrapper->context.HasExceptionContext();
}

FitCode SerializeExceptionContext(ContextObj ctx, Fit::string& result)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_PARAM;
    }
    return wrapper->context.SerializeExceptionContext(result);
}

FitCode DeserializeExceptionContext(ContextObj ctx, Fit::string& data)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_PARAM;
    }
    return wrapper->context.DeserializeExceptionContext(data);
}
}
}
}
