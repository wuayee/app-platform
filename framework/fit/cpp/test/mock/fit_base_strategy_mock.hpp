/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fit registry strategy mock.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#ifndef FIT_REGISTRY_BASE_STRATEGY_MOCK_HPP
#define FIT_REGISTRY_BASE_STRATEGY_MOCK_HPP
#include <registry_server/v3/fit_registry_base/include/fit_base_strategy.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
namespace Fit {
namespace Registry {
class FitBaseStrategyMock : public FitBaseStrategy {
public:
    MOCK_METHOD0(Type, Fit::string(void));
    MOCK_METHOD1(Check, int32_t(const Fit::map<Fit::string, Fit::string>&));
};
}
}
#endif