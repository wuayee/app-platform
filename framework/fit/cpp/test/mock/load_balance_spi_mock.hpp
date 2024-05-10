/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2022/3/31
 * Notes:       :
 */

#ifndef CONFIG_VALUE_MOCK_HPP
#define CONFIG_VALUE_MOCK_HPP

#include <gmock/gmock.h>
#include <loadbalance/include/load_balance_spi.hpp>

class LoadBalanceSpiMock : public Fit::LoadBalance::LoadBalanceSpi {
public:
    MOCK_CONST_METHOD0(GetWorkerId, ::Fit::string&());
    MOCK_CONST_METHOD0(GetEnvironment, ::Fit::string&());
    MOCK_CONST_METHOD0(GetProtocols, ::Fit::vector<int32_t>&());
    MOCK_CONST_METHOD0(GetEnvironmentChain, ::Fit::vector<::Fit::string>&());
};
#endif // CONFIG_VALUE_MOCK_HPP
