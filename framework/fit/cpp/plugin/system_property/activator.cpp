/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/1
 * Notes:       :
 */

#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>
#include "core/fit_system_property_service.h"

using namespace Fit;
using namespace Fit::SDK::System;
namespace {
void LoadMappingPorts(Fit::Plugin::PluginConfig& config)
{
    auto& portMappingNode = config.Get("server-port-mapping");
    if (portMappingNode.IsNull() || !portMappingNode.IsArray()) {
        return;
    }

    map<int32_t, int32_t> mappingPorts;
    for (int32_t i = 0; i < portMappingNode.Size(); i++) {
        auto& mappingNode = portMappingNode[i];
        auto& from = mappingNode["from"];
        if (!from.IsInt()) {
            continue;
        }
        auto fromPort = from.AsInt();
        auto& to = mappingNode["to"];
        if (!to.IsInt()) {
            continue;
        }
        mappingPorts[fromPort] = to.AsInt();
        FIT_LOG_INFO("Load port mapping, (%d->%d).", fromPort, to.AsInt());
    }
    FitSystemPropertyService::GetService()->SetMappingPorts(move(mappingPorts));
}
FitCode Start(::Fit::Framework::PluginContext *context)
{
    Fit::string workerId = context->GetConfig()->Get("worker_id").AsString("");
    if (workerId.empty()) {
        return FIT_ERR_NOT_FOUND;
    }
    FitSystemPropertyService::GetService()->Put("fit_worker_id", workerId, true);

    Fit::string environment = context->GetConfig()->Get("environment").AsString("");
    if (environment.empty()) {
        return FIT_ERR_NOT_FOUND;
    }
    FitSystemPropertyService::GetService()->Put("fit_worker_env_type", environment, true);

    Fit::string environmentChain = context->GetConfig()->Get("environment_chain").AsString("");
    if (environmentChain.empty()) {
        return FIT_ERR_NOT_FOUND;
    }
    FitSystemPropertyService::GetService()->Put("fit_worker_env_callchain", environmentChain, true);

    auto& app = context->GetConfig()->Get("app");
    if (!app.IsObject()) {
        return FIT_ERR_NOT_FOUND;
    }
    FitSystemPropertyService::GetService()->SetApplicationExtensions(app["extensions"]);
    FitSystemPropertyService::GetService()->SetWorkerExtensions(context->GetConfig()->Get("worker.extensions"));
    LoadMappingPorts(*context->GetConfig());
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start);
}
} // LCOV_EXCL_LINE