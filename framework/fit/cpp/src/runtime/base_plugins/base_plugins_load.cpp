/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/06/01
 * Notes:       :
 */

#include <fit/internal/runtime/base_plugins_load.h>
#include <fit/fit_log.h>

namespace {
const int LOAD_PLUGIN_MODE = RTLD_NOW | RTLD_GLOBAL;
constexpr const char* COMPONENTS_KEY = "components";
}

FitCode BasePluginsLoad::LoadPlugin(const Fit::string& pluginPath, int mode)
{
    auto *handle = dlopen(pluginPath.c_str(), mode);
    if (handle == nullptr) {
        FIT_LOG_ERROR("Failed to load library %s, %s", pluginPath.c_str(), dlerror());
        return FIT_ERR_FAIL;
    }
    FIT_LOG_DEBUG("Load component successfully. path=%s.", pluginPath.c_str());
    return FIT_ERR_SUCCESS;
}

FitCode BasePluginsLoad::LoadBasePlugins(const Fit::Config::SystemConfig *config)
{
    auto &componentsValue = config->GetValue(COMPONENTS_KEY);

    for (int32_t i = 0; componentsValue.IsArray() && i < componentsValue.Size(); ++i) {
        auto& component = componentsValue[i];
        if (!component.IsObject()) {
            FIT_LOG_ERROR("Wrong component config. index=%d.", i);
            return FIT_ERR_PARAM;
        }
        auto ret = LoadPlugin(component["location"].AsString(""), LOAD_PLUGIN_MODE);
        if (ret != FIT_OK) {
            return ret;
        }
    }

    return FIT_OK;
}
