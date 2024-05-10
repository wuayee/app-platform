/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */

#include "broker_fitable_discovery.h"

#include <utility>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_get_fitable_addresses/1.0.0/cplusplus/getFitableAddresses.hpp>
#include <fit/internal/framework/entity.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include "fit/fit_code.h"
#include "fit/fit_log.h"

namespace Fit {
using Framework::ServiceAddress;

BrokerFitableDiscovery::BrokerFitableDiscovery(
    Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr, unique_ptr<RegistryListenerApi> registryListenerApi)
    : fitableDiscoveryPtr_(std::move(fitableDiscoveryPtr)), registryListenerApi_(move(registryListenerApi))
{
}
Fit::Framework::Annotation::FitableDetailPtrList BrokerFitableDiscovery::GetLocalFitable(
    const Framework::Fitable &id)
{
    if (fitableDiscoveryPtr_ == nullptr) {
        FIT_LOG_ERROR("%s", "FitableDiscoveryPtr_ is null.");
        return Fit::Framework::Annotation::FitableDetailPtrList {};
    }
    return fitableDiscoveryPtr_->GetLocalFitable(id);
}
vector<Framework::ServiceAddress> BrokerFitableDiscovery::GetFitableAddresses(
    const Fit::IFitConfig& config, const Framework::Fitable& fitable)
{
    vector<Framework::ServiceAddress> result;
    if (config.IsRegistryFitable()) {
        registryListenerApi_->GetRegistryFitableAddresses(result);
        return result;
    }
    registryListenerApi_->GetFitableAddresses(fitable, result);
    return result;
}

vector<Framework::ServiceAddress> BrokerFitableDiscovery::GetFitablesAddresses(
    const Fit::IFitConfig& config, const vector<Framework::Fitable>& fitables)
{
    Fit::vector<ServiceAddress> res;
    for (const auto &fitable : fitables) {
        auto result = GetFitableAddresses(config, fitable);
        move(result.begin(), result.end(), std::back_inserter(res));
    }
    return res;
}
} // LCOV_EXCL_LINE