/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */
#ifndef BROKER_FITABLE_DISCOVERY_H
#define BROKER_FITABLE_DISCOVERY_H

#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <unordered_map>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/framework/entity.hpp>

#include "broker/client/application/gateway/fit_discovery.h"
#include "registry_listener_api.h"

namespace Fit {
class BrokerFitableDiscovery : public IBrokerFitableDiscovery {
public:
    explicit BrokerFitableDiscovery(
        Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr, unique_ptr<RegistryListenerApi> registryListenerApi);
    ~BrokerFitableDiscovery() override = default;
    Fit::Framework::Annotation::FitableDetailPtrList GetLocalFitable(const Framework::Fitable &fitable) override;
    Fit::vector<Framework::ServiceAddress> GetFitableAddresses(
        const Fit::IFitConfig& config, const Framework::Fitable& fitable) override;
    Fit::vector<Framework::ServiceAddress> GetFitablesAddresses(
        const Fit::IFitConfig& config, const vector<Framework::Fitable>& fitables) override;

private:
    Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr_ {};
    unique_ptr<RegistryListenerApi> registryListenerApi_ {};
};
}
#endif