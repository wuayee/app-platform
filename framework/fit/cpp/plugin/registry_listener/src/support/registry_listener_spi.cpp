/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for registry listener SPIs.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/20
 */

#include <support/registry_listener_spi.hpp>
#include <fit/fit_log.h>
#include <genericable/com_huawei_fit_sdk_system_get_system_property/1.0.0/cplusplus/getSystemProperty.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

namespace {
class FunctionalFitablesChangedCallback : public virtual FitablesChangedCallback {
public:
    explicit FunctionalFitablesChangedCallback(std::function<FitCode(const vector<FitableInstance>&)> action)
        : action_(std::move(action)) {}
    ~FunctionalFitablesChangedCallback() override = default;
    FitCode Notify(const Fit::vector<FitableInstance>& instances) override
    {
        return action_(instances);
    }
private:
    std::function<FitCode(const vector<FitableInstance>&)> action_ {nullptr};
};
}

FitableInstanceListGuard::FitableInstanceListGuard(vector<FitableInstance> fitableInstances, FitCode resultCode)
    : fitableInstances_(std::move(fitableInstances)), resultCode_(resultCode)
{
}

FitableInstanceListGuard::FitableInstanceListGuard(FitableInstanceListGuard&& input)
    : fitableInstances_(std::move(input.fitableInstances_)), resultCode_(input.resultCode_)
{
}

FitableInstanceListGuard& FitableInstanceListGuard::operator=(FitableInstanceListGuard&& other)
{
    if (this != &other) {
        fitableInstances_ = std::move(other.fitableInstances_);
        resultCode_ = other.resultCode_;
    }
    return *this;
}

FitableInstanceListGuard::~FitableInstanceListGuard()
{
    for (auto& fitableInstance : fitableInstances_) {
        if (fitableInstance.fitable != nullptr) {
            delete fitableInstance.fitable;
            fitableInstance.fitable = nullptr;
        }
        
        for (auto& applicationInstance : fitableInstance.applicationInstances) {
            if (applicationInstance.application != nullptr) {
                delete applicationInstance.application;
                applicationInstance.application = nullptr;
            }
        }
    }
}

const Fit::vector<FitableInstance>& FitableInstanceListGuard::Get() const
{
    return fitableInstances_;
}

FitCode FitableInstanceListGuard::GetResultCode() const
{
    return resultCode_;
}

FitablesChangedCallbackPtr FitablesChangedCallback::Create(
    std::function<FitCode(const vector<FitableInstance>&)> action)
{
    return std::make_shared<FunctionalFitablesChangedCallback>(std::move(action));
}

const ::Fit::string& BaseRegistryListenerSpi::GetWorkerId() const
{
    static string workerId {};
    if (workerId.empty()) {
        fit::sdk::system::getSystemProperty proxy;
        string key = "fit_worker_id";
        string* value = nullptr;
        auto ret = proxy(&key, &value);
        if (ret == FIT_OK) {
            workerId = *value;
            FIT_LOG_INFO("Successful to fetch local worker id. [workerId=%s]", workerId.c_str());
        } else {
            FIT_LOG_WARN("Failed to fetch local worker id. [error=%x]", ret);
        }
    }
    return workerId;
}