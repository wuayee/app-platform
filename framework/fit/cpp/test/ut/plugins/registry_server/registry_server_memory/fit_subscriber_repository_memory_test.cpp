/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/10
*/
#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <registry_server/registry_server_memory/subscriber/fit_subscriber_repository_memory.h>
#include <fit/internal/registry/repository/fit_subscription_repository_decorator.h>
#include <fit/internal/registry/repository/fit_subscription_node_sync.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

class FitSubscriberRepositoryMemoryTest : public ::testing::Test {
public:

    void SetUp() override
    {
        defaultKey_.generic_id = "test_gid";
        defaultKey_.generic_version = "test_g_version";
        defaultKey_.fitable_id = "test_fid";

        defaultListener_.fitable_id = "listener_fid";
        defaultListener_.address.ip = "127.0.0.1";
        defaultListener_.address.port = 8866;
        defaultListener_.address.protocol = Fit::fit_protocol_type::GRPC;
        defaultListener_.address.id = "127.0.0.1:8866";

        defaultListener2_.fitable_id = "listener_fid2";
        defaultListener2_.address.ip = "127.0.0.1";
        defaultListener2_.address.port = 8866;
        defaultListener2_.address.protocol = Fit::fit_protocol_type::GRPC;
        defaultListener2_.address.id = "127.0.0.1:8866";

        subscriptionRepository_ = FitSubscriptionMemoryRepositoryFactory::Create(
            FitSubscriptionRepositoryDecoratorFactory::Create(
                fit_subscription_repository_factory::Create()
            ),
            FitSubscriptionNodeSyncPtrFactory::Create());
        emptySubscriptionRepository_ = FitSubscriptionMemoryRepositoryFactory::Create(nullptr, nullptr);
    }

    void TearDown() override
    {
    }
public:
    fit_fitable_key_t defaultKey_;
    listener_t defaultListener_;
    listener_t defaultListener2_;
    FitSubscriptionMemoryRepositoryPtr subscriptionRepository_;
    FitSubscriptionMemoryRepositoryPtr emptySubscriptionRepository_;
};

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_listener_given_key_listener_and_empty_repo)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet
        = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
}

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_same_listener_given_key_listener_and_empty_repo)
{
    // given
    auto curListener = defaultListener_;
    curListener.syncCount = 2;

    // when
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, curListener);

    // then
    EXPECT_EQ(actualInsertRet, REGISTRY_SUCCESS);
    EXPECT_EQ(actualInsertRet2, REGISTRY_SUCCESS);
}

TEST_F(FitSubscriberRepositoryMemoryTest, should_return_success_when_save_same_listener_given_key_and_listener)
{
    // given
    // when
    int32_t actualInsertRet = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);

    // then
    EXPECT_EQ(actualInsertRet, REGISTRY_SUCCESS);
    EXPECT_EQ(actualInsertRet2, REGISTRY_SUCCESS);
}

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_and_remove_listener_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualRemoveRet = subscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
}

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_and_query_subscription_set_given_key_and_listener)
{
    // given
    db_subscription_set expectedSubscriptionSet;
    db_subscription_entry_t subscriptionEntry;
    subscriptionEntry.fitable_key = defaultKey_;
    subscriptionEntry.listener = defaultListener2_;
    expectedSubscriptionSet.push_back(subscriptionEntry);

    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = subscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);
    db_subscription_set actualSubscriptionSet = subscriptionRepository_->query_subscription_set(defaultKey_);

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

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_and_query_subscription_set_given_key_listener_and_empty_repo)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;

    db_subscription_set expectedSubscriptionSet;
    db_subscription_entry_t subscriptionEntry;
    subscriptionEntry.fitable_key = defaultKey_;
    subscriptionEntry.listener = defaultListener2_;
    expectedSubscriptionSet.push_back(subscriptionEntry);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = emptySubscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);
    db_subscription_set actualSubscriptionSet = emptySubscriptionRepository_->query_subscription_set(defaultKey_);

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

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_and_query_listener_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    int32_t expectedQueryRet = REGISTRY_SUCCESS;

    listener_set expectedListenerSet;
    expectedListenerSet.push_back(defaultListener2_);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = subscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);
    listener_set actualListenerSet = subscriptionRepository_->query_listener_set(defaultKey_);

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

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_success_when_save_and_query_subscription_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    int32_t expectedQueryRet = REGISTRY_SUCCESS;

    db_subscription_entry_t expectedSubscriptionEntry;
    expectedSubscriptionEntry.fitable_key = defaultKey_;
    expectedSubscriptionEntry.listener = defaultListener2_;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = subscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = subscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);
    db_subscription_entry_t actualSubscriptionEntry;
    int32_t actualQueryRet =
        subscriptionRepository_->query_subscription_entry(defaultKey_, defaultListener2_, actualSubscriptionEntry);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.ip, expectedSubscriptionEntry.listener.address.ip);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.port, expectedSubscriptionEntry.listener.address.port);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.protocol, expectedSubscriptionEntry.listener.address.protocol);
    EXPECT_EQ(actualSubscriptionEntry.listener.fitable_id, expectedSubscriptionEntry.listener.fitable_id);
    EXPECT_EQ(actualSubscriptionEntry.listener.address.id, expectedSubscriptionEntry.listener.address.id);
}

TEST_F(FitSubscriberRepositoryMemoryTest, should_return_success_when_save_and_query_all_subscription_given_empty)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;
    int32_t expectedQueryRet = REGISTRY_SUCCESS;

    db_subscription_set expectedQueryAllRet;
    db_subscription_entry_t expectedSubscriptionEntry;
    expectedSubscriptionEntry.fitable_key = defaultKey_;
    expectedSubscriptionEntry.listener = defaultListener2_;
    expectedQueryAllRet.push_back(expectedSubscriptionEntry);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = emptySubscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);
    db_subscription_set actualQueryAllRet = emptySubscriptionRepository_->query_all_subscriptions();

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    ASSERT_EQ(actualQueryAllRet.size(), expectedQueryAllRet.size());
    EXPECT_EQ(actualQueryAllRet.front().listener.address.ip, expectedQueryAllRet.front().listener.address.ip);
    EXPECT_EQ(actualQueryAllRet.front().listener.address.port, expectedQueryAllRet.front().listener.address.port);
    EXPECT_EQ(actualQueryAllRet.front().listener.address.protocol,
              expectedQueryAllRet.front().listener.address.protocol);
    EXPECT_EQ(actualQueryAllRet.front().listener.fitable_id, expectedQueryAllRet.front().listener.fitable_id);
    EXPECT_EQ(actualQueryAllRet.front().listener.address.id, expectedQueryAllRet.front().listener.address.id);
}


TEST_F(FitSubscriberRepositoryMemoryTest, should_return_listener_ids_when_save_and_query_listeners_given_empty)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;

    Fit::unordered_set<Fit::string> expectedQueryAllRet;
    expectedQueryAllRet.insert(defaultListener_.address.id);
    expectedQueryAllRet.insert(defaultListener2_.address.id);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    Fit::unordered_set<Fit::string> actualQueryAllRet = emptySubscriptionRepository_->query_all_listener_ids();

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(expectedQueryAllRet.size(), 1);
    ASSERT_EQ(actualQueryAllRet.size(), expectedQueryAllRet.size());
    EXPECT_EQ(*actualQueryAllRet.begin(), *expectedQueryAllRet.begin());
}

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_listener_ids_when_save_2_and_remove_1_and_query_listeners_given_empty)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;

    Fit::unordered_set<Fit::string> expectedQueryAllRet;
    expectedQueryAllRet.insert(defaultListener_.address.id);
    expectedQueryAllRet.insert(defaultListener2_.address.id);

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = emptySubscriptionRepository_->remove_subscription_entry(defaultKey_, defaultListener_);
    Fit::unordered_set<Fit::string> actualQueryAllRet = emptySubscriptionRepository_->query_all_listener_ids();

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(expectedQueryAllRet.size(), 1);
    ASSERT_EQ(actualQueryAllRet.size(), expectedQueryAllRet.size());
    EXPECT_EQ(*actualQueryAllRet.begin(), *expectedQueryAllRet.begin());
}

TEST_F(FitSubscriberRepositoryMemoryTest,
    should_return_listener_ids_empty_when_save_and_remove_by_ids_and_query_listeners_given_empty)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    int32_t actualRemoveRet = emptySubscriptionRepository_->remove_subscription_entry({defaultListener_.address.id});
    Fit::unordered_set<Fit::string> actualQueryAllRet = emptySubscriptionRepository_->query_all_listener_ids();

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualInsertRet2, expectedInsertRet2);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    ASSERT_EQ(actualQueryAllRet.empty(), true);
}