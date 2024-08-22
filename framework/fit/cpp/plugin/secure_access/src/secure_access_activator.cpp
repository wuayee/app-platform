/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include <include/secure_access_config.h>
#include <include/token_life_cycle_observer.h>
#include <include/secure_access.h>

namespace {
FitCode Start(::Fit::Framework::PluginContext* context)
{
    Fit::TokenLifeCycleObserver* tokenLifeCycleObserver =
        new Fit::TokenLifeCycleObserver(&Fit::SecureAccess::Instance());
    tokenLifeCycleObserver->Init();
    int32_t ret = Fit::SecureAccessConfig::Instance().InitConfig(context->GetConfig());
    Fit::SecureAccess::Instance().AuthKeyRepo()->Save(Fit::SecureAccessConfig::Instance().AuthKeys());
    Fit::SecureAccess::Instance().RolePermissionsRepo()->Save(Fit::SecureAccessConfig::Instance().RolePermissionsSet());
    FIT_LOG_CORE("Start secure access success.");
    return ret;
}

FitCode Stop()
{
    FIT_LOG_INFO("Stop secure access.");
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start)
        .SetStop(Stop);
}
} // LCOV_EXCL_LINE