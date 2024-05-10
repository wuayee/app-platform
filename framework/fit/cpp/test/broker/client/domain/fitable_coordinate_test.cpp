/*
* Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
* Description  :
* Author       : w00561424
* Date         : 2021/08/04
* Notes:       :
*/
#include <src/broker/client/domain/fitable_coordinate.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/formatter_service_mock.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "broker_client_fit_config.h"
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

class FitableCoordinateTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
public:
};

TEST_F(FitableCoordinateTest, should_return_0_when_compare_given_same_coordinate)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    Fit::FitableCoordinatePtr coordinate2 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_EQ(coordinate1->Compare(*coordinate2), 0);
}

TEST_F(FitableCoordinateTest, should_return_non_zero_when_compare_given_diff_gid)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId("diff_gid")
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    Fit::FitableCoordinatePtr coordinate2 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_NE(coordinate1->Compare(*coordinate2), 0);
}

TEST_F(FitableCoordinateTest, should_return_non_zero_when_compare_given_diff_gvesion)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("2.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    Fit::FitableCoordinatePtr coordinate2 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_NE(coordinate1->Compare(*coordinate2), 0);
}

TEST_F(FitableCoordinateTest, should_return_non_zero_when_compare_given_diff_fid)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("diff_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    Fit::FitableCoordinatePtr coordinate2 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_NE(coordinate1->Compare(*coordinate2), 0);
}

TEST_F(FitableCoordinateTest, should_return_non_zero_when_compare_given_diff_fversion)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("2.0.0")
            .Build();
    Fit::FitableCoordinatePtr coordinate2 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_NE(coordinate1->Compare(*coordinate2), 0);
}

TEST_F(FitableCoordinateTest, should_return_true_when_equals_given_same_coordinate)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    Fit::FitableCoordinatePtr coordinate2 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_EQ(coordinate1->Equals(*coordinate2), true);
}

TEST_F(FitableCoordinateTest, should_return_string_when_to_string_given_empty)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_EQ(coordinate1->ToString(), "test_fitable_genericable_id:1.0.0:test_fitable_fid:1.0.0");
}

TEST_F(FitableCoordinateTest, should_return_value_when_compute_hash_given_empty)
{
    // given
    Fit::FitableCoordinatePtr coordinate1 = Fit::FitableCoordinate::Custom()
            .SetGenericableId(TestFitable::GENERIC_ID)
            .SetGenericableVersion("1.0.0")
            .SetFitableId("test_fitable_fid")
            .SetFitableVersion("1.0.0")
            .Build();
    // when
    // then
    EXPECT_NE(coordinate1->ComputeHash(), 0);
}