/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 插件的启动和卸载的回调函数
 * Author       : wangpanbo
 * Date:        : 2024/08/06
 */
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include "broker_server_config.h"
namespace {
const char * const IS_ENABLE_ACCESS_TOKEN = "secure-access.enable";

FitCode Start(::Fit::Framework::PluginContext* context)
{
    Fit::BrokerServerConfig::Instance()->SetIsEnableAccessToken(
        context->GetConfig()->Get(IS_ENABLE_ACCESS_TOKEN).AsBool(false));
    return FIT_OK;
}
FitCode Stop()
{
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start)
        .SetStop(Stop);
}
} // LCOV_EXCL_LINE