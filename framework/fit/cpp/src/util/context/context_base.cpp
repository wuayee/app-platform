/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 15:45
 */

#include <fit/external/util/context/context_base.h>
#include <cstring>
#include <fit/internal/util/context/context_wrapper.hpp>
#include <fit/fit_log.h>
#include "allocator_impl.hpp"

using namespace Fit::Context;

extern "C" {
ContextObj NewContextDefault()
{
    return new ContextWrapper(AllocatorDefault::CreateDefaultAllocator());
}

ContextObj NewContextWithMemFunc(AllocFunc allocFunc, FreeFunc freeFunc)
{
    if (allocFunc == nullptr || freeFunc == nullptr) {
        FIT_LOG_ERROR("Bad alloc func!");
        return nullptr;
    }

    return new ContextWrapper(AllocatorImpl::CreateAllocatorImpl(
        [allocFunc](size_t size) -> void* {
            return allocFunc(size);
        },
        [freeFunc](void *obj) {
            freeFunc(obj);
        }));
}

void ContextFreeAll(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.DeleteClassObjList();
}

void ContextDestroy(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.DeleteClassObjList();
    delete wrapper;
}

void *ContextMallocNoManage(ContextObj ctx, int32_t size)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return nullptr;
    }
    return wrapper->context.Malloc(size);
}

void ContextFreeNoManage(ContextObj ctx, void *obj)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.Free(obj);
}

void *ContextMallocWithOutCache(ContextObj ctx, size_t size)
{
    return ContextMallocNoManage(ctx, size);
}

void ContextFree(ContextObj ctx, void *obj)
{
    ContextFreeNoManage(ctx, obj);
}

void ContextSetAlias(ContextObj ctx, const char *alias)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    // 当 alias 不是空白字符串时设置策略为 POLICY_ALIAS，否则应该考虑不改变或设置为 POLICY_DEFAULT。
    if (alias == nullptr) {
        ContextSetPolicy(ctx, FitablePolicy::POLICY_DEFAULT);
    } else {
        ContextSetPolicy(ctx, FitablePolicy::POLICY_ALIAS);
        wrapper->context.SetAlias(Fit::string(alias));
    }
}

FitCode ContextSetFitableId(ContextObj ctx, const char *val)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FIT_ERR_PARAM;
    }
    // 当 fitableid 不是空白字符串时设置策略为 POLICY_FITABLE_ID，否则，恢复默认设置为 POLICY_DEFAULT。
    if (val == nullptr) {
        ContextSetPolicy(ctx, FitablePolicy::POLICY_DEFAULT);
    } else {
        ContextSetPolicy(ctx, FitablePolicy::POLICY_FITABLE_ID);
        wrapper->context.SetFitableId(val);
    }
    return FIT_OK;
}

const char *ContextGetFitableId(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return "";
    }
    return wrapper->context.GetFitableId().c_str();
}

const char *ContextGetAlias(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return "";
    }
    return wrapper->context.GetAlias().data();
}

void ContextSetTargetWorker(ContextObj ctx, const char* targetWorkerId)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.SetTargetWorker(targetWorkerId);
}

const char* ContextGetTargetWorker(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return "";
    }
    return wrapper->context.GetTargetWorker().data();
}

uint32_t ContextGetRetry(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return 0;
    }

    return wrapper->context.GetRetry();
}

void ContextSetRetry(ContextObj ctx, uint32_t count)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.SetRetry(count);
}

uint32_t ContextGetTimeout(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        constexpr uint32_t defaultTimeoutMs = 5000;
        return defaultTimeoutMs;
    }

    return wrapper->context.GetTimeout();
}

void ContextSetTimeout(ContextObj ctx, uint32_t ms)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.SetTimeout(ms);
}

void ContextSetPolicy(ContextObj ctx, FitablePolicy policy)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.SetPolicy(static_cast<uint8_t>(policy));
}

FitablePolicy ContextGetPolicy(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return FitablePolicy::POLICY_DEFAULT;
    }
    return static_cast<FitablePolicy>(wrapper->context.GetPolicy());
}

void ContextSetAccessToken(ContextObj ctx,  const char* accessToken)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return;
    }
    wrapper->context.SetAccessToken(accessToken);
}

const char* ContextGetAccessToken(ContextObj ctx)
{
    auto wrapper = ContextWrapperCast(ctx);
    if (wrapper == nullptr) {
        return "";
    }
    return wrapper->context.GetAccessToken().data();
}
}