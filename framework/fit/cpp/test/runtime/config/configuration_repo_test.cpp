/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/06/25
 * Notes:       :
 */
#include <runtime/config/configuration_repo_impl.h>
#include <fit/fit_code.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

using namespace Fit::Configuration;

class ConfigurationRepoTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(ConfigurationRepoTest, should_return_not_found_when_get_given_not_exist_generic_id)
{
    // given
    ConfigurationRepoImpl repo {};
    Fit::string genericable_id = "empty";
    GenericableConfiguration result {};
    int32_t expected_ret = FIT_ERR_NOT_FOUND;

    // when
    int32_t ret = repo.Get(genericable_id, result);

    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_ret));
}

TEST_F(ConfigurationRepoTest, should_return_exist_generic_conf_when_get_given_exist_generic_id)
{
    // given
    ConfigurationRepoImpl repo {};
    Fit::string genericable_id = "exist";
    GenericableConfiguration result {};
    int32_t expected_ret = FIT_ERR_SUCCESS;

    // when
    // GenericableConfiguration exist {};
    auto exist = std::make_shared<GenericableConfiguration>();
    exist->SetGenericId(genericable_id);
    repo.Set(exist);
    int32_t ret = repo.Get(genericable_id, result);

    // then
    ASSERT_THAT(ret, ::testing::Eq(expected_ret));
    EXPECT_THAT(result.GetGenericId(), ::testing::Eq(genericable_id));
}

TEST_F(ConfigurationRepoTest, should_return_replaced_generic_conf_when_get_given_set_exist_generic_id)
{
    // given
    ConfigurationRepoImpl repo {};
    Fit::string genericable_id = "exist";
    Fit::string default_fitable_id = "default_fitable_id";
    Fit::string loadbalance_id = "loadbalance_id";
    Fit::string route_id = "route_id";
    TrustConfiguration trust;
    trust.after = "after";
    trust.validate = "validate";
    trust.before = "before";
    trust.error = "error";

    GenericableConfiguration result {};
    int32_t expected_ret = FIT_ERR_SUCCESS;

    // when
    auto exist = std::make_shared<GenericableConfiguration>();
    exist->SetGenericId(genericable_id);
    repo.Set(exist);
    exist->SetDefaultFitableId(default_fitable_id);
    exist->SetLoadbalance(loadbalance_id);
    exist->SetRoute(route_id);
    exist->SetTrust(trust);
    repo.Set(exist);
    int32_t ret = repo.Get(genericable_id, result);

    // then
    ASSERT_THAT(ret, ::testing::Eq(expected_ret));
    EXPECT_THAT(result.GetGenericId(), ::testing::Eq(genericable_id));
    EXPECT_THAT(result.GetDefaultFitableId(), ::testing::Eq(default_fitable_id));
    EXPECT_THAT(result.GetLoadbalance(), ::testing::Eq(loadbalance_id));
    EXPECT_THAT(result.GetRoute(), ::testing::Eq(route_id));
    EXPECT_THAT(result.GetTrust().validate, ::testing::Eq(trust.validate));
    EXPECT_THAT(result.GetTrust().before, ::testing::Eq(trust.before));
    EXPECT_THAT(result.GetTrust().after, ::testing::Eq(trust.after));
    EXPECT_THAT(result.GetTrust().error, ::testing::Eq(trust.error));
}