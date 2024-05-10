/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-02 15:56:05
 */

#include <fit/fit_code.h>

#include <fit/fit_log.h>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/external/plugin/plugin_context.hpp>

namespace {
FitCode Start(::Fit::Framework::PluginContext* context)
{
    FIT_LOG_INFO("Call plugin start func");
    Fit::string value = context->GetConfig()->Get("testKey").AsString("");
    if (value != "testValue") {
        return FIT_ERR_NOT_FOUND;
    }
    return FIT_OK;
}

FitCode Stop()
{
    FIT_LOG_INFO("Call plugin stop func");
    return FIT_OK;
}
}  // namespace

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar().SetStart(Start).SetStop(Stop);
}