/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : start broker client
 * Author       : songyongtan
 * Date         : 2022/5/19
 * Notes:       :
 */


#include "broker_client_starter_element.hpp"

#include <fit/fit_log.h>
#include <fit/internal/broker/broker_client_inner.h>
#include <fit/external/util/string_utils.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_request_response_v5/1.0.0/cplusplus/requestResponseV5.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include "runtime/config/configuration_service.h"

using namespace Fit;
using Framework::FitableDiscovery;
using Framework::FitableDiscoveryPtr;
using Plugin::PluginManager;

BrokerClientStarterElement::BrokerClientStarterElement() : RuntimeElementBase("brokerClientStarter") {}

BrokerClientStarterElement::~BrokerClientStarterElement() = default;

bool BrokerClientStarterElement::Start()
{
    auto pluginManager = GetRuntime().GetElementIs<PluginManager>();
    if (pluginManager == nullptr) {
        FIT_LOG_ERROR("Need plugin manager.");
        return false;
    }
    pluginManager->ObserveSystemPluginsStarted([this](const vector<Plugin::Plugin*>&) {
        LoadSupportedTransportClient();
    });

    return true;
}

bool BrokerClientStarterElement::Stop()
{
    return true;
}

FitCode BrokerClientStarterElement::LoadSupportedTransportClient()
{
    // 采集本地支持的通信client接口支持的协议
    auto configurationService = GetRuntime().GetElementIs<Configuration::ConfigurationService>();
    auto fitableDiscovery = GetRuntime().GetElementIs<Framework::FitableDiscovery>();
    auto requestFitables = configurationService->GetFitables(
        ::fit::hakuna::kernel::broker::client::requestResponseV5::GENERIC_ID);
    Fit::vector<Fit::string> supportProtocols;
    const Fit::string protocolHeader = "protocol=";
    for (const auto &fitableConfig : requestFitables) {
        Framework::Fitable queryFitable;
        queryFitable.genericId = ::fit::hakuna::kernel::broker::client::requestResponseV5::GENERIC_ID;
        queryFitable.genericVersion = "1.0.0";
        queryFitable.fitableId = fitableConfig.fitableId;
        if (fitableDiscovery->GetLocalFitable(queryFitable).empty()) {
            continue;
        }

        for (const auto &aliases : fitableConfig.aliases) {
            if (aliases.find(protocolHeader) == 0) {
                auto protocol = aliases.substr(protocolHeader.length());
                supportProtocols.push_back(protocol);
                GetLocalProtocol().push_back(Fit::stoi(protocol));
            }
        }
    }
    auto value = Fit::StringUtils::Join(",", supportProtocols);
    FIT_LOG_INFO("Support broker client protocols = %s", value.c_str());
    FitSystemPropertyUtils::Set(FitSystemPropertyKey::LOCAL_PROTOCOL_KEY, value, false);
    return FIT_OK;
}
