/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 测试运行的配置信息
 * Author       : songyongtan
 * Create       : 2022-07-12
 */

#pragma once

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/memory.hpp>
#include "external/plugin/plugin_config.hpp"

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/external/framework/plugin_activator.hpp>

namespace RegistryTest {
class Config {
public:
    Config();
    Config(Fit::vector<Fit::string> targetWorkerIdsOfRegistry, uint32_t syncDelaySeconds,
        Fit::vector<Fit::string> gtestArgs, fit::registry::Address localAddress);
    ~Config();
    const Fit::vector<Fit::string>& GetTargetWorkerIdsOfRegistry() const;
    uint32_t GetSyncDelaySeconds() const;
    const Fit::vector<Fit::string>& GetGtestArgs() const;
    const fit::registry::Address& GetLocalAddress() const;

private:
    Fit::vector<Fit::string> targetWorkerIdsOfRegistry_{};
    uint32_t syncDelaySeconds_{};
    Fit::vector<Fit::string> gtestArgs_{};
    fit::registry::Address localAddress_{};
};

class ConfigBuilder {
public:
    ConfigBuilder& SetPluginConfig(::Fit::Plugin::PluginConfig* config);
    Fit::unique_ptr<Config> Build();

private:
    ::Fit::Plugin::PluginConfig* config_;
};
}  // namespace RegistryTest
