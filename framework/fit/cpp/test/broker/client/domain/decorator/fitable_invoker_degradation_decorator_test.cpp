/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/

#include <src/broker/client/domain/remote_invoker.hpp>
#include <src/broker/client/domain/decorator/fitable_invoker_degradation_decorator.hpp>
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
#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "broker_client_fit_config.h"

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
    ~TestFitable() {}
};

class FitableInvokerDegradationDecoratorTest : public ::testing::Test {
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

TEST_F(FitableInvokerDegradationDecoratorTest,
    should_return_ok_when_degradation_invoke_given_degradation_invoke_ok)
{
    // given
    string degradation = "degradation";
    auto config = std::make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, GetDegradation(Eq(coordinate_->GetFitableId())))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(degradation));

    auto decoratedInvoker = make_unique<FitableInvokerMock>();
    FitConfigPtr fitConfig = config;
    EXPECT_CALL(*decoratedInvoker, GetConfig()).WillRepeatedly(ReturnRef(fitConfig));
    EXPECT_CALL(*decoratedInvoker, Invoke(_, _, _)).Times(1).WillOnce(Return(FIT_ERR_FAIL));
    EXPECT_CALL(*decoratedInvoker, GetFactory()).Times(AtLeast(1)).WillRepeatedly(Return(factoryMock_.get()));
    EXPECT_CALL(*decoratedInvoker, GetCoordinate()).Times(AtLeast(1)).WillRepeatedly(ReturnRef(coordinate_));

    TestFitable testFitable;
    EXPECT_CALL(
        *factoryMock_, GetRawInvoker(A<FitableCoordinatePtr>(), A<FitConfigPtr>()))
        .Times(1)
        .WillOnce(Invoke([&fitConfig, this](FitableCoordinatePtr, FitConfigPtr) {
            auto result = make_unique<FitableInvokerMock>();
            EXPECT_CALL(*result, Invoke(_, _, _)).Times(1).WillOnce(Return(FIT_OK));
            return result;
        }));

    std::unique_ptr<Fit::FitableInvoker> invoker =
        make_unique<FitableInvokerDegradationDecorator>(std::move(decoratedInvoker));

    const int a = 20;
    bool* ret = {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    // when
    FitCode result = invoker->Invoke(testFitable.ctx_, in, out);
    // then
    EXPECT_EQ(result, FIT_OK);
}