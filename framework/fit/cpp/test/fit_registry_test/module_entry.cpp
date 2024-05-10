/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 模块单例管理
 * Author       : songyongtan
 * Create       : 2022-07-30
 * Notes:       :
 */

#include "module_entry.h"

namespace RegistryTest {
ModuleEntry ModuleEntry::instance_;
ModuleEntry& ModuleEntry::Instance()
{
    return instance_;
}
const Config& ModuleEntry::GetConfig() const
{
    return *config_;
}
void ModuleEntry::Start(::Fit::Plugin::PluginConfig* config)
{
    config_ = ConfigBuilder().SetPluginConfig(config).Build();
}
}  // namespace RegistryTest