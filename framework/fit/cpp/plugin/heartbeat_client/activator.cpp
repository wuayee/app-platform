/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/6/1
 * Notes:       :
 */

#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include "Core/ClientManager.hpp"
namespace {
FitCode Start(::Fit::Framework::PluginContext *context)
{
    return FIT_OK;
}

FitCode Stop()
{
    Fit::Heartbeat::Client::ClientManager::Instance()->Uninit();
    Fit::Heartbeat::Client::ClientManager::Destroy();
    FIT_LOG_CORE("Uninit heartbeat client");
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start)
        .SetStop(Stop);
}
}