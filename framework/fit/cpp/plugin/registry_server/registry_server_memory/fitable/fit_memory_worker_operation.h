/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#ifndef FIT_MEMORY_WORKER_OPERATION_H
#define FIT_MEMORY_WORKER_OPERATION_H
#include <memory>
#include <fit/stl/mutex.hpp>
#include <fit/internal/registry/fit_registry_entities.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/set.hpp>
namespace Fit {
namespace Registry {
struct WorkerWithMeta {
    Fit::RegistryInfo::Worker worker;
    Fit::timer::timer_handle_t handle { Fit::timer::INVALID_TASK_ID };
    uint64_t syncCount {0};
};

class FitMemoryWorkerOperation {
public:
    using WorkerPtrIndexByWorkerId = Fit::unordered_map<Fit::string, std::shared_ptr<WorkerWithMeta>>;
    using WorkerPtrIndexByApplication = Fit::unordered_map<Fit::RegistryInfo::Application,
        Fit::set<shared_ptr<WorkerWithMeta>>,
        Fit::RegistryInfo::ApplicationHash, Fit::RegistryInfo::ApplicationEqual>;
public:
    ~FitMemoryWorkerOperation();
    bool Stop();
    void Init(std::function<void(std::weak_ptr<WorkerWithMeta> workerWithMeta)> callback);
    int32_t Save(std::shared_ptr<WorkerWithMeta> worker);
    std::shared_ptr<WorkerWithMeta> Query(const Fit::string& workerId);
    Fit::vector<std::shared_ptr<WorkerWithMeta>> Query(const Fit::RegistryInfo::Application& application);
    Fit::vector<std::shared_ptr<WorkerWithMeta>> QueryAll();
    void Remove(const Fit::string& workerId);
    int32_t Remove(const Fit::string& workerId, const Fit::RegistryInfo::Application& application);
    static Fit::shared_ptr<FitMemoryWorkerOperation> Create();
public:
    void WorkerTimeoutCallback(const std::weak_ptr<WorkerWithMeta>& workerWithMeta);
    bool UpdateWorkerSyncCount(const string& workerId, uint64_t syncCount);
private:
    void DeleteWorker(FitMemoryWorkerOperation::WorkerPtrIndexByWorkerId& workerPtrIndexByWorkerId,
        FitMemoryWorkerOperation::WorkerPtrIndexByApplication& workerPtrSetIndexByApplication,
        const Fit::string& workerId);
private:
    timer::timer_handle_t ObserveWorkerTimeout(std::weak_ptr<WorkerWithMeta> workerWithMeta);
    void DeleteWorkerTimeout(Fit::timer::timer_handle_t taskId);
    void UpdateWorkerTimeout(std::shared_ptr<WorkerWithMeta> workerWithMeta);
private:
    Fit::mutex workerPtrIndexByWorkerIdMutex_;
    WorkerPtrIndexByWorkerId workerPtrIndexByWorkerId_ {};
    Fit::mutex workerPtrIndexByApplicationMutex_;
    WorkerPtrIndexByApplication workerPtrSetIndexByApplication_ {};
    std::shared_ptr<Fit::timer> timer_;
    std::function<void(std::weak_ptr<WorkerWithMeta> workerWithMeta)> callback_;
};
}
}
#endif