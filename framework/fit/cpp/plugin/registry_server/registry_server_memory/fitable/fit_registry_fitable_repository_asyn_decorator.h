/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/08
 * Notes:       :
 */
#ifndef FIT_REGISTRY_FITABLE_REPOSITORY_ASYN_DECORATOR_H
#define FIT_REGISTRY_FITABLE_REPOSITORY_ASYN_DECORATOR_H
#include <fit/internal/registry/repository/fit_registry_repository_decorator.h>
#include <fit/internal/registry/fit_registry_entities.h>
#include <thread>
#include <atomic>
#include <condition_variable>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/stl/list.hpp>
#include <fit/stl/set.hpp>
#include <fit/memory/fit_base.hpp>
namespace Fit {
namespace Registry {
class FitRegistryFitableRepositoryAsynDecorator : public FitRegistryRepositoryDecorator {
public:
    struct AsyncServiceInfo : public FitBase {
        enum class ServiceInfoState {
            STATE_INVALID = -1,
            STATE_SAVE = 0,
            STATE_REMOVE = 1,
            STATE_REMOVE_BY_WORKER_ADDRESS = 2
        };
        ServiceInfoState state;
        Fit::vector<db_service_info_t> dbServiceSet;
        Fit::fit_address workerAddress;
    };
    using FitableKeyAsyncServiceInfoMap = Fit::unordered_map<fit_fitable_key_t, std::weak_ptr<AsyncServiceInfo>,
        fitable_key_hasher, FitableKeyEqual>;
    using RegistryWorkerIdAsyncServiceInfoMap = Fit::unordered_map<Fit::string, FitableKeyAsyncServiceInfoMap>;
    using AsyncServiceInfoPtr = std::shared_ptr<FitRegistryFitableRepositoryAsynDecorator::AsyncServiceInfo>;

    explicit FitRegistryFitableRepositoryAsynDecorator(FitRegistryServiceRepositoryPtr serviceRepository);
    ~FitRegistryFitableRepositoryAsynDecorator() override;
    bool Start() override;
    bool Stop() override;
    bool Save(const db_service_info_t &dbService) override;
    bool Save(const db_service_set &services) override;
    db_service_set Query(const fit_fitable_key_t &key) override;
    bool Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address) override;
    bool Remove(const db_service_set &services) override;
    db_service_set Remove(const Fit::fit_address &address) override;
private:
    void SaveServiceSet(const db_service_set &services);
    void RemoveServiceSet(const db_service_set &services);
    void RemoveServiceByWorkerAddress(const Fit::fit_address &address);
    bool AddSyncList(const db_service_info_t &dbService, AsyncServiceInfo::ServiceInfoState state);
    bool AddSyncListEnd(const db_service_info_t &dbService, AsyncServiceInfo::ServiceInfoState state);
    bool AddSyncList(std::shared_ptr<AsyncServiceInfo> asyncServiceInfo, uint64_t index);
    bool RemoveSyncList(const Fit::fit_address &address);
    void ClearSameFitable(const db_service_info_t& serviceInfo);
    void CreateRunTask(uint64_t index);
    void RunMemoryToDbTask();
    bool CanMergeBack(Fit::vector<Fit::list<std::shared_ptr<AsyncServiceInfo>>>& asyncServiceInfoSets,
        uint64_t index,
        AsyncServiceInfo::ServiceInfoState state);
private:
    FitRegistryFitableRepositoryAsynDecorator::AsyncServiceInfoPtr
        RemoveAndGetAsyncServiceInfoPtrNoLock(const db_service_info_t& serviceInfo);
private:
    uint64_t GetIdByAddress(const Fit::fit_address &address);
private:
    Fit::mutex asyncServiceInfoSetMutex_;
    std::condition_variable asyncServiceInfoSetCondition_;
    std::atomic<bool> exit_ {false};
    Fit::vector<Fit::list<std::shared_ptr<AsyncServiceInfo>>> asyncServiceInfoSets_;
    RegistryWorkerIdAsyncServiceInfoMap indexOfAsyncTaskByAddress_;
    Fit::vector<std::thread> consumer_;
    Fit::unordered_map<int32_t, std::function<void(const AsyncServiceInfo&)>> operatorSet_;
};
}
}
#endif // FIT_REGISTRY_FITABLE_REPOSITORY_ASYN_DECORATOR_H
