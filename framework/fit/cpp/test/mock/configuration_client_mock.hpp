/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/25
 * Notes:       :
 */

#ifndef CONFIGURATION_SERVICE_MOCK_HPP
#define CONFIGURATION_SERVICE_MOCK_HPP
#include <runtime/config/configuration_client.h>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace Fit::Configuration;
using namespace ::testing;

class ConfigurationClientMock : public ConfigurationClient {
public:
    MOCK_METHOD2(Download, int32_t(const Fit::string &, ItemValueSet &));
    MOCK_METHOD2(Get, int32_t(const Fit::string &, Fit::string &));
    MOCK_CONST_METHOD1(IsSubscribed, bool(const Fit::string &));
    MOCK_METHOD2(Subscribe, int32_t(const Fit::string &, ConfigurationClient::ConfigSubscribePathCallback));
    MOCK_METHOD2(Subscribe, int32_t(const Fit::string &, ConfigurationClient::ConfigSubscribeNodeCallback));
};
#endif // CONFIGURATION_SERVICE_MOCK_HPP
