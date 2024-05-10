/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : implement
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#include "broker_server_starter_element.hpp"

#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/broker/broker_server.h>
#include <fit/internal/fit_system_property_utils.h>

using namespace Fit;
using Framework::FitableDiscovery;
using Framework::FitableDiscoveryPtr;
using Framework::Formatter::FormatterService;
using Framework::Formatter::FormatterServicePtr;
using Config::SystemConfig;

BrokerServerStarterElement::BrokerServerStarterElement() : RuntimeElementBase("brokerServerStarter") {}
BrokerServerStarterElement::~BrokerServerStarterElement() = default;

bool BrokerServerStarterElement::Start()
{
    auto serverAddresses = BrokerServer::Instance().StartServer(
        FormatterServicePtr(GetRuntime().GetElementIs<FormatterService>(), [](FormatterService*) {}),
        FitableDiscoveryPtr(GetRuntime().GetElementIs<FitableDiscovery>(), [](FitableDiscovery*) {}));

    for (auto &item : serverAddresses) {
        item.id = GetRuntime().GetElementIs<SystemConfig>()->GetWorkerId();
        item.environment = GetRuntime().GetElementIs<SystemConfig>()->GetEnvName();
    }
    FitSystemPropertyUtils::SetAddresses(serverAddresses);
    return true;
}

bool BrokerServerStarterElement::Stop()
{
    BrokerServer::Instance().StopServer();
    return true;
}
