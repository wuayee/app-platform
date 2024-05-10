/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/08
 * Notes:       :
 */
#ifndef FIT_REGISTRY_FITABLE_REPOSITORY_MEMORY_H
#define FIT_REGISTRY_FITABLE_REPOSITORY_MEMORY_H

#include <fit/internal/fit_fitable.h>
#include <fit/internal/registry/repository/fit_registry_memory_repository.h>
#include <fit/internal/registry/repository/fit_fitable_memory_repository.h>
#include <fit/internal/registry/repository/fitable_registry_fitable_node_sync.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Registry {
class FitRegistryFitableRepositoryMemory : public FitRegistryMemoryRepository {
public:
    explicit FitRegistryFitableRepositoryMemory(
        FitRegistryServiceRepositoryPtr serviceRepository,
        FitableRegistryFitableNodeSyncPtr fitableNodeSyncPtr,
        FitRegistryServiceRepositoryPtr syncServiceRepository,
        FitFitableMemoryRepositoryPtr serviceMemoryRepository);
    ~FitRegistryFitableRepositoryMemory() override;
    bool Start() override;
    bool Stop() override;
    bool Save(const db_service_info_t &dbService) override;
    bool Save(const db_service_set &services) override;
    db_service_set Query(const fit_fitable_key_t &key) override;
    bool Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address) override;
    bool Remove(const db_service_set &services) override;
    db_service_set GetAllServices() override;

    int32_t SyncSave(const db_service_info_t &service) override;
    int32_t SyncRemove(const db_service_info_t &service) override;
    int32_t SyncSave(const db_service_set &services) override;
    int32_t SyncRemove(const db_service_set &services) override;
    db_service_set Remove(const Fit::fit_address &address) override;

    db_service_set QueryService(const fit_fitable_key_t &key, const Fit::fit_address &address) override;
    bool InitTimeoutCallback(std::function<void(const db_service_set &)> callback) override;

    vector<FitableMetaAddress> GetFitableInstances(const string& genericId) const override;
    FitCode QueryWorkerDetail(const Fit::string& workerId, Fit::RegistryInfo::WorkerDetail& result) const override;
    vector<RegistryInfo::WorkerMeta> QueryAllWorkers() const override;

public:
    bool InsertServiceOrUpdateSyncCount(const db_service_set &services);
    void ServiceTimeoutCallback(const db_service_set &serviceInfo);
private:
    int32_t SaveServiceToMemory(const db_service_info_t &dbService);
private:
    void InitMemory();
    bool IsAlreadyReadyForV3();
    bool ReadyForWorking(const db_service_info_t &service);
    void AddSyncServiceBetweenMemoryAndDbTask();
    void SyncServiceBetweenMemoryAndDb();
private:
    void MarkDBToMemoryOrder(db_service_set &serviceSet, uint64_t syncCount);
private:
    // db操作,直连
    FitRegistryServiceRepositoryPtr serviceRepository_ {nullptr};
    // 节点间同步
    FitableRegistryFitableNodeSyncPtr registrySyncService_ {nullptr};
    // 异步包装器，异步操作db
    FitRegistryServiceRepositoryPtr syncServiceRepository_ {nullptr};
    // 内存操作
    FitFitableMemoryRepositoryPtr serviceMemoryRepository_ {nullptr};
    Fit::timer::timer_handle_t syncServiceInfoFromDbTaskHandle_ {Fit::timer::INVALID_TASK_ID};
    std::atomic<bool> isAlreadyInitSync_ {false};
    Fit::mutex saveFitableCacheMutex_;
    Fit::vector<db_service_info_t> saveFitableCache_ {};
    std::shared_ptr<Fit::timer> timer_;
    std::function<void(const db_service_set &)> timeOutCallback_ {nullptr};
    std::atomic<uint64_t> syncCount_ {DEFAULT_SYNC_COUNT};
    Fit::timer::timer_handle_t syncServiceInfoFromDbTaskTimeoutHandle_ {Fit::timer::INVALID_TASK_ID};
};
}
}
#endif // FIT_REGISTRY_FITABLE_REPOSITORY_ASYN_DECORATOR_H
