/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 注册中心服务,同一个fit_id可能对应多个address
 * Author       : songyongtan
 * Date         : 2020-06-16
 * Notes:       :
 */

#ifndef FIT_REGISTRY_SERVICE_H
#define FIT_REGISTRY_SERVICE_H
#include "fit/internal/registry/repository/fit_registry_memory_repository.h"
#include "fit_data_publisher.h"

#include <list>
#include <memory>

namespace Fit {
namespace Registry {
using fitable_changed_data_t = fit_changed_data_t<db_service_info_t>;
using fit_fitable_changed_publisher_ptr = std::shared_ptr<fit_data_changed_publisher<db_service_info_t>>;

class fit_registry_service {
public:
    explicit fit_registry_service(FitRegistryMemoryRepositoryPtr repo,
        fit_fitable_changed_publisher_ptr publisher);
    ~fit_registry_service();
    bool Start();
    bool Stop();

    bool register_services(const fit_service_instance_set &services);

    bool unregister_services(const fit_service_instance_set &services, const fit_worker_info_t &worker);

    db_service_set get_services(const fit_fitable_key_t &key) const;

    db_service_set get_all_services() const;

    address_set get_addresses(const Fit::fitable_id &fitable) const;

    db_service_set get_services_by_generic_id(const Fit::string &generic_id);

    int32_t SyncSave(const db_service_info_t &services);
    int32_t SyncRemove(const db_service_info_t &services);
    int32_t SyncSave(const db_service_set &services);
    int32_t SyncRemove(const db_service_set &services);
    void Remove(const Fit::fit_address &address);
    db_service_set QueryService(const fit_fitable_key_t &key, const Fit::fit_address &address);
    void TimeoutCallback(const db_service_set &services);
    bool InitTimeoutCallback(std::function<void(const db_service_set &)> callback);

    vector<RegistryInfo::FitableMetaAddress> GetFitableInstances(const string& genericId) const;
    FitCode QueryWorkerDetail(const string& workerId, RegistryInfo::WorkerDetail& result) const;
    vector<RegistryInfo::WorkerMeta> QueryAllWorkers() const;

protected:
    void notify(const db_service_set &services, fit_data_changed_type type);

private:
    address_set get_addresses_from_service_set(const db_service_set &service_set) const;

    FitRegistryMemoryRepositoryPtr fit_registry_service_repo_ {};

    fit_fitable_changed_publisher_ptr publisher_ {};
};

using fit_registry_service_ptr = std::shared_ptr<fit_registry_service>;
}
}

#endif // FIT_REGISTRY_SERVICE_H
