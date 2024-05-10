/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : null
 * Date         : 2021/1/31 13:33
 * Notes:       :
 */

#include <fit/internal/broker/broker_client_inner.h>
#include <fit/internal/runtime/runtime.hpp>
#include "broker_client_impl.h"
#include "broker_fitable_discovery.h"
#include "broker_client_fit_config.h"

namespace {
    void SetBrokerClient(Fit::BrokerClientPtr brokerClientPtr)
    {
        Fit::GetBrokerClient() = brokerClientPtr;
    }
}

namespace Fit {
using Framework::ParamJsonFormatter::ParamJsonFormatterService;
using Framework::ParamJsonFormatter::ParamJsonFormatterPtr;
using Framework::Formatter::FormatterService;
using Framework::FitableDiscovery;

unique_ptr<IBrokerClient> CreateBrokerClientInner(Runtime* runtime)
{
    if (Fit::GetBrokerClient() != nullptr) {
        return nullptr;
    }
    auto target = Fit::make_unique<BrokerClient>(runtime);
    auto sharedTarget = BrokerClientPtr(target.get(), [](IBrokerClient*) {});

    SetBrokerClient(sharedTarget);

    return std::move(target);
}

Fit::BrokerClientPtr& GetBrokerClient()
{
    static Fit::BrokerClientPtr g_brokerClientPtr = nullptr;
    return g_brokerClientPtr;
}

Configuration::ConfigurationServicePtr &GetConfigServiceInstance()
{
    static Configuration::ConfigurationServicePtr g_configRepo {nullptr};
    return g_configRepo;
}

Framework::FitableDiscoveryPtr &GetDiscoveryInstance()
{
    static Framework::FitableDiscoveryPtr g_discoveryPtr {nullptr};
    return g_discoveryPtr;
}

Fit::vector<uint8_t> &GetLocalProtocol()
{
    static Fit::vector<uint8_t> localProtocol;
    return localProtocol;
}

void InitBrokerInstance(
    const Framework::FitableDiscoveryPtr& fitableDiscoveryPtr,
    const Configuration::ConfigurationServicePtr &configServicePtr)
{
    GetConfigServiceInstance() = configServicePtr;
    GetDiscoveryInstance() = fitableDiscoveryPtr;
}

unique_ptr<IBrokerClient> CreateBrokerClient(Runtime* runtime)
{
    auto sharedConfService = Configuration::ConfigurationServicePtr(
        runtime->GetElementIs<Configuration::ConfigurationService>(),
        [](Configuration::ConfigurationService* p) {});
    Fit::InitBrokerInstance(
        Framework::FitableDiscoveryPtr(runtime->GetElementIs<FitableDiscovery>(), [](Framework::FitableDiscovery*) {}),
        sharedConfService);
    return CreateBrokerClientInner(runtime);
}
} // LCOV_EXCL_LINE
