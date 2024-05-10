/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 17:13:37
 */

#include "plugin_context_impl.hpp"

#include "fit/external/plugin/plugin_config.hpp"
#include "fit/external/plugin/plugin_context.hpp"
#include "fit/fit_code.h"
#include "fit/fit_log.h"

#include <memory>

namespace Fit {
namespace Plugin {
PluginContextImpl::PluginContextImpl(PluginConfigPtr pluginConfig) : pluginConfig_(std::move(pluginConfig)) {}

PluginConfigPtr PluginContextImpl::GetConfig()
{
    return pluginConfig_;
}

PluginContextPtr CreatePluginContext(PluginConfigPtr pluginConfig)
{
    return std::make_shared<PluginContextImpl>(pluginConfig);
}
}  // namespace Plugin
}  // namespace Fit