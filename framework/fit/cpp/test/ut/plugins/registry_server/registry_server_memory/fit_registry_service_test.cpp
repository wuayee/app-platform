/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <registry_server/core/service/fit_registry_service.h>
#include <registry_server_memory/fitable/fitable_memory_repository.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/registry/repository/fit_registry_repository_decorator.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>
#include <fit/fit_log.h>
#include <fit/stl/string.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;
using namespace Fit::Registry;

class FitRegistryServiceTest : public ::testing::Test {
public:
    void SetUp() override
    {
        // fitable的memory适配层
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

        auto fitableChangedPublisher = std::make_shared<fit_data_changed_publisher<db_service_info_t>>();
        fitableService_ = std::make_shared<fit_registry_service>(serviceRepository, fitableChangedPublisher);
        fitableService_->Start();

        fitable_.fitable_id = "test_fitable_id";
        fitable_.fitable_version = "test_fitable_version";
        fitable_.generic_id = "test_genericable_id";
        fitable_.generic_version = "genericable_version";

        syncFitable_.fitable_id = "202954b6897a4e2da49aa29ac572f5fb";
        syncFitable_.fitable_version = "1.0.0";
        syncFitable_.generic_id = fit::hakuna::kernel::registry::server::synchronizeFitService::GENERIC_ID;
        syncFitable_.generic_version = "1.0.0";

        auto address = FitSystemPropertyUtils::Address();
        address_.ip = address.host;
        address_.port = address.port;
        address_.protocol = static_cast<Fit::fit_protocol_type>(address.protocol);
        address_.id = address.id;

        Fit::RegistryInfo::Application application;
        application.name = "app_name";
        application.nameVersion = "app_name_version";
        serviceInfo_.fitable = fitable_;
        serviceInfo_.timeoutSeconds = 60;
        serviceInfo_.application = application;
        serviceInfo_.addresses.push_back(address_);

        syncServiceInfo_.fitable = syncFitable_;
        syncServiceInfo_.timeoutSeconds = 60;
        syncServiceInfo_.application = application;
        syncServiceInfo_.addresses.emplace_back(address_);

        worker_.address = address_;
        worker_.id = "127.0.0.1:8080";
    }

    void TearDown() override
    {
        fitableService_->Stop();
    }
public:
    fit_registry_service_ptr fitableService_{};
    fit_service_instance_t serviceInfo_;
    fit_service_instance_t syncServiceInfo_;
    Fit::fit_address address_;
    fit_worker_info_t worker_;
    Fit::fitable_id fitable_;
    Fit::fitable_id syncFitable_;
};

TEST_F(FitRegistryServiceTest, should_return_false_when_register_services_given_null_repo)
{
    // given

    // when
    auto fitableService = std::make_shared<fit_registry_service>(nullptr, nullptr);
    bool actualRegisterResult = fitableService->register_services(
        fit_service_instance_set {serviceInfo_});

    // then
    EXPECT_EQ(actualRegisterResult, false);
}

TEST_F(FitRegistryServiceTest, should_return_true_when_register_services_given_service_and_address)
{
    // given

    // when
    bool actualRegisterResult = fitableService_->register_services(
        fit_service_instance_set {serviceInfo_});

    // then
    EXPECT_EQ(actualRegisterResult, true);
}


TEST_F(FitRegistryServiceTest, should_return_empty_when_get_all_services_given_null_repo)
{
    // given

    // when
    auto fitableService = std::make_shared<fit_registry_service>(nullptr, nullptr);
    db_service_set result = fitableService->get_all_services();

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_empty_when_get_all_services_given_repo)
{
    // given

    // when
    db_service_set result = fitableService_->get_all_services();

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_false_when_unregister_services_given_null_repo)
{
    // given

    // when
    auto fitableService = std::make_shared<fit_registry_service>(nullptr, nullptr);
    bool actualRegisterResult = fitableService->unregister_services(
        fit_service_instance_set {serviceInfo_}, worker_);

    // then
    EXPECT_EQ(actualRegisterResult, false);
}

TEST_F(FitRegistryServiceTest, should_return_true_when_unregister_services_given_service_and_address)
{
    // given

    // when
    bool actualRegisterResult = fitableService_->unregister_services(
        fit_service_instance_set {serviceInfo_}, worker_);

    // then
    EXPECT_EQ(actualRegisterResult, true);
}

TEST_F(FitRegistryServiceTest, should_return_empty_when_register_and_get_services_given_key)
{
    // given
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;

    // when
    auto fitableService = std::make_shared<fit_registry_service>(nullptr, nullptr);
    bool actualRegisterResult = fitableService->register_services(
        fit_service_instance_set {serviceInfo_});

    db_service_set serviceSet = fitableService->get_services(key);

    // then
    EXPECT_EQ(actualRegisterResult, false);
    EXPECT_EQ(serviceSet.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_service_set_when_register_and_get_services_given_key)
{
    // given
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;

    db_service_set expectedServiceSet;
    db_service_info_t serviceInfo;
    serviceInfo.service = serviceInfo_;
    expectedServiceSet.emplace_back(serviceInfo);

    // when
    bool actualRegisterResult1 = fitableService_->register_services(
        fit_service_instance_set {syncServiceInfo_});
    bool actualRegisterResult2 = fitableService_->register_services(
        fit_service_instance_set {serviceInfo_});

    db_service_set serviceSet = fitableService_->get_services(key);

    // then
    EXPECT_EQ(actualRegisterResult1, true);
    EXPECT_EQ(actualRegisterResult2, true);
    ASSERT_EQ(expectedServiceSet.size(), serviceSet.size());
    EXPECT_EQ(expectedServiceSet.front().service.fitable.generic_id, serviceSet.front().service.fitable.generic_id);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.generic_version,
              serviceSet.front().service.fitable.generic_version);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.fitable_id, serviceSet.front().service.fitable.fitable_id);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.fitable_version,
              serviceSet.front().service.fitable.fitable_version);
    EXPECT_EQ(expectedServiceSet.front().service.application.name, serviceSet.front().service.application.name);
    EXPECT_EQ(expectedServiceSet.front().service.application.nameVersion,
              serviceSet.front().service.application.nameVersion);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.size(), serviceSet.front().service.addresses.size());
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().id, serviceSet.front().service.addresses.front().id);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().ip, serviceSet.front().service.addresses.front().ip);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().port,
              serviceSet.front().service.addresses.front().port);
    EXPECT_EQ(static_cast<int32_t>(expectedServiceSet.front().service.addresses.front().protocol),
        static_cast<int32_t>(serviceSet.front().service.addresses.front().protocol));
}

TEST_F(FitRegistryServiceTest, should_return_address_set_when_register_and_get_services_given_fitable)
{
    // given
    address_set expectedAddressSet;
    expectedAddressSet.emplace_back(address_);

    // when
    bool actualRegisterResult1 = fitableService_->register_services(
        fit_service_instance_set {syncServiceInfo_});
    bool actualRegisterResult2 = fitableService_->register_services(
        fit_service_instance_set {serviceInfo_});

    address_set addressSet = fitableService_->get_addresses(fitable_);

    // then
    EXPECT_EQ(actualRegisterResult1, true);
    EXPECT_EQ(actualRegisterResult2, true);
    ASSERT_EQ(expectedAddressSet.size(), addressSet.size());
    EXPECT_EQ(expectedAddressSet.size(), addressSet.size());
    EXPECT_EQ(expectedAddressSet.front().id, addressSet.front().id);
    EXPECT_EQ(expectedAddressSet.front().ip, addressSet.front().ip);
    EXPECT_EQ(expectedAddressSet.front().port, addressSet.front().port);
    EXPECT_EQ(static_cast<int32_t>(expectedAddressSet.front().protocol),
              static_cast<int32_t>(addressSet.front().protocol));
}

TEST_F(FitRegistryServiceTest,
    should_return_empty_when_register_and_get_services_given_null_repo_and_genericable_id)
{
    // given
    // when
    auto fitableService = std::make_shared<fit_registry_service>(nullptr, nullptr);
    bool actualRegisterResult = fitableService->register_services(
        fit_service_instance_set {serviceInfo_});

    db_service_set serviceSet = fitableService->get_services_by_generic_id(fitable_.generic_id);

    // then
    EXPECT_EQ(actualRegisterResult, false);
    EXPECT_EQ(serviceSet.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_service_set_when_register_and_get_services_given_genericable_id)
{
    // given
    // when
    db_service_set serviceSet = fitableService_->get_services_by_generic_id(fitable_.generic_id);

    // then
    ASSERT_EQ(serviceSet.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_empty_when_register_remove_and_get_services_given_address)
{
    // given
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;

    db_service_set expectedServiceSet;
    db_service_info_t serviceInfo;
    serviceInfo.service = serviceInfo_;
    expectedServiceSet.emplace_back(serviceInfo);

    // when
    bool actualRegisterResult1 = fitableService_->register_services(
        fit_service_instance_set {syncServiceInfo_});
    bool actualRegisterResult2 = fitableService_->register_services(
        fit_service_instance_set {serviceInfo_});
    fitableService_->Remove(address_);
    db_service_set serviceSet = fitableService_->get_services(key);

    // then
    EXPECT_EQ(actualRegisterResult1, true);
    EXPECT_EQ(actualRegisterResult2, true);
    EXPECT_EQ(serviceSet.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_invalid_value_when_sync_save_and_remove_given_null_repo)
{
    // given
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;
    Fit::fit_address address = address_;
    db_service_info_t serviceInput;
    serviceInput.service = serviceInfo_;
    // when
    auto fitableService = std::make_shared<fit_registry_service>(nullptr, nullptr);
    int32_t syncSaveRet = fitableService->SyncSave(serviceInput);
    int32_t syncRemoveRet = fitableService->SyncRemove(serviceInput);
    int32_t syncSaveServiceSetRet = fitableService->SyncSave(db_service_set {serviceInput});
    int32_t syncRemoveServiceSetRet = fitableService->SyncRemove(db_service_set {serviceInput});
    db_service_set queryResult = fitableService->QueryService(key, address);
    fitableService->Remove(address_);
    fitableService->TimeoutCallback(db_service_set {serviceInput});

    // then
    EXPECT_EQ(syncSaveRet, FIT_ERR_FAIL);
    EXPECT_EQ(syncRemoveRet, FIT_ERR_FAIL);
    EXPECT_EQ(syncSaveServiceSetRet, FIT_ERR_FAIL);
    EXPECT_EQ(syncRemoveServiceSetRet, FIT_ERR_FAIL);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_service_when_sync_save_and_query_given_key)
{
    // given
    db_service_info_t serviceSyncFitableInput;
    serviceSyncFitableInput.service = syncServiceInfo_;
    db_service_info_t serviceInput;
    serviceInput.service = serviceInfo_;

    db_service_set expectedServiceSet;
    expectedServiceSet.emplace_back(serviceInput);

    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;
    Fit::fit_address address = address_;

    // when
    int32_t syncSaveRet1 = fitableService_->SyncSave(serviceSyncFitableInput);
    int32_t syncSaveRet2 = fitableService_->SyncSave(serviceInput);
    db_service_set serviceSet = fitableService_->QueryService(key, address);

    // then
    EXPECT_EQ(syncSaveRet1, FIT_ERR_SUCCESS);
    EXPECT_EQ(syncSaveRet2, FIT_ERR_SUCCESS);
    ASSERT_EQ(expectedServiceSet.size(), serviceSet.size());
    EXPECT_EQ(expectedServiceSet.front().service.fitable.generic_id, serviceSet.front().service.fitable.generic_id);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.generic_version,
              serviceSet.front().service.fitable.generic_version);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.fitable_id, serviceSet.front().service.fitable.fitable_id);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.fitable_version,
              serviceSet.front().service.fitable.fitable_version);
    EXPECT_EQ(expectedServiceSet.front().service.application.name, serviceSet.front().service.application.name);
    EXPECT_EQ(expectedServiceSet.front().service.application.nameVersion,
              serviceSet.front().service.application.nameVersion);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.size(), serviceSet.front().service.addresses.size());
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().id, serviceSet.front().service.addresses.front().id);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().ip, serviceSet.front().service.addresses.front().ip);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().port,
              serviceSet.front().service.addresses.front().port);
    EXPECT_EQ(static_cast<int32_t>(expectedServiceSet.front().service.addresses.front().protocol),
        static_cast<int32_t>(serviceSet.front().service.addresses.front().protocol));
}

TEST_F(FitRegistryServiceTest, should_return_service_when_sync_save_remove_and_query_given_key)
{
    // given
    db_service_info_t serviceSyncFitableInput;
    serviceSyncFitableInput.service = syncServiceInfo_;
    db_service_info_t serviceInput;
    serviceInput.service = serviceInfo_;

    db_service_set expectedServiceSet;
    expectedServiceSet.emplace_back(serviceInput);

    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;
    Fit::fit_address address = address_;

    // when
    int32_t syncSaveRet1 = fitableService_->SyncSave(serviceSyncFitableInput);
    int32_t syncSaveRet2 = fitableService_->SyncSave(serviceInput);
    int32_t syncRemoveRet = fitableService_->SyncRemove(serviceInput);
    db_service_set serviceSet = fitableService_->QueryService(key, address);

    // then
    EXPECT_EQ(syncSaveRet1, FIT_ERR_SUCCESS);
    EXPECT_EQ(syncSaveRet2, FIT_ERR_SUCCESS);
    EXPECT_EQ(syncRemoveRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(serviceSet.empty(), true);
}

TEST_F(FitRegistryServiceTest, should_return_service_when_sync_save_and_query_services_given_key)
{
    // given
    db_service_info_t serviceSyncFitableInput;
    serviceSyncFitableInput.service = syncServiceInfo_;
    db_service_info_t serviceInput;
    serviceInput.service = serviceInfo_;

    db_service_set expectedServiceSet;
    expectedServiceSet.emplace_back(serviceInput);

    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;
    Fit::fit_address address = address_;

    // when
    int32_t syncSaveRet1 = fitableService_->SyncSave(db_service_set {serviceSyncFitableInput});
    int32_t syncSaveRet2 = fitableService_->SyncSave(db_service_set {serviceInput});
    db_service_set serviceSet = fitableService_->QueryService(key, address);

    // then
    EXPECT_EQ(syncSaveRet1, FIT_ERR_SUCCESS);
    EXPECT_EQ(syncSaveRet2, FIT_ERR_SUCCESS);
    ASSERT_EQ(expectedServiceSet.size(), serviceSet.size());
    EXPECT_EQ(expectedServiceSet.front().service.fitable.generic_id, serviceSet.front().service.fitable.generic_id);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.generic_version,
              serviceSet.front().service.fitable.generic_version);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.fitable_id, serviceSet.front().service.fitable.fitable_id);
    EXPECT_EQ(expectedServiceSet.front().service.fitable.fitable_version,
              serviceSet.front().service.fitable.fitable_version);
    EXPECT_EQ(expectedServiceSet.front().service.application.name, serviceSet.front().service.application.name);
    EXPECT_EQ(expectedServiceSet.front().service.application.nameVersion,
              serviceSet.front().service.application.nameVersion);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.size(), serviceSet.front().service.addresses.size());
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().id, serviceSet.front().service.addresses.front().id);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().ip, serviceSet.front().service.addresses.front().ip);
    EXPECT_EQ(expectedServiceSet.front().service.addresses.front().port,
              serviceSet.front().service.addresses.front().port);
    EXPECT_EQ(static_cast<int32_t>(expectedServiceSet.front().service.addresses.front().protocol),
        static_cast<int32_t>(serviceSet.front().service.addresses.front().protocol));
}


TEST_F(FitRegistryServiceTest, should_return_service_when_sync_save_remove_and_query_service_given_key)
{
    // given
    db_service_info_t serviceSyncFitableInput;
    serviceSyncFitableInput.service = syncServiceInfo_;
    db_service_info_t serviceInput;
    serviceInput.service = serviceInfo_;

    db_service_set expectedServiceSet;
    expectedServiceSet.emplace_back(serviceInput);

    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitable_id;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;
    Fit::fit_address address = address_;

    // when
    int32_t syncSaveRet1 = fitableService_->SyncSave(db_service_set {serviceSyncFitableInput});
    int32_t syncSaveRet2 = fitableService_->SyncSave(db_service_set {serviceInput});
    int32_t syncRemoveRet = fitableService_->SyncRemove(db_service_set {serviceInput});
    db_service_set serviceSet = fitableService_->QueryService(key, address);

    // then
    EXPECT_EQ(syncSaveRet1, FIT_ERR_SUCCESS);
    EXPECT_EQ(syncSaveRet2, FIT_ERR_SUCCESS);
    EXPECT_EQ(syncRemoveRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(serviceSet.empty(), true);
}