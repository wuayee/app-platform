/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/2/22
 * Notes:       :
 */
#include <runtime/config/configuration_client_v2.hpp>
#include <fit/fit_code.h>
#include <mock/runtime_mock.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <genericable/com_huawei_matata_notification_client_subscribe/1.0.0/cplusplus/subscribe.hpp>
#include <genericable/com_huawei_matata_conf_subscription_client_append/1.0.0/cplusplus/append.hpp>
#include <genericable/com_huawei_matata_conf_client_get/1.0.0/cplusplus/get.hpp>
#include <genericable/com_huawei_matata_conf_client_download/1.0.0/cplusplus/download.hpp>
#include <genericable/com_huawei_matata_notification_client_consume/1.0.0/cplusplus/consume.hpp>

using namespace ::Fit;
using namespace ::Fit::Configuration;
using namespace ::Fit::Framework::Annotation;
using namespace ::testing;

namespace {
/**
 * mock for matata::conf::subscription::client::append
 */
class ConfSubscriptionClientAppendMock {
public:
    MOCK_METHOD3(Invoke, FitCode(ContextObj ctx,
        const Fit::string *subscription, const Fit::vector<Fit::string> *paths));
};

/**
 * mock for matata::notification::client::subscribe
 */
class NotificationClientSubscribeMock {
public:
    MOCK_METHOD4(Invoke, FitCode(ContextObj ctx,
        const Fit::string *consumer, const Fit::string *topic, const Fit::string *fitableId));
};

/**
 * mock for matata::conf::client::get
 */
class ConfClientGetMock {
public:
    MOCK_METHOD3(Invoke, FitCode(ContextObj ctx,
        const Fit::string *key, Fit::string **result));
};

/**
 * mock for matata::conf::client::download
 */
class ConfClientDownloadMock {
public:
    MOCK_METHOD3(Invoke, FitCode(ContextObj ctx,
        const Fit::string *key, Fit::map<Fit::string, Fit::string> **result));
};
}

class ConfigurationClient_Test : public testing::Test {
public:
    void SetUp() override
    {
        groupName_ = "group";
        consumerName_ = "consumer";
        runtimeMock_.SetUp();
        client_ = std::make_shared<ConfigurationClientV2>("", groupName_, consumerName_);

        const char *defaultFitableId = "default";
        GenericableConfiguration clientSubscribeConfig;
        clientSubscribeConfig.SetGenericId(matata::notification::client::subscribe::GENERIC_ID);
        clientSubscribeConfig.SetDefaultFitableId(defaultFitableId);
        runtimeMock_.SetGenericableConfig(clientSubscribeConfig);

        GenericableConfiguration clientAppendConfig;
        clientAppendConfig.SetGenericId(matata::conf::subscription::client::append::GENERIC_ID);
        clientAppendConfig.SetDefaultFitableId(defaultFitableId);
        runtimeMock_.SetGenericableConfig(clientAppendConfig);

        GenericableConfiguration clientGetConfig;
        clientGetConfig.SetGenericId(matata::conf::client::get::GENERIC_ID);
        clientGetConfig.SetDefaultFitableId(defaultFitableId);
        runtimeMock_.SetGenericableConfig(clientGetConfig);

        GenericableConfiguration clientDownloadConfig;
        clientDownloadConfig.SetGenericId(matata::conf::client::download::GENERIC_ID);
        clientDownloadConfig.SetDefaultFitableId(defaultFitableId);
        runtimeMock_.SetGenericableConfig(clientDownloadConfig);

        GenericableConfiguration clientConsumeConfig;
        clientConsumeConfig.SetGenericId(matata::notification::client::consume::GENERIC_ID);
        clientConsumeConfig.SetDefaultFitableId("consume_cpp");
        runtimeMock_.SetGenericableConfig(clientConsumeConfig);

        runtimeMock_.RegisterFitable(std::function<FitCode(ContextObj, const Fit::string *, const Fit::string *,
                const Fit::string *)>(std::bind(&NotificationClientSubscribeMock::Invoke, &clientSubscribeMock_,
                std::placeholders::_1, std::placeholders::_2, std::placeholders::_3, std::placeholders::_4)),
            matata::notification::client::subscribe::GENERIC_ID,
            defaultFitableId);

        runtimeMock_.RegisterFitable(
            std::function<FitCode(ContextObj, const Fit::string *, const Fit::vector<Fit::string> *)>(
                std::bind(&ConfSubscriptionClientAppendMock::Invoke, &clientAppendMock_,
                    std::placeholders::_1, std::placeholders::_2, std::placeholders::_3)),
            matata::conf::subscription::client::append::GENERIC_ID,
            defaultFitableId);

        runtimeMock_.RegisterFitable(std::function<FitCode(ContextObj, const Fit::string *, Fit::string **)>(
            std::bind(&ConfClientGetMock::Invoke, &clientGetMock_,
                std::placeholders::_1, std::placeholders::_2, std::placeholders::_3)),
            matata::conf::client::get::GENERIC_ID,
            defaultFitableId);

        runtimeMock_.RegisterFitable(
            std::function<FitCode(ContextObj, const Fit::string *, Fit::map<Fit::string, Fit::string> **)>(
                std::bind(&ConfClientDownloadMock::Invoke, &clientDownloadMock_,
                    std::placeholders::_1, std::placeholders::_2, std::placeholders::_3)),
            matata::conf::client::download::GENERIC_ID,
            defaultFitableId);
    }

    void TearDown() override
    {
        runtimeMock_.TearDown();
    }

protected:
    std::shared_ptr<ConfigurationClientV2> client_;
    RuntimeMock runtimeMock_;
    Fit::string groupName_;
    Fit::string consumerName_;

    NotificationClientSubscribeMock clientSubscribeMock_;
    ConfSubscriptionClientAppendMock clientAppendMock_;
    ConfClientGetMock clientGetMock_;
    ConfClientDownloadMock clientDownloadMock_;
};

TEST_F(ConfigurationClient_Test, should_return_false_when_IsSubscribed_given_not_exist_generic_id)
{
    bool result = client_->IsSubscribed("xxx");
    EXPECT_THAT(result, Eq(false));
}

TEST_F(ConfigurationClient_Test, should_return_success_when_Subscribe_given_generic_id_no_subscribed)
{
    Fit::string subscribedKey = "xxx";
    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_OK));
    EXPECT_CALL(clientAppendMock_, Invoke(_, Pointee(Eq(groupName_)), Pointee(Contains(Eq(subscribedKey)))))
        .WillOnce(Return(FIT_OK));

    ConfigurationClientV2::ConfigSubscribePathCallback cb;
    auto result = client_->Subscribe(subscribedKey, cb);

    EXPECT_THAT(result, Eq(FIT_OK));
}

TEST_F(ConfigurationClient_Test,
    should_return_false_when_Subscribe_given_generic_id_no_subscribed_and_subscribe_success_and_append_failed)
{
    Fit::string subscribedKey = "xxx";
    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_OK));
    EXPECT_CALL(clientAppendMock_, Invoke(_, Pointee(Eq(groupName_)), Pointee(Contains(Eq(subscribedKey)))))
        .WillOnce(Return(FIT_ERR_PARAM));

    ConfigurationClientV2::ConfigSubscribePathCallback cb;
    auto result = client_->Subscribe(subscribedKey, cb);

    EXPECT_THAT(result, Eq(FIT_ERR_PARAM));
}

TEST_F(ConfigurationClient_Test, should_return_false_when_Subscribe_given_subscribe_fail)
{
    Fit::string subscribedKey = "xxx";
    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_ERR_PARAM));

    ConfigurationClientV2::ConfigSubscribePathCallback cb;
    auto result = client_->Subscribe(subscribedKey, cb);

    EXPECT_THAT(result, Eq(FIT_ERR_PARAM));
}

TEST_F(ConfigurationClient_Test, should_return_success_when_Subscribe_node_given_generic_id_no_subscribed)
{
    Fit::string subscribedKey = "xxx";
    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_OK));
    EXPECT_CALL(clientAppendMock_, Invoke(_, Pointee(Eq(groupName_)), Pointee(Contains(Eq(subscribedKey)))))
        .WillOnce(Return(FIT_OK));

    ConfigurationClientV2::ConfigSubscribeNodeCallback cb;
    auto result = client_->Subscribe(subscribedKey, cb);

    EXPECT_THAT(result, Eq(FIT_OK));
}

TEST_F(ConfigurationClient_Test,
    should_return_false_when_Subscribe_node_given_generic_id_no_subscribed_and_subscribe_success_and_append_failed)
{
    Fit::string subscribedKey = "xxx";
    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_OK));
    EXPECT_CALL(clientAppendMock_, Invoke(_, Pointee(Eq(groupName_)), Pointee(Contains(Eq(subscribedKey)))))
        .WillOnce(Return(FIT_ERR_PARAM));

    ConfigurationClientV2::ConfigSubscribeNodeCallback cb;
    auto result = client_->Subscribe(subscribedKey, cb);

    EXPECT_THAT(result, Eq(FIT_ERR_PARAM));
}

TEST_F(ConfigurationClient_Test, should_return_false_when_Subscribe_node_given_subscribe_fail)
{
    Fit::string subscribedKey = "xxx";
    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_ERR_PARAM));

    ConfigurationClientV2::ConfigSubscribeNodeCallback cb;
    auto result = client_->Subscribe(subscribedKey, cb);

    EXPECT_THAT(result, Eq(FIT_ERR_PARAM));
}

TEST_F(ConfigurationClient_Test, should_get_value_when_Get_given_exist_key)
{
    Fit::string key = "xxx";
    Fit::string resultValue = "result";
    EXPECT_CALL(clientGetMock_,
        Invoke(_, Pointee(Eq(key)), (_)))
        .WillOnce(DoAll(SetArgPointee<2>(&resultValue), Return(FIT_OK)));

    Fit::string value;
    auto result = client_->Get(key, value);

    EXPECT_THAT(result, Eq(FIT_OK));
    EXPECT_THAT(value, Eq(resultValue));
}

TEST_F(ConfigurationClient_Test, should_return_fail_when_Get_given_exist_key_and_null_result)
{
    Fit::string key = "xxx";
    EXPECT_CALL(clientGetMock_,
        Invoke(_, Pointee(Eq(key)), _))
        .WillOnce(Return(FIT_ERR_PARAM));

    Fit::string value;
    auto result = client_->Get(key, value);

    EXPECT_THAT(result, Eq(FIT_ERR_NOT_FOUND));
}

namespace testing {
namespace internal {
bool operator==(const ItemValue &l, const ItemValue &r)
{
    return l.value == r.value && l.key == r.key;
}
}
}

TEST_F(ConfigurationClient_Test, should_return_items_when_Download_given_valid_key)
{
    Fit::string key = "xxx";
    Fit::map<Fit::string, Fit::string> mockValues = {
        {"1", "1"},
        {"2", "2"}
    };

    EXPECT_CALL(clientDownloadMock_,
        Invoke(_, Pointee(Eq(key)), _))
        .WillOnce(DoAll(SetArgPointee<2>(&mockValues), Return(FIT_OK)));

    ItemValueSet value;
    auto result = client_->Download(key, value);

    EXPECT_THAT(result, Eq(FIT_OK));
    EXPECT_THAT(value.size(), Eq(mockValues.size()));
    EXPECT_THAT(value, Contains(ItemValue {"1", "1"}));
    EXPECT_THAT(value, Contains(ItemValue {"2", "2"}));
}

TEST_F(ConfigurationClient_Test, should_return_ok_when_Download_given_valid_key_and_null_result)
{
    Fit::string key = "xxx";
    EXPECT_CALL(clientDownloadMock_,
        Invoke(_, Pointee(Eq(key)), _))
        .WillOnce(Return(FIT_OK));

    ItemValueSet value;
    auto result = client_->Download(key, value);

    EXPECT_THAT(result, Eq(FIT_OK));
}

TEST_F(ConfigurationClient_Test, should_return_fail_when_Download_given_download_error)
{
    Fit::string key = "xxx";
    EXPECT_CALL(clientDownloadMock_,
        Invoke(_, Pointee(Eq(key)), _))
        .WillOnce(Return(FIT_ERR_PARAM));

    ItemValueSet value;
    auto result = client_->Download(key, value);

    EXPECT_THAT(result, Eq(FIT_ERR_PARAM));
}

TEST_F(ConfigurationClient_Test, should_notify_item_child_value_changed_when_consume_changed)
{
    Fit::string subscribedKey = "xxx";

    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_OK));
    EXPECT_CALL(clientAppendMock_, Invoke(_, Pointee(Eq(groupName_)), Pointee(Contains(Eq(subscribedKey)))))
        .WillOnce(Return(FIT_OK));
    Fit::map<Fit::string, Fit::string> mockValues = {
        {"1", "1"},
        {"2", "2"}
    };
    EXPECT_CALL(clientDownloadMock_,
        Invoke(_, Pointee(Eq(subscribedKey)), _))
        .WillOnce(DoAll(SetArgPointee<2>(&mockValues), Return(FIT_OK)));

    ConfigurationClientV2::ConfigSubscribePathCallback cb = [&subscribedKey](
        const Fit::string &key, ItemValueSet &items) {
        EXPECT_THAT(Fit::to_std_string(key), StartsWith(Fit::to_std_string(subscribedKey)));
        EXPECT_THAT(items, Contains(ItemValue {"1", "1"}));
        EXPECT_THAT(items, Contains(ItemValue {"2", "2"}));
    };
    auto subscribeResult = client_->Subscribe(subscribedKey, cb);
    matata::notification::client::consume consume;
    Fit::bytes buffer = subscribedKey;
    auto consumeRet = consume(&groupName_, &buffer);

    EXPECT_THAT(subscribeResult, Eq(FIT_OK));
    EXPECT_THAT(consumeRet, Eq(FIT_OK));
}

TEST_F(ConfigurationClient_Test, should_notify_item_value_changed_when_consume_changed)
{
    Fit::string subscribedKey = "xxx";

    EXPECT_CALL(clientSubscribeMock_,
        Invoke(_, Pointee(Eq(consumerName_)), Pointee(Eq(groupName_)), Pointee(Eq("consume_cpp"))))
        .WillOnce(Return(FIT_OK));
    EXPECT_CALL(clientAppendMock_, Invoke(_, Pointee(Eq(groupName_)), Pointee(Contains(Eq(subscribedKey)))))
        .WillOnce(Return(FIT_OK));

    Fit::string getResultValue = "result";
    EXPECT_CALL(clientGetMock_,
        Invoke(_, Pointee(Eq(subscribedKey)), (_)))
        .WillOnce(DoAll(SetArgPointee<2>(&getResultValue), Return(FIT_OK)));

    ConfigurationClientV2::ConfigSubscribeNodeCallback cb = [&subscribedKey, &getResultValue](
        const Fit::string &key, const Fit::string &value) {
        EXPECT_THAT(key, Eq(subscribedKey));
        EXPECT_THAT(value, Eq(getResultValue));
    };
    auto subscribeResult = client_->Subscribe(subscribedKey, cb);
    matata::notification::client::consume consume;
    Fit::bytes buffer = subscribedKey;
    auto consumeRet = consume(&groupName_, &buffer);

    EXPECT_THAT(subscribeResult, Eq(FIT_OK));
    EXPECT_THAT(consumeRet, Eq(FIT_OK));
}