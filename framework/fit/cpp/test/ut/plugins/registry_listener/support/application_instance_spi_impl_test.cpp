/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Description  : provide ut for application instance spi.
 * Author       : wangpanbo
 * Date         : 2023/09/13
 */
#include <registry_listener/include/support/application_instance_spi_impl.hpp>
#include <fit/internal/runtime/config/configuration_entities.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace ::testing;
using namespace Fit::Registry::Listener;
class ApplicationInstanceSpiImplTest : public ::testing::Test {
public:
    void SetUp() override
    {
        application_.name = "test_name";
        application_.nameVersion = "test_version";
        applicationInstanceSpi_ = Fit::make_shared<ApplicationInstanceSpiImpl>();
    }
    void TearDown() override
    {
    }
public:
    Fit::shared_ptr<ApplicationInstanceSpiImpl> applicationInstanceSpi_ {};
        ApplicationInfo application_ {};
};

TEST_F(ApplicationInstanceSpiImplTest, should_return_empty_when_query_given_application)
{
    // given
    Fit::vector<ApplicationInfo> applications {application_};
    // when
    Fit::vector<ApplicationInstance> queryResult = applicationInstanceSpi_->Query(applications);
    // then
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(ApplicationInstanceSpiImplTest, should_return_empty_when_subscribe_given_application)
{
    // given
    Fit::vector<ApplicationInfo> applications {application_};
    // when
    Fit::vector<ApplicationInstance> subscribeResult = applicationInstanceSpi_->Subscribe(applications);
    // then
    EXPECT_EQ(subscribeResult.empty(), true);
}