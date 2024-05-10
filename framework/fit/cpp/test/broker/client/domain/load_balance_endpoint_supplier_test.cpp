/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/
#include <src/broker/client/domain/load_balance_endpoint_supplier.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/formatter_service_mock.h>
#include <fit/fit_log.h>
#include <mock/broker_fitable_discovery_mock.h>

#include "gtest/gtest.h"
#include "broker_client_fit_config.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Formatter;
using namespace testing;

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

class LoadBalanceEndpointSupplierTest : public ::testing::Test {
public:
    void SetUp() override
    {
        localAddress_ = FitSystemPropertyUtils::Address();
        coordinate_ = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
        discovery_ = make_shared<BrokerFitableDiscoveryMock>();
    }

    void TearDown() override
    {
    }
public:
    fit::registry::Address localAddress_;
    std::shared_ptr<BrokerClientFitConfig> fitableConfig_;
    Fit::FitableCoordinatePtr coordinate_ {nullptr};
    std::shared_ptr<BrokerFitableDiscoveryMock> discovery_ {};
};

TEST_F(LoadBalanceEndpointSupplierTest, should_return_nullptr_when_invoke_given_discovery_return_empty)
{
    // given
    std::unique_ptr<FitableEndpointPredicate> predicate1{nullptr};
    std::unique_ptr<FitableEndpointPredicate> predicate = FitableEndpointPredicate::Combine(std::move(predicate1),
        Fit::FitableEndpointPredicate::Create([](const FitableEndpoint& endpoint) -> bool { return true; }));

    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    std::shared_ptr<BrokerClientFitConfig> fitableConfig =
        std::make_shared<BrokerClientFitConfig>(std::move(genericConfigGetter));
    EXPECT_CALL(*discovery_, GetFitableAddresses(_, _))
        .WillOnce(::testing::Return(Fit::vector<Fit::Framework::ServiceAddress>{}));

    Fit::LoadBalanceEndpointSupplier loadBalanceEndpointSupplier(
        coordinate_, fitableConfig, discovery_, std::move(predicate));
    // when
    ::Fit::FitableEndpointPtr endpoint = loadBalanceEndpointSupplier.Get();
    // then
    EXPECT_EQ(endpoint, nullptr);
}