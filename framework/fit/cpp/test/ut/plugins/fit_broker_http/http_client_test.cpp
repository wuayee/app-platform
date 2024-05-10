/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-09-20
 * Notes:       :
 */

#include <gtest/gtest.h>

#include "fit_broker_http/httplib_util.hpp"

using namespace Fit;

TEST(FitBrokerHttpClient, should_return_http_address_when_get_given_host_port)
{
    // given
    string expectAddress="http://host:1234";

    // when
    auto result = HttplibUtil::GetHttpAddress("host", 1234);

    // then
    ASSERT_EQ(result, expectAddress);
}
TEST(FitBrokerHttpClient, should_return_https_address_when_get_given_host_port)
{
    // given
    string expectAddress = "https://host:1234";

    // when
    auto result = HttplibUtil::GetHttpsAddress("host", 1234);

    // then
    ASSERT_EQ(result, expectAddress);
}