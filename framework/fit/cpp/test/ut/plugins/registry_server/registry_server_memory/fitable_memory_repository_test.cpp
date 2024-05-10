/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <registry_server/registry_server_memory/fitable/fitable_memory_repository.h>
#include <fit/fit_log.h>
#include <fit/stl/string.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using namespace Fit::Registry;

class FitableMemoryRepositoryTest : public ::testing::Test {
public:

    void SetUp() override
    {
        addressOperate_ = std::make_shared<FitMemoryAddressOperation>();
        fitableOperate_ = std::make_shared<FitMemoryFitableOperation>();
        workerOperate_ = std::make_shared<FitMemoryWorkerOperation>();

        workerId_ = "test_worker_id";

        address_.ip = "127.0.0.1";
        address_.port = 8080;
        address_.protocol = Fit::fit_protocol_type::GRPC;
        address_.id = workerId_;

        application_.name = "test_app_name";
        application_.nameVersion = "test_app_version";
        application2_.name = "test_app_name2";
        application2_.nameVersion = "test_app_version2";
        fitable_.fitableId = "test_fitableId";
        fitable_.genericableId = "test_genericable_id";
        fitable_.genericableVersion = "test_genericable_version";

        serviceInfo_.service.addresses.emplace_back(address_);
        serviceInfo_.service.application = application_;
        serviceInfo_.service.fitable.fitable_id = fitable_.fitableId;
        serviceInfo_.service.fitable.fitable_version = fitable_.fitableVersion;
        serviceInfo_.service.fitable.generic_id = fitable_.genericableId;
        serviceInfo_.service.fitable.generic_version = fitable_.genericableVersion;
        serviceInfo_.service.timeoutSeconds = 20;
        serviceInfo2_ = serviceInfo_;
        serviceInfo2_.service.application = application2_;
    }

    void TearDown() override
    {
    }
public:
    std::shared_ptr<FitMemoryAddressOperation> addressOperate_;
    std::shared_ptr<FitMemoryFitableOperation> fitableOperate_;
    std::shared_ptr<FitMemoryWorkerOperation> workerOperate_;
    Fit::fit_address address_;
    Fit::RegistryInfo::Application application_;
    Fit::RegistryInfo::Application application2_;
    Fit::RegistryInfo::Fitable fitable_;
    Fit::string workerId_;
    db_service_info_t serviceInfo_;
    db_service_info_t serviceInfo2_;
};

void AddressCompare(const Fit::fit_address& addressBase, const Fit::fit_address& addressIn)
{
    EXPECT_EQ(addressBase.ip, addressIn.ip);
    EXPECT_EQ(addressBase.port, addressIn.port);
    EXPECT_EQ(addressBase.protocol, addressIn.protocol);
    EXPECT_EQ(addressBase.id, addressIn.id);
}

void ApplicationCompare(const Fit::RegistryInfo::Application& applicationBase,
                        const Fit::RegistryInfo::Application& applicationIn)
{
    EXPECT_EQ(applicationBase.name, applicationIn.name);
    EXPECT_EQ(applicationBase.nameVersion, applicationIn.nameVersion);
}

void FitableCompare(const Fit::fitable_id& fitableBase, const Fit::fitable_id& fitableIn)
{
    EXPECT_EQ(fitableBase.fitable_id, fitableIn.fitable_id);
    EXPECT_EQ(fitableBase.fitable_version, fitableIn.fitable_version);
    EXPECT_EQ(fitableBase.generic_id, fitableIn.generic_id);
    EXPECT_EQ(fitableBase.generic_version, fitableIn.generic_version);
}

TEST_F(FitableMemoryRepositoryTest, should_return_false_when_save_fitable_given_worker_nullptr)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(nullptr, nullptr, nullptr);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    // then
    EXPECT_EQ(saveRet, false);
}

TEST_F(FitableMemoryRepositoryTest, should_return_false_when_save_service_given_worker_address_and_fitable)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    // then
    EXPECT_EQ(saveRet, true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_false_when_save_services_given_worker_address_and_fitable)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    db_service_set services;
    services.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(services);

    // then
    EXPECT_EQ(saveRet, true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_query_given_invalid_operate)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitableId;
    key.generic_id = fitable_.genericableId;
    key.generic_version = fitable_.genericableVersion;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, nullptr);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(key);

    // then
    EXPECT_EQ(saveRet, false);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_query_given_invalid_key)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    fit_fitable_key_t key;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(key);

    // then
    EXPECT_EQ(saveRet, true);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_service_when_query_given_fitable_key)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitableId;
    key.generic_id = fitable_.genericableId;
    key.generic_version = fitable_.genericableVersion;

    db_service_set expectedServices;
    expectedServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(key);

    // then
    EXPECT_EQ(saveRet, true);
    ASSERT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedServices.front().service.addresses.size());
    AddressCompare(queryResult.front().service.addresses.front(), expectedServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedServices.front().service.fitable);
}

TEST_F(FitableMemoryRepositoryTest,
    should_return_service_when_save_same_worker_info_and_diff_application_service_and_query_given_fitable_key)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    fit_fitable_key_t key;
    key.fitable_id = fitable_.fitableId;
    key.generic_id = fitable_.genericableId;
    key.generic_version = fitable_.genericableVersion;

    db_service_set expectedServices;
    expectedServices.push_back(serviceInfo2_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    bool saveRet2 = fitableMemoryRepository->Save(serviceInfo2_);

    auto queryResult = fitableMemoryRepository->Query(key);

    // then
    EXPECT_EQ(saveRet, true);
    EXPECT_EQ(saveRet2, true);
    ASSERT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedServices.front().service.addresses.size());

    AddressCompare(queryResult.front().service.addresses.front(), expectedServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedServices.front().service.fitable);

    EXPECT_EQ(fitableOperate_->Query(application_).empty(), true);
    EXPECT_EQ(fitableOperate_->Query(application2_).empty(), false);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_query_given_fitable_address_and_invalid_operator)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    Fit::fitable_id fitable;
    Fit::fit_address address;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, nullptr);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(fitable, address);

    // then
    EXPECT_EQ(saveRet, false);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_service_when_query_given_fitable_and_address)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    Fit::fitable_id fitable = serviceInfo_.service.fitable;
    Fit::fit_address address = serviceInfo_.service.addresses.front();

    db_service_set expectedServices;
    expectedServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(fitable, address);

    // then
    EXPECT_EQ(saveRet, true);
    ASSERT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedServices.front().service.addresses.size());

    AddressCompare(queryResult.front().service.addresses.front(), expectedServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedServices.front().service.fitable);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_query_given_service)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    db_service_info_t service;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(service);

    // then
    EXPECT_EQ(saveRet, true);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_service_when_query_given_service)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_FAIL;
    db_service_info_t service = serviceInfo_;

    db_service_set expectedServices;
    expectedServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->Query(service);

    // then
    EXPECT_EQ(saveRet, true);
    ASSERT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedServices.front().service.addresses.size());

    AddressCompare(queryResult.front().service.addresses.front(), expectedServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedServices.front().service.fitable);

    EXPECT_EQ(queryResult.front().syncCount, expectedServices.front().syncCount);
}

TEST_F(FitableMemoryRepositoryTest, should_return_false_when_remove_given_invalid_operate)
{
    // given
    Fit::fitable_id fitable = serviceInfo_.service.fitable;
    Fit::fit_address address = serviceInfo_.service.addresses.front();

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, nullptr);
    bool removeRet = fitableMemoryRepository->Remove(fitable, address);

    // then
    EXPECT_EQ(removeRet, false);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_save_remove_and_query_given_fitable_and_address)
{
    // given
    Fit::fitable_id fitable = serviceInfo_.service.fitable;
    Fit::fit_address address = serviceInfo_.service.addresses.front();

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    bool removeRet = fitableMemoryRepository->Remove(fitable, address);
    auto queryResult = fitableMemoryRepository->Query(fitable, address);

    // then
    EXPECT_EQ(saveRet, true);
    EXPECT_EQ(removeRet, true);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_get_all_service_given_empty)
{
    // given
    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    auto queryResult = fitableMemoryRepository->GetAllServices();

    // then
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_save_remove_and_query_given_invalid_operate)
{
    // given
    Fit::fitable_id fitable = serviceInfo_.service.fitable;
    Fit::fit_address address = serviceInfo_.service.addresses.front();

    db_service_set expectedServices;
    expectedServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, nullptr);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    auto removeRet = fitableMemoryRepository->Remove(address);
    auto queryResult = fitableMemoryRepository->Query(fitable, address);

    // then
    EXPECT_EQ(saveRet, false);
    EXPECT_EQ(removeRet.empty(), true);
    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_save_remove_and_query_given_invalid_address)
{
    // given
    Fit::fit_address address;
    address.id = "invalid_id";

    db_service_set expectedRemoveServices;
    db_service_set expectedQueryServices;
    expectedQueryServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    auto removeRet = fitableMemoryRepository->Remove(address);
    auto queryResult = fitableMemoryRepository->Query(serviceInfo_.service.fitable,
                                                      serviceInfo_.service.addresses.front());

    // then
    EXPECT_EQ(saveRet, true);
    EXPECT_EQ(removeRet.empty(), true);
    EXPECT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedQueryServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedQueryServices.front().service.addresses.size());

    AddressCompare(queryResult.front().service.addresses.front(),
                   expectedQueryServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedQueryServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedQueryServices.front().service.fitable);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_save_remove_and_query_given_address)
{
    // given
    Fit::fitable_id fitable = serviceInfo_.service.fitable;
    Fit::fit_address address = serviceInfo_.service.addresses.front();

    db_service_set expectedRemoveServices;
    expectedRemoveServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    auto removeRet = fitableMemoryRepository->Remove(address);
    auto queryResult = fitableMemoryRepository->Query(fitable, address);

    // then
    EXPECT_EQ(saveRet, true);
    ASSERT_EQ(removeRet.empty(), false);
    ASSERT_EQ(removeRet.size(), expectedRemoveServices.size());
    EXPECT_EQ(removeRet.front().service.addresses.size(), expectedRemoveServices.front().service.addresses.size());

    AddressCompare(removeRet.front().service.addresses.front(),
                   expectedRemoveServices.front().service.addresses.front());
    ApplicationCompare(removeRet.front().service.application, expectedRemoveServices.front().service.application);
    FitableCompare(removeRet.front().service.fitable, expectedRemoveServices.front().service.fitable);

    EXPECT_EQ(queryResult.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_service_call_back_given_worker_with_meta_nullptr)
{
    // given
    std::shared_ptr<WorkerWithMeta> workerWithMeta = nullptr;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    fitableMemoryRepository->ServiceTimeoutCallback(workerWithMeta);

    // then
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_save_and_service_call_back_given_worker_with_meta)
{
    // given
    std::shared_ptr<WorkerWithMeta> workerWithMeta = std::make_shared<WorkerWithMeta>();
    workerWithMeta->worker.application = application_;
    workerWithMeta->worker.workerId = workerId_;

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    fitableMemoryRepository->ServiceTimeoutCallback(workerWithMeta);

    // then
    EXPECT_EQ(saveRet, true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_service_when_save_and_get_service_given_worker_meta)
{
    // given
    uint64_t syncCount = 2;

    db_service_set expectedRemoveServices;
    db_service_set expectedQueryServices;
    expectedQueryServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    auto getServicesExcludeDB = fitableMemoryRepository->GetServicesNotUpdated(syncCount);

    // then
    EXPECT_EQ(saveRet, true);
    EXPECT_EQ(getServicesExcludeDB.empty(), false);
    ASSERT_EQ(getServicesExcludeDB.size(), expectedQueryServices.size());
    EXPECT_EQ(getServicesExcludeDB.front()->service.addresses.size(),
              expectedQueryServices.front().service.addresses.size());

    AddressCompare(getServicesExcludeDB.front()->service.addresses.front(),
                   expectedQueryServices.front().service.addresses.front());
    ApplicationCompare(getServicesExcludeDB.front()->service.application,
                       expectedQueryServices.front().service.application);
    FitableCompare(getServicesExcludeDB.front()->service.fitable, expectedQueryServices.front().service.fitable);
}

TEST_F(FitableMemoryRepositoryTest, should_return_empty_when_save_and_get_service_given_invalid_operate)
{
    // given
    uint64_t syncCount = 2;

    db_service_set expectedRemoveServices;
    db_service_set expectedQueryServices;
    expectedQueryServices.push_back(serviceInfo_);

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, nullptr);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    auto getServicesExcludeDB = fitableMemoryRepository->GetServicesNotUpdated(syncCount);

    // then
    EXPECT_EQ(saveRet, false);
    EXPECT_EQ(getServicesExcludeDB.empty(), true);
}

TEST_F(FitableMemoryRepositoryTest, should_return_false_when_modify_service_given_null_operate)
{
    // given
    db_service_set modifyServices;
    modifyServices.push_back(serviceInfo_);

    {
        // when
        std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
            = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, nullptr);
        bool modifyRet = fitableMemoryRepository->InsertServiceOrUpdateSyncCount(modifyServices);
        // then
        EXPECT_EQ(modifyRet, false);
    }

    {
        // when
        std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
            = std::make_shared<FitableMemoryRepository>(workerOperate_, nullptr, fitableOperate_);
        bool modifyRet = fitableMemoryRepository->InsertServiceOrUpdateSyncCount(modifyServices);
        // then
        EXPECT_EQ(modifyRet, false);
    }

    {
        // when
        std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
            = std::make_shared<FitableMemoryRepository>(nullptr, addressOperate_, fitableOperate_);
        bool modifyRet = fitableMemoryRepository->InsertServiceOrUpdateSyncCount(modifyServices);
        // then
        EXPECT_EQ(modifyRet, false);
    }
}

TEST_F(FitableMemoryRepositoryTest,
    should_return_true_when_InsertServiceOrUpdateSynCount_given_cache_not_have_fitable)
{
    // given
    db_service_info_t serviceInfoTemp = serviceInfo_;
    serviceInfoTemp.syncCount++;
    db_service_set modifyServices {serviceInfoTemp};
    db_service_set expectedQueryServices {serviceInfoTemp};

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool removeRet = fitableMemoryRepository->Remove(serviceInfoTemp.service.fitable,
                                                     serviceInfoTemp.service.addresses.front());
    bool modifyRet = fitableMemoryRepository->InsertServiceOrUpdateSyncCount(modifyServices);
    auto queryResult = fitableMemoryRepository->Query(serviceInfoTemp.service.fitable,
                                                      serviceInfoTemp.service.addresses.front());

    // then
    EXPECT_EQ(removeRet, true);
    EXPECT_EQ(modifyRet, true);
    ASSERT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedQueryServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedQueryServices.front().service.addresses.size());
    AddressCompare(queryResult.front().service.addresses.front(),
                   expectedQueryServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedQueryServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedQueryServices.front().service.fitable);
    EXPECT_EQ(queryResult.front().syncCount, expectedQueryServices.front().syncCount);
}

TEST_F(FitableMemoryRepositoryTest,
    should_return_true_when_save_and_InsertServiceOrUpdateSynCount_given_cache_have_fitable)
{
    // given
    db_service_info_t serviceInfoTemp = serviceInfo_;
    serviceInfoTemp.syncCount++;
    db_service_set modifyServices {serviceInfoTemp};
    db_service_set expectedQueryServices {serviceInfoTemp};
    db_service_set expectedQueryServicesAfterSave {serviceInfo_};

    Fit::fitable_id fitable = serviceInfo_.service.fitable;
    Fit::fit_address address = serviceInfo_.service.addresses.front();

    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);
    bool modifyRet = fitableMemoryRepository->InsertServiceOrUpdateSyncCount(modifyServices);
    auto queryResult = fitableMemoryRepository->Query(fitable, address);

    // then
    EXPECT_EQ(modifyRet, true);
    ASSERT_EQ(queryResult.empty(), false);
    ASSERT_EQ(queryResult.size(), expectedQueryServices.size());
    EXPECT_EQ(queryResult.front().service.addresses.size(), expectedQueryServices.front().service.addresses.size());
    AddressCompare(queryResult.front().service.addresses.front(),
                   expectedQueryServices.front().service.addresses.front());
    ApplicationCompare(queryResult.front().service.application, expectedQueryServices.front().service.application);
    FitableCompare(queryResult.front().service.fitable, expectedQueryServices.front().service.fitable);
    EXPECT_EQ(queryResult.front().syncCount, expectedQueryServices.front().syncCount);
}

TEST_F(FitableMemoryRepositoryTest, should_return_fitable_address_when_GetFitableInstances_given_exist_generic_id)
{
    // given
    bool expectedSaveRet = true;
    // when
    std::shared_ptr<FitableMemoryRepository> fitableMemoryRepository
        = std::make_shared<FitableMemoryRepository>(workerOperate_, addressOperate_, fitableOperate_);
    bool saveRet = fitableMemoryRepository->Save(serviceInfo_);

    auto queryResult = fitableMemoryRepository->GetFitableInstances(serviceInfo_.service.fitable.generic_id);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_EQ(queryResult.size(), 1);
    EXPECT_EQ(queryResult.front().workers.size(), serviceInfo_.service.addresses.size());
    auto& workerResult = queryResult.front().workers[0];
    auto& fitableResult = queryResult.front().fitableMeta.fitable;
    EXPECT_EQ(workerResult.workerId, serviceInfo_.service.addresses.front().id);
    EXPECT_EQ(workerResult.environment, serviceInfo_.service.addresses.front().environment);
    EXPECT_TRUE(Fit::RegistryInfo::ApplicationEqual()(workerResult.application, serviceInfo_.service.application));
    EXPECT_EQ(fitableResult.fitableId, serviceInfo_.service.fitable.fitable_id);
    EXPECT_EQ(fitableResult.fitableVersion, serviceInfo_.service.fitable.fitable_version);
    EXPECT_EQ(fitableResult.genericableId, serviceInfo_.service.fitable.generic_id);
    EXPECT_EQ(fitableResult.genericableVersion, serviceInfo_.service.fitable.generic_version);
}