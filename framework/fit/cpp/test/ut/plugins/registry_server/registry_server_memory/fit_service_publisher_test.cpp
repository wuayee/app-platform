/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <utility>
#include <fit/stl/string.hpp>
#include <mock/runtime_mock.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <registry_server/core/fit_service_publisher.h>
#include <registry_server/core/fit_registry_fitable_status_listener.h>
#include <registry_server_memory/fitable/fitable_memory_repository.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_notify_fitables/1.0.0/cplusplus/notifyFitables.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;
using namespace Fit::Registry;

class FitServicePublisherTest : public ::testing::Test {
public:
    void SetUp() override
    {
        // fitable的memory适配层
        auto wrokerMemoryRepo = FitMemoryWorkerOperation::Create();
        auto addressMemoryRepo = FitMemoryAddressOperation::Create();
        auto fitableMemoryRepo = FitMemoryFitableOperation::Create();
        FitFitableMemoryRepositoryPtr newMemory = Fit::make_shared<FitableMemoryRepository>(
            wrokerMemoryRepo, addressMemoryRepo, fitableMemoryRepo);
        // 只操作新老内存结构
        auto service_repository = FitRegistryMemoryRepositoryFactory::Create(
            nullptr, nullptr, nullptr, newMemory);

        auto fitable_changed_publisher = std::make_shared<fit_data_changed_publisher<db_service_info_t>>();
        fitableService_ = std::make_shared<fit_registry_service>(service_repository, fitable_changed_publisher);

        auto subscription_repository = FitSubscriptionMemoryRepositoryFactory::Create(
            nullptr, nullptr);
        subscriptionService_ = std::make_shared<fit_subscription_service>(subscription_repository);
        fitableStatusListener_ = std::make_shared<fit_registry_fitable_status_listener>(subscriptionService_,
            fitableService_);

        Fit::RegistryInfo::Application application;
        application.name = "app_name";
        application.nameVersion = "app_name_version";
        Fit::fit_address address;
        auto addressTemp = FitSystemPropertyUtils::Address();
        address.ip = addressTemp.host;
        address.port = addressTemp.port;
        address.protocol = static_cast<Fit::fit_protocol_type>(addressTemp.protocol);
        address.id = addressTemp.id;

        syncFitable_.fitable_id = "202954b6897a4e2da49aa29ac572f5fb";
        syncFitable_.fitable_version = "1.0.0";
        syncFitable_.generic_id = fit::hakuna::kernel::registry::server::synchronizeFitService::GENERIC_ID;
        syncFitable_.generic_version = "1.0.0";
        syncServiceInfo_.fitable = syncFitable_;
        syncServiceInfo_.timeoutSeconds = 600;
        syncServiceInfo_.application = application;
        syncServiceInfo_.addresses.emplace_back(address);

        testFitable_.fitable_id = "test_fitable_id";
        testFitable_.fitable_version = "1.0.0";
        testFitable_.generic_id = "test_genericable_id";
        testFitable_.generic_version = "1.0.0";
        testServiceInfo_.fitable = testFitable_;
        testServiceInfo_.timeoutSeconds = 600;
        testServiceInfo_.application = application;
        testServiceInfo_.addresses.emplace_back(address);

        testNoApplicationFitable_.fitable_id = "test_no_applicaition_fitable_id";
        testNoApplicationFitable_.fitable_version = "1.0.0";
        testNoApplicationFitable_.generic_id = "test_no_applicaition_genericable_id";
        testNoApplicationFitable_.generic_version = "1.0.0";
        testNoApplicationServiceInfo_.fitable = testNoApplicationFitable_;
        testNoApplicationServiceInfo_.timeoutSeconds = 600;
        testNoApplicationServiceInfo_.addresses.emplace_back(address);

        testNewListenerFitable_.fitable_id = "test_new_listener_fid";
        testNewListenerFitable_.fitable_version = "1.0.0";
        testNewListenerFitable_.generic_id = fit::hakuna::kernel::registry::server::notifyFitables::GENERIC_ID;
        testNewListenerFitable_.generic_version = "1.0.0";
        testNewListenerServiceInfo_.fitable = testNewListenerFitable_;
        testNewListenerServiceInfo_.timeoutSeconds = 600;
        testNewListenerServiceInfo_.application = application;
        testNewListenerServiceInfo_.addresses.emplace_back(address);

        fitableService_->register_services(fit_service_instance_set {syncServiceInfo_});
        fitableService_->register_services(fit_service_instance_set {testServiceInfo_});
        fitableService_->register_services(fit_service_instance_set {testNoApplicationServiceInfo_});
        fitableService_->register_services(fit_service_instance_set {testNewListenerServiceInfo_});

        testNewListener_.fitable_id = "test_new_listener_fid";
        testNewListener_.address.ip = address.ip;
        testNewListener_.address.port = address.port;
        testNewListener_.address.protocol = address.protocol;
        testNewListener_.address.id = address.id;

        testFitableKey_.generic_id = testFitable_.generic_id;
        testFitableKey_.fitable_id = testFitable_.fitable_id;
        testFitableKey_.generic_version = testFitable_.generic_version;
        testNoApplicationFitableKey_.generic_id = testNoApplicationFitable_.generic_id;
        testNoApplicationFitableKey_.fitable_id = testNoApplicationFitable_.fitable_id;
        testNoApplicationFitableKey_.generic_version = testNoApplicationFitable_.generic_version;

        subscriptionService_->insert_subscription_entry(testFitableKey_, testNewListener_);
    }

    void TearDown() override
    {
    }
public:
    fit_registry_service_ptr fitableService_;
    fit_subscription_service_ptr subscriptionService_;
    fitable_status_listener_ptr fitableStatusListener_;

    Fit::fitable_id syncFitable_;
    fit_service_instance_t syncServiceInfo_;

    Fit::fitable_id testFitable_;
    fit_service_instance_t testServiceInfo_;

    Fit::fitable_id testNoApplicationFitable_;
    fit_service_instance_t testNoApplicationServiceInfo_;

    fit_fitable_key_t testFitableKey_;

    Fit::fitable_id testNewListenerFitable_;
    fit_service_instance_t testNewListenerServiceInfo_;
    listener_t testNewListener_;
    fit_fitable_key_t testNoApplicationFitableKey_;
};

TEST_F(FitServicePublisherTest, should_return_void_when_add_given_fitable_id)
{
    // given
    fit_fitable_key_t testFitableKey;
    testFitableKey.generic_id = testNewListenerFitable_.generic_id;
    testFitableKey.generic_version = testNewListenerFitable_.generic_version;
    testFitableKey.fitable_id = testNewListenerFitable_.fitable_id;
    // when
    fitableStatusListener_->add(testFitable_);
    fitableStatusListener_->process();
    auto seriviceInfo = fitableService_->QueryService(testFitableKey, testNewListener_.address);
    // then
    EXPECT_EQ(seriviceInfo.empty(), false);
}

TEST_F(FitServicePublisherTest, should_return_void_when_add_given_no_fitable_and_subscription_service)
{
    // given
    auto fitableStatusListener = std::make_shared<fit_registry_fitable_status_listener>(nullptr, nullptr);
    // when
    fitableStatusListener->add(testFitable_);
    fitableStatusListener->process();
    // then
}

TEST_F(FitServicePublisherTest, should_return_void_when_add_given_no_fitable_service)
{
    // given
    auto fitableStatusListener = std::make_shared<fit_registry_fitable_status_listener>(subscriptionService_, nullptr);
    // when
    fitableStatusListener->add(testFitable_);
    fitableStatusListener->process();
    // then
}

TEST_F(FitServicePublisherTest, should_return_void_when_add_given_no_subscription_service)
{
    // given
    auto fitableStatusListener = std::make_shared<fit_registry_fitable_status_listener>(nullptr, fitableService_);
    // when
    fitableStatusListener->add(testFitable_);
    fitableStatusListener->process();
    // then
}