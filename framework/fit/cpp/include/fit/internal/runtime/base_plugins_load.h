/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/06/01
 * Notes:       :
 */

#ifndef BASE_PLUGINS_LOAD_H
#define BASE_PLUGINS_LOAD_H

#include <dlfcn.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/internal/runtime/config/system_config.hpp>

class BasePluginsLoad {
public:
    FitCode LoadBasePlugins(const Fit::Config::SystemConfig *config);

private:
    FitCode LoadPlugin(const Fit::string& pluginPath, int mode);
};
#endif