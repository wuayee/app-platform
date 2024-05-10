/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/
#include <src/broker/client/domain/remote_invoker.hpp>
#include <src/broker/client/domain/fitable_invoker_factory.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/broker_fitable_discovery_mock.h>
#include <mock/formatter_service_mock.h>
#include <mock/broker_genericable_config_mock.hpp>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace testing;
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
    static constexpr const char *GENERIC_ID = "7f2f22b898294201ba4ad0697febf0e0";
    TestFitable() : ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                          TestFitableStruct::OutType)>(GENERIC_ID) {}
    ~TestFitable() override = default;
};

class RemoteInvokerTest : public ::testing::Test {
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

        formats_.emplace_back(0);
        endpoint_ = Fit::FitableEndpoint::Custom().SetEnvironment(localAddress_.environment)
            .SetFormats(localAddress_.formats)
            .SetHost(localAddress_.host)
            .SetPort(localAddress_.port)
            .SetProtocol(localAddress_.protocol)
            .SetWorkerId(localAddress_.id)
            .Build();
    }

    void TearDown() override
    {
    }

protected:
    fit::registry::Address localAddress_;
    std::shared_ptr<FormatterServiceMock> formatterService_;
    std::shared_ptr<BrokerFitableDiscoveryMock> fitableDiscoveryPtr_;
    Fit::FitableInvokerFactoryPtr factory_;
    Fit::FitableCoordinatePtr coordinate_ {nullptr};
    ::Fit::FitableEndpointPtr endpoint_;
    std::vector<int32_t> formats_;
};

TEST_F(RemoteInvokerTest, should_return_not_found_when_invoke_given_mock_formatter_nullptr)
{
    // given
    EXPECT_CALL(*formatterService_, GetFormatter(testing::A<const BaseSerialization&>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(nullptr));

    auto configMock = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*configMock, TraceIgnore()).Times(AtLeast(1)).WillRepeatedly(Return(true));

    TestFitable testFitable;
    RemoteInvokerBuilder remoteInvokerBuild;
    std::unique_ptr<Fit::FitableInvoker> remoteInvoker =
        remoteInvokerBuild
        .SetFactory(factory_.get())
        .SetCoordinate(coordinate_)
        .SetFitableType(::Fit::Framework::Annotation::FitableType::MAIN)
        .SetEndpoint(endpoint_)
        .SetFitConfig(std::move(configMock))
        .Build();

    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};

    // when
    FitCode result = remoteInvoker->Invoke(testFitable.ctx_, in, out);

    // then
    EXPECT_EQ(result, FIT_ERR_NOT_FOUND);
}

TEST_F(RemoteInvokerTest, should_return_error_when_invoke_given_SerializeRequest_failed)
{
    // given
    Fit::Framework::Formatter::FormatterMetaPtr formatterMetaPtr = std::make_shared<FormatterMeta>();
    formatterMetaPtr->SetGenericId(TestFitable::GENERIC_ID);
    formatterMetaPtr->SetFormat(0);

    EXPECT_CALL(*formatterService_, GetFormatter(testing::A<const BaseSerialization&>()))
        .Times(1)
        .WillRepeatedly(testing::Return(formatterMetaPtr));
    EXPECT_CALL(*formatterService_, SerializeRequest(_, _, _, _))
        .Times(1)
        .WillRepeatedly(testing::Return(FIT_ERR_SERIALIZE));

    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, TraceIgnore()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(true));
    TestFitable testFitable;
    RemoteInvokerBuilder remoteInvokerBuild;
    std::unique_ptr<Fit::FitableInvoker> remoteInvoker =
        remoteInvokerBuild.SetFactory(factory_.get())
            .SetCoordinate(coordinate_)
            .SetFitableType(::Fit::Framework::Annotation::FitableType::MAIN)
            .SetEndpoint(endpoint_)
            .SetFitConfig(std::move(config))
            .Build();
    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};

    // when
    FitCode result = remoteInvoker->Invoke(testFitable.ctx_, in, out);

    // then
    EXPECT_EQ(result, FIT_ERR_SERIALIZE);
}

TEST_F(RemoteInvokerTest, should_return_error_when_invoke_given_illgel_genericable_id)
{
    // given
    coordinate_ = Fit::FitableCoordinate::Custom()
            .SetGenericableId("xxxxxx")
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    Fit::Framework::Formatter::FormatterMetaPtr formatterMetaPtr = std::make_shared<FormatterMeta>();
    formatterMetaPtr->SetGenericId(coordinate_->GetGenericableId());
    formatterMetaPtr->SetFormat(0);

    EXPECT_CALL(*formatterService_, GetFormatter(testing::A<const BaseSerialization&>()))
        .Times(1)
        .WillRepeatedly(testing::Return(formatterMetaPtr));

    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, TraceIgnore()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(true));
    TestFitable testFitable;
    std::unique_ptr<Fit::FitableInvoker> remoteInvoker =
        RemoteInvokerBuilder()
            .SetFactory(factory_.get())
            .SetCoordinate(coordinate_)
            .SetFitableType(::Fit::Framework::Annotation::FitableType::MAIN)
            .SetEndpoint(endpoint_)
            .SetFitConfig(std::move(config))
            .Build();
    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};

    // when
    FitCode result = remoteInvoker->Invoke(testFitable.ctx_, in, out);

    // then
    EXPECT_EQ(result, FIT_ERR_SERIALIZE);
}


TEST_F(RemoteInvokerTest, should_return_non_nullptr_when_create_trace_context_given_param)
{
    // given
    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, TraceIgnore()).Times(testing::AtLeast(1)).WillRepeatedly(testing::Return(false));
    std::unique_ptr<Fit::RemoteInvoker> remoteInvoker = make_unique<RemoteInvoker>(
        factory_.get(), std::move(coordinate_), ::Fit::Framework::Annotation::FitableType::MAIN, endpoint_, config);

    Tracer::GetInstance()->SetEnabled(true);
    Tracer::GetInstance()->SetGlobalTraceEnabled(true);
    // when
    ::Fit::TraceContextPtr result = remoteInvoker->CreateTraceContext(nullptr);

    // then
    EXPECT_EQ(result != nullptr, true);
}