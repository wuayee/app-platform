/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/9
 * Notes:       :
 */

#include <fit/fit_log.h>
#include <fit/internal/plugin/plugin_library.hpp>
#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>
#include <fit/internal/framework/plugin_activator_collector_inner.hpp>
#include <fit/internal/fit_scope_guard.h>
#include <fit/external/util/string_utils.hpp>
#include <fit/internal/fit_filesystem_util.hpp>

using ::Fit::Framework::Annotation::FitableDetailFlowTo;
using ::Fit::Framework::Annotation::FitableDetailReceiver;
using ::Fit::Framework::Annotation::FitableDetailPtrList;
using ::Fit::Framework::PluginActivatorFlowTo;
using ::Fit::Framework::PluginActivatorReceiver;
using ::Fit::Framework::PluginActivatorPtrList;

namespace Fit {
namespace Plugin {
PluginLibrary::PluginLibrary(PluginArchive pluginArchive,
    PluginContextCreateFunc pluginContextCreator,
    LibraryLoader *libraryLoader)
    : pluginArchive_(std::move(pluginArchive)), pluginContextCreator_(std::move(pluginContextCreator)),
      libraryLoader_(libraryLoader) {}

PluginLibrary::~PluginLibrary() {}

FitCode PluginLibrary::Install()
{
    if (libraryLoader_ == nullptr) {
        FIT_LOG_ERROR("Null library loader.");
        return FIT_ERR_PARAM;
    }
    FitableDetailReceiver fitableReceiver {
        [this](const FitableDetailPtrList &fitables) {
            fitables_.insert(fitables_.end(), fitables.begin(), fitables.end());
        },
        [](const FitableDetailPtrList &) {}
    };
    auto oldFitableReceiver = FitableDetailFlowTo(&fitableReceiver);
    PluginActivatorReceiver activatorReceiver {
        [this](const PluginActivatorPtrList &activators) {
            pluginActivator_.insert(pluginActivator_.end(), activators.begin(), activators.end());
        },
        [](const PluginActivatorPtrList &) {}
    };
    auto oldActivatorReceiver = PluginActivatorFlowTo(&activatorReceiver);
    auto exitGuard = [oldFitableReceiver, oldActivatorReceiver]() {
        FitableDetailFlowTo(oldFitableReceiver);
        PluginActivatorFlowTo(oldActivatorReceiver);
    };
    FIT_ON_SCOPE_EXIT(exitGuard);
    auto loadRet = libraryLoader_->Load(pluginArchive_.location, libraryInfo_);
    if (loadRet != FIT_OK) {
        FIT_LOG_ERROR("Failed to load library, location:%s.", pluginArchive_.location.c_str());
        return loadRet;
    }
    FIT_LOG_DEBUG("Plugin(%s) successfully installed.", pluginArchive_.location.c_str());

    return FIT_OK;
}

FitCode PluginLibrary::Resolve()
{
    if (!pluginContextCreator_) {
        FIT_LOG_ERROR("Failed to resolve plugin(%s), null context creator.", pluginArchive_.location.c_str());
        return FIT_ERR_PARAM;
    }

    TrySetPluginConfigWithLibraryPath();
    TrySetGenericableConfigWithLibraryPath();

    pluginContext_ = pluginContextCreator_(pluginArchive_);
    if (!pluginContext_) {
        FIT_LOG_ERROR("Failed to resolve plugin(%s).", pluginArchive_.location.c_str());
        return FIT_ERR_PARAM;
    }
    FIT_LOG_DEBUG("Plugin(%s) successfully resolved.", libraryInfo_.path.c_str());

    return FIT_OK;
}

void PluginLibrary::TrySetGenericableConfigWithLibraryPath()
{
    if (pluginArchive_.genericablesConfigFilePath.empty()) {
        auto genericablesConfigFile = StringUtils::Replace(libraryInfo_.path, ".so",
            ".genericables.json");
        if (Util::Filesystem::FileExists(genericablesConfigFile)) {
            FIT_LOG_DEBUG("Find plugin genericables config file(%s).", genericablesConfigFile.c_str());
            pluginArchive_.genericablesConfigFilePath = genericablesConfigFile;
        }
    }
}

void PluginLibrary::TrySetPluginConfigWithLibraryPath()
{
    if (pluginArchive_.configFilePath.empty()) {
        auto configFilePath = StringUtils::Replace(libraryInfo_.path, ".so", ".json");
        if (Util::Filesystem::FileExists(configFilePath)) {
            FIT_LOG_DEBUG("Find plugin config file(%s).", configFilePath.c_str());
            pluginArchive_.configFilePath = configFilePath;
        }
    }
}

FitCode PluginLibrary::Start()
{
    if (!pluginContext_) {
        FIT_LOG_ERROR("Failed to start plugin(%s), null plugin context.", pluginArchive_.location.c_str());
        return FIT_ERR_PARAM;
    }

    FitableDetailReceiver fitableReceiver {
        [this](const FitableDetailPtrList &fitables) {
            fitables_.insert(fitables_.end(), fitables.begin(), fitables.end());
        },
        [](const FitableDetailPtrList &) {}
    };
    auto oldFitableReceiver = FitableDetailFlowTo(&fitableReceiver);

    auto exitGuard = [oldFitableReceiver]() {
        FitableDetailFlowTo(oldFitableReceiver);
    };
    FIT_ON_SCOPE_EXIT(exitGuard);

    for (auto &activator : pluginActivator_) {
        if (!activator || !activator->GetStart()) {
            FIT_LOG_INFO("Plugin(%s) ignore null activator.", pluginArchive_.location.c_str());
            continue;
        }
        auto ret = activator->GetStart()(pluginContext_.get());
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to start plugin(%s).", pluginArchive_.location.c_str());
            return ret;
        }
    }
    FIT_LOG_INFO("Plugin(%s) successfully started.", pluginArchive_.location.c_str());

    return FIT_OK;
}

FitCode PluginLibrary::Stop()
{
    for (auto &activator : pluginActivator_) {
        if (!activator || !activator->GetStop()) {
            FIT_LOG_INFO("Plugin(%s) ignore null activator.", pluginArchive_.location.c_str());
            continue;
        }
        auto ret = activator->GetStop()();
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to stop plugin(%s).", pluginArchive_.location.c_str());
            return ret;
        }
    }
    FIT_LOG_INFO("Plugin(%s) successfully stopped.", pluginArchive_.location.c_str());

    return FIT_OK;
}

FitCode PluginLibrary::Uninstall()
{
    fitables_.clear();
    pluginActivator_.clear();
    if (libraryLoader_ == nullptr) {
        FIT_LOG_ERROR("Null library loader.");
        return FIT_ERR_PARAM;
    }

    if (libraryInfo_.handle == nullptr) {
        return FIT_OK;
    }

    auto unloadRet = libraryLoader_->Unload(libraryInfo_);
    if (unloadRet != FIT_OK) {
        FIT_LOG_ERROR("Failed to unload library. [ret=%d, location=%s]", unloadRet,
            pluginArchive_.location.c_str());
        return unloadRet;
    }
    FIT_LOG_INFO("Plugin(%s) successfully uninstalled.", pluginArchive_.location.c_str());

    return FIT_OK;
}

FitCode PluginLibrary::GetStartLevel() const noexcept
{
    return pluginArchive_.startLevel;
}

const char *PluginLibrary::GetLocation() const noexcept
{
    return pluginArchive_.location.c_str();
}

const FitableDetailPtrList &PluginLibrary::GetFitables() const noexcept
{
    return fitables_;
}

const PluginArchive &PluginLibrary::GetPluginArchive() const noexcept
{
    return pluginArchive_;
}
}
} // LCOV_EXCL_LINE