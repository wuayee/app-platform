/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide config service invoke proxy.
 * Author       : w00561424
 * Date         : 2023/08/28
 * Notes:       :
 */
#include <configuration_service_proxy.h>
#include <fit/internal/runtime/runtime.hpp>
#include <configuration_client.h>
#include <configuration_repo.h>
#include <configuration_service_factory.h>
#include <fit/external/util/string_utils.hpp>
#include <fit_log.h>
#include <fit_code.h>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <genericable/com_huawei_matata_conf_client_download/1.0.0/cplusplus/download.hpp>
using namespace Fit::Framework::Formatter;
using namespace Fit::Config;
namespace Fit {
namespace Configuration {
ConfigurationServiceProxy::ConfigurationServiceProxy(ConfigurationServicePtr baseConfigService,
    ConfigurationServiceCompositePtr configurationServiceComposite)
    : baseConfigService_(std::move(baseConfigService)),
    configurationServiceCompositeDefault_(configurationServiceComposite)
{
}

GenericConfigPtr ConfigurationServiceProxy::GetGenericableConfigPtr(const Fit::string &genericId) const
{
    // get genericable config source config
    auto configurationServiceComposite = configurationServiceCompositeDefault_;
    GenericConfigPtr getter = baseConfigService_->GetGenericableConfigPtr(genericId);
    ConfigSourceTypes configSourceTypes;
    if (getter != nullptr) {
        configSourceTypes = getter->GetConfigSourceTypes();
    }
    if (!configSourceTypes.empty()) {
        configurationServiceComposite
            = ConfigurationServiceFactory::Instance()->CreateConfigurationServiceComposite(configSourceTypes);
    }
    if (configurationServiceComposite == nullptr) {
        FIT_LOG_ERROR("Config service is null.");
        return nullptr;
    }
    return configurationServiceComposite->GetGenericableConfigPtr(genericId);
}

int32_t ConfigurationServiceProxy::GetGenericableConfig(const Fit::string &genericId,
    GenericableConfiguration &genericable)
{
    // get genericable config source config
    auto configurationServiceComposite = configurationServiceCompositeDefault_;
    GenericConfigPtr getter = baseConfigService_->GetGenericableConfigPtr(genericId);
    ConfigSourceTypes configSourceTypes;
    if (getter != nullptr) {
        configSourceTypes = getter->GetConfigSourceTypes();
    }
    if (!configSourceTypes.empty()) {
        configurationServiceComposite
            = ConfigurationServiceFactory::Instance()->CreateConfigurationServiceComposite(configSourceTypes);
    }

    if (configurationServiceComposite == nullptr) {
        FIT_LOG_ERROR("Config service is null.");
        return FIT_ERR_NOT_FOUND;
    }
    return configurationServiceComposite->GetGenericableConfig(genericId, genericable);
}

bool ConfigurationServiceProxy::HasConfigClient()
{
    return !GetRuntime().GetElementIs<Framework::FitableDiscovery>()->GetLocalFitableByGenericId(
        matata::conf::client::download::GENERIC_ID).empty();
}

bool ConfigurationServiceProxy::Start()
{
    auto pluginManager = GetRuntime().GetElementIs<Plugin::PluginManager>();
    if (pluginManager == nullptr) {
        FIT_LOG_ERROR("Need plugin manager.");
        return false;
    }
    SystemConfigPtr systemConfig
        = SystemConfigPtr(GetRuntime().GetElementIs<SystemConfig>(), [](SystemConfig*) {});
    FormatterServicePtr formatterService
        = FormatterServicePtr(GetRuntime().GetElementIs<FormatterService>(), [](FormatterService*) {});
    Fit::string configFilePath =
        systemConfig->GetValue("broker_config_file").AsString("need set broker config file!!!");
    baseConfigService_ = ConfigurationServiceFactory::Instance()->CreateConfigurationServiceForConfigFile(
        ConfigurationServiceFactory::Instance()->CreateConfigurationRepo(), pluginManager, configFilePath,
        systemConfig, formatterService);
    if (!baseConfigService_->Start()) {
        FIT_LOG_ERROR("Start configuration service for config file filed.");
        return false;
    }

    if (HasConfigClient()) {
        auto confClient = GetRuntime().GetElementIs<Configuration::ConfigurationClient>();
        if (confClient == nullptr) {
            FIT_LOG_ERROR("Need config client.");
            return false;
        }
        ConfigurationClientPtr configClient = ConfigurationClientPtr(confClient, [](ConfigurationClient*) {});
        ConfigurationServiceFactory::Instance()->CreateConfigurationServiceForConfigCenter(
            configClient, ConfigurationServiceFactory::Instance()->CreateConfigurationRepo());
    }

    Fit::string environment = systemConfig->GetEnvName();

    FIT_LOG_CORE("Environment is: %s.", environment.c_str());
    ConfigurationServiceFactory::Instance()->CreateConfigurationServiceForRegistryServer(
        ConfigurationServiceFactory::Instance()->CreateConfigurationRepo(), environment,
        ConfigurationServiceFactory::Instance()->CreateConfigurationServiceSpi());

    // parse from worker_config.json
    Fit::string priorityConfigSourcesStr =
        systemConfig->GetValue("genericable-config-sources").AsString("config_file");
    FIT_LOG_CORE("Default config source types is: %s.", priorityConfigSourcesStr.c_str());
    Fit::vector<Fit::string> priorityConfigSources = Fit::StringUtils::Split(priorityConfigSourcesStr, ',');
    configurationServiceCompositeDefault_ = ConfigurationServiceFactory::Instance()->
        CreateConfigurationServiceComposite(priorityConfigSources);
    if (configurationServiceCompositeDefault_ == nullptr) {
        FIT_LOG_ERROR("Config sources is error, %s.", priorityConfigSourcesStr.c_str());
        return false;
    }
    return true;
}

bool ConfigurationServiceProxy::Stop()
{
    return true;
}

ConfigurationServicePtr ConfigurationServiceProxy::BaseConfigurationService()
{
    return baseConfigService_;
}

// 只会调用一次，用于引擎启动编排
Fit::unique_ptr<ConfigurationService> ConfigurationService::Create()
{
    return Fit::unique_ptr<ConfigurationService>((ConfigurationService*)ConfigurationServiceProxy::Instance());
}

ConfigurationService* ConfigurationService::Instance()
{
    static ConfigurationServiceProxy* configServicePtr {nullptr};
    if (configServicePtr == nullptr) {
        configServicePtr = new ConfigurationServiceProxy();
    }
    return configServicePtr;
}

ConfigurationServicePtr ConfigurationService::BaseConfigurationService()
{
    return static_cast<ConfigurationServiceProxy*>(ConfigurationService::Instance())->BaseConfigurationService();
}
}
}