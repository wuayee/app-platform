/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/08/29
 * Notes:       :
 */
#ifndef CONFIGURATION_SERVICE_FOR_CONFIG_FILE_H
#define CONFIGURATION_SERVICE_FOR_CONFIG_FILE_H

#include <functional>

#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>
#include <fit/internal/fit_range_utils.h>
#include <fit/internal/runtime/runtime_factory.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include "configuration_client.h"
#include "configuration_repo.h"
#include "configuration_service.h"

namespace Fit {
namespace Configuration {
class ConfigurationServiceForConfigFile : public ConfigurationService {
public:
    ConfigurationServiceForConfigFile(ConfigurationRepoPtr repo, Plugin::PluginManager* pluginManager,
        const Fit::string& configFilePath, Fit::Config::SystemConfigPtr systemConfig,
        Fit::Framework::Formatter::FormatterServicePtr formatterService);
    ~ConfigurationServiceForConfigFile() override = default;

    bool Start() override;
    static Fit::string Type();
    int32_t GetGenericableConfig(const Fit::string &genericId, GenericableConfiguration &genericable) override;
    GenericConfigPtr GetGenericableConfigPtr(const Fit::string &genericId) const override;
    int32_t LoadFromFile(const Fit::string &file);
private:
    void MergeFitableConfiguration(const GenericConfigPtr &config);
    ::fit::hakuna::kernel::registry::shared::Application GetApplication() const;
private:
    ConfigurationRepoPtr repo_ {};
    mutable Fit::shared_mutex sharedMutex_ {};
    Plugin::PluginManager* pluginManager_ {};
    Fit::string configFilePath_ {};
    Fit::Config::SystemConfigPtr systemConfig_;
    Fit::Framework::Formatter::FormatterServicePtr formatterService_;
};
}
}

#endif // CONFIGURATION_SERVICE_IMPL_H
