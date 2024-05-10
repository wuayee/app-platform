/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 16:46:31
 */

#include "plugin_config_impl.hpp"

#include <fstream>
#include <memory>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include <fit/internal/fit_filesystem_util.hpp>

#include "fit/external/plugin/plugin_config.hpp"
#include "fit/fit_code.h"
#include "fit/fit_log.h"
#include "rapidjson/document.h"
#include "rapidjson/istreamwrapper.h"
#include "rapidjson/rapidjson.h"

using rapidjson::Document;
using rapidjson::IStreamWrapper;

namespace Fit {
namespace Plugin {
Fit::Config::Value g_nullValue;
PluginConfigPtr CreateDefaultPluginConfig(const Fit::string &configPath, const Fit::Config::SystemConfig *systemConfig)
{
    auto config = std::make_shared<PluginConfigImpl>();
    config->Init(configPath, systemConfig);

    return config;
}

FitCode PluginConfigImpl::Init(const Fit::string &configPath)
{
    std::ifstream ifs(configPath.c_str());
    if (!ifs.is_open()) {
        FIT_LOG_ERROR("Failed to open plugin config file `%s`", configPath.c_str());
        return FIT_ERR_FAIL;
    }
    IStreamWrapper wrapper(ifs);

    Document configJson;
    configJson.ParseStream(wrapper);
    if (configJson.HasParseError()) {
        FIT_LOG_ERROR("Failed to parse config file, invalid file format `%s`", configPath.c_str());
        return FIT_ERR_FAIL;
    }

    jsonDocument_ = std::move(configJson);
    valueRapidJson_ = make_unique<Config::ValueRapidJson>(&jsonDocument_, &jsonDocument_);

    return FIT_OK;
}

FitCode PluginConfigImpl::Init(const Fit::string &configPath, const Fit::Config::SystemConfig *config)
{
    systemConfig_ = config;
    if (!Util::Filesystem::FileExists(configPath)) {
        return FIT_OK;
    }

    return Init(configPath);
}

Fit::Config::Value& PluginConfigImpl::Get(const Fit::string &key)
{
    if (systemConfig_) {
        auto &value = systemConfig_->GetValue(key.c_str());
        if (!value.IsNull()) {
            return value;
        }
    }
    if (!valueRapidJson_) {
        return g_nullValue;
    }
    return (*valueRapidJson_)[key.c_str()];
}

PluginConfigPtr CreatePluginConfig(const Fit::string &configPath)
{
    auto config = std::make_shared<PluginConfigImpl>();
    if (!configPath.empty() && config->Init(configPath) != FIT_OK) {
        return nullptr;
    }

    return config;
}
}  // namespace Plugin
} // LCOV_EXCL_LINE