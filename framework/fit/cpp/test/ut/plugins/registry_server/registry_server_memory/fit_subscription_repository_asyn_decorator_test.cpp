/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/10
*/
#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <registry_server/registry_server_memory/subscriber/fit_subscription_repository_asyn_decorator.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

class MockMySqlSubscriptionRepo : public fit_subscription_repository {
public:
    MOCK_METHOD0(Start, bool());
    MOCK_METHOD0(Stop, bool());
    MOCK_METHOD1(query_listener_set, listener_set(const fit_fitable_key_t &fitableKey));
    MOCK_METHOD1(query_subscription_set, db_subscription_set(const fit_fitable_key_t &fitableKey));
    MOCK_CONST_METHOD0(query_all_subscriptions, db_subscription_set());
    MOCK_CONST_METHOD3(query_subscription_entry,
                       int32_t(const fit_fitable_key_t &fitableKey,
                               const listener_t &listener, db_subscription_entry_t &resultSubscriptionEntry));
    MOCK_METHOD2(insert_subscription_entry, int32_t(const fit_fitable_key_t &fitable, const listener_t &listener));
    MOCK_METHOD2(remove_subscription_entry, int32_t(const fit_fitable_key_t &fitable, const listener_t &listener));
};

class FitSubscriptionRepositoryAsynDecoratorTest : public ::testing::Test {
public:

    void SetUp() override
    {
        key_.generic_id = "test_gid";
        key_.generic_version = "test_g_version";
        key_.fitable_id = "test_fid";

        listener_.fitable_id = "listener_fid";
        listener_.address.id = "127.0.0.1:8866";
        listener_.address.ip = "127.0.0.1";
        listener_.address.port = 8866;
        listener_.address.protocol = Fit::fit_protocol_type::GRPC;
    }

    void TearDown() override
    {
    }
public:
    fit_fitable_key_t key_;
    listener_t listener_;
};

TEST_F(FitSubscriptionRepositoryAsynDecoratorTest, should_return_success_when_save_listener_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    FitSubscriptionRepositoryDecoratorPtr subscriptionRepo = FitSubscriptionRepositoryDecoratorFactory::Create(nullptr);
    int32_t actualInsertRet = subscriptionRepo->insert_subscription_entry(key_, listener_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
}

TEST_F(FitSubscriptionRepositoryAsynDecoratorTest,
    should_return_success_when_save_and_remove_listener_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    // when
    int32_t ret = REGISTRY_SUCCESS;
    FitSubscriptionRepositoryDecoratorPtr subscriptionRepo = FitSubscriptionRepositoryDecoratorFactory::Create(nullptr);
    int32_t actualInsertRet = subscriptionRepo->insert_subscription_entry(key_, listener_);
    int32_t actualRemoveRet = subscriptionRepo->remove_subscription_entry(key_, listener_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
}

TEST_F(FitSubscriptionRepositoryAsynDecoratorTest,
    should_return_success_when_save_and_query_subscription_set_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    int32_t expectedQueryRet = REGISTRY_SUCCESS;

    listener_t listener2;
    listener2.fitable_id = "listener_fid2";
    listener2.address.id = "127.0.0.1:8866";
    listener2.address.ip = "127.0.0.1";
    listener2.address.port = 8866;
    listener2.address.protocol = Fit::fit_protocol_type::GRPC;

    db_subscription_set expectedSubscriptionSet;
    db_subscription_entry_t subscriptionEntry;
    subscriptionEntry.fitable_key = key_;
    subscriptionEntry.listener = listener2;
    expectedSubscriptionSet.push_back(subscriptionEntry);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    std::shared_ptr<MockMySqlSubscriptionRepo> mySqlRepo = std::make_shared<MockMySqlSubscriptionRepo>();
    EXPECT_CALL(*mySqlRepo, query_subscription_set(key_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(expectedSubscriptionSet));

    FitSubscriptionRepositoryDecoratorPtr subscriptionRepo =
        FitSubscriptionRepositoryDecoratorFactory::Create(mySqlRepo);
    int32_t actualInsertRet = subscriptionRepo->insert_subscription_entry(key_, listener_);
    int32_t actualInsertRet2 = subscriptionRepo->insert_subscription_entry(key_, listener2);
    int32_t actualRemoveRet = subscriptionRepo->remove_subscription_entry(key_, listener_);
    db_subscription_set actualSubscriptionSet = subscriptionRepo->query_subscription_set(key_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(actualSubscriptionSet.size(), expectedSubscriptionSet.size());
    EXPECT_EQ(actualSubscriptionSet.front().fitable_key.generic_id,
              expectedSubscriptionSet.front().fitable_key.generic_id);
    EXPECT_EQ(actualSubscriptionSet.front().fitable_key.generic_version,
              expectedSubscriptionSet.front().fitable_key.generic_version);
    EXPECT_EQ(actualSubscriptionSet.front().fitable_key.fitable_id,
              expectedSubscriptionSet.front().fitable_key.fitable_id);
    EXPECT_EQ(actualSubscriptionSet.front().listener.address.ip, expectedSubscriptionSet.front().listener.address.ip);
    EXPECT_EQ(actualSubscriptionSet.front().listener.address.port,
              expectedSubscriptionSet.front().listener.address.port);
    EXPECT_EQ(actualSubscriptionSet.front().listener.address.protocol,
              expectedSubscriptionSet.front().listener.address.protocol);
    EXPECT_EQ(actualSubscriptionSet.front().listener.fitable_id, expectedSubscriptionSet.front().listener.fitable_id);
    EXPECT_EQ(actualSubscriptionSet.front().listener.address.id, expectedSubscriptionSet.front().listener.address.id);
}

TEST_F(FitSubscriptionRepositoryAsynDecoratorTest,
    should_return_success_when_save_and_query_listener_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    int32_t expectedQueryRet = REGISTRY_SUCCESS;

    listener_t listener2;
    listener2.fitable_id = "listener_fid2";
    listener2.address.id = "127.0.0.1:8866";
    listener2.address.ip = "127.0.0.1";
    listener2.address.port = 8866;
    listener2.address.protocol = Fit::fit_protocol_type::GRPC;

    listener_set expectedListenerSet;
    expectedListenerSet.push_back(listener2);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    std::shared_ptr<MockMySqlSubscriptionRepo> mySqlRepo = std::make_shared<MockMySqlSubscriptionRepo>();
    EXPECT_CALL(*mySqlRepo, query_listener_set(key_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(expectedListenerSet));

    FitSubscriptionRepositoryDecoratorPtr subscriptionRepo =
        FitSubscriptionRepositoryDecoratorFactory::Create(mySqlRepo);
    int32_t actualInsertRet = subscriptionRepo->insert_subscription_entry(key_, listener_);
    int32_t actualInsertRet2 = subscriptionRepo->insert_subscription_entry(key_, listener2);
    int32_t actualRemoveRet = subscriptionRepo->remove_subscription_entry(key_, listener_);
    listener_set actualListenerSet = subscriptionRepo->query_listener_set(key_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(actualListenerSet.size(), expectedListenerSet.size());
    EXPECT_EQ(actualListenerSet.front().address.ip, expectedListenerSet.front().address.ip);
    EXPECT_EQ(actualListenerSet.front().address.port, expectedListenerSet.front().address.port);
    EXPECT_EQ(actualListenerSet.front().address.protocol, expectedListenerSet.front().address.protocol);
    EXPECT_EQ(actualListenerSet.front().fitable_id, expectedListenerSet.front().fitable_id);
    EXPECT_EQ(actualListenerSet.front().address.id, expectedListenerSet.front().address.id);
}

TEST_F(FitSubscriptionRepositoryAsynDecoratorTest,
    should_return_success_when_save_and_query_subscription_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    int32_t expectedQueryRet = REGISTRY_SUCCESS;

    listener_t listener2;
    listener2.fitable_id = "listener_fid2";
    listener2.address.id = "127.0.0.1:8866";
    listener2.address.ip = "127.0.0.1";
    listener2.address.port = 8866;
    listener2.address.protocol = Fit::fit_protocol_type::GRPC;

    db_subscription_entry_t expectedSubscriptionEntry;
    expectedSubscriptionEntry.fitable_key = key_;
    expectedSubscriptionEntry.listener = listener2;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    db_subscription_entry_t actualSubscriptionEntry;
    std::shared_ptr<MockMySqlSubscriptionRepo> mySqlRepo = std::make_shared<MockMySqlSubscriptionRepo>();
    EXPECT_CALL(*mySqlRepo, query_subscription_entry(key_, listener2, testing::_))
        .WillOnce(testing::DoAll(testing::SetArgReferee<2>(expectedSubscriptionEntry),
                                 testing::Return(expectedQueryRet)));

    FitSubscriptionRepositoryDecoratorPtr subscriptionRepo =
        FitSubscriptionRepositoryDecoratorFactory::Create(mySqlRepo);

    int32_t actualInsertRet = subscriptionRepo->insert_subscription_entry(key_, listener_);
    int32_t actualInsertRet2 = subscriptionRepo->insert_subscription_entry(key_, listener2);
    int32_t actualRemoveRet = subscriptionRepo->remove_subscription_entry(key_, listener_);
    int32_t actualQueryRet = subscriptionRepo->query_subscription_entry(key_, listener2, actualSubscriptionEntry);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(actualQueryRet, expectedQueryRet);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.ip, expectedSubscriptionEntry.listener.address.ip);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.port, expectedSubscriptionEntry.listener.address.port);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.protocol, expectedSubscriptionEntry.listener.address.protocol);
    EXPECT_EQ(actualSubscriptionEntry.listener.fitable_id, expectedSubscriptionEntry.listener.fitable_id);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.id, expectedSubscriptionEntry.listener.address.id);
}