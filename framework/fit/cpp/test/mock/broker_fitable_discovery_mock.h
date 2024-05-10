/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 * Description  : broker_test
 * Author       : w00561424
 * Date         : 2022/2/23
 */

#ifndef BROKER_FITABLE_DISCOVERY_MOCK_H
#define BROKER_FITABLE_DISCOVERY_MOCK_H

#include <src/broker/client/application/gateway/fit_discovery.h>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
#include "framework/entity.hpp"

class BrokerFitableDiscoveryMock : public Fit::IBrokerFitableDiscovery {
public:
    MOCK_METHOD1(GetLocalFitable, Fit::Framework::Annotation::FitableDetailPtrList(const Fit::Framework::Fitable &));
    MOCK_METHOD2(GetFitableAddresses,
        Fit::vector<Fit::Framework::ServiceAddress>(const Fit::IFitConfig& config, const Fit::Framework::Fitable&));
    MOCK_METHOD2(GetFitablesAddresses, Fit::vector<Fit::Framework::ServiceAddress>(const Fit::IFitConfig& config,
                                           const Fit::vector<Fit::Framework::Fitable>&));
};
#endif // BROKER_FITABLE_DISCOVERY_MOCK_H
