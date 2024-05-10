/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-14 16:58:28
 */

#ifndef PLUGIN_STRUCT_HPP
#define PLUGIN_STRUCT_HPP

#include <fit/external/framework/plugin_activator.hpp>
#include <fit/external/plugin/plugin_context.hpp>
#include <fit/stl/string.hpp>

#include <utility>

namespace Fit {
namespace Plugin {
struct LibraryInfo {
    explicit LibraryInfo() = default;
    explicit LibraryInfo(string _name, string _path, void *_handle)
        : name(std::move(_name)), path(std::move(_path)), handle(_handle)
    {
    }
    Fit::string name{};
    Fit::string path{};
    void *handle{};
};

struct PluginInfo {
    explicit PluginInfo() = default;
    explicit PluginInfo(LibraryInfo lib) : libraryInfo{std::move(lib)} {}
    LibraryInfo libraryInfo{};
    PluginContextPtr pluginContext{};
    Framework::PluginActivatorPtr pluginActivator{};
};
}  // namespace Plugin
}  // namespace Fit

#endif