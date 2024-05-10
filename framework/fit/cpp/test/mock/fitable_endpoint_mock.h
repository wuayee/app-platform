/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2022/2/23
 * Notes:       :
 */

#ifndef FITABLE_ENDPOINT_MOCK_H
#define FITABLE_ENDPOINT_MOCK_H
#include <src/broker/client/domain/fitable_endpoint.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
class FitableEndpointMock : public Fit::FitableEndpoint {
public:
    MOCK_CONST_METHOD0(GetWorkerId, const ::Fit::string&());
    MOCK_CONST_METHOD0(GetEnvironment, const ::Fit::string&());
    MOCK_CONST_METHOD0(GetHost, const ::Fit::string&());
    MOCK_CONST_METHOD0(GetPort, uint16_t());
    MOCK_CONST_METHOD0(GetProtocol, int32_t());
    MOCK_CONST_METHOD0(GetFormats, const ::Fit::vector<int32_t>&());
    MOCK_CONST_METHOD0(IsLocal, bool());
    MOCK_CONST_METHOD0(GetContext, const Context&());
};

#endif