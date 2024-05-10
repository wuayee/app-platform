/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <fit/internal/registry/repository/fit_subscription_memory_repository.h>
#include <fit/internal/registry/repository/fit_subscription_repository_decorator.h>
#include <registry_server/core/service/fit_subscription_service.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;
using namespace Fit::Registry;
class FitSubscriptionServiceTest : public ::testing::Test {
public:
    void SetUp() override
    {
        auto subscription_repository = FitSubscriptionMemoryRepositoryFactory::Create(
            FitSubscriptionRepositoryDecoratorFactory::Create(
                fit_subscription_repository_factory::Create()
            ),
        FitSubscriptionNodeSyncPtrFactory::Create());

        subscriptionService_ = std::make_shared<fit_subscription_service>(subscription_repository);
        nullRepoSubscriptionService_ = std::make_shared<fit_subscription_service>(nullptr);

        fitableKey_.generic_id = "test_genericable_id";
        fitableKey_.fitable_id = "test_fitable_id";
        fitableKey_.generic_version = "test_genericable_version";

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
    }

    void TearDown() override
    {
    }
public:
    fit_subscription_service_ptr subscriptionService_{};
    fit_subscription_service_ptr nullRepoSubscriptionService_{};
    fit_fitable_key_t fitableKey_;
    listener_t defaultListener_;
    listener_t defaultListener2_;
};

TEST_F(FitSubscriptionServiceTest, should_return_invalid_result_when_crud_given_null_repo)
{
    // given
    db_subscription_entry_t resultSubscriptionEntry;

    // when
    int32_t insertResult = nullRepoSubscriptionService_->insert_subscription_entry(fitableKey_, defaultListener_);
    int32_t removeResult = nullRepoSubscriptionService_->remove_subscription_entry(fitableKey_, defaultListener_);
    int32_t syncInsertResult = nullRepoSubscriptionService_->SyncInsertSubscriptionEntry(fitableKey_, defaultListener_);
    int32_t syncRemoveResult = nullRepoSubscriptionService_->SyncRemoveSubscriptionEntry(fitableKey_, defaultListener_);

    int32_t querySubscriptionEntryResult
        = nullRepoSubscriptionService_->query_subscription_entry(fitableKey_,
                                                                 defaultListener_,
                                                                 resultSubscriptionEntry);
    db_subscription_set subscriptionSet
        = nullRepoSubscriptionService_->query_subscription_set(fitableKey_);
    db_subscription_set allSubscription = nullRepoSubscriptionService_->query_all_subscriptions();
    listener_set queryListenerSet = nullRepoSubscriptionService_->query_listener_set(fitableKey_);

    // then
    EXPECT_EQ(insertResult, FIT_ERR_FAIL);
    EXPECT_EQ(removeResult, FIT_ERR_FAIL);
    EXPECT_EQ(syncInsertResult, FIT_ERR_FAIL);
    EXPECT_EQ(syncRemoveResult, FIT_ERR_FAIL);
    EXPECT_EQ(querySubscriptionEntryResult, FIT_ERR_FAIL);
    EXPECT_EQ(subscriptionSet.empty(), true);
    EXPECT_EQ(allSubscription.empty(), true);
    EXPECT_EQ(queryListenerSet.empty(), true);
}

TEST_F(FitSubscriptionServiceTest, should_return_subscription_entry_when_insert_and_query_given_key_and_listener)
{
    // given
    db_subscription_entry_t resultSubscriptionEntry;

    // when
    int32_t insertResult = subscriptionService_->insert_subscription_entry(fitableKey_, defaultListener_);
    int32_t querySubscriptionEntryResult
        = subscriptionService_->query_subscription_entry(fitableKey_, defaultListener_, resultSubscriptionEntry);

    // then
    EXPECT_EQ(insertResult, FIT_ERR_SUCCESS);
    EXPECT_EQ(querySubscriptionEntryResult, FIT_ERR_SUCCESS);
    EXPECT_EQ(resultSubscriptionEntry.fitable_key.generic_id, fitableKey_.generic_id);
    EXPECT_EQ(resultSubscriptionEntry.fitable_key.fitable_id, fitableKey_.fitable_id);
    EXPECT_EQ(resultSubscriptionEntry.listener.address.id, defaultListener_.address.id);
    EXPECT_EQ(resultSubscriptionEntry.listener.address.ip, defaultListener_.address.ip);
    EXPECT_EQ(resultSubscriptionEntry.listener.address.port, defaultListener_.address.port);
    EXPECT_EQ(static_cast<int32_t>(resultSubscriptionEntry.listener.address.protocol),
        static_cast<int32_t>(defaultListener_.address.protocol));
}

TEST_F(FitSubscriptionServiceTest,
    should_return_subscription_entry_when_sync_insert_and_query_given_key_and_listener)
{
    // given
    db_subscription_entry_t resultSubscriptionEntry;

    // when
    int32_t insertResult = subscriptionService_->SyncInsertSubscriptionEntry(fitableKey_, defaultListener_);
    int32_t querySubscriptionEntryResult
        = subscriptionService_->query_subscription_entry(fitableKey_, defaultListener_, resultSubscriptionEntry);

    // then
    EXPECT_EQ(insertResult, FIT_ERR_SUCCESS);
    EXPECT_EQ(querySubscriptionEntryResult, FIT_ERR_SUCCESS);
    EXPECT_EQ(resultSubscriptionEntry.fitable_key.generic_id, fitableKey_.generic_id);
    EXPECT_EQ(resultSubscriptionEntry.fitable_key.fitable_id, fitableKey_.fitable_id);
    EXPECT_EQ(resultSubscriptionEntry.listener.address.id, defaultListener_.address.id);
    EXPECT_EQ(resultSubscriptionEntry.listener.address.ip, defaultListener_.address.ip);
    EXPECT_EQ(resultSubscriptionEntry.listener.address.port, defaultListener_.address.port);
    EXPECT_EQ(static_cast<int32_t>(resultSubscriptionEntry.listener.address.protocol),
        static_cast<int32_t>(defaultListener_.address.protocol));
}

TEST_F(FitSubscriptionServiceTest, should_return_empty_when_sync_insert_remove_and_query_given_key_and_listener)
{
    // given
    db_subscription_entry_t resultSubscriptionEntry;

    // when
    int32_t insertResult = subscriptionService_->SyncInsertSubscriptionEntry(fitableKey_, defaultListener_);
    int32_t removeResult = subscriptionService_->SyncRemoveSubscriptionEntry(fitableKey_, defaultListener_);
    int32_t querySubscriptionEntryResult
        = subscriptionService_->query_subscription_entry(fitableKey_, defaultListener_, resultSubscriptionEntry);

    // then
    EXPECT_EQ(insertResult, FIT_ERR_SUCCESS);
    EXPECT_EQ(removeResult, FIT_ERR_SUCCESS);
    EXPECT_EQ(querySubscriptionEntryResult, FIT_ERR_FAIL);
}

TEST_F(FitSubscriptionServiceTest, should_return_empty_when_insert_and_query_all_given_empty)
{
    // given
    db_subscription_entry_t resultSubscriptionEntry;

    // when
    int32_t insertResult = subscriptionService_->SyncInsertSubscriptionEntry(fitableKey_, defaultListener_);
    db_subscription_set queryAllSubscriptionSet = subscriptionService_->query_all_subscriptions();

    // then
    EXPECT_EQ(insertResult, FIT_ERR_SUCCESS);
    ASSERT_EQ(queryAllSubscriptionSet.empty(), false);
    EXPECT_EQ(queryAllSubscriptionSet.front().fitable_key.generic_id, fitableKey_.generic_id);
    EXPECT_EQ(queryAllSubscriptionSet.front().fitable_key.fitable_id, fitableKey_.fitable_id);
    EXPECT_EQ(queryAllSubscriptionSet.front().listener.address.id, defaultListener_.address.id);
    EXPECT_EQ(queryAllSubscriptionSet.front().listener.address.ip, defaultListener_.address.ip);
    EXPECT_EQ(queryAllSubscriptionSet.front().listener.address.port, defaultListener_.address.port);
    EXPECT_EQ(static_cast<int32_t>(queryAllSubscriptionSet.front().listener.address.protocol),
        static_cast<int32_t>(defaultListener_.address.protocol));
}


TEST_F(FitSubscriptionServiceTest, should_return_empty_when_insert_and_query_subscription_given_fitable)
{
    // given
    db_subscription_entry_t resultSubscriptionEntry;

    // when
    int32_t insertResult = subscriptionService_->SyncInsertSubscriptionEntry(fitableKey_, defaultListener_);
    db_subscription_set querySubscriptionSet = subscriptionService_->query_subscription_set(fitableKey_);

    // then
    EXPECT_EQ(insertResult, FIT_ERR_SUCCESS);
    ASSERT_EQ(querySubscriptionSet.empty(), false);
    EXPECT_EQ(querySubscriptionSet.front().fitable_key.generic_id, fitableKey_.generic_id);
    EXPECT_EQ(querySubscriptionSet.front().fitable_key.fitable_id, fitableKey_.fitable_id);
    EXPECT_EQ(querySubscriptionSet.front().listener.address.id, defaultListener_.address.id);
    EXPECT_EQ(querySubscriptionSet.front().listener.address.ip, defaultListener_.address.ip);
    EXPECT_EQ(querySubscriptionSet.front().listener.address.port, defaultListener_.address.port);
    EXPECT_EQ(static_cast<int32_t>(querySubscriptionSet.front().listener.address.protocol),
        static_cast<int32_t>(defaultListener_.address.protocol));
}

TEST_F(FitSubscriptionServiceTest, should_return_listener_set_when_insert_and_query_listener_given_fitable)
{
    // given
    // when
    int32_t insertResult = subscriptionService_->SyncInsertSubscriptionEntry(fitableKey_, defaultListener_);
    listener_set listenerSet = subscriptionService_->query_listener_set(fitableKey_);

    // then
    EXPECT_EQ(insertResult, FIT_ERR_SUCCESS);
    ASSERT_EQ(listenerSet.empty(), false);
    EXPECT_EQ(listenerSet.front().address.id, defaultListener_.address.id);
    EXPECT_EQ(listenerSet.front().address.ip, defaultListener_.address.ip);
    EXPECT_EQ(listenerSet.front().address.port, defaultListener_.address.port);
    EXPECT_EQ(static_cast<int32_t>(listenerSet.front().address.protocol),
        static_cast<int32_t>(defaultListener_.address.protocol));
}