/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 16:36
 */

#include "fit/internal/util/context/context.hpp"
#include <fit/stl/memory.hpp>

namespace Fit {
namespace Context {
thread_local GlobalContextPtr Context::globalContext_ = std::make_shared<GlobalContext>();
thread_local MapContextPtr Context::exceptionContext_ = std::make_shared<MapContext>();

Context::Context(AllocatorPtr allocator)
    : ObjContext(std::move(allocator))
{
    globalContext_->Ref();
    exceptionContext_->Ref();
}

Context::~Context()
{
    globalContext_->UnRef();
    exceptionContext_->Unref();
}

void Context::SetGerericableId(const Fit::string &genericableId) noexcept
{
    genericableId_ = genericableId;
}

const Fit::string &Context::GetGenericableId() const noexcept
{
    return genericableId_;
}

void Context::SetAlias(const Fit::string &alias) noexcept
{
    alias_ = alias;
}

void Context::SetFitableId(const char* val)
{
    fitableId_ = val;
}
const string& Context::GetFitableId() const noexcept
{
    return fitableId_;
}

const Fit::string &Context::GetAlias() const noexcept
{
    return alias_;
}

void Context::SetTargetWorker(const Fit::string& targetWorkerId) noexcept
{
    targetWorkerId_ = targetWorkerId;
}

const Fit::string& Context::GetTargetWorker() const noexcept
{
    return targetWorkerId_;
}

uint32_t Context::GetRetry() const noexcept
{
    return retry_;
}

void Context::SetRetry(uint32_t count) noexcept
{
    retry_ = count;
}

uint32_t Context::GetTimeout() const noexcept
{
    return timeout_;
}

void Context::SetTimeout(uint32_t ms) noexcept
{
    timeout_ = ms;
}

uint8_t Context::GetPolicy() const noexcept
{
    return policy_;
}

void Context::SetPolicy(uint8_t policy) noexcept
{
    policy_ = policy;
}

void Context::SetAccessToken(const Fit::string& accessToken) noexcept
{
    accessToken_ = accessToken;
}

const Fit::string& Context::GetAccessToken() const noexcept
{
    return accessToken_;
}

const std::unique_ptr<TargetAddress>& Context::GetTargetAddress() const noexcept
{
    return targetAddressPtr_;
}

FitCode Context::SetTargetAddress(const TargetAddress* targetAddressPtr) noexcept
{
    if (targetAddressPtr == nullptr) {
        targetAddressPtr_.reset(nullptr);
    } else {
        targetAddressPtr_ = make_unique<TargetAddress>(*targetAddressPtr);
    }
    return FIT_OK;
}

const Fit::map<Fit::string, Fit::string> &Context::GetCtxKV() const noexcept
{
    return globalContext_->GetAllGlobalContext();
}

void Context::SetCtxKV(const Fit::map<Fit::string, Fit::string> &kv) noexcept
{
    globalContext_->RestoreGlobalContext(kv);
}

/**
    * 向global context中设置一组值
    * @param ctx 上下文对象句柄
    * @param key
    * @param value
    * @return 是否设置成功
    */
bool Context::PutGlobalContext(const Fit::string &key, const Fit::string &value)
{
    return globalContext_->PutGlobalContext(key, value);
}

/**
 * 删除globalContext中key对应的值
 * @param ctx 上下文对象句柄
 * @param key
 * @return 是否成功
 */
bool Context::RemoveGlobalContext(const Fit::string &key)
{
    return globalContext_->RemoveGlobalContext(key);
}

/**
* 获取globalContext中key对应的值
* @param ctx 上下文对象句柄
* @param key
* @return value，不存在时为空值
*/
Fit::string Context::GetGlobalContext(const Fit::string &key)
{
    return globalContext_->GetGlobalContext(key);
}

/**
 * 获取globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @return 缓存数据
 */
Fit::map<Fit::string, Fit::string> Context::GetAllGlobalContext()
{
    return globalContext_->GetAllGlobalContext();
}

/**
 * 设置globalContext中所有的缓存数据
 * @param ctx 上下文对象句柄
 * @param data 数据
 * @return 是否成功
 */
bool Context::RestoreGlobalContext(const Fit::map<Fit::string, Fit::string> &data)
{
    return globalContext_->RestoreGlobalContext(data);
}

bool Context::HasGlobalContext()
{
    return !globalContext_->IsEmpty();
}

FitCode Context::SerializeGlobalContext(Fit::string &result)
{
    return globalContext_->Serialize(result);
}

FitCode Context::DeserializeGlobalContext(const Fit::string &data)
{
    return globalContext_->Deserialize(data);
}

bool Context::PutExceptionContext(const Fit::string &key, const Fit::string &value)
{
    exceptionContext_->Put(key, value);
    return true;
}

bool Context::RemoveExceptionContext(const Fit::string &key)
{
    exceptionContext_->Remove(key);
    return true;
}

Fit::string Context::GetExceptionContext(const Fit::string &key)
{
    return exceptionContext_->Get(key);
}

Fit::map<Fit::string, Fit::string> Context::GetAllExceptionContext()
{
    return exceptionContext_->GetAll();
}

bool Context::RestoreExceptionContext(const Fit::map<Fit::string, Fit::string> &data)
{
    exceptionContext_->Reset(data);
    return true;
}

bool Context::HasExceptionContext()
{
    return !exceptionContext_->IsEmpty();
}

FitCode Context::SerializeExceptionContext(Fit::string &result)
{
    return exceptionContext_->Serialize(result);
}

FitCode Context::DeserializeExceptionContext(const Fit::string &data)
{
    return exceptionContext_->Deserialize(data);
}
}
}
