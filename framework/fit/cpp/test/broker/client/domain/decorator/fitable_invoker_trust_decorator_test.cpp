/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/
#include <gtest/gtest.h>
#include <src/broker/client/domain/remote_invoker.hpp>
#include <src/broker/client/domain/decorator/fitable_invoker_trust_decorator.hpp>
#include <src/broker/client/domain/fitable_invoker_factory.hpp>
#include <src/broker/client/adapter/south/gateway/broker_fitable_discovery.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/broker_fitable_discovery_mock.h>
#include <mock/formatter_service_mock.h>
#include <mock/broker_genericable_config_mock.hpp>
#include <mock/fitable_invoker_mock.hpp>
#include <mock/fitable_invoker_factory_mock.h>
#include <fit/fit_log.h>

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
    static constexpr const char *GENERIC_ID = "test_fitable_genericable_id";
    TestFitable() : ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                          TestFitableStruct::OutType)>(GENERIC_ID) {}
    ~TestFitable() override = default;
};

class FitableInvokerTrustDecoratorTest : public ::testing::Test {
public:
    void SetUp() override
    {
        factoryMock_ = std::make_shared<FitableInvokerFactoryMock>();

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
protected:
    std::shared_ptr<FitableInvokerFactoryMock> factoryMock_;
    Fit::FitableCoordinatePtr coordinate_ {nullptr};
};

TEST_F(FitableInvokerTrustDecoratorTest,
    should_return_ok_when_degradation_invoke_given_decorated_invoke_ok_and_trust_config)
{
    // given
    auto configMock = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*configMock, EnableTrust()).Times(AtLeast(1)).WillRepeatedly(Return(true));
    EXPECT_CALL(*configMock, GetValidate()).Times(1).WillOnce(Return("validate"));
    EXPECT_CALL(*configMock, GetBefore()).Times(1).WillOnce(Return("before"));
    EXPECT_CALL(*configMock, GetAfter()).Times(1).WillOnce(Return("after"));

    TestFitable testFitable;
    auto decoratedInvoker = make_unique<FitableInvokerMock>();
    FitConfigPtr fitConfig = configMock;
    EXPECT_CALL(*decoratedInvoker, GetConfig()).WillRepeatedly(ReturnRef(fitConfig));
    EXPECT_CALL(*decoratedInvoker, Invoke(_, _, _)).Times(1).WillOnce(Return(FIT_OK));
    EXPECT_CALL(*decoratedInvoker, GetFitableType()).Times(1).WillOnce(Return(Annotation::FitableType::MAIN));
    EXPECT_CALL(*decoratedInvoker, GetFactory()).Times(AtLeast(1)).WillRepeatedly(Return(factoryMock_.get()));
    EXPECT_CALL(*decoratedInvoker, GetCoordinate()).Times(AtLeast(1)).WillRepeatedly(ReturnRef(coordinate_));

    EXPECT_CALL(
        *factoryMock_, GetRawInvoker(A<FitableCoordinatePtr>(), A<Annotation::FitableType>(), A<FitConfigPtr>()))
        .Times(AtLeast(1))
        .WillRepeatedly(Invoke([&fitConfig, this](FitableCoordinatePtr, Annotation::FitableType, FitConfigPtr) {
            auto result = make_unique<FitableInvokerMock>();
            EXPECT_CALL(*result, Invoke(_, _, _)).Times(1).WillOnce(Return(FIT_OK));
            return result;
        }));

    std::unique_ptr<Fit::FitableInvoker> invoker
        = make_unique<FitableInvokerTrustDecorator>(move(decoratedInvoker));

    const int a = 20;
    bool* ret = new bool();
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    // when
    FitCode result = invoker->Invoke(testFitable.ctx_, in, out);
    Fit::Framework::Annotation::FitableType fitableType = invoker->GetFitableType();
    // then
    EXPECT_EQ(result, FIT_OK);
    EXPECT_EQ(static_cast<int32_t>(fitableType), static_cast<int32_t>(::Fit::Framework::Annotation::FitableType::MAIN));
}