/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */
#ifndef IBROKER_CLIENT_INNER_H
#define IBROKER_CLIENT_INNER_H

#include <fit/stl/any.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/framework/param_json_formatter_service.hpp>
#include "broker_client.h"
#include "runtime/config/configuration_service.h"

namespace Fit {
unique_ptr<IBrokerClient> CreateBrokerClient(Runtime* runtime);

Fit::BrokerClientPtr& GetBrokerClient();

// todo 实例暂时放在此
Configuration::ConfigurationServicePtr &GetConfigServiceInstance();
Framework::FitableDiscoveryPtr &GetDiscoveryInstance();
Fit::vector<uint8_t> &GetLocalProtocol();

void InitBrokerInstance(
    const Framework::FitableDiscoveryPtr& fitableDiscoveryPtr,
    const Configuration::ConfigurationServicePtr &configServicePtr);

}
#endif