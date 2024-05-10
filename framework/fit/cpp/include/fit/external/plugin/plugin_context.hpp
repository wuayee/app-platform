/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 16:38:43
 */

#ifndef PLUGIN_CONTEXT_HPP
#define PLUGIN_CONTEXT_HPP

#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <memory>
#include "plugin_config.hpp"


namespace Fit {
namespace Plugin {
class PluginContext {
public:
    virtual PluginConfigPtr GetConfig() = 0;
};

using PluginContextPtr = std::shared_ptr<PluginContext>;

PluginContextPtr CreatePluginContext(PluginConfigPtr config);
}  // namespace Plugin
}  // namespace Fit

#endif