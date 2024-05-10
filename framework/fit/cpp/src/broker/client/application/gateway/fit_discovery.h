/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */
#ifndef IBROKER_FITABLE_DISCOVERY_H
#define IBROKER_FITABLE_DISCOVERY_H

#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/internal/framework/entity.hpp>

#include "broker/client/application/gateway/fit_config.h"

namespace Fit {
class IBrokerFitableDiscovery {
public:
    virtual ~IBrokerFitableDiscovery() = default;
    virtual Fit::Framework::Annotation::FitableDetailPtrList GetLocalFitable(const Framework::Fitable &id) = 0;
    virtual Fit::vector<Framework::ServiceAddress> GetFitableAddresses(
        const Fit::IFitConfig& config, const Framework::Fitable& id) = 0;
    virtual Fit::vector<Framework::ServiceAddress> GetFitablesAddresses(const Fit::IFitConfig& config,
        const vector<Framework::Fitable>& ids) = 0;
};
using BrokerFitableDiscoveryPtr = std::shared_ptr<IBrokerFitableDiscovery>;
}
#endif