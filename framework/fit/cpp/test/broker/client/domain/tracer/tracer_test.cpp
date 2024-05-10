/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/
#include <src/broker/client/domain/trace/tracer.hpp>
#include <src/broker/client/domain/trace/trace_id.h>
#include <fit/external/framework/proxy_client.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "broker_client_fit_config.h"

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

class TracerImplTest : public ::testing::Test {
public:
    void SetUp() override
    {
        coordinate_ = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
        
        localAddress_ = FitSystemPropertyUtils::Address();
    }

    void TearDown() override
    {
    }
public:
    Fit::FitableCoordinatePtr coordinate_ {nullptr};
    fit::registry::Address localAddress_;
};

TEST_F(TracerImplTest, should_return_void_when_invoking_and_invoked_given_coordinate)
{
    // given
    TestFitable testFitable;
    Fit::TraceContextPtr traceContextPtr = Fit::TraceContext::Custom()
        .SetContext(testFitable.ctx_)
        .SetFitableCoordinate(coordinate_)
        .SetCallType(Fit::CallType::LOCAL)
        .SetTrustStage(Fit::GetTrustStage(::Fit::Framework::Annotation::FitableType::MAIN))
        .SetTargetHost(localAddress_.host)
        .SetTargetPort(localAddress_.port)
        .Build();
    // when
    traceContextPtr->OnFitableInvoking();
    traceContextPtr->OnFitableInvoked("OK");
}

TEST_F(TracerImplTest, should_return_trust_stage_when_GetTrustStage_given_fitable_type)
{
    // given
    // when
    // then
    EXPECT_EQ(static_cast<int>(Fit::TrustStage::VALIDATION),
              static_cast<int>(Fit::GetTrustStage(::Fit::Framework::Annotation::FitableType::VALIDATE)));
    EXPECT_EQ(static_cast<int>(Fit::TrustStage::BEFORE),
              static_cast<int>(Fit::GetTrustStage(::Fit::Framework::Annotation::FitableType::BEFORE)));
    EXPECT_EQ(static_cast<int>(Fit::TrustStage::AFTER),
              static_cast<int>(Fit::GetTrustStage(::Fit::Framework::Annotation::FitableType::AFTER)));
    EXPECT_EQ(static_cast<int>(Fit::TrustStage::ERROR),
              static_cast<int>(Fit::GetTrustStage(::Fit::Framework::Annotation::FitableType::ERROR)));
}

TEST_F(TracerImplTest, should_return_trace_id_when_create_id_given_ip)
{
    // given
    // when
    TestFitable testFitable;
    Fit::string traceId = Fit::TraceId::CreateId(testFitable.ctx_, "127.0.0.1");
    // then
    EXPECT_NE(traceId.empty(), true);
}

