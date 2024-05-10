/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/9
 * Notes:       :
 */

#ifndef PLUGIN_LIBRARY_HPP
#define PLUGIN_LIBRARY_HPP

#include <fit/external/plugin/plugin_context.hpp>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/internal/plugin/library_loader.hpp>
#include "plugin.hpp"

namespace Fit {
namespace Plugin {
class PluginLibrary : public Plugin {
public:
    using PluginContextCreateFunc = std::function<PluginContextPtr(const PluginArchive &)>;
    PluginLibrary(PluginArchive pluginArchive, PluginContextCreateFunc pluginContextCreator,
        LibraryLoader *libraryLoader);
    ~PluginLibrary() override;

    FitCode Install() override;
    FitCode Resolve() override;
    FitCode Start() override;
    FitCode Stop() override;
    FitCode Uninstall() override;
    FitCode GetStartLevel() const noexcept override;
    const char *GetLocation() const noexcept override;
    const ::Fit::Framework::Annotation::FitableDetailPtrList &GetFitables() const noexcept override;
    const PluginArchive &GetPluginArchive() const noexcept override;

protected:
    void TrySetPluginConfigWithLibraryPath();
    void TrySetGenericableConfigWithLibraryPath();

private:
    PluginArchive pluginArchive_;
    PluginContextPtr pluginContext_;
    PluginContextCreateFunc pluginContextCreator_;
    LibraryLoader *libraryLoader_ {};
    LibraryInfo libraryInfo_;
    ::Fit::Framework::PluginActivatorPtrList pluginActivator_;
    ::Fit::Framework::Annotation::FitableDetailPtrList fitables_;
};
}
}
#endif // PLUGIN_LIBRARY_HPP
