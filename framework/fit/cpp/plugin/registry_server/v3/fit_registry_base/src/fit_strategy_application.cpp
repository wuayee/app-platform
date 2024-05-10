/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : application implementation of strategy.
 * Author       : w00561424
 * Date:        : 2023/10/17
 */
#include <v3/fit_registry_base/include/fit_strategy_application.h>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
namespace Fit {
namespace Registry {
FitStrategyApplication::FitStrategyApplication(FitFitableMetaServicePtr fitableMetaService)
    : fitableMetaService_(std::move(fitableMetaService))
{
}

Fit::string FitStrategyApplication::Type()
{
    constexpr const char* type = "application";
    return type;
}

// key:appVersion，value：appname
int32_t FitStrategyApplication::Check(const Fit::map<Fit::string, Fit::string>& kvs)
{
    if (fitableMetaService_ == nullptr) {
        FIT_LOG_ERROR("Meta service is null.");
        return FIT_ERR_FAIL;
    }

    Fit::RegistryInfo::Application application;
    // check的维度是一个节点，只要有一个application不一致，全部重新注册
    for (const auto& it : kvs) {
        application.name = it.second;
        application.nameVersion = it.first;
        if (!fitableMetaService_->IsApplicationExist(application)) {
            FIT_LOG_ERROR("Application not exist, app:version (%s:%s).", application.name.c_str(),
                application.nameVersion.c_str());
            return FIT_ERR_NOT_EXIST;
        }
        FIT_LOG_DEBUG("Application check, app:version (%s:%s).", application.name.c_str(),
            application.nameVersion.c_str());
    }

    return FIT_OK;
}
}
}