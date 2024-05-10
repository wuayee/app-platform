/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 模块单例管理
 * Author       : songyongtan
 * Create       : 2022-07-30
 * Notes:       :
 */

#pragma once

#include <fit/stl/memory.hpp>
#include "config.h"
#include "fit_code.h"

namespace RegistryTest {
class ModuleEntry {
public:
    static ModuleEntry& Instance();
    const RegistryTest::Config& GetConfig() const;

protected:
    void Start(::Fit::Plugin::PluginConfig* config);
    friend FitCode Start(::Fit::Framework::PluginContext* context);

private:
    ModuleEntry() = default;
    ModuleEntry(ModuleEntry&&) = delete;
    ModuleEntry(const ModuleEntry&) = delete;
    ModuleEntry& operator=(const ModuleEntry&) = delete;
    ModuleEntry& operator=(ModuleEntry&&) = delete;

    Fit::unique_ptr<RegistryTest::Config> config_;
    static ModuleEntry instance_;
};
}