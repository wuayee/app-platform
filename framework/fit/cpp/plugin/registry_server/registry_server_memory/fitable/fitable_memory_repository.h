/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#ifndef FITABLE_MEMORY_REPOSITORY_H
#define FITABLE_MEMORY_REPOSITORY_H
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/stl/unordered_set.hpp>
#include <fit/internal/registry/repository/fit_fitable_memory_repository.h>
#include <fit/internal/registry/fit_registry_entities.h>
#include "fit_memory_worker_operation.h"
#include "fit_memory_address_operation.h"
#include "fit_memory_fitable_operation.h"

namespace Fit {
namespace Registry {
class FitableMemoryRepository : public FitFitableMemoryRepository {
public:
    explicit FitableMemoryRepository(
        std::shared_ptr<FitMemoryWorkerOperation> memoryWorkerOperation,
        std::shared_ptr<FitMemoryAddressOperation> memoryAddressOperation,
        std::shared_ptr<FitMemoryFitableOperation> memoryFitableOperation);
    ~FitableMemoryRepository() override;
    bool Start() override;
    bool Stop() override;
    bool Save(const db_service_info_t &service) override;
    bool Save(const db_service_set &services) override;

    db_service_set Query(const fit_fitable_key_t &key) override;
    // 按服务查询时，只能查询一个worker下的service
    db_service_set Query(const db_service_info_t &service) override;
    // query a instance
    db_service_set Query(const Fit::fitable_id &fitable, const Fit::fit_address &address) override;

    bool Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address) override;
    bool Remove(const db_service_set &services) override;

    db_service_set GetAllServices() override;
    db_service_set Remove(const Fit::fit_address &address) override;
    bool InitTimeoutCallback(std::function<void(const db_service_set &)> callback) override;
    Fit::vector<std::shared_ptr<db_service_info_t>> GetServicesNotUpdated(uint64_t syncCount) override;
    void ServiceTimeoutCallback(std::weak_ptr<WorkerWithMeta> workerWithMeta);
    bool IsServiceAlreadyExist(const db_service_info_t &service) override;
    bool InsertServiceOrUpdateSyncCount(const db_service_set &services) override;
    vector<FitableMetaAddress> GetFitableInstances(const string& genericId) const override;
    FitCode QueryWorkerDetail(const Fit::string& workerId, Fit::RegistryInfo::WorkerDetail& result) const override;
    vector<RegistryInfo::WorkerMeta> QueryAllWorkers() const override;

private:
    bool CheckStatus();
    int32_t UpdateWorkerInfo(std::shared_ptr<WorkerWithMeta> workerWithMeta);
private:
    std::function<void(const db_service_set &)> timeOutCallback_ {nullptr};
    std::shared_ptr<FitMemoryWorkerOperation> memoryWorkerOperation_ {nullptr};
    std::shared_ptr<FitMemoryAddressOperation> memoryAddressOperation_ {nullptr};
    std::shared_ptr<FitMemoryFitableOperation> memoryFitableOperation_ {nullptr};
};
}
}

#endif