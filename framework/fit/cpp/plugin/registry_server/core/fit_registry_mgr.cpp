/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2020/6/23
 * Notes:       :
 */

#include "fit_registry_mgr.h"
#include <mutex>
#include <thread>
#include <algorithm>
#include <fit/internal/registry/repository/fit_fitable_memory_repository.h>
#include <v3/fit_application_instance/include/fit_application_instance_factory.h>
#include <v3/fit_fitable_meta/include/fit_fitable_meta_factory.h>
#include <registry_server_memory/fitable/fitable_memory_repository.h>
#include <fit/internal/registry/repository/fit_registry_application_repo.h>
#include "fit_registry_conf.h"
namespace Fit {
namespace Registry {
fit_registry_mgr_ptr fit_registry_mgr::instance_;

fit_registry_mgr_ptr &fit_registry_mgr::instance()
{
    static std::once_flag once;
    std::call_once(once, [] { instance_ = new fit_registry_mgr(); });

    return instance_;
}

fit_registry_mgr::fit_registry_mgr()
{
    auto subscription_repository = FitSubscriptionMemoryRepositoryFactory::Create(
        FitSubscriptionRepositoryDecoratorFactory::Create(
            fit_subscription_repository_factory::Create()
        ),
        FitSubscriptionNodeSyncPtrFactory::Create());
    // fitable的memory适配层
    auto wrokerMemoryRepo = FitMemoryWorkerOperation::Create();
    auto addressMemoryRepo = FitMemoryAddressOperation::Create();
    auto fitableMemoryRepo = FitMemoryFitableOperation::Create();
    FitFitableMemoryRepositoryPtr newMemory = Fit::make_shared<FitableMemoryRepository>(
        wrokerMemoryRepo, addressMemoryRepo, fitableMemoryRepo);

    // fitable db的repo层
    auto newRepository = FitRegistryServiceRepositoryFactoryV2::Create();

    auto service_repository = FitRegistryMemoryRepositoryFactory::Create(
        FitRegistryRepositoryFactoryWithServiceRepository::Create(newRepository),
        FitableRegistryFitableNodeSyncPtrFacotry::Create(), newRepository, newMemory);

    auto fitable_changed_publisher = std::make_shared<fit_data_changed_publisher<db_service_info_t>>();
    fitable_service_ = std::make_shared<fit_registry_service>(service_repository, fitable_changed_publisher);
    subscription_service_ = std::make_shared<fit_subscription_service>(subscription_repository);
    worker_status_listener_ = std::make_shared<Fit::Registry::fit_registry_worker_status_listener>(fitable_service_);
    fitable_status_listener_ = std::make_shared<Fit::Registry::fit_registry_fitable_status_listener>
    (subscription_service_, fitable_service_);
    fitable_changed_publisher->subscribe([this](const fitable_changed_data_t &data) {
        fitable_status_listener_->add(data.data.service.fitable);
    });
    subscriptionGarbageCollect_ = FitSubscriptionGarbageCollectFactory::Create(
        subscription_repository, service_repository);

    applicationInstanceServicePtr_ = FitApplicationInstanceFactory::Instance()->CreateServiceForRepo(
        wrokerMemoryRepo, addressMemoryRepo);
    fitableMetaServicePtr_ = FitFitableMetaFactory::CreateFitFitableMetaServiceForRepo(fitableMemoryRepo);
    applicationRepo_ = RegistryApplicationRepoFactory::CreateRepo();
}

fit_registry_mgr::~fit_registry_mgr()
{
}

fit_registry_service &fit_registry_mgr::get_registry_service()
{
    return *fitable_service_.get();
}

const fit_subscription_service &fit_registry_mgr::get_subscription_service()
{
    return *subscription_service_;
}

const worker_status_listener_ptr &fit_registry_mgr::get_worker_status_listener()
{
    return worker_status_listener_;
}

const FitApplicationInstanceServicePtr &fit_registry_mgr::get_application_instance_service()
{
    return applicationInstanceServicePtr_;
}

const FitFitableMetaServicePtr &fit_registry_mgr::get_fitable_meta_service()
{
    return fitableMetaServicePtr_;
}

RegistryApplicationRepo* fit_registry_mgr::get_application_repo()
{
    return applicationRepo_.get();
}

void fit_registry_mgr::start_task()
{
    if (!is_exit_) {
        return;
    }
    is_exit_ = false;
    task_processor_ = std::thread([this]() {
        while (!is_exit_) {
            worker_status_listener_->process(GetProcessWorkerStatusDelayMs());
            if (fitable_status_listener_->process()) {
                std::this_thread::sleep_for(std::chrono::milliseconds(GetThreadWaitInternalMs())); // LCOV_EXCL_LINE
                continue;
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(GetThreadIdleWaitInternalMs()));
        }
    });
    if (fitable_service_ != nullptr) {
        fitable_service_->Start();
    }
    if (subscription_service_ != nullptr) {
        subscription_service_->Start();
    }
    if (subscriptionGarbageCollect_ != nullptr) {
        subscriptionGarbageCollect_->Init();
    }
}

void fit_registry_mgr::stop_task()
{
    is_exit_ = true;
    if (task_processor_.joinable()) {
        task_processor_.join();
    }
    if (fitable_service_ != nullptr) {
        fitable_service_->Stop();
    }
    if (subscription_service_ != nullptr) {
        subscription_service_->Stop();
    }
    if (subscriptionGarbageCollect_ != nullptr) {
        subscriptionGarbageCollect_->UnInit();
    }
}
}
} // LCOV_EXCL_BR_LINE