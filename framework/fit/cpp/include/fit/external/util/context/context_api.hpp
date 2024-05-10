/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/13 16:16
 */
#ifndef FITCONTEXTAPI_H
#define FITCONTEXTAPI_H

#include <functional>
#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>
#include <cstring>
#include "context_base.h"
#include "securec.h"

namespace Fit {
namespace Context {
using AllocFunctor = std::function<void* (size_t)>;
using FreeFunctor = std::function<void(void *obj)>;
using DeconstructWrapper = std::function<void()>;

struct TargetAddress {
    Fit::string workerId;
    Fit::string host;
    int32_t port;
    int32_t protocol;
    Fit::vector<int32_t> formats;
    map<string, string> extensions;
};

ContextObj NewContext();
ContextObj NewContext(AllocFunctor alloc, FreeFunctor free);

class Guard {
public:
    explicit Guard(ContextObj ctx) : ctx_(ctx) {}
    Guard(Guard&&) = delete;
    Guard(const Guard&) = delete;
    Guard& operator=(const Guard&) = delete;
    Guard& operator=(Guard&&) = delete;
    ~Guard() { ContextDestroy(ctx_); }
    ContextObj GetContext() const noexcept { return ctx_; }

private:
    ContextObj ctx_;
};

// 外部禁止调用
int32_t ContextLinkObj(ContextObj ctx, void *obj, void *realMem,
    DeconstructWrapper deconstruct);

void SetGenericableId(ContextObj ctx, const Fit::string& genericableId);
Fit::string GetGenericableId(ContextObj ctx);

template<typename T>
T *NewObj(ContextObj ctx)
{
    auto realMem = ContextMallocWithOutCache(ctx, sizeof(T));
    if (realMem == nullptr) {
        return nullptr;
    }
    memset_s(realMem, sizeof(T), 0x0, sizeof(T));
    auto obj = new(realMem) T();
    auto ret = ContextLinkObj(ctx, obj, realMem, [obj]() {
        obj->~T();
    });
    if (ret != FIT_ERR_SUCCESS) {
        obj->~T();
        ContextFree(ctx, realMem);
        return nullptr;
    }

    return obj;
}

// context address
/**
 * 向context中设置指定调用地址，nullptr取消已设置地址
 * @param ctx 上下文对象句柄
 * @param targetAddressPtr
 * @return 是否设置成功
*/
FitCode ContextSetTargetAddress(ContextObj ctx, const TargetAddress* targetAddressPtr);

/**
 * 获取context中指定调用地址
 * @param ctx 上下文对象句柄
 * @return 指定调用地址
*/
TargetAddress* ContextGetTargetAddress(ContextObj ctx);

// context route
/**
* 向route context中设置一组值
* @param ctx 上下文对象句柄
* @param key
* @param value
* @return 是否设置成功
*/
bool PutRouteContext(ContextObj ctx, const Fit::string &key, const Fit::string &value);

/**
 * 获取routeContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return 缓存数据
 */
Fit::map<Fit::string, Fit::string> GetAllRouteContext(ContextObj ctx);

namespace Global {
/**
* 向global context中设置一组值
* @param ctx 上下文对象句柄
* @param key
* @param value
* @return 是否设置成功
*/
bool PutGlobalContext(ContextObj ctx, const Fit::string &key, const Fit::string &value);

/**
 * 删除globalContext中key对应的值
 * @param ctx 上下文对象句柄
 * @param key
 * @return 是否成功
 */
bool RemoveGlobalContext(ContextObj ctx, const Fit::string &key);

/**
* 获取globalContext中key对应的值
* @param ctx 上下文对象句柄
* @param key
* @return value，不存在时为空值
*/
Fit::string GetGlobalContext(ContextObj ctx, const Fit::string &key);

/**
 * 获取globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return 缓存数据
 */
Fit::map<Fit::string, Fit::string> GetAllGlobalContext(ContextObj ctx);

/**
 * 设置globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @param data 数据
 * @return 是否成功
 */
bool RestoreGlobalContext(ContextObj ctx, const Fit::map<Fit::string, Fit::string> &data);

/**
 * 获取一个值，该值指示指定的上下文中是否存在全局上下文信息。
 *
 * @param ctx 表示待检查的上下文。
 * @return 若存在全局上下文，则为 <code>true</code>；否则为 <code>false</code>。
 */
bool HasGlobalContext(ContextObj ctx);

FitCode GlobalContextSerialize(ContextObj ctx, Fit::string &result);

FitCode GlobalContextDeserialize(ContextObj ctx, const Fit::string &data);
}

namespace Exception {
    /**
     * 设置异常上下文中指定键的值。
     *
     * @param ctx 表示上下文对象的句柄。
     * @param key 表示待设置值的键的字符串。
     * @param value 表示待设置的值的字符串。
     * @return 若为 <code>true</code>，则设置成功；否则设置失败。
     */
    bool PutExceptionContext(ContextObj ctx, const Fit::string &key, const Fit::string &value);

    /**
     * 移除异常上下文中指定键的值。
     *
     * @param ctx 表示上下文对象的句柄。
     * @param key 表示待设置值的键的字符串。
     * @return 若为 <code>true</code>，则设置成功；否则设置失败。
     */
    bool RemoveExceptionContext(ContextObj ctx, const Fit::string &key);

    /**
     * 获取异常上下文中指定键的值。
     *
     * @param ctx 表示上下文对象的句柄。
     * @param key 表示待设置值的键的字符串。
     * @return 表示待设置的值的字符串。
     */
    Fit::string GetExceptionContext(ContextObj ctx, const Fit::string &key);

    /**
     * 获取异常上下文中包含的键值对。
     *
     * @param ctx 表示上下文对象的句柄。
     * @return 表示异常上下文的内容的键值对。
     */
    Fit::map<Fit::string, Fit::string> GetAllExceptionContext(ContextObj ctx);

    /**
     * 获取一个值，该值指示指定的上下文中是否存在异常上下文信息。
     *
     * @param ctx 表示待检查的上下文。
     * @return 若存在异常上下文，则为 <code>true</code>；否则为 <code>false</code>。
     */
    bool HasExceptionContext(ContextObj ctx);

    /**
     * 序列化异常上下文，并将结果输出到指定的字符串中。
     *
     * @param ctx 表示上下文对象的句柄。
     * @param result 表示输出到的字符串。
     * @return 若为 <code>FIT_OK</code>，则序列化成功；否则序列化失败。
     */
    FitCode SerializeExceptionContext(ContextObj ctx, Fit::string &result);

    /**
     * 将指定字符串中包含的异常上下文信息反序列化到上下文对象中。
     *
     * @param ctx 表示上下文对象的句柄。
     * @param data 表示包含异常上下文信息的字符串。
     * @return 若为 <code>FIT_OK</code>，则反序列化成功；否则反序列化失败。
     */
    FitCode DeserializeExceptionContext(ContextObj ctx, Fit::string &data);
}
}
}

#endif // FITCONTEXTAPI_H
