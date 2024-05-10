/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : Test
 * Author       : l00558918
 * Date         : 2021/7/20 20:41
 */
#include <src/broker/client/adapter/south/gateway/broker_fitable_discovery.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/fitable_discovery_mock.hpp>
#include <mock/formatter_service_mock.h>
#include <mock/fitable_endpoint_mock.h>
#include <fit/fit_log.h>
#include "broker_client_fit_config.h"
#include "gmock/gmock.h"
using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Formatter;
struct TestFitableStruct {
    using InType = ::Fit::Framework::ArgumentsIn<const int *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};

class TestFitable : public ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                                 TestFitableStruct::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "test_fitable_genericable_id";
    TestFitable() : ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                          TestFitableStruct::OutType)>(GENERIC_ID) {}
    ~TestFitable() {}
};

class BrokerClientFitConfigTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
public:
};

TEST_F(BrokerClientFitConfigTest, should_return_gid_when_get_gid_given_empty)
{
    // given
    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    config->SetGenericId("test_gid");
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    BrokerClientFitConfig brokerClientFitConfig(genericConfigGetter);
    // when
    Fit::string result = brokerClientFitConfig.GetGenericId();
    // then
    EXPECT_EQ(result, Fit::string("test_gid"));
}

TEST_F(BrokerClientFitConfigTest, should_return_routine_when_get_routine_given_empty)
{
    // given
    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    config->SetRoute("test_route");
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    BrokerClientFitConfig brokerClientFitConfig(genericConfigGetter);
    // when
    Fit::string result = brokerClientFitConfig.GetRoutine();
    // then
    EXPECT_EQ(result, Fit::string("test_route"));
}

TEST_F(BrokerClientFitConfigTest, should_return_empty_fitable_id_when_get_routine_given_empty)
{
    // given
    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    BrokerClientFitConfig brokerClientFitConfig(genericConfigGetter);
    // when
    Fit::string result = brokerClientFitConfig.GetFitableIdByAlias("test_alias");
    // then
    EXPECT_EQ(result.empty(), true);
}