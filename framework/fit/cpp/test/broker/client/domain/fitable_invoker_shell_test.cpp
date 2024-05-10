/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/
#include <src/broker/client/domain/fitable_invoker_shell.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/framework/proxy_client.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <mock/broker_fitable_discovery_mock.h>
#include <mock/formatter_service_mock.h>
#include <mock/fitable_endpoint_mock.h>
#include <mock/broker_genericable_config_mock.hpp>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
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

class FitableInvokerShellTest : public ::testing::Test {
public:
    void SetUp() override
    {
        localAddress_ = FitSystemPropertyUtils::Address();

        fitableDiscoveryPtr_ = std::make_shared<BrokerFitableDiscoveryMock>();
        formatterService_ = std::make_shared<FormatterServiceMock>();
        factory_ = Fit::FitableInvokerFactory::Custom()
            .SetCurrentWorkerId(std::move(localAddress_.id))
            .SetFitableDiscovery(fitableDiscoveryPtr_)
            .SetFormatterService(formatterService_)
            .Build();
        coordinate_ = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    }

    void TearDown() override
    {
    }
public:
    std::shared_ptr<FormatterServiceMock> formatterService_;
    std::shared_ptr<BrokerFitableDiscoveryMock> fitableDiscoveryPtr_;
    Fit::FitableInvokerFactoryPtr factory_;
    fit::registry::Address localAddress_;
    Fit::FitableCoordinatePtr coordinate_ {nullptr};
    Fit::vector<int32_t> formats_ {0};
};


TEST_F(FitableInvokerShellTest, should_return_not_found_when_invoke_given_null_supplier)
{
    // given
    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, LocalOnly()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(false));

    FitableInvokerShell fitableInvokerShell(factory_.get(), coordinate_,
        Fit::Framework::Annotation::FitableType::MAIN, nullptr, config);
    // when

    FitCode result = fitableInvokerShell.Invoke(testFitable.ctx_, in, out);
    // then
    EXPECT_EQ(result, FIT_ERR_NOT_FOUND);
}


TEST_F(FitableInvokerShellTest, should_return_not_found_when_invoke_given_null_endpoint)
{
    // given
    std::unique_ptr<FitableEndpointSupplier> endpointSupplier
        = FitableEndpointSupplier::CreateDirectSupplier(nullptr);
    const int a = 20;
    bool* ret = new bool();
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, LocalOnly()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(false));

    FitableInvokerShell fitableInvokerShell(factory_.get(), coordinate_,
        Fit::Framework::Annotation::FitableType::MAIN, std::move(endpointSupplier), config);
    // when

    FitCode result = fitableInvokerShell.Invoke(testFitable.ctx_, in, out);
    // then
    EXPECT_EQ(result, FIT_ERR_NOT_FOUND);
}

TEST_F(FitableInvokerShellTest, should_return_error_when_invoke_given_param)
{
    // given
    std::unique_ptr<FitableEndpointSupplier> endpointSupplier
        = FitableEndpointSupplier::CreateDirectSupplier(FitableEndpoint::Custom()
        .SetWorkerId(localAddress_.id)
        .SetEnvironment(localAddress_.environment)
        .SetHost(localAddress_.host)
        .SetPort(localAddress_.port)
        .SetProtocol((int32_t)localAddress_.protocol)
        .SetFormats(std::move(formats_))
        .Build());
    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, LocalOnly()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(false));

    FitableInvokerShell fitableInvokerShell(factory_.get(), coordinate_,
        Fit::Framework::Annotation::FitableType::MAIN, std::move(endpointSupplier), config);
    // when

    FitCode result = fitableInvokerShell.Invoke(testFitable.ctx_, in, out);
    // then
    EXPECT_EQ(result, FIT_ERR_NOT_FOUND);
}


TEST_F(FitableInvokerShellTest,
    should_return_not_found_when_invoke_given_mock_end_point_is_not_local_and_no_converter)
{
    // given
    uint16_t port = 0;
    Fit::string host {};
    std::shared_ptr<FitableEndpointMock> fitableEndPointPtr = std::make_shared<FitableEndpointMock>();
    EXPECT_CALL(*fitableEndPointPtr, IsLocal())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(false));
    EXPECT_CALL(*fitableEndPointPtr, GetFormats())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(formats_));
    EXPECT_CALL(*fitableEndPointPtr, GetPort())
        .WillRepeatedly(testing::Return(port));
    EXPECT_CALL(*fitableEndPointPtr, GetHost())
        .WillRepeatedly(testing::ReturnRef(host));

    std::unique_ptr<FitableEndpointSupplier> endpointSupplier
        = FitableEndpointSupplier::CreateDirectSupplier(fitableEndPointPtr);
    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, LocalOnly()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(false));

    FitableInvokerShell fitableInvokerShell(factory_.get(), coordinate_,
        Fit::Framework::Annotation::FitableType::MAIN, std::move(endpointSupplier), config);
    // when

    FitCode result = fitableInvokerShell.Invoke(testFitable.ctx_, in, out);
    // then
    EXPECT_EQ(result, FIT_ERR_NOT_FOUND);
}