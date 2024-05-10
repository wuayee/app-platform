/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 测试运行的配置信息
 * Author       : songyongtan
 * Create       : 2022-07-15
 */

#include "config.h"

#include <fit/fit_log.h>
#include <genericable/com_huawei_fit_sdk_system_get_local_addresses/1.0.0/cplusplus/getLocalAddresses.hpp>

using namespace Fit;

namespace RegistryTest {
Config::Config(vector<string> targetWorkerIdsOfRegistry, uint32_t syncDelaySeconds, vector<string> gtestArgs,
    fit::registry::Address localAddress)
    : targetWorkerIdsOfRegistry_(std::move(targetWorkerIdsOfRegistry)), syncDelaySeconds_(syncDelaySeconds),
      gtestArgs_(std::move(gtestArgs)), localAddress_(std::move(localAddress))
{
}
Config::~Config() = default;
const vector<string>& Config::GetTargetWorkerIdsOfRegistry() const
{
    return targetWorkerIdsOfRegistry_;
}
uint32_t Config::GetSyncDelaySeconds() const
{
    return syncDelaySeconds_;
}
const vector<string>& Config::GetGtestArgs() const
{
    return gtestArgs_;
}
const fit::registry::Address& Config::GetLocalAddress() const
{
    return localAddress_;
}

ConfigBuilder& ConfigBuilder::SetPluginConfig(Plugin::PluginConfig* config)
{
    config_ = config;
    return *this;
}
Fit::unique_ptr<Config> ConfigBuilder::Build()
{
    auto& args = config_->Get("registry-test.gtest-args");
    vector<string> gtestArgValues;
    // 兼容gtest 入参格式，第一个参数为进程，实际不消费，只占位
    gtestArgValues.push_back("xxx");
    for (int32_t i = 0; i < args.Size(); ++i) {
        gtestArgValues.push_back(args[i].AsString());
    }
    auto& nodes = config_->Get("registry-test.test-nodes");
    vector<string> nodeValues;
    for (int32_t i = 0; i < nodes.Size(); ++i) {
        nodeValues.push_back(nodes[i].AsString());
    }
    constexpr int32_t defaultSyncDelaySeconds = 2;
    auto delay = config_->Get("registry-test.sync-delay").AsInt(defaultSyncDelaySeconds);

    ::fit::sdk::system::getLocalAddresses getLocalAddresses;
    Fit::vector<fit::registry::Address>* value;
    auto ret = getLocalAddresses(&value);
    if (ret != FIT_ERR_SUCCESS || value == nullptr || value->empty()) {
        FIT_LOG_ERROR("%s", "Failed to get local addresses.");
    }

    return make_unique<Config>(nodeValues, delay, gtestArgValues, (*value)[0]);
}
}  // namespace RegistryTest