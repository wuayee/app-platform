/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-14 16:24:23
 */

#ifndef PLUGINLOADER_H
#define PLUGINLOADER_H

#include <memory>
#include <fit/fit_code.h>
#include <fit/internal/plugin/library_loader.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/runtime/runtime_element.hpp>

#include "plugin.hpp"

namespace Fit {
namespace Plugin {
class PluginManager : public RuntimeElementBase {
public:
    using PluginCallbackFunc = std::function<void(const Plugin &plugin)>;
    using PluginsCallbackFunc = std::function<void(const vector<Plugin*> &plugin)>;

    PluginManager() : RuntimeElementBase("pluginManager") {}
    ~PluginManager() override = default;
    PluginManager(const PluginManager&) = delete;
    PluginManager(PluginManager&&) = delete;
    PluginManager& operator=(const PluginManager&) = delete;
    PluginManager& operator=(PluginManager&&) = delete;

    virtual FitCode LoadFrom(const Fit::Config::SystemConfig *config) = 0;
    virtual FitCode StartSystemPlugins() = 0;
    virtual FitCode StartUserPlugins() = 0;
    virtual FitCode StopSystemPlugins() = 0;
    virtual FitCode StopUserPlugins() = 0;
    virtual void UninitAllPlugin() = 0;

    // todo rename with starting
    virtual void ObservePluginsStartBefore(PluginCallbackFunc callback) = 0;
    virtual void ObservePluginsStarted(PluginCallbackFunc callback) = 0;
    virtual void ObservePluginsStopped(PluginCallbackFunc callback) = 0;
    virtual void ObserveSystemPluginsStarted(PluginsCallbackFunc callback) = 0;
    virtual void ObserveUserPluginsStarted(PluginsCallbackFunc callback) = 0;
};

std::unique_ptr<PluginManager> CreatePluginManager(
    std::unique_ptr<LibraryLoader> loader = CreateLibraryLoader(LibraryLoader::LoadType::LOCAL));
}  // namespace Plugin
}  // namespace Fit

#endif