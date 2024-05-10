/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : wangpanbo
 * Date         : 2023/08/03
 * Notes:       :
 */
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include <config.h>
namespace {
constexpr const char* KEY_IS_REGISTRY_SERVER = "is-registry-server";
FitCode Start(::Fit::Framework::PluginContext* context)
{
    bool isRegistryServer = context->GetConfig()->Get(KEY_IS_REGISTRY_SERVER).AsBool(false);
    Fit::SetIsRegistryServer(isRegistryServer);
    FIT_LOG_INFO("Registry address is start, registry status : %d.", static_cast<int32_t>(isRegistryServer));
    return FIT_OK;
}

FitCode Stop()
{
    FIT_LOG_INFO("Registry address config is stopped.");
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start)
        .SetStop(Stop);
}
} // LCOV_EXCL_LINE