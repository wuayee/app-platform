/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/10
*/
#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "fit_code.h"
#include "gmock/gmock.h"
#define private public
#include <registry_server/registry_server_memory/subscriber/fit_subscriber_repository_memory_node_sync.h>

using namespace Fit::Registry;
class FitSubscriberRepositoryMemoryNodeSyncTest : public ::testing::Test {
public:

    void SetUp() override
    {
        key_.generic_id = "test_gid";
        key_.generic_version = "1.0.0";
        key_.fitable_id = "test_fid";

        listener_.fitable_id = "listener_fid";
        listener_.address.id = "127.0.0.1:8866";
        listener_.address.ip = "127.0.0.1";
        listener_.address.port = 8866;
        listener_.address.protocol = Fit::fit_protocol_type::GRPC;
        listener_.address.formats.push_back(Fit::fit_format_type::JSON);
    }

    void TearDown() override
    {
    }
public:
    fit_fitable_key_t key_;
    listener_t listener_;
};

TEST_F(FitSubscriberRepositoryMemoryNodeSyncTest,
    should_return_success_when_save_listener_given_key_listener_and_min_service_in_time_5)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    FitSubscriptionNodeSyncPtr subscriptionRepo = std::make_shared<FitSubscriberRepositoryMemoryNodeSync>(5, 1);
    int32_t actualInsertRet = subscriptionRepo->Add(key_, listener_);
    std::this_thread::sleep_for(std::chrono::milliseconds(1));

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
}

TEST_F(FitSubscriberRepositoryMemoryNodeSyncTest, should_return_success_when_save_listener_given_key_and_listener)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    FitSubscriptionNodeSyncPtr subscriptionRepo = std::make_shared<FitSubscriberRepositoryMemoryNodeSync>(1, 10000);
    int32_t actualInsertRet = subscriptionRepo->Add(key_, listener_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
}

TEST_F(FitSubscriberRepositoryMemoryNodeSyncTest,
    should_return_success_when_save_listener_given_key_listener_and_min_service_in_time_1)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedRemoveRet = REGISTRY_SUCCESS;

    // when
    int32_t ret = REGISTRY_SUCCESS;
    FitSubscriptionNodeSyncPtr subscriptionRepo = FitSubscriptionNodeSyncPtrFactory::Create();
    int32_t actualInsertRet = subscriptionRepo->Add(key_, listener_);
    int32_t actualRemoveRet = subscriptionRepo->Remove(key_, listener_);

    // then
    EXPECT_EQ(actualInsertRet, expectedInsertRet);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
}

TEST_F(FitSubscriberRepositoryMemoryNodeSyncTest,
    should_return_fail_when_sync_to_other_given_param_and_invoke_fail)
{
    // given
    int32_t expectedSyncToOtherRegistryRet = FIT_ERR_FAIL;
    FitSubscriberRepositoryMemoryNodeSync fitSubscriberRepositoryMemoryNodeSync(100, 100);

    std::shared_ptr<Fit::vector<FitSubscriberRepositoryMemoryNodeSync::SyncSubscriptionServiceInner>>
        syncSubscriptionServicesPtr
        = std::make_shared<Fit::vector<FitSubscriberRepositoryMemoryNodeSync::SyncSubscriptionServiceInner>>();
    FitSubscriberRepositoryMemoryNodeSync::SyncSubscriptionServiceInner syncSubscriptionService {};
    syncSubscriptionService.fitable.genericId = key_.generic_id;
    syncSubscriptionService.fitable.genericVersion = key_.generic_version;
    syncSubscriptionService.fitable.fitId = key_.fitable_id;
    syncSubscriptionService.listenerAddress.host = listener_.address.ip;
    syncSubscriptionService.listenerAddress.port = listener_.address.port;
    syncSubscriptionService.listenerAddress.protocol = static_cast<int32_t>(listener_.address.protocol);
    syncSubscriptionService.listenerAddress.id = listener_.address.id;
    syncSubscriptionService.listenerAddress.environment = listener_.address.environment;
    for (const auto& format : listener_.address.formats) {
        syncSubscriptionService.listenerAddress.formats.push_back(static_cast<int32_t>(format));
    }
    syncSubscriptionService.callbackFitId = listener_.fitable_id;
    syncSubscriptionService.operateType = 0;
    syncSubscriptionServicesPtr->push_back(syncSubscriptionService);

    Fit::RegistryInfo::FlatAddress address;
    address.host = "127.0.0.1";
    address.port = 8888;
    address.workerId = "127.0.0.1:8888";
    address.protocol = Fit::fit_protocol_type::RSOCKET;
    address.formats = {Fit::fit_format_type::JSON};
    address.environment = "debug";

    // when
    int32_t ret = fitSubscriberRepositoryMemoryNodeSync.SyncToOtherRegistry(syncSubscriptionServicesPtr, address);

    // then
    EXPECT_EQ(ret, expectedSyncToOtherRegistryRet);
}
