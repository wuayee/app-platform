/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 17:12:07
 */

#ifndef PLUGIN_CONTEXT_IMPL_HPP
#define PLUGIN_CONTEXT_IMPL_HPP

#include "fit/external/plugin/plugin_config.hpp"
#include "fit/external/plugin/plugin_context.hpp"

namespace Fit {
namespace Plugin {
class PluginContextImpl : public PluginContext {
public:
    explicit PluginContextImpl(PluginConfigPtr pluginConfig);
    ~PluginContextImpl() = default;
    PluginConfigPtr GetConfig() override;

private:
    PluginConfigPtr pluginConfig_{};
};
}  // namespace Plugin
}  // namespace Fit

#endif