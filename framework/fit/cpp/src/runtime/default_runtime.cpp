/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : implement for runtime
 * Author       : songyongtan
 * Date         : 2022/4/15
 * Notes:       :
 */

#include "default_runtime.hpp"
#include <fit/fit_log.h>

using namespace ::Fit;
using ::Fit::RuntimeElementBase;
using ::Fit::DefaultRuntime;

Runtime& RuntimeElementBase::GetRuntime()
{
    return *runtime_;
}

void Fit::RuntimeElementBase::SetRuntime(Runtime& runtime)
{
    runtime_ = &runtime;
}

bool Fit::DefaultRuntime::Start()
{
    for (auto& e : elements_) {
        e->SetStarted(e->Start());
        if (!e->IsStarted()) {
            FIT_LOG_ERROR("Failed to start element. [name=%s]", e->GetName().c_str());
            return false;
        }
        FIT_LOG_INFO("Start element successfully. [name=%s]", e->GetName().c_str());
    }

    return true;
}

bool DefaultRuntime::Stop()
{
    for (auto iter = elements_.rbegin(); iter != elements_.rend(); ++iter) {
        if (!(*iter)->IsStarted()) {
            continue;
        }
        (*iter)->Stop();
        (*iter)->SetStarted(false);
    }
    return true;
}

void DefaultRuntime::AddElement(unique_ptr<RuntimeElement> element)
{
    element->SetRuntime(*this);
    elements_.emplace_back(move(element));
}

bool Fit::DefaultRuntime::GetElementAnyOf(const std::function<bool(RuntimeElement*)>& condition)
{
    for (auto& e : elements_) {
        if (condition(e.get())) {
            return true;
        }
    }
    return false;
}
