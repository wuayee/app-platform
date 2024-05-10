/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide configuration service from registry server test
 * Author       : w00561424
 * Date         : 2023/09/01
 * Notes:       :
 */
#include <configuration_service_spi.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace ::testing;
using namespace Fit::Configuration;
class ConfigurationServiceSpiTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }
    void TearDown() override
    {
    }
public:
};

TEST_F(ConfigurationServiceSpiTest, should_return_error_when_get_running_fitables_given_genericable_ids)
{
    // given
    Fit::vector<Fit::string> genericableIds {"test_gid"};
    Fit::vector<GenericConfigPtr> genericableConfigs;
    Fit::string environment {"test_env"};
    // when
    ConfigurationServiceSpiPtr spi = std::make_shared<ConfigurationServiceSpiImpl>();
    int32_t ret = spi->GetRunningFitables(genericableIds, environment, genericableConfigs);
    // then
    EXPECT_EQ(ret, FIT_ERR_FAIL);
}