/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#include "fit_memory_worker_operation.h"
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <genericable/com_huawei_fit_heartbeat_is_alive/1.0.0/cplusplus/is_alive.hpp>

namespace Fit {
namespace Registry {
void PrintWorker(const WorkerWithMeta& worker, Fit::string type = "")
{
    FIT_LOG_DEBUG("Worker %s, id:%s, version:%s, expire:%lu, env:%s, createtime:%lu, appname:%s, appversion:%s.",
        type.c_str(),
        worker.worker.workerId.c_str(),
        worker.worker.version.c_str(),
        worker.worker.expire,
        worker.worker.environment.c_str(),
        worker.worker.createTime,
        worker.worker.application.name.c_str(),
        worker.worker.application.nameVersion.c_str());
}

void FitMemoryWorkerOperation::DeleteWorker(
    FitMemoryWorkerOperation::WorkerPtrIndexByWorkerId& workerPtrIndexByWorkerId,
    FitMemoryWorkerOperation::WorkerPtrIndexByApplication& workerPtrSetIndexByApplication,
    const Fit::string& workerId)
{
    std::shared_ptr<WorkerWithMeta> workerPtr {nullptr};
    auto workerIt = workerPtrIndexByWorkerId.find(workerId);
    if (workerIt != workerPtrIndexByWorkerId.end()) {
        workerPtr = workerIt->second;
        workerPtrIndexByWorkerId.erase(workerIt);
        FIT_LOG_INFO("The worker is removed, (id=%s, version=%s, expire=%lu, env=%s, appname=%s, appVersion=%s).",
            workerPtr->worker.workerId.c_str(), workerPtr->worker.version.c_str(), workerPtr->worker.expire,
            workerPtr->worker.environment.c_str(), workerPtr->worker.application.name.c_str(),
            workerPtr->worker.application.nameVersion.c_str());
    }
    if (workerPtr == nullptr) {
        FIT_LOG_DEBUG("WorkerPtr is nullptr.");
        return;
    }
    DeleteWorkerTimeout(workerPtr->handle);

    auto workerSetIt = workerPtrSetIndexByApplication.find(workerPtr->worker.application);
    if (workerSetIt == workerPtrSetIndexByApplication.end()) {
        return;
    }
    auto workerMetaPtrIt = workerSetIt->second.begin();
    for (; workerMetaPtrIt != workerSetIt->second.end();) {
        if ((*workerMetaPtrIt)->worker.workerId == workerId) {
            workerMetaPtrIt = workerSetIt->second.erase(workerMetaPtrIt);
            FIT_LOG_DEBUG("Worker is removed from appindex, id:%s.", workerId.c_str());
            break;
        }
        ++workerMetaPtrIt;
    }
    if (workerSetIt->second.empty()) {
        workerPtrSetIndexByApplication.erase(workerSetIt);
        FIT_LOG_DEBUG("WorkerSet is removed from appindex, name:version (%s:%s).",
            workerPtr->worker.application.name.c_str(),
            workerPtr->worker.application.nameVersion.c_str());
    }
}
FitMemoryWorkerOperation::~FitMemoryWorkerOperation()
{
    Stop();
}

bool FitMemoryWorkerOperation::Stop()
{
    Fit::unique_lock<Fit::mutex> lockWorkerId(workerPtrIndexByWorkerIdMutex_);
    for (const auto& it : workerPtrIndexByWorkerId_) {
        if (it.second != nullptr && it.second->handle != Fit::timer::INVALID_TASK_ID) {
            DeleteWorkerTimeout(it.second->handle);
        }
    }
    FIT_LOG_INFO("Worker stop.");
    return true;
}

void FitMemoryWorkerOperation::Init(std::function<void(std::weak_ptr<WorkerWithMeta> workerWithMeta)> callback)
{
    callback_ = std::move(callback);
    timer_ = Fit::timer_instance();
    FIT_LOG_DEBUG("Init worker callback timeout.");
}

timer::timer_handle_t FitMemoryWorkerOperation::ObserveWorkerTimeout(std::weak_ptr<WorkerWithMeta> workerWithMeta)
{
    auto handle = Fit::timer::INVALID_TASK_ID;
    auto workerWithMetaPtr = workerWithMeta.lock();
    if (workerWithMetaPtr != nullptr && timer_ != nullptr) {
        handle = timer_->set_timeout(
            workerWithMetaPtr->worker.expire * MILLION_SECONDS_PER_SECOND, [this, workerWithMeta]() {
            WorkerTimeoutCallback(workerWithMeta);
        });
    }
    return handle;
}

void FitMemoryWorkerOperation::DeleteWorkerTimeout(Fit::timer::timer_handle_t taskId)
{
    if (timer_ != nullptr) {
        timer_->remove(taskId);
    }
}

void FitMemoryWorkerOperation::WorkerTimeoutCallback(const std::weak_ptr<WorkerWithMeta>& workerWithMeta)
{
    if (callback_ != nullptr) {
        // check with heartbeat server
        auto meta = workerWithMeta.lock();
        if (!meta) {
            return;
        }
        auto check = [](const string& workerId, const string& scene) {
            bool* isAlive {};
            fit::heartbeat::IsAlive isAliveClient;
            auto checkRet = isAliveClient(&workerId, &scene, &isAlive);
            if (checkRet != FIT_OK) {
                FIT_LOG_WARN("Failed to check worker alive. (ret=%x, id=%s, scene=%s).", checkRet, workerId.c_str(),
                    scene.c_str());
            }
            return (isAlive != nullptr && *isAlive);
        };
        if (check(meta->worker.workerId, "fit_registry") || check(meta->worker.workerId, "fit_registry_server")) {
            FIT_LOG_INFO("The worker is still alive. (id=%s, env=%s, expire=%lu).", meta->worker.workerId.c_str(),
                meta->worker.environment.c_str(), meta->worker.expire);
            UpdateWorkerTimeout(meta);
            return;
        }

        FIT_LOG_INFO("The worker is offline. (id=%s, env=%s, expire=%lu).", meta->worker.workerId.c_str(),
            meta->worker.environment.c_str(), meta->worker.expire);
        callback_(workerWithMeta);
    }
}

bool FitMemoryWorkerOperation::UpdateWorkerSyncCount(const string& workerId, uint64_t syncCount)
{
    Fit::unique_lock<Fit::mutex> lockWorkerId(workerPtrIndexByWorkerIdMutex_);
    auto workerIt = workerPtrIndexByWorkerId_.find(workerId);
    if (workerIt != workerPtrIndexByWorkerId_.end()) {
        workerIt->second->syncCount = std::max(syncCount, workerIt->second->syncCount);
    }
    return true;
}

void FitMemoryWorkerOperation::UpdateWorkerTimeout(std::shared_ptr<WorkerWithMeta> workerWithMeta)
{
    if (workerWithMeta != nullptr && timer_ != nullptr) {
        timer_->insert_or_update_timeout(
            workerWithMeta->handle,
            workerWithMeta->worker.expire * MILLION_SECONDS_PER_SECOND,
            [this, workerWithMeta]() {
                WorkerTimeoutCallback(workerWithMeta);
            });
    }
}

int32_t FitMemoryWorkerOperation::Save(std::shared_ptr<WorkerWithMeta> worker)
{
    if (worker == nullptr) {
        FIT_LOG_ERROR("Worker is nullptr.");
        return FIT_ERR_FAIL;
    }

    Fit::unique_lock<Fit::mutex> lockWorkerId(workerPtrIndexByWorkerIdMutex_);
    Fit::unique_lock<Fit::mutex> lockApplication(workerPtrIndexByApplicationMutex_);
    auto workerIt = workerPtrIndexByWorkerId_.find(worker->worker.workerId);
    if (workerIt != workerPtrIndexByWorkerId_.end()) {
        auto old =  workerIt->second;
        worker->syncCount = std::max(worker->syncCount, uint64_t(old->syncCount));
        worker->handle = old->handle;
        workerIt->second = worker;
        auto& appWorkers = workerPtrSetIndexByApplication_[old->worker.application];
        auto appWorkersIter = appWorkers.find(old);
        if (appWorkersIter == appWorkers.end()) {
            FIT_LOG_ERROR("The old worker is not in app index, it's unexpected. (id=%s, version=%s, expire=%lu, "
                          "env=%s, appname=%s, appVersion=%s).",
                old->worker.workerId.c_str(), old->worker.version.c_str(), old->worker.expire,
                old->worker.environment.c_str(), old->worker.application.name.c_str(),
                old->worker.application.nameVersion.c_str());
        } else {
            appWorkers.erase(appWorkersIter);
        }
        workerPtrSetIndexByApplication_[worker->worker.application].insert(worker);
        // 更新timer
        UpdateWorkerTimeout(workerIt->second);
    } else {
        // 加入timer
        worker->handle = ObserveWorkerTimeout(worker);
        workerPtrIndexByWorkerId_[worker->worker.workerId] = worker;
        workerPtrSetIndexByApplication_[worker->worker.application].insert(worker);
        FIT_LOG_INFO("The worker is added, (id=%s, version=%s, expire=%lu, env=%s, appname=%s, appVersion=%s).",
            worker->worker.workerId.c_str(), worker->worker.version.c_str(), worker->worker.expire,
            worker->worker.environment.c_str(), worker->worker.application.name.c_str(),
            worker->worker.application.nameVersion.c_str());
    }
    Fit::string tag = "Save, size is " + Fit::to_string(workerPtrIndexByWorkerId_.size());
    PrintWorker(*worker, tag);
    return FIT_ERR_SUCCESS;
}

Fit::vector<std::shared_ptr<WorkerWithMeta>> FitMemoryWorkerOperation::Query(
    const Fit::RegistryInfo::Application& application)
{
    Fit::unique_lock<Fit::mutex> lockApplication(workerPtrIndexByApplicationMutex_);
    auto workerMetasIt = workerPtrSetIndexByApplication_.find(application);
    if (workerMetasIt == workerPtrSetIndexByApplication_.end()) {
        FIT_LOG_DEBUG("Query by app empty, name:version %s:%s.",
            application.name.c_str(), application.nameVersion.c_str());
        return Fit::vector<std::shared_ptr<WorkerWithMeta>> {};
    }
    Fit::vector<std::shared_ptr<WorkerWithMeta>> result;
    for (const auto& item : workerMetasIt->second) {
        result.emplace_back(item);
        PrintWorker(*item, "Query by app");
    }
    return result;
}

std::shared_ptr<WorkerWithMeta> FitMemoryWorkerOperation::Query(const Fit::string& workerId)
{
    Fit::unique_lock<Fit::mutex> lockWorkerId(workerPtrIndexByWorkerIdMutex_);
    auto workerMetaIt = workerPtrIndexByWorkerId_.find(workerId);
    if (workerMetaIt == workerPtrIndexByWorkerId_.end()) {
        return nullptr;
    }
    Fit::string tag = "query by id, size is " + Fit::to_string(workerPtrIndexByWorkerId_.size());
    PrintWorker(*(workerMetaIt->second), tag);
    return workerMetaIt->second;
}

Fit::vector<std::shared_ptr<WorkerWithMeta>> FitMemoryWorkerOperation::QueryAll()
{
    Fit::vector<std::shared_ptr<WorkerWithMeta>> result;
    Fit::unique_lock<Fit::mutex> lockApplication(workerPtrIndexByApplicationMutex_);
    auto it = workerPtrSetIndexByApplication_.begin();
    for (; it != workerPtrSetIndexByApplication_.end();) {
        auto workerPtrSet = it->second;
        if (workerPtrSet.empty()) {
            it = workerPtrSetIndexByApplication_.erase(it);
            continue;
        }
        ++it;

        for (const auto& workerPtr : workerPtrSet) {
            result.push_back(workerPtr);
            PrintWorker(*(workerPtr), "Query all");
        }
    }
    return result;
}
int32_t FitMemoryWorkerOperation::Remove(const Fit::string& workerId,
    const Fit::RegistryInfo::Application& application)
{
    Remove(workerId);
    return FIT_OK;
}
void FitMemoryWorkerOperation::Remove(const Fit::string& workerId)
{
    Fit::unique_lock<Fit::mutex> lockWorkerId(workerPtrIndexByWorkerIdMutex_);
    Fit::unique_lock<Fit::mutex> lockApplication(workerPtrIndexByApplicationMutex_);
    DeleteWorker(workerPtrIndexByWorkerId_, workerPtrSetIndexByApplication_, workerId);
}

Fit::shared_ptr<FitMemoryWorkerOperation> FitMemoryWorkerOperation::Create()
{
    return Fit::make_shared<FitMemoryWorkerOperation>();
}
}
} // LCOV_EXCL_BR_LINE
