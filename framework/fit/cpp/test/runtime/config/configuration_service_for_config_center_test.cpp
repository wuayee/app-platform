/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide configuration service from config center test
 * Author       : w00561424
 * Date         : 2023/08/29
 * Notes:       :
 */

#include <runtime/config/configuration_service_for_config_center.h>
#include <mock/configuration_repo_mock.hpp>
#include <mock/configuration_client_mock.hpp>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
#include <fit/stl/memory.hpp>

using namespace testing;

class ConfigurationServiceForConfigCenterTest : public testing::Test {
public:
    void SetUp() override
    {
        default_result_ = "default_value";
        loadbalance_result_ = "loadbalance_value";
        tags_ = {"tag1", "tag2"};
        tags_string_ = "tag1,tag2";
        trust_validate_ = "validate_value";
        trust_before_ = "before_value";
        trust_after_ = "after_value";
        trust_error_ = "error_value";

        client_ = std::make_shared<ConfigurationClientMock>();
        repo_ = std::make_shared<ConfigurationRepoMock>();
        service_ = std::make_shared<ConfigurationServiceForConfigCenter>(client_, repo_);
    }
    void TearDown() override
    {
    }
public:
    Fit::string default_result_;
    Fit::string loadbalance_result_;
    TagList tags_;
    Fit::string tags_string_;
    Fit::string trust_validate_;
    Fit::string trust_before_;
    Fit::string trust_after_;
    Fit::string trust_error_;
    std::shared_ptr<ConfigurationRepoMock> repo_;
    std::shared_ptr<ConfigurationClientMock> client_;
    std::shared_ptr<ConfigurationServiceForConfigCenter> service_;
};

TEST_F(ConfigurationServiceForConfigCenterTest,
    should_call_download_and_return_not_found_when_get_genericable_config_given_not_exist_generic_id)
{
    // given

    Fit::string not_exist_generic_id = "not_exist";
    Fit::string expected_result;

    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(Return(FIT_ERR_NOT_FOUND));
    EXPECT_CALL(*client_, IsSubscribed(_)).WillOnce(Return(false));

    Fit::string key_generic_id = "fit.public.genericables.not_exist";
    ConfigurationClient::ConfigSubscribePathCallback subscribePathCallback;
    EXPECT_CALL(*client_, Subscribe(Eq(key_generic_id), A<ConfigurationClient::ConfigSubscribePathCallback>()))
        .WillOnce(Return(FIT_ERR_SUCCESS));
    EXPECT_CALL(*client_, Download(_, _)).WillOnce(Return(FIT_ERR_NOT_FOUND));

    // when
    auto ret = service_->GetGenericableDefaultFitableId(not_exist_generic_id);
    EXPECT_THAT(ret, ::testing::Eq(expected_result));
}

TEST_F(ConfigurationServiceForConfigCenterTest,
    should_return_download_config_when_get_genericable_config_given_not_exist_generic_id_and_download_default_config)
{
    // given
    Fit::string not_exist_generic_id = "not_exist";
    GenericableConfiguration download_genericable_config {};
    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id, GenericableConfiguration &out) -> int32_t {
            if (generic_id == download_genericable_config.GetGenericId()) {
                out = download_genericable_config;
                return FIT_ERR_SUCCESS;
            }
            return FIT_ERR_NOT_FOUND;
        }));
    EXPECT_CALL(*repo_, Set(_)).WillRepeatedly(
        Invoke([&](GenericConfigPtr val) -> int32_t {
            download_genericable_config = *val;
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, Download(_, _)).WillOnce(
        Invoke([&](const Fit::string &generic_id, ItemValueSet &out) -> int32_t {
            out.push_back(ItemValue {"route.default", default_result_});
            out.push_back(ItemValue {"loadbalance", loadbalance_result_});
            out.push_back(ItemValue {"tags", tags_string_});
            out.push_back(ItemValue {"trust.validate", trust_validate_});
            out.push_back(ItemValue {"trust.before", trust_before_});
            out.push_back(ItemValue {"trust.after", trust_after_});
            out.push_back(ItemValue {"trust.error", trust_error_});
            out.push_back(ItemValue {"trust.error.1", trust_error_});
            out.push_back(ItemValue {"trust.xxx", trust_error_});
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, IsSubscribed(_)).WillRepeatedly(Return(true));

    // when
    auto default_ret = service_->GetGenericableDefaultFitableId(not_exist_generic_id);
    auto route_ret = service_->GetGenericableRouteId(not_exist_generic_id);
    auto loadbalance_ret = service_->GetGenericableLoadbalanceId(not_exist_generic_id);
    auto trust_ret = service_->GetGenericableTrust(not_exist_generic_id);

    // then
    EXPECT_THAT(default_ret, ::testing::Eq(default_result_));
    EXPECT_THAT(loadbalance_ret, ::testing::Eq(loadbalance_result_));
    EXPECT_THAT(trust_ret.validate, ::testing::Eq(trust_validate_));
    EXPECT_THAT(trust_ret.before, ::testing::Eq(trust_before_));
    EXPECT_THAT(trust_ret.after, ::testing::Eq(trust_after_));
    EXPECT_THAT(trust_ret.error, ::testing::Eq(trust_error_));
    for (auto &tag : tags_) {
        EXPECT_TRUE(service_->GenericableHasTag(not_exist_generic_id, tag));
    }
}

TEST_F(ConfigurationServiceForConfigCenterTest,
    should_return_download_config_when_get_fitable_config_given_not_exist_generic_id_and_download_return_fitable_config)
{
    // given
    Fit::string not_exist_generic_id = "not_exist";
    Fit::string genericable_config_key = "fit.public.genericables." + not_exist_generic_id;
    Fit::string expected_degradation = "degradation_value";
    Fit::string fitable_id = "a.b.c";
    AliasList aliases = {"1", "2"};
    Fit::string aliases_string = "1,2";

    GenericableConfiguration download_genericable_config {};

    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id, GenericableConfiguration &out) -> int32_t {
            if (generic_id == download_genericable_config.GetGenericId()) {
                out = download_genericable_config;
                return FIT_ERR_SUCCESS;
            }
            return FIT_ERR_NOT_FOUND;
        }));
    EXPECT_CALL(*repo_, Set(_)).WillRepeatedly(
        Invoke([&](GenericConfigPtr val) -> int32_t {
            download_genericable_config = *val;
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, Download(_, _)).WillOnce(
        Invoke([&](const Fit::string &generic_id, ItemValueSet &out) -> int32_t {
            out.push_back(ItemValue {"fitables.a-b-c.aliases", aliases_string});
            out.push_back(ItemValue {"fitables.a-b-c.degradation", expected_degradation});
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, IsSubscribed(_)).WillOnce(Return(false))
        .WillRepeatedly(Return(true));

    ConfigurationClient::ConfigSubscribePathCallback subscribePathCallback;
    EXPECT_CALL(*client_,
        Subscribe(Eq(genericable_config_key),
            A<ConfigurationClient::ConfigSubscribePathCallback>()))
        .WillOnce(
        DoAll(Invoke(
            [&subscribePathCallback](const Fit::string &id, ConfigurationClient::ConfigSubscribePathCallback cb) {
                subscribePathCallback = std::move(cb);
            }), Return(FIT_OK)));

    // when
    auto degradation_id = service_->GetFitableDegradationId(not_exist_generic_id, fitable_id);
    EXPECT_THAT(degradation_id, ::testing::Eq(expected_degradation));

    for (auto &alias : aliases) {
        Fit::string target_fitable_id = service_->GetFitableIdByAlias(not_exist_generic_id, alias);
        ASSERT_THAT(target_fitable_id, ::testing::Eq(fitable_id));
    }

    // check for notify
    ItemValueSet notifyItems {
        {"fitables.a-b-c.degradation", ""}
    };
    subscribePathCallback(genericable_config_key, notifyItems);
    degradation_id = service_->GetFitableDegradationId(not_exist_generic_id, fitable_id);
    EXPECT_THAT(degradation_id, IsEmpty());
}

TEST_F(ConfigurationServiceForConfigCenterTest, should_return_xxx_when_download_xxx_given_xxx)
{
    // given
    Fit::string generic_id = "test_gid";

    GenericableConfiguration download_genericable_config {};
    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id, GenericableConfiguration &out) -> int32_t {
            return FIT_ERR_NOT_FOUND;
        }));
    EXPECT_CALL(*repo_, Set(_)).WillRepeatedly(
        Invoke([&](GenericConfigPtr val) -> int32_t {
            download_genericable_config = *val;
            return FIT_ERR_SUCCESS;
        }));

    EXPECT_CALL(*client_, Download(_, _)).WillOnce(
        Invoke([&](const Fit::string &generic_id, ItemValueSet &out) -> int32_t {
            out.push_back(ItemValue {"invalid_taf.a-b-c", "invalid_value"});
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, IsSubscribed(_))
        .WillRepeatedly(Return(true));
    // when
    Fit::Configuration::FitableSet fitableSet = service_->GetFitables(generic_id);

    // check for notify
    EXPECT_EQ(fitableSet.empty(), true);
}


TEST_F(ConfigurationServiceForConfigCenterTest,
    should_return_download_config_when_get_getter_given_not_exist_generic_id_and_download_default_config)
{
    // given
    Fit::string not_exist_generic_id = "not_exist";
    GenericConfigPtr getter_store {nullptr};
    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(Invoke([&](const Fit::string &genericId) {
            return getter_store;
        }));
    EXPECT_CALL(*repo_, Set(_)).WillRepeatedly(
        Invoke([&](GenericConfigPtr val) -> int32_t {
            getter_store = val;
            FIT_LOG_CORE("Test ++++: %s.", getter_store->GetDefaultFitableId().c_str());
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, Download(_, _)).WillOnce(
        Invoke([&](const Fit::string &generic_id, ItemValueSet &out) -> int32_t {
            out.push_back(ItemValue {"route.default", default_result_});
            out.push_back(ItemValue {"loadbalance", loadbalance_result_});
            out.push_back(ItemValue {"tags", tags_string_});
            out.push_back(ItemValue {"trust.validate", trust_validate_});
            out.push_back(ItemValue {"trust.before", trust_before_});
            out.push_back(ItemValue {"trust.after", trust_after_});
            out.push_back(ItemValue {"trust.error", trust_error_});
            out.push_back(ItemValue {"trust.error.1", trust_error_});
            out.push_back(ItemValue {"trust.xxx", trust_error_});
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*client_, IsSubscribed(_)).WillRepeatedly(Return(true));

    // when
    auto getter = service_->GetGenericableConfigPtr(not_exist_generic_id);

    // then
    EXPECT_EQ(getter != nullptr, true);
    EXPECT_THAT(getter->GetDefaultFitableId(), ::testing::Eq(default_result_));
    EXPECT_THAT(getter->GetLoadbalance(), ::testing::Eq(loadbalance_result_));
    EXPECT_THAT(getter->GetTrust().validate, ::testing::Eq(trust_validate_));
    EXPECT_THAT(getter->GetTrust().before, ::testing::Eq(trust_before_));
    EXPECT_THAT(getter->GetTrust().after, ::testing::Eq(trust_after_));
    EXPECT_THAT(getter->GetTrust().error, ::testing::Eq(trust_error_));
}