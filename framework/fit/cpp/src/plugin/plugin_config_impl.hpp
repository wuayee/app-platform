/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 16:42:55
 */

#ifndef PLUGIN_CONFIG_IMPL_HPP
#define PLUGIN_CONFIG_IMPL_HPP

#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/runtime/config/config_value_rapidjson.hpp>
#include "fit/external/plugin/plugin_config.hpp"
#include "fit/fit_code.h"
#include "rapidjson/document.h"
#include "fit/stl/string.hpp"

namespace Fit {
namespace Plugin {
class PluginConfigImpl : public PluginConfig {
public:
    FitCode Init(const Fit::string& configPath);
    FitCode Init(const Fit::string& configPath, const Fit::Config::SystemConfig *config);
    
    Fit::Config::Value& Get(const Fit::string &key) override;

private:
    rapidjson::Document jsonDocument_ {};
    std::unique_ptr<Config::ValueRapidJson> valueRapidJson_;
    const Fit::Config::SystemConfig *systemConfig_ {};
};
PluginConfigPtr CreateDefaultPluginConfig(const Fit::string &configPath, const Fit::Config::SystemConfig *systemConfig);
}  // namespace Plugin
}  // namespace Fit

#endif