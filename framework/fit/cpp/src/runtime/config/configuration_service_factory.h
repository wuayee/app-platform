/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : Provide config service factory
 * Author       : w00561424
 * Date         : 2023/08/28
 * Notes:       :
 */
#ifndef CONFIG_SERVICE_FACTORY_H
#define CONFIG_SERVICE_FACTORY_H
#include <configuration_service.h>
#include <configuration_service_composite.h>
#include <configuration_repo.h>
#include <configuration_client.h>
#include <configuration_service_spi.h>
#include <fit/internal/plugin/plugin_manager.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/stl/unordered_map.hpp>
#include <configuration_service_spi.h>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
namespace Fit {
namespace Configuration {
class ConfigurationServiceFactory {
public:
    ConfigurationServiceFactory();
    ConfigurationServicePtr CreateConfigurationServiceForConfigFile(ConfigurationRepoPtr repo,
        Plugin::PluginManager* pluginManager, const Fit::string& configFilePath,
        Fit::Config::SystemConfigPtr systemConfig, Fit::Framework::Formatter::FormatterServicePtr formatterService);
    ConfigurationServicePtr CreateConfigurationServiceForConfigCenter(ConfigurationClientPtr configClient,
        ConfigurationRepoPtr repo);
    ConfigurationServicePtr CreateConfigurationServiceForRegistryServer(ConfigurationRepoPtr repo,
        const Fit::string& environment, ConfigurationServiceSpiPtr spi);
    ConfigurationServiceCompositePtr CreateConfigurationServiceComposite(const Fit::vector<Fit::string>&
        priorityConfigSourceTypes);
    ConfigurationRepoPtr CreateConfigurationRepo();
    ConfigurationServiceSpiPtr CreateConfigurationServiceSpi();
    static Fit::shared_ptr<ConfigurationServiceFactory> Instance();
private:
    Fit::unordered_map<Fit::string, ConfigurationServicePtr> configurationServices_ {};
    Fit::mutex factoryMutex_ {};
};
using ConfigurationServiceFactoryPtr = Fit::shared_ptr<ConfigurationServiceFactory>;
}
}
#endif