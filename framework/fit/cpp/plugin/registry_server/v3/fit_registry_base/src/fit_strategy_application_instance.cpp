/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance implementation of strategy.
 * Author       : w00561424
 * Date:        : 2023/10/17
 */
#include <v3/fit_registry_base/include/fit_strategy_application_instance.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace Fit {
namespace Registry {
FitStrategyApplicationInstance::FitStrategyApplicationInstance(
    FitApplicationInstanceServicePtr applicationInstanceService)
    : applicationInstanceService_(std::move(applicationInstanceService))
{
}

Fit::string FitStrategyApplicationInstance::Type()
{
    constexpr const char* type = "application_instance";
    return type;
}

int32_t FitStrategyApplicationInstance::Check(const Fit::map<Fit::string, Fit::string>& kvs)
{
    if (applicationInstanceService_ == nullptr) {
        FIT_LOG_ERROR("Application instance service is null.");
        return FIT_ERR_FAIL;
    }
    if (kvs.empty()) {
        FIT_LOG_ERROR("Kvs is empty.");
        return FIT_ERR_FAIL;
    }
    auto kv = kvs.begin();
    FIT_LOG_DEBUG("Application instance check, workerId:version (%s:%s).", kv->second.c_str(), kv->first.c_str());
    // 每次针对单节点验证
    return applicationInstanceService_->Check(kv->second, kv->first);
}
}
}