/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/1
 * Notes:       :
 */
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include <genericable/com_huawei_fit_heartbeat_subscribe_heartbeat/1.0.0/cplusplus/subscribeHeartbeat.hpp>
#include "core/fit_registry_mgr.h"
#include "core/fit_registry_conf.h"

namespace {
FitCode Start(::Fit::Framework::PluginContext* context)
{
    auto ret = Fit::Registry::InitConfig(context->GetConfig());
    if (ret != FIT_OK) {
        return ret;
    }

    Fit::Registry::fit_registry_mgr::instance()->start_task();
    FIT_LOG_INFO("Start registry server.");
    return FIT_OK;
}

FitCode Stop()
{
    Fit::Registry::fit_registry_mgr::instance()->stop_task();
    FIT_LOG_INFO("Stop registry server.");
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start)
        .SetStop(Stop);
}
} // LCOV_EXCL_LINE