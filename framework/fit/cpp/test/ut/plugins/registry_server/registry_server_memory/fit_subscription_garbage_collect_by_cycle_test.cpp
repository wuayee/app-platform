/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/18
 * Notes:       :
 */
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/registry/repository/fit_fitable_memory_repository.h>
#include <fit/internal/registry/repository/fit_registry_repository_decorator.h>
#include <registry_server/registry_server_memory/subscriber/include/fit_subscription_garbage_collect_by_cycle.h>
#include <registry_server_memory/fitable/fitable_memory_repository.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_notify_fitables/1.0.0/cplusplus/notifyFitables.hpp>
#include <fit/stl/unordered_set.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"
using namespace  Fit::Registry;
using namespace testing;
constexpr const char* NOTIFY_FITABLE_ID = "7847dac3feac4e549e85670341146b8a";
class FitSubscriptionGarbageCollectByCycleTest : public ::testing::Test {
public:

    void SetUp() override
    {
        notifyFitable_.fitable_id = "7847dac3feac4e549e85670341146b8a";
        notifyFitable_.fitable_version = "1.0.0";
        notifyFitable_.generic_id = fit::hakuna::kernel::registry::server::notifyFitables::GENERIC_ID;
        notifyFitable_.generic_version = "1.0.0";

        auto address = FitSystemPropertyUtils::Address();
        address_.ip = address.host;
        address_.port = address.port;
        address_.protocol = static_cast<Fit::fit_protocol_type>(address.protocol);
        address_.id = "listener_id1";

        Fit::RegistryInfo::Application application;
        application.name = "app_name";
        application.nameVersion = "app_name_version";
        serviceInfo_.fitable = notifyFitable_;
        serviceInfo_.timeoutSeconds = 60;
        serviceInfo_.application = application;
        serviceInfo_.addresses.push_back(address_);

        defaultKey_.generic_id = "test_gid";
        defaultKey_.generic_version = "test_g_version";
        defaultKey_.fitable_id = "test_fid";

        defaultListener2_.fitable_id = "listener_fid2";
        defaultListener2_.address.ip = "127.0.0.1";
        defaultListener2_.address.port = 8866;
        defaultListener2_.address.protocol = Fit::fit_protocol_type::GRPC;
        defaultListener2_.address.id = "127.0.0.1:8866";

        defaultListener_.fitable_id = "listener_fid";
        defaultListener_.address.ip = "127.0.0.1";
        defaultListener_.address.port = 8866;
        defaultListener_.address.protocol = Fit::fit_protocol_type::GRPC;
        defaultListener_.address.id = "127.0.0.1:8866";

        defaultListener3_.fitable_id = "listener_fid3";
        defaultListener3_.address.ip = "127.0.0.1";
        defaultListener3_.address.port = 8899;
        defaultListener3_.address.protocol = Fit::fit_protocol_type::GRPC;
        defaultListener3_.address.id = "127.0.0.1:8899";

        auto wrokerMemoryRepo = FitMemoryWorkerOperation::Create();
        auto addressMemoryRepo = FitMemoryAddressOperation::Create();
        auto fitableMemoryRepo = FitMemoryFitableOperation::Create();
        FitFitableMemoryRepositoryPtr newMemory = Fit::make_shared<FitableMemoryRepository>(
            wrokerMemoryRepo, addressMemoryRepo, fitableMemoryRepo);
        auto serviceRepository = FitRegistryMemoryRepositoryFactory::Create(
            FitRegistryRepositoryFactoryWithServiceRepository::Create(nullptr),
            FitableRegistryFitableNodeSyncPtrFacotry::Create(),
            nullptr,
            newMemory);

        emptySubscriptionRepository_ = FitSubscriptionMemoryRepositoryFactory::Create(nullptr, nullptr);
        subscriptionGarbageCollectByCycle_ = Fit::make_shared<FitSubscriptionGarbageCollectByCycle>(
            emptySubscriptionRepository_, serviceRepository, Fit::timer_instance());
        subscriptionGarbageCollectNullTimer_ = Fit::make_shared<FitSubscriptionGarbageCollectByCycle>(
            emptySubscriptionRepository_, serviceRepository, nullptr);

        listenersIds_ = {"listener_id1", "listener_id2"};
    }

    void TearDown() override
    {
    }
public:
    fit_fitable_key_t defaultKey_;
    listener_t defaultListener_;
    listener_t defaultListener2_;
    listener_t defaultListener3_;
    Fit::fitable_id notifyFitable_;
    Fit::fit_address address_;
    fit_service_instance_t serviceInfo_;
    Fit::shared_ptr<FitSubscriptionGarbageCollectByCycle> subscriptionGarbageCollectByCycle_ {};
    Fit::shared_ptr<FitSubscriptionGarbageCollectByCycle> subscriptionGarbageCollectNullTimer_ {};
    Fit::unordered_set<Fit::string> listenersIds_;
    FitSubscriptionMemoryRepositoryPtr emptySubscriptionRepository_ {};
};

TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_error_when_init_uninit_given_timer_null)
{
    // given
    int32_t expectedInitRet = FIT_ERR_FAIL;
    int32_t expectedUnInitRet = FIT_ERR_FAIL;

    // when
    int32_t actualInitRet = subscriptionGarbageCollectNullTimer_->Init();
    int32_t actualUnInitRet = subscriptionGarbageCollectNullTimer_->UnInit();

    // then
    EXPECT_EQ(actualInitRet, expectedInitRet);
    EXPECT_EQ(actualUnInitRet, expectedUnInitRet);
}

TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_ok_when_init_uninit_given_empty)
{
    // given
    int32_t expectedInitRet = FIT_OK;
    int32_t expectedUnInitRet = FIT_OK;

    // when
    int32_t actualInitRet = subscriptionGarbageCollectByCycle_->Init();
    int32_t actualUnInitRet = subscriptionGarbageCollectByCycle_->UnInit();

    // then
    EXPECT_EQ(actualInitRet, expectedInitRet);
    EXPECT_EQ(actualUnInitRet, expectedUnInitRet);
}

TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_ok_when_add_ids_given_ids)
{
    // given
    int32_t expectedAddRet = FIT_OK;

    // when
    int32_t actualAddRet = subscriptionGarbageCollectByCycle_->AddDyingListenerIds(listenersIds_);

    // then
    EXPECT_EQ(actualAddRet, expectedAddRet);
}

TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_ok_when_remove_ids_given_ids)
{
    // given
    int32_t expectedRemoveRet = FIT_OK;

    // when
    int32_t actualRemoveRet = subscriptionGarbageCollectByCycle_->RemoveDyingListenerIds(listenersIds_);

    // then
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
}
// 1. 第一轮存在，第二轮存在删除订阅信息
// 2. listener 不在线
TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_ok_when_update_same_ids_given_same_ids)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedUpdateRet = FIT_OK;
    int32_t expectedUpdateRet2 = FIT_OK;
    Fit::unordered_set<Fit::string> expectedQueryAllRet;
    expectedQueryAllRet.insert(defaultListener_.address.id);
    expectedQueryAllRet.insert(defaultListener2_.address.id);

    // when
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener2_);
    Fit::unordered_set<Fit::string> actualQueryAllRet = emptySubscriptionRepository_->query_all_listener_ids();

    int32_t actualUpdateRet = subscriptionGarbageCollectByCycle_->UpdateDyingListenerIds({defaultListener_});
    int32_t actualUpdateRet2 = subscriptionGarbageCollectByCycle_->UpdateDyingListenerIds({defaultListener_});

    Fit::unordered_set<Fit::string> actualQueryAllRetAfterUpdate =
        emptySubscriptionRepository_->query_all_listener_ids();

    // then
    EXPECT_EQ(expectedQueryAllRet.size(), 1);
    ASSERT_EQ(actualQueryAllRet.size(), expectedQueryAllRet.size());
    EXPECT_EQ(*actualQueryAllRet.begin(), *expectedQueryAllRet.begin());
    EXPECT_EQ(actualUpdateRet, expectedUpdateRet);
    EXPECT_EQ(actualUpdateRet2, expectedUpdateRet2);
    EXPECT_EQ(actualQueryAllRetAfterUpdate.empty(), true);
}

// 1. 第一轮存在，第二轮存在删除订阅信息
// 2. listener 不在线
TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_ok_when_update_same_ids_given_insert_diff_ids)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedUpdateRet = FIT_OK;
    int32_t expectedUpdateRet2 = FIT_OK;
    Fit::unordered_set<Fit::string> expectedQueryAllRet;
    expectedQueryAllRet.insert(defaultListener_.address.id);
    expectedQueryAllRet.insert(defaultListener3_.address.id);
    Fit::unordered_set<Fit::string> expectedQueryAllRetAfterUpdate {defaultListener3_.address.id};

    // when
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener3_);
    Fit::unordered_set<Fit::string> actualQueryAllRet = emptySubscriptionRepository_->query_all_listener_ids();

    int32_t actualUpdateRet = subscriptionGarbageCollectByCycle_->UpdateDyingListenerIds({defaultListener_});
    int32_t actualUpdateRet2 = subscriptionGarbageCollectByCycle_->UpdateDyingListenerIds({defaultListener_});

    Fit::unordered_set<Fit::string> actualQueryAllRetAfterUpdate =
        emptySubscriptionRepository_->query_all_listener_ids();

    // then
    EXPECT_EQ(expectedQueryAllRet.size(), 2);
    ASSERT_EQ(actualQueryAllRet.size(), expectedQueryAllRet.size());
    auto actualIt1 = actualQueryAllRet.begin();
    auto expectedIt1 = expectedQueryAllRet.begin();
    Fit::string actualValue1 = *actualIt1++;
    Fit::string expectedValue1 = *expectedIt1++;
    Fit::string actualValue2 = *actualIt1;
    Fit::string expectedValue2 = *expectedIt1;
    EXPECT_EQ((actualValue1 == expectedValue1 && actualValue2 == expectedValue2)
        || (actualValue1 == expectedValue2 && actualValue2 == expectedValue1), true);

    EXPECT_EQ(actualUpdateRet, expectedUpdateRet);
    EXPECT_EQ(actualUpdateRet2, expectedUpdateRet2);
    EXPECT_EQ(actualQueryAllRetAfterUpdate.size(), 1);
    EXPECT_EQ(*expectedQueryAllRetAfterUpdate.begin(), *actualQueryAllRetAfterUpdate.begin());
}

// 1. 第一轮存在，第二轮不存在不删除订阅信息
// 2. listener 不在线
TEST_F(FitSubscriptionGarbageCollectByCycleTest, should_return_ok_when_update_diff_ids_given_insert_diff_ids)
{
    // given
    int32_t expectedInsertRet = REGISTRY_SUCCESS;
    int32_t expectedInsertRet2 = REGISTRY_SUCCESS;
    int32_t expectedUpdateRet = FIT_OK;
    int32_t expectedUpdateRet2 = FIT_OK;
    Fit::unordered_set<Fit::string> expectedQueryAllRet;
    expectedQueryAllRet.insert(defaultListener_.address.id);
    expectedQueryAllRet.insert(defaultListener3_.address.id);

    // when
    int32_t actualInsertRet = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener_);
    int32_t actualInsertRet2 = emptySubscriptionRepository_->insert_subscription_entry(defaultKey_, defaultListener3_);
    Fit::unordered_set<Fit::string> actualQueryAllRet = emptySubscriptionRepository_->query_all_listener_ids();

    int32_t actualUpdateRet = subscriptionGarbageCollectByCycle_->UpdateDyingListenerIds({defaultListener_});
    int32_t actualUpdateRet2 = subscriptionGarbageCollectByCycle_->UpdateDyingListenerIds({defaultListener3_});

    Fit::unordered_set<Fit::string> actualQueryAllRetAfterUpdate =
        emptySubscriptionRepository_->query_all_listener_ids();

    // then
    EXPECT_EQ(expectedQueryAllRet.size(), 2);
    ASSERT_EQ(actualQueryAllRet.size(), expectedQueryAllRet.size());
    auto actualIt1 = actualQueryAllRet.begin();
    auto expectedIt1 = expectedQueryAllRet.begin();
    Fit::string actualValue1 = *actualIt1++;
    Fit::string expectedValue1 = *expectedIt1++;
    Fit::string actualValue2 = *actualIt1;
    Fit::string expectedValue2 = *expectedIt1;
    EXPECT_EQ((actualValue1 == expectedValue1 && actualValue2 == expectedValue2)
        || (actualValue1 == expectedValue2 && actualValue2 == expectedValue1), true);

    EXPECT_EQ(actualUpdateRet, expectedUpdateRet);
    EXPECT_EQ(actualUpdateRet2, expectedUpdateRet2);

    actualIt1 = actualQueryAllRetAfterUpdate.begin();
    actualValue1 = *actualIt1++;
    actualValue2 = *actualIt1;
    EXPECT_EQ((actualValue1 == expectedValue1 && actualValue2 == expectedValue2)
        || (actualValue1 == expectedValue2 && actualValue2 == expectedValue1), true);
}