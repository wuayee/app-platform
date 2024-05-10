/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/08/29
 * Notes:       :
 */
#include "configuration_service_for_config_file.h"

#include <functional>
#include <mutex>
#include <cstring>

#include <fit/internal/runtime/runtime.hpp>
#include <genericable_configuration_json_parser.hpp>
#include <fit/internal/runtime/config/system_config.hpp>

namespace Fit {
namespace Configuration {
ConfigurationServiceForConfigFile::ConfigurationServiceForConfigFile(ConfigurationRepoPtr repo,
    Plugin::PluginManager* pluginManager,
    const Fit::string& configFilePath, Fit::Config::SystemConfigPtr systemConfig,
    Fit::Framework::Formatter::FormatterServicePtr formatterService)
    : repo_(move(repo)), pluginManager_(pluginManager), configFilePath_(configFilePath),
    systemConfig_(systemConfig), formatterService_(formatterService)
{
}

Fit::string ConfigurationServiceForConfigFile::Type()
{
    return "config_file";
}
::fit::hakuna::kernel::registry::shared::Application ConfigurationServiceForConfigFile::GetApplication() const
{
    ::fit::hakuna::kernel::registry::shared::Application application;
    if (systemConfig_ != nullptr) {
        application.name = systemConfig_->GetAppName();
        application.nameVersion = systemConfig_->GetAppVersion();
    }
    return application;
}
int32_t ConfigurationServiceForConfigFile::GetGenericableConfig(const Fit::string &genericId,
    GenericableConfiguration &genericable)
{
    auto configPtr = GetGenericableConfigPtr(genericId);
    if (configPtr == nullptr) {
        return FIT_ERR_NOT_FOUND;
    }
    genericable = *configPtr;
    return FIT_ERR_SUCCESS;
}

GenericConfigPtr ConfigurationServiceForConfigFile::GetGenericableConfigPtr(const Fit::string &genericId) const
{
    Fit::shared_lock<Fit::shared_mutex> lock(sharedMutex_);
    if (!repo_) {
        FIT_LOG_ERROR("Null repo.");
        return nullptr;
    }
    auto config = repo_->Getter(genericId);
    if (config == nullptr) {
        return config;
    }
    auto application = GetApplication();
    auto fitables = config->GetFitables();
    for (auto& fitable : fitables) {
        fitable.applications = {application};
        if (formatterService_ != nullptr) {
            fitable.applicationsFormats = {formatterService_->GetFormats(genericId)};
        }
        config->SetFitable(fitable);
    }
    return config;
}

void ConfigurationServiceForConfigFile::MergeFitableConfiguration(const GenericConfigPtr &config)
{
    GenericableConfiguration current {};
    if (repo_->Get(config->GetGenericId(), current) == FIT_OK) {
        for (auto &fitable : current.GetFitables()) {
            config->SetFitable(fitable);
        }
    }
}

FitCode ConfigurationServiceForConfigFile::LoadFromFile(const Fit::string &file)
{
    return GenericableConfigurationJsonParser([this](GenericConfigPtr config) {
        repo_->Set(std::move(config));
    }).LoadFromFile(file);
}


bool ConfigurationServiceForConfigFile::Start()
{
    if (FIT_ERR_SUCCESS != LoadFromFile(configFilePath_)) {
        return false;
    }
    pluginManager_->ObservePluginsStartBefore(
        [this](const ::Fit::Plugin::Plugin& plugin) {
            if (plugin.GetPluginArchive().genericablesConfigFilePath.empty()) {
                return;
            }
            GenericableConfigurationJsonParser([this](GenericConfigPtr config) {
                MergeFitableConfiguration(config);
                repo_->Set(std::move(config));
            }).LoadFromFile(plugin.GetPluginArchive().genericablesConfigFilePath);
        });
    FIT_LOG_INFO("Genericable config service is started.");
    return true;
}
}
} // LCOV_EXCL_LINE