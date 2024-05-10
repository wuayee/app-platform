/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 16:33:55
 */

#ifndef PLUGIN_CONFIG_HPP
#define PLUGIN_CONFIG_HPP
#include <fit/stl/string.hpp>
#include <fit/external/runtime/config/config_value.hpp>
#include <memory>

namespace Fit {
namespace Plugin {
class PluginConfig {
public:
    virtual Fit::Config::Value& Get(const Fit::string &key) = 0;
};

using PluginConfigPtr = std::shared_ptr<PluginConfig>;

PluginConfigPtr CreatePluginConfig(const Fit::string &configPath = "");
}  // namespace Plugin
}  // namespace Fit

#endif