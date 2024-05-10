/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides mock for registry listener SPIs.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/23
 */

#ifndef FIT_REGISTRY_LISTENER_MOCK_REGISTRY_LISTENER_SPI_HPP
#define FIT_REGISTRY_LISTENER_MOCK_REGISTRY_LISTENER_SPI_HPP
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
using namespace Fit;
using namespace Fit::Registry::Listener;
class MockRegistryListenerSpi : public RegistryListenerSpi {
public:
    MOCK_CONST_METHOD0(GetWorkerId, const ::Fit::string&());
    MOCK_CONST_METHOD1(QueryFitableInstances, FitableInstanceListGuard(const ::Fit::vector<FitableInfo>&));
    MOCK_METHOD1(SubscribeFitables, FitableInstanceListGuard(const ::Fit::vector<FitableInfo>&));
    MOCK_METHOD1(UnsubscribeFitables, FitCode(const ::Fit::vector<FitableInfo>&));
    MOCK_METHOD1(SubscribeFitablesChanged, void(FitablesChangedCallbackPtr));
    MOCK_METHOD1(UnsubscribeFitablesChanged, void(FitablesChangedCallbackPtr));
};
#endif // FIT_REGISTRY_LISTENER_MOCK_REGISTRY_LISTENER_SPI_HPP
