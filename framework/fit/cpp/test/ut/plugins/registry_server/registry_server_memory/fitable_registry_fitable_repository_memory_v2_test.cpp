/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <fit/internal/registry/repository/fit_registry_memory_repository.h>
#include <fit/internal/registry/repository/fit_fitable_memory_repository.h>
#include <registry_server_memory/fitable/fitable_memory_repository.h>
#include <fit/internal/registry/repository/fit_registry_repository_decorator.h>
#include <fit/internal/fit_system_property_utils.h>
#include <registry_server/registry_server_memory/fitable/fitable_registry_fitable_repository_memory.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Registry;
class FitableRegistryFitableRepositoryMemoryV2Test : public ::testing::Test {
public:

    void SetUp() override
    {
        defaultServiceInfo_.start_time = 123456;
        Fit::fit_address defaultAddress;
        defaultAddress.ip = "127.0.0.1";
        defaultAddress.port = 8883;
        defaultAddress.protocol = Fit::fit_protocol_type::GRPC;
        defaultAddress.id = "default_worker_id";
        defaultServiceInfo_.service.addresses.emplace_back(defaultAddress);
        defaultServiceInfo_.service.fitable.generic_id = "test_gid";
        defaultServiceInfo_.service.fitable.fitable_id = "test_fid";
        defaultServiceInfo_.service.fitable.generic_version = "test_version";
        defaultServiceInfo_.service.fitable.fitable_version = "1.0.0";
        defaultServiceInfo_.service.application.name = "application_name";
        defaultServiceInfo_.service.application.nameVersion = "application_name_version";
        defaultServiceInfo_.service.timeoutSeconds = 100;

        addressRemove_.ip = "127.0.0.1";
        addressRemove_.port = 8883;
        addressRemove_.protocol = Fit::fit_protocol_type::GRPC;
        addressRemove_.id = "default_worker_id";

        syncServiceInfo_.start_time = 123456;
        syncServiceInfo_.service.fitable.generic_id =
            fit::hakuna::kernel::registry::server::synchronizeFitService::GENERIC_ID;
        syncServiceInfo_.service.fitable.fitable_id = "202954b6897a4e2da49aa29ac572f5fb";
        syncServiceInfo_.service.fitable.generic_version = "1.0.0";
        syncServiceInfo_.service.fitable.fitable_version = "1.0.0";
        auto address = FitSystemPropertyUtils::Address();
        Fit::fit_address syncAddress;
        syncAddress.ip = address.host;
        syncAddress.port = address.port;
        syncAddress.protocol = static_cast<Fit::fit_protocol_type>(address.protocol);
        syncAddress.id = address.id;
        syncServiceInfo_.service.addresses.emplace_back(syncAddress);
        syncServiceInfo_.service.application.name = "registry";
        syncServiceInfo_.service.application.nameVersion = "registry_version";
        syncServiceInfo_.service.timeoutSeconds = 100;

        auto wrokerMemoryRepo = FitMemoryWorkerOperation::Create();
        auto addressMemoryRepo = FitMemoryAddressOperation::Create();
        auto fitableMemoryRepo = FitMemoryFitableOperation::Create();
        fitableMemoryRepository_ = Fit::make_shared<FitableMemoryRepository>(
            wrokerMemoryRepo, addressMemoryRepo, fitableMemoryRepo);

        fitableNodeSyncPtr_ = FitableRegistryFitableNodeSyncPtrFacotry::Create();
        serviceRepository_ = FitRegistryRepositoryFactoryWithServiceRepository::Create(nullptr);
        fitableMemoryRepository_->Start();
        fitableNodeSyncPtr_->Start();
        serviceRepository_->Start();
    }

    void TearDown() override
    {
        fitableMemoryRepository_->Stop();
        fitableNodeSyncPtr_->Stop();
        serviceRepository_->Stop();
    }
public:
    db_service_info_t defaultServiceInfo_;
    db_service_info_t syncServiceInfo_;
    FitFitableMemoryRepositoryPtr fitableMemoryRepository_ {nullptr};
    FitableRegistryFitableNodeSyncPtr fitableNodeSyncPtr_ {nullptr};
    FitRegistryRepositoryDecoratorPtr serviceRepository_ {nullptr};
    Fit::fit_address addressRemove_;
};

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test, should_return_true_when_save_fit_service_given_fit_service)
{
    // given
    bool expectedRet = true;
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);

    // when
    bool ret = memoryRepo->Save(defaultServiceInfo_);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_save_fit_service_set_given_fit_service_set)
{
    // given
    bool expectedRet = true;
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);
    db_service_set serviceSet;
    serviceSet.push_back(defaultServiceInfo_);
    // when
    bool ret = memoryRepo->Save(serviceSet);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_save_service_and_query_given_fit_service_set_and_key)
{
    // given
    bool expectedRet = true;

    db_service_set serviceSet;
    serviceSet.push_back(defaultServiceInfo_);

    db_service_set syncServiceSet;
    syncServiceSet.push_back(syncServiceInfo_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    // when
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);

    memoryRepo->Save(syncServiceInfo_);
    bool ret = memoryRepo->Save(serviceSet);
    db_service_set actualServiceSet = memoryRepo->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    ASSERT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().id,
              expectedSet.begin()->service.addresses.front().id);
}

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_delete_and_query_given_fitable_and_address)
{
    // given
    bool expectedRet = true;

    db_service_set serviceSet;
    serviceSet.push_back(defaultServiceInfo_);

    db_service_set syncServiceSet;
    syncServiceSet.push_back(syncServiceInfo_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    Fit::fitable_id fitableRemove;
    fitableRemove.generic_id = "test_gid";
    fitableRemove.generic_version = "test_version";
    fitableRemove.fitable_id = "test_fid";

    db_service_set expectedSetAfterRemove;
    bool expectedRetRemove = true;

    // when
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);
    memoryRepo->Save(syncServiceInfo_);
    bool ret = memoryRepo->Save(serviceSet);

    db_service_set actualServiceSet = memoryRepo->Query(key);
    bool actualRemoveRet = memoryRepo->Remove(fitableRemove, addressRemove_);
    db_service_set actualServiceSetAfterRemove = memoryRepo->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().id,
              expectedSet.begin()->service.addresses.front().id);

    EXPECT_EQ(actualRemoveRet, expectedRetRemove);
    EXPECT_EQ(actualServiceSetAfterRemove.size(), expectedSetAfterRemove.size());
}

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_sync_save_fit_service_given_fit_service)
{
    // given
    int32_t expectedRet = REGISTRY_SUCCESS;
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);

    // when
    bool ret = memoryRepo->SyncSave(defaultServiceInfo_);

    // then
    EXPECT_EQ(ret, expectedRet);
}


TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_sync_save_fit_service_set_given_fit_service_set)
{
    // given
    int32_t expectedRet = REGISTRY_SUCCESS;
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);
    db_service_set serviceSet;
    serviceSet.push_back(defaultServiceInfo_);

    // when
    bool ret = memoryRepo->SyncSave(serviceSet);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_sync_delete_and_query_given_fitable_and_address)
{
    // given
    int32_t expectedRet = REGISTRY_SUCCESS;

    db_service_set serviceSet;
    serviceSet.push_back(defaultServiceInfo_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    Fit::fitable_id fitableRemove;
    fitableRemove.generic_id = "test_gid";
    fitableRemove.generic_version = "test_version";
    fitableRemove.fitable_id = "test_fid";

    db_service_info_t serviceInfoRemove;
    serviceInfoRemove.service.addresses.emplace_back(addressRemove_);
    serviceInfoRemove.service.fitable = fitableRemove;
    db_service_set expectedSetAfterRemove;
    int32_t expectedRetRemove = REGISTRY_SUCCESS;

    // when
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);
    bool ret = memoryRepo->SyncSave(serviceSet);

    db_service_set actualServiceSet = memoryRepo->Query(key);
    bool actualRemoveRet = memoryRepo->SyncRemove(serviceInfoRemove);
    db_service_set actualServiceSetAfterRemove = memoryRepo->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().id,
              expectedSet.begin()->service.addresses.front().id);

    EXPECT_EQ(actualRemoveRet, expectedRetRemove);
    EXPECT_EQ(actualServiceSetAfterRemove.size(), expectedSetAfterRemove.size());
}

TEST_F(FitableRegistryFitableRepositoryMemoryV2Test,
    should_return_true_when_sync_remove_by_address_and_query_given_address)
{
    // given
    int32_t expectedRet = REGISTRY_SUCCESS;

    db_service_set serviceSet;
    serviceSet.push_back(defaultServiceInfo_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    db_service_set expectedSetAfterRemove;
    db_service_set expectedRemoveSets;
    expectedRemoveSets.push_back(defaultServiceInfo_);

    // when
    FitRegistryMemoryRepositoryPtr memoryRepo =
        FitRegistryMemoryRepositoryFactory::Create(serviceRepository_,
                                                   fitableNodeSyncPtr_, nullptr, fitableMemoryRepository_);
    bool ret = memoryRepo->SyncSave(serviceSet);

    db_service_set actualServiceSet = memoryRepo->Query(key);
    db_service_set actualRemoveRet = memoryRepo->Remove(addressRemove_);
    db_service_set actualServiceSetAfterRemove = memoryRepo->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().id,
              expectedSet.begin()->service.addresses.front().id);

    EXPECT_EQ(actualRemoveRet.size(), expectedRemoveSets.size());
    EXPECT_EQ(actualRemoveRet.begin()->start_time, expectedRemoveSets.begin()->start_time);
    EXPECT_EQ(actualRemoveRet.begin()->service.fitable.generic_id,
              expectedRemoveSets.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualRemoveRet.begin()->service.fitable.generic_version,
              expectedRemoveSets.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualRemoveRet.begin()->service.fitable.fitable_id,
              expectedRemoveSets.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualRemoveRet.begin()->service.addresses.size(), expectedRemoveSets.begin()->service.addresses.size());
    EXPECT_EQ(actualRemoveRet.begin()->service.addresses.front().ip,
              expectedRemoveSets.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualRemoveRet.begin()->service.addresses.front().port,
              expectedRemoveSets.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualRemoveRet.begin()->service.addresses.front().protocol),
              int(expectedRemoveSets.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualRemoveRet.begin()->service.addresses.front().id,
              expectedRemoveSets.begin()->service.addresses.front().id);
    EXPECT_EQ(actualServiceSetAfterRemove.size(), expectedSetAfterRemove.size());
}