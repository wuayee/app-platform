/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-15 16:52:30
 */

#include "plugin_manager_impl.hpp"

#include <algorithm>
#include <memory>
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/framework/plugin_activator_collector_inner.hpp>
#include <fit/internal/plugin/plugin_library.hpp>
#include <fit/internal/util/task_stream.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>

#include "plugin_config_impl.hpp"
#include "plugin_archive_builder.hpp"

using Fit::Config::SystemConfig;

namespace Fit {
namespace Plugin {
PluginManagerImpl::PluginManagerImpl(std::unique_ptr<LibraryLoader> libraryLoader)
    : loader_ {std::move(libraryLoader)} {}

PluginManagerImpl::~PluginManagerImpl() = default;

void PluginManagerImpl::UninitAllPlugin()
{
    FIT_LOG_INFO("Begin uninstall user plugins.");
    UninstallPlugins(orderedUserPluginsByLevel_);
    FIT_LOG_INFO("End uninstall user plugins.");

    FIT_LOG_INFO("Begin uninstall system plugins.");
    UninstallPlugins(orderedSystemPluginsByLevel_);
    FIT_LOG_INFO("End uninstall system plugins.");
}

FitCode PluginManagerImpl::LoadFrom(const Fit::Config::SystemConfig* config)
{
    constexpr ValidLevelRange defaultSystemPluginLevel {-6, 0, 0};
    constexpr ValidLevelRange defaultUserPluginLevel {1, 6, 3};
    systemConfig_ = config;

    Fit::string workerDirectoryRealPath;
    if (!GetWorkDirectory(config, workerDirectoryRealPath)) {
        return FIT_ERR_PARAM;
    }
    auto& systemPluginsValue = config->GetValue("plugins.system-plugins");
    auto& userPluginsValue = config->GetValue("plugins.user-plugins");

    auto ret = TaskStream()
        .Then([this, &workerDirectoryRealPath, &defaultSystemPluginLevel, &systemPluginsValue]() -> FitCode {
                return LoadPlugin(workerDirectoryRealPath, defaultSystemPluginLevel, systemPluginsValue,
                    orderedSystemPluginsByLevel_);
            },
        FIT_OK)
        .Then([this, &workerDirectoryRealPath, &defaultUserPluginLevel, &userPluginsValue]() -> FitCode {
                return LoadPlugin(workerDirectoryRealPath, defaultUserPluginLevel, userPluginsValue,
                    orderedUserPluginsByLevel_);
            },
        FIT_OK)
        .Run();
    if (!ret) {
        return FIT_ERR_FAIL;
    }

    return FIT_OK;
}

bool PluginManagerImpl::GetWorkDirectory(const Config::SystemConfig* config, string& workerDirectoryRealPath)
{
    Fit::string workerDirectoryValue;
    char realPath[PATH_MAX] {};
    workerDirectoryValue = config->GetValue("worker.directory").AsString(".");
    auto realpathRet = realpath(workerDirectoryValue.c_str(), realPath);
    if (realpathRet == nullptr) {
        FIT_LOG_ERROR("Failed to realpath(%s).", workerDirectoryValue.c_str());
        return false;
    }
    workerDirectoryRealPath = realPath;

    return true;
}

FitCode PluginManagerImpl::LoadPlugin(const Fit::string& workerDirectory, const ValidLevelRange& levelRange,
    Config::Value& pluginsValue, Fit::map<int32_t, Plugins>& resultPlugins)
{
    for (int32_t i = 0; i < pluginsValue.Size(); ++i) {
        auto& plugin = pluginsValue[i];
        PluginArchive pluginArchive = PluginArchiveBuilder()
            .SetConfig(plugin)
            .SetWorkDirectory(workerDirectory)
            .SetValidLevelRange(levelRange)
            .Build();
        if (!levelRange.IsValid(pluginArchive.startLevel)) {
            FIT_LOG_ERROR("Illegal plugin(%s), start level = %d, support range(%d~%d).", pluginArchive.location.c_str(),
                pluginArchive.startLevel, levelRange.begin, levelRange.end);
            return FIT_ERR_PARAM;
        }

        resultPlugins[pluginArchive.startLevel].emplace_back(
            make_unique<PluginLibrary>(pluginArchive,
                [this](const PluginArchive &pluginArchive) -> PluginContextPtr {
                    return CreatePluginContext(CreateDefaultPluginConfig(pluginArchive.configFilePath, systemConfig_));
                }, loader_.get()));
    }

    return FIT_OK;
}

FitCode PluginManagerImpl::StartSystemPlugins()
{
    FIT_LOG_INFO("Begin start system plugins.");
    auto ret = StartWithLevel(orderedSystemPluginsByLevel_);
    FIT_LOG_INFO("End start system plugins.");

    return ret;
}

FitCode PluginManagerImpl::StartUserPlugins()
{
    FIT_LOG_INFO("Begin start user plugins.");
    auto ret = StartWithLevel(orderedUserPluginsByLevel_);
    FIT_LOG_INFO("End start user plugins.");

    return ret;
}

FitCode PluginManagerImpl::InstallPlugins(const Plugins& plugins)
{
    for (auto& plugin : plugins) {
        auto ret = plugin->Install();
        if (ret != FIT_OK) {
            return ret;
        }
    }
    return FIT_OK;
}

FitCode PluginManagerImpl::ResolvePlugins(const Plugins& plugins)
{
    for (auto& plugin : plugins) {
        auto ret = plugin->Resolve();
        if (ret != FIT_OK) {
            return ret;
        }
    }
    return FIT_OK;
}

FitCode PluginManagerImpl::StartPlugins(const Plugins& plugins)
{
    for (auto& plugin : plugins) {
        CallbackPluginsStartBefore(*plugin);
        auto ret = plugin->Start();
        if (ret != FIT_OK) {
            return ret;
        }
        CallbackPluginsStarted(*plugin);
    }
    return FIT_OK;
}

FitCode PluginManagerImpl::StartWithLevel(const Fit::map<int32_t, Plugins>& plugins)
{
    for (auto& levelPlugins : plugins) {
        FIT_LOG_INFO("Start: begin the start level %d.", levelPlugins.first);
        auto ret = TaskStream()
            .Then([&levelPlugins]() -> FitCode { return InstallPlugins(levelPlugins.second); }, FIT_OK)
            .Then([&levelPlugins]() -> FitCode { return ResolvePlugins(levelPlugins.second); }, FIT_OK)
            .Then([&levelPlugins, this]() -> FitCode { return StartPlugins(levelPlugins.second); }, FIT_OK)
            .Run();
        if (!ret) {
            return FIT_ERR_FAIL;
        }
        FIT_LOG_INFO("Start: end the start level %d.", levelPlugins.first);
    }

    return FIT_OK;
}

FitCode PluginManagerImpl::StopSystemPlugins()
{
    FIT_LOG_INFO("Begin stop system plugins.");
    auto ret = StopWithLevel(orderedSystemPluginsByLevel_);
    FIT_LOG_INFO("End stop system plugins.");

    return ret;
}

FitCode PluginManagerImpl::StopUserPlugins()
{
    FIT_LOG_INFO("Begin stop user plugins.");
    auto ret = StopWithLevel(orderedUserPluginsByLevel_);
    FIT_LOG_INFO("End stop user plugins.");

    return ret;
}

FitCode PluginManagerImpl::StopWithLevel(const map<int32_t, Plugins>& plugins)
{
    // 停止时按照启动时的逆序操作
    std::for_each(plugins.rbegin(), plugins.rend(),
        [this](const map<int32_t, Plugins>::value_type& item) {
            FIT_LOG_INFO("Stop: begin the start level %d.", item.first);
            std::for_each(item.second.rbegin(), item.second.rend(), [this](const PluginPtr& plugin) {
                plugin->Stop();
                CallbackPluginsStopped(*plugin);
            });
            FIT_LOG_INFO("Stop: end the start level %d.", item.first);
        });

    return FIT_OK;
}

void PluginManagerImpl::UninstallPlugins(const map<int32_t, Plugins>& plugins)
{
    std::for_each(plugins.rbegin(), plugins.rend(),
        [](const map<int32_t, Plugins>::value_type& item) {
            FIT_LOG_INFO("Uninstall: begin the start level %d.", item.first);
            std::for_each(item.second.rbegin(), item.second.rend(), [](const PluginPtr& plugin) {
                plugin->Uninstall();
            });
            FIT_LOG_INFO("Uninstall: end the start level %d.", item.first);
        });
}

bool PluginManagerImpl::Stop()
{
    StopUserPlugins();
    StopSystemPlugins();
    UninitAllPlugin();
    FIT_LOG_INFO("Plugin manager is stopped.");
    return true;
}

bool PluginManagerImpl::Start()
{
    return TaskStream()
        .Then([this]() -> FitCode { return LoadFrom(GetRuntime().GetElementIs<SystemConfig>()); }, FIT_OK)
        .Then([this]() -> FitCode { return StartSystemPlugins(); }, FIT_OK)
        .Then([this]() { CallbackSystemPluginsStarted(Map(orderedSystemPluginsByLevel_)); })
        .Then([this]() -> FitCode { return StartUserPlugins(); }, FIT_OK)
        .Then([this]() { CallbackUserPluginsStarted(Map(orderedUserPluginsByLevel_)); })
        .Then([] { FIT_LOG_INFO("Plugin manager is started."); })
        .Run();
}

void PluginManagerImpl::ObserveSystemPluginsStarted(PluginManager::PluginsCallbackFunc callback)
{
    systemPluginsStartedCallbacks_.push_back(callback);
}

void PluginManagerImpl::ObserveUserPluginsStarted(PluginManager::PluginsCallbackFunc callback)
{
    userPluginsStartedCallbacks_.push_back(callback);
}

void PluginManagerImpl::ObservePluginsStartBefore(PluginManager::PluginCallbackFunc callback)
{
    pluginsStartBeforeCallbacks_.push_back(callback);
}

void PluginManagerImpl::ObservePluginsStarted(PluginManager::PluginCallbackFunc callback)
{
    pluginsStartedCallbacks_.push_back(callback);
}

void PluginManagerImpl::ObservePluginsStopped(PluginManager::PluginCallbackFunc callback)
{
    pluginsStoppedCallbacks_.push_back(callback);
}

void PluginManagerImpl::CallbackPluginsStarted(const Plugin& plugin)
{
    for (auto& callback : pluginsStartedCallbacks_) {
        callback(plugin);
    }
}

void PluginManagerImpl::CallbackPluginsStartBefore(const Plugin& plugin)
{
    for (auto& callback : pluginsStartBeforeCallbacks_) {
        callback(plugin);
    }
}

void PluginManagerImpl::CallbackPluginsStopped(const Plugin& plugin)
{
    for (auto& callback : pluginsStoppedCallbacks_) {
        callback(plugin);
    }
}

void PluginManagerImpl::CallbackSystemPluginsStarted(const vector<Plugin*>& plugins)
{
    for (auto& callback : systemPluginsStartedCallbacks_) {
        callback(plugins);
    }
}

void PluginManagerImpl::CallbackUserPluginsStarted(const vector<Plugin*>& plugins)
{
    for (auto& callback : userPluginsStartedCallbacks_) {
        callback(plugins);
    }
}

vector<Plugin*> PluginManagerImpl::Map(const map<int32_t, Plugins>& plugins)
{
    vector<Plugin*> result;
    result.reserve(plugins.size());
    for (auto& levelItem : plugins) {
        for (auto& item : levelItem.second) {
            result.emplace_back(item.get());
        }
    }
    return result;
}

std::unique_ptr<PluginManager> CreatePluginManager(std::unique_ptr<LibraryLoader> loader)
{
    return make_unique<PluginManagerImpl>(move(loader));
}
}  // namespace Plugin
}  // LCOV_EXCL_LINE