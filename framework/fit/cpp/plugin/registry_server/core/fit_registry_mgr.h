/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2020/6/23
 * Notes:       :
 */

#ifndef FIT_CPP_FIT_REGISTRY_MGR_H
#define FIT_CPP_FIT_REGISTRY_MGR_H

#include <fit/internal/registry/repository/fit_subscription_memory_repository.h>
#include <fit/internal/registry/repository/fit_subscription_repository_decorator.h>
#include <fit/internal/registry/repository/fit_subscription_node_sync.h>
#include <fit/internal/registry/repository/fit_registry_repository_decorator.h>
#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <fit/internal/registry/repository/fitable_registry_fitable_node_sync.h>
#include <fit/internal/registry/repository/fit_subscription_node_sync.h>
#include <v3/fit_application_instance/include/fit_application_instance_service.h>
#include <v3/fit_fitable_meta/include/fit_fitable_meta_service.h>
#include <memory>
#include <vector>
#include <thread>
#include "fit_registry_fitable_status_listener.h"
#include "fit_registry_worker_status_listener.h"
#include "registry/repository/fit_registry_application_repo.h"
#include "registry_server_memory/subscriber/include/fit_subscription_garbage_collect.h"

namespace Fit {
namespace Registry {
using fit_subscription_repository_ptr = std::shared_ptr<fit_subscription_repository>;

class fit_registry_mgr;
using fit_registry_mgr_ptr = fit_registry_mgr *;

class fit_registry_mgr {
public:
    ~fit_registry_mgr();

    static fit_registry_mgr_ptr &instance();
    fit_registry_service &get_registry_service();
    const fit_subscription_service &get_subscription_service();
    const worker_status_listener_ptr &get_worker_status_listener();
    const FitApplicationInstanceServicePtr &get_application_instance_service();
    const FitFitableMetaServicePtr &get_fitable_meta_service();
    RegistryApplicationRepo* get_application_repo();
    void start_task();
    void stop_task();

    fit_registry_mgr(fit_registry_mgr &) = delete;
    fit_registry_mgr &operator=(fit_registry_mgr &) = delete;

private:
    fit_registry_mgr();
    fit_registry_service_ptr fitable_service_ {};
    fit_subscription_service_ptr subscription_service_ {};
    worker_status_listener_ptr worker_status_listener_;
    fitable_status_listener_ptr fitable_status_listener_;
    FitSubscriptionGarbageCollectPtr subscriptionGarbageCollect_ {};
    FitApplicationInstanceServicePtr applicationInstanceServicePtr_ {};
    FitFitableMetaServicePtr fitableMetaServicePtr_ {};
    unique_ptr<RegistryApplicationRepo> applicationRepo_ {};
    std::thread task_processor_;
    volatile bool is_exit_ {true};

    static fit_registry_mgr_ptr instance_;
};
}
}

#endif  // FIT_CPP_FIT_REGISTRY_MGR_H
