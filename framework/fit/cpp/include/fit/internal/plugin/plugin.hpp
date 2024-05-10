/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/9
 * Notes:       :
 */

#ifndef PLUGIN_HPP
#define PLUGIN_HPP

#include <cstdint>
#include <fit/fit_code.h>
#include <fit/external/framework/annotation/fitable_detail.hpp>

namespace Fit {
namespace Plugin {
struct PluginArchive {
    Fit::string name;
    Fit::string location;
    int32_t startLevel;
    Fit::string configFilePath;
    Fit::string genericablesConfigFilePath;
};

class Plugin {
public:
    Plugin() = default;
    virtual ~Plugin() = default;

    virtual FitCode Install() = 0;
    virtual FitCode Resolve() = 0;
    virtual FitCode Start() = 0;
    virtual FitCode Stop() = 0;
    virtual FitCode Uninstall() = 0;
    virtual FitCode GetStartLevel() const noexcept = 0;
    virtual const char *GetLocation() const noexcept = 0;
    virtual const ::Fit::Framework::Annotation::FitableDetailPtrList &GetFitables() const noexcept = 0;
    virtual const PluginArchive &GetPluginArchive() const noexcept = 0;
};

using PluginPtr = std::unique_ptr<Plugin>;

struct ValidLevelRange {
    int32_t begin;
    int32_t end;
    int32_t defaultValue;

    bool IsValid(int32_t value) const
    {
        return value >= begin && value <= end;
    }
};
}
}
#endif // PLUGIN_HPP
