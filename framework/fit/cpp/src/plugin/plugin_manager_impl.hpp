/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-14 16:34:34
 */

#ifndef PLUGINLOADERIMPL_HPP
#define PLUGINLOADERIMPL_HPP

#include <fit/internal/plugin/library_loader.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>
#include <fit/internal/plugin/plugin_struct.hpp>
#include <fit/internal/plugin/plugin.hpp>
#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/list.hpp>

#include <memory>

namespace Fit {
namespace Plugin {
class PluginManagerImpl : public PluginManager {
public:
    using Plugins = Fit::vector<PluginPtr>;
    explicit PluginManagerImpl(std::unique_ptr<LibraryLoader> libraryLoader);

    ~PluginManagerImpl() override;

    bool Start() override;
    bool Stop() override;

    FitCode LoadFrom(const Fit::Config::SystemConfig *config) override;
    void UninitAllPlugin() override;
    FitCode StartSystemPlugins() override;
    FitCode StartUserPlugins() override;
    FitCode StopSystemPlugins() override;

    FitCode StopUserPlugins() override;

    void ObservePluginsStartBefore(PluginCallbackFunc callback) override;

    void ObservePluginsStarted(PluginCallbackFunc callback) override;

    void ObservePluginsStopped(PluginCallbackFunc callback) override;

    void ObserveSystemPluginsStarted(PluginsCallbackFunc callback) override;
    void ObserveUserPluginsStarted(PluginsCallbackFunc callback) override;

protected:
    void CallbackPluginsStarted(const Plugin &plugin);
    void CallbackPluginsStartBefore(const Plugin &plugin);

    void CallbackPluginsStopped(const Plugin &plugin);

    void CallbackSystemPluginsStarted(const vector<Plugin*>& plugins);
    void CallbackUserPluginsStarted(const vector<Plugin*>& plugins);

    static FitCode InstallPlugins(const Plugins &plugins);
    static FitCode ResolvePlugins(const Plugins &plugins);
    FitCode StartPlugins(const Plugins &plugins);

    static vector<Plugin*> Map(const Fit::map<int32_t, Plugins> &plugins);

private:
    static void UninstallPlugins(const Fit::map<int32_t, Plugins> &plugins);

    FitCode LoadPlugin(const Fit::string &workerDirectory, const ValidLevelRange &levelRange,
        Config::Value &pluginsValue, Fit::map<int32_t, Plugins> &resultPlugins);
    FitCode StartWithLevel(const Fit::map<int32_t, Plugins> &plugins);
    FitCode StopWithLevel(const Fit::map<int32_t, Plugins> &plugins);
    static bool GetWorkDirectory(const Config::SystemConfig *config, string &workerDirectoryRealPath) ;
private:
    std::unique_ptr<LibraryLoader> loader_;
    const Fit::Config::SystemConfig *systemConfig_ {};
    // 从小到大启动插件，当前map遍历时从小到大
    Fit::map<int32_t, Plugins> orderedSystemPluginsByLevel_;
    Fit::map<int32_t, Plugins> orderedUserPluginsByLevel_;

    Fit::list<PluginCallbackFunc> pluginsStartBeforeCallbacks_;
    Fit::list<PluginCallbackFunc> pluginsStartedCallbacks_;
    Fit::list<PluginCallbackFunc> pluginsStoppedCallbacks_;
    Fit::list<PluginsCallbackFunc> systemPluginsStartedCallbacks_;
    Fit::list<PluginsCallbackFunc> userPluginsStartedCallbacks_;
};
}  // namespace Plugin
}  // namespace Fit

#endif