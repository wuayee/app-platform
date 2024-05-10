/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : Provide config service factory
 * Author       : w00561424
 * Date         : 2023/08/28
 * Notes:       :
 */
#include <configuration_service_factory.h>
#include <configuration_service_for_config_file.h>
#include <configuration_service_for_config_center.h>
#include <configuration_service_for_registry_server.h>
#include <configuration_service_composite.h>
#include <configuration_repo_impl.h>
#include <fit/internal/fit_string_util.h>
#include <fit_log.h>
namespace Fit {
namespace Configuration {
ConfigurationServiceFactory::ConfigurationServiceFactory()
{
}
ConfigurationServicePtr ConfigurationServiceFactory::CreateConfigurationServiceForConfigFile(
    ConfigurationRepoPtr repo, Plugin::PluginManager* pluginManager, const Fit::string& configFilePath,
    Fit::Config::SystemConfigPtr systemConfig, Fit::Framework::Formatter::FormatterServicePtr formatterService)
{
    configurationServices_[ConfigurationServiceForConfigFile::Type()]
        = Fit::make_shared<ConfigurationServiceForConfigFile>(repo, pluginManager, configFilePath,
        systemConfig, formatterService);
    return configurationServices_[ConfigurationServiceForConfigFile::Type()];
}

ConfigurationServicePtr ConfigurationServiceFactory::CreateConfigurationServiceForConfigCenter(
    ConfigurationClientPtr configClient, ConfigurationRepoPtr repo)
{
    configurationServices_[ConfigurationServiceForConfigCenter::Type()]
        = Fit::make_shared<ConfigurationServiceForConfigCenter>(configClient, repo);
    return configurationServices_[ConfigurationServiceForConfigCenter::Type()];
}

ConfigurationServicePtr ConfigurationServiceFactory::CreateConfigurationServiceForRegistryServer(
    ConfigurationRepoPtr repo, const Fit::string& environment, ConfigurationServiceSpiPtr spi)
{
    configurationServices_[ConfigurationServiceForRegistryServer::Type()]
        = Fit::make_shared<ConfigurationServiceForRegistryServer>(std::move(repo), environment, std::move(spi));
    return configurationServices_[ConfigurationServiceForRegistryServer::Type()];
}

ConfigurationServiceCompositePtr ConfigurationServiceFactory::CreateConfigurationServiceComposite(
    const Fit::vector<Fit::string>& priorityConfigSourceTypes)
{
    Fit::vector<ConfigurationServicePtr> configServices;
    for (const auto& type : priorityConfigSourceTypes) {
        if (configurationServices_.count(type) == 0) {
            FIT_LOG_WARN("Config service not found, type=%s.", type.c_str());
            continue;
        }
        configServices.emplace_back(configurationServices_[type]);
    }
    ConfigurationServiceCompositePtr configServiceComposite
        = Fit::make_shared<ConfigurationServiceComposite>(configServices);
    return configServiceComposite;
}

ConfigurationRepoPtr ConfigurationServiceFactory::CreateConfigurationRepo()
{
    return Fit::make_shared<ConfigurationRepoImpl>();
}

ConfigurationServiceSpiPtr ConfigurationServiceFactory::CreateConfigurationServiceSpi()
{
    return Fit::make_shared<ConfigurationServiceSpiImpl>();
}

ConfigurationServiceFactoryPtr ConfigurationServiceFactory::Instance()
{
    static ConfigurationServiceFactoryPtr configurationServiceFactoryPtr
        = Fit::make_shared<ConfigurationServiceFactory>();;
    return configurationServiceFactoryPtr;
}
}
}