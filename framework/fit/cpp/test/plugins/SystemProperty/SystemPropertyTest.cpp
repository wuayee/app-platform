/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : Test
 * Author       : wangpanbo
 * Create       : 2021/4/14
 */

#include <fit/internal/fit_system_property_utils.h>
#include <fit/fit_log.h>
#include <chrono>
#include <thread>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

class SystemPropertyTest : public ::testing::Test {
public:

    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
};

TEST_F(SystemPropertyTest, should_get_local_ip_when_get_given_key)
{
    // given
    Fit::string  key = "LocalHost";
    Fit::string value = "127.0.0.1";
    bool readOnly = true;
    Fit::string expectedValue = "127.0.0.1";

    // when
    auto ret = FitSystemPropertyUtils::Set(key, value, readOnly);
    Fit::string actualValue = FitSystemPropertyUtils::Get(key);
    // then
    EXPECT_EQ(actualValue, expectedValue);
}

TEST_F(SystemPropertyTest, should_get_local_addresses_when_get_given_empty)
{
    // given
    Fit::vector<fit::registry::Address> preAddresses = {FitSystemPropertyUtils::Addresses()};
    Fit::vector<fit::registry::Address> addresses;
    fit::registry::Address address;
    address.host = "127.0.0.1";
    address.port = 8080;
    address.id = "127.0.0.18080";
    addresses.push_back(address);
    Fit::vector<fit::registry::Address> expectedAddresses;
    expectedAddresses.push_back(address);

    // when
    auto ret = FitSystemPropertyUtils::SetAddresses(addresses);
    auto actualAddresses = FitSystemPropertyUtils::Addresses();
    // then
    EXPECT_EQ(ret, true);
    EXPECT_EQ(actualAddresses[0].host, expectedAddresses[0].host);
    EXPECT_EQ(actualAddresses[0].port, expectedAddresses[0].port);
    EXPECT_EQ(actualAddresses[0].id, expectedAddresses[0].id);
    FitSystemPropertyUtils::SetAddresses(preAddresses);
}

TEST_F(SystemPropertyTest, should_get_registry_addresses_when_get_given_empty)
{
    // given
    fit::registry::Address address;
    address.host = "127.0.0.1";
    address.port = 8888;
    address.id = "127.0.0.18888";
    fit::registry::Address expectedAddress;
    expectedAddress.host = "127.0.0.1";
    expectedAddress.port = 8888;
    expectedAddress.id = "127.0.0.18888";

    // when
    auto ret = FitSystemPropertyUtils::SetRegistryMatchedAddress(address);
    auto actualAddress = FitSystemPropertyUtils::GetRegistryMatchedAddress();
    // then
    EXPECT_EQ(ret, true);
    EXPECT_EQ(actualAddress.host, expectedAddress.host);
    EXPECT_EQ(actualAddress.port, expectedAddress.port);
    EXPECT_EQ(actualAddress.id, expectedAddress.id);
}