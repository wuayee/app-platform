/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : start component
 * Author       : songyongtan
 * Date         : 2022/5/23
 * Notes:       :
 */


#include "component_starter_element.hpp"

#include <fit/fit_log.h>
#include <fit/internal/runtime/base_plugins_load.h>
#include <fit/internal/framework/plugin_activator_collector_inner.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include "plugin/plugin_config_impl.hpp"

using namespace Fit;
using Fit::Framework::PopPluginActivatorCache;
using Plugin::CreateDefaultPluginConfig;
using Config::SystemConfig;

ComponentStarterElement::ComponentStarterElement() : RuntimeElementBase("componentStarter") {}

ComponentStarterElement::~ComponentStarterElement() = default;

bool ComponentStarterElement::Start()
{
    if (!Load()) {
        return false;
    }
    return StartGlobalActivators() == FIT_OK;
}

bool ComponentStarterElement::Stop()
{
    StopGlobalActivators();
    return true;
}

FitCode ComponentStarterElement::StartGlobalActivators()
{
    context_ = CreatePluginContext(
        CreateDefaultPluginConfig("", GetRuntime().GetElementIs<SystemConfig>()));
    auto globalActivators = PopPluginActivatorCache();
    activators_.reserve(globalActivators.size());
    for (auto &activator : globalActivators) {
        auto ret = activator->GetStart()(context_.get());
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to start activator.");
            return ret;
        }
        activators_.push_back(activator);
    }

    return FIT_OK;
}

void ComponentStarterElement::StopGlobalActivators()
{
    auto iter = activators_.rbegin();
    for (; iter != activators_.rend(); ++iter) {
        auto& info = *iter;
        if (info->GetStop()) {
            info->GetStop()();
        }
    }
    activators_.clear();
}

bool ComponentStarterElement::Load()
{
    BasePluginsLoad basePluginsLoad;
    return basePluginsLoad.LoadBasePlugins(GetRuntime().GetElementIs<SystemConfig>()) == FIT_OK;
}
