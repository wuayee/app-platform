/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/19 14:52
 * Notes:       :
 */
#include <runtime/config/configuration_client.h>
#include <runtime/config/configuration_service_for_config_file.h>
#include <mock/configuration_repo_mock.hpp>
#include <runtime/config/configuration_repo_impl.h>
#include <fit/internal/plugin/plugin_manager.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace Fit::Configuration;
using namespace ::testing;

class ConfigurationServiceTest : public testing::Test {
public:
    void SetUp() override
    {
        repo_ = std::make_shared<ConfigurationRepoMock>();
        Fit::Plugin::PluginManager* pluginManager {nullptr};
        Fit::string configFilePath = "";
        service_ = std::make_shared<ConfigurationServiceForConfigFile>(
            repo_, pluginManager, configFilePath, nullptr, nullptr);
        exist_generic_config_ = Fit::make_shared<GenericableConfiguration>();
        exist_generic_config_->SetGenericId("generic_id");
        exist_generic_config_->SetDefaultFitableId("default_fitable_id");
        exist_generic_config_->SetLoadbalance("loadbalance_id");
        exist_generic_config_->SetRoute("route_id");
        TagList tags {"tag1", "tag2"};
        exist_generic_config_->SetTags(tags);
        TrustConfiguration trust {};
        trust.validate = "validate";
        trust.before = "before";
        trust.after = "after";
        trust.error = "error";
        exist_generic_config_->SetTrust(trust);

        exist_fitable_.aliases = {"alias1", "alias2"};
        exist_fitable_.degradation = "degradation";
        exist_fitable_.fitableId = "fitable_id";
        exist_generic_config_->SetFitable(exist_fitable_);
    }

    void TearDown() override {}

    std::shared_ptr<ConfigurationRepoMock> repo_;
    GenericConfigPtr exist_generic_config_;
    FitableConfiguration exist_fitable_;
    std::shared_ptr<ConfigurationServiceForConfigFile> service_;
};

TEST_F(ConfigurationServiceTest,
    should_has_genericable_tag_when_genericable_has_tag_given_exist_generic_id_and_tag)
{
    // given
    bool expected_ret = true;
    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    for (auto &tag : exist_generic_config_->GetTags()) {
        bool ret = service_->GenericableHasTag(exist_generic_config_->GetGenericId(), tag);
        ASSERT_THAT(ret, ::testing::Eq(expected_ret));
    }
}

TEST_F(ConfigurationServiceTest,
    should_return_default_fitable_when_get_genericable_default_fitable_id_given_exist_generic_id)
{
    // given

    Fit::string expected_ret = exist_generic_config_->GetDefaultFitableId();

    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    auto result = service_->GetGenericableDefaultFitableId(exist_generic_config_->GetGenericId());
    ASSERT_THAT(result, ::testing::Eq(expected_ret));
}

TEST_F(ConfigurationServiceTest,
    should_return_route_when_get_genericable_route_id_given_exist_generic_id)
{
    // given

    Fit::string expected_ret = exist_generic_config_->GetRoute();

    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    auto result = service_->GetGenericableRouteId(exist_generic_config_->GetGenericId());
    ASSERT_THAT(result, ::testing::Eq(expected_ret));
}

TEST_F(ConfigurationServiceTest,
    should_return_loadbalance_when_get_genericable_loadbalance_id_given_exist_generic_id)
{
    // given

    Fit::string expected_ret = exist_generic_config_->GetLoadbalance();

    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    auto result = service_->GetGenericableLoadbalanceId(exist_generic_config_->GetGenericId());
    ASSERT_THAT(result, ::testing::Eq(expected_ret));
}

TEST_F(ConfigurationServiceTest,
    should_return_trust_when_get_genericable_trust_given_exist_generic_id)
{
    // given

    TrustConfiguration expected_ret = exist_generic_config_->GetTrust();

    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    auto result = service_->GetGenericableTrust(exist_generic_config_->GetGenericId());
    EXPECT_THAT(result.validate, ::testing::Eq(expected_ret.validate));
    EXPECT_THAT(result.before, ::testing::Eq(expected_ret.before));
    EXPECT_THAT(result.after, ::testing::Eq(expected_ret.after));
    EXPECT_THAT(result.error, ::testing::Eq(expected_ret.error));
}

TEST_F(ConfigurationServiceTest,
    should_return_fitable_degradation_when_get_fitable_degradation_id_given_exist_generic_id_and_fitable_id)
{
    // given

    Fit::string expected_ret = exist_fitable_.degradation;

    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    auto result = service_->GetFitableDegradationId(exist_generic_config_->GetGenericId(),
        exist_fitable_.fitableId);
    EXPECT_THAT(result, ::testing::Eq(expected_ret));
}

TEST_F(ConfigurationServiceTest,
    should_return_fitable_id_when_get_fitable_id_by_alias_given_exist_generic_id_and_alias)
{
    // given

    Fit::string expected_ret = exist_fitable_.fitableId;

    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) -> GenericConfigPtr {
            return exist_generic_config_;
        }));

    // when
    for (auto &alias : exist_fitable_.aliases) {
        Fit::string ret = service_->GetFitableIdByAlias(exist_generic_config_->GetGenericId(), alias);
        ASSERT_THAT(ret, ::testing::Eq(expected_ret));
    }
}

TEST_F(ConfigurationServiceTest, should_return_config_file_generic_conf_when_get_given_config_file)
{
    // given
    ConfigurationRepoPtr configRepoPtr = std::make_shared<ConfigurationRepoImpl>();
    Fit::string genericable_id = "exist_genericable_id";
    Fit::string default_fitable_id = "default_fitable_id";
    Fit::string loadbalance_id = "loadbalance_id";
    Fit::string route_id = "route_id";
    TagList tags {"tag1", "tag2"};
    ConfigSourceTypes types {"config_source1", "config_source2"};

    TrustConfiguration trust;
    trust.after = "after_id";
    trust.validate = "validate_id";
    trust.before = "before_id";
    trust.error = "error_id";

    Fit::string fitable_id = "fitable_id";
    AliasList aliases {"alias1", "alias2"};
    Fit::string degradation = "degradation_id";

    int32_t expected_ret = FIT_ERR_SUCCESS;
    Fit::string config_file = "configuration_file_test.json";
    auto servicePtr = std::make_shared<ConfigurationServiceForConfigFile>(configRepoPtr, nullptr, config_file,
        nullptr, nullptr);

    // when
    int32_t load_ret = servicePtr->LoadFromFile(config_file);
    GenericableConfiguration result;
    int32_t getConfigRet = servicePtr->GetGenericableConfig(genericable_id, result);

    // then
    ASSERT_THAT(load_ret, ::testing::Eq(expected_ret));
    ASSERT_THAT(getConfigRet, ::testing::Eq(expected_ret));
    EXPECT_THAT(result.GetGenericId(), ::testing::Eq(genericable_id));
    EXPECT_THAT(result.GetDefaultFitableId(), ::testing::Eq(default_fitable_id));
    EXPECT_THAT(result.GetLoadbalance(), ::testing::Eq(loadbalance_id));
    EXPECT_THAT(result.GetTrust().validate, ::testing::Eq(trust.validate));
    EXPECT_THAT(result.GetTrust().before, ::testing::Eq(trust.before));
    EXPECT_THAT(result.GetTrust().after, ::testing::Eq(trust.after));
    EXPECT_THAT(result.GetTrust().error, ::testing::Eq(trust.error));
    EXPECT_THAT(result.GetTags().size(), ::testing::Eq(tags.size()));
    for (auto &tag : tags) {
        EXPECT_THAT(true, ::testing::Eq(result.HasTag(tag)));
    }
    FitableConfiguration fitable;
    result.GetFitable(fitable_id, fitable);
    EXPECT_THAT(fitable.fitableId, ::testing::Eq(fitable_id));
    EXPECT_THAT(fitable.degradation, ::testing::Eq(degradation));
    EXPECT_THAT(fitable.aliases.size(), ::testing::Eq(aliases.size()));
    for (uint32_t i = 0; i < aliases.size(); ++i) {
        EXPECT_THAT(aliases[i], ::testing::Eq(fitable.aliases[i]));
    }

    EXPECT_THAT(result.GetConfigSourceTypes().size(), ::testing::Eq(types.size()));
    for (uint32_t i = 0; i < types.size(); ++i) {
        EXPECT_THAT(result.GetConfigSourceTypes()[i], ::testing::Eq(types[i]));
    }
}