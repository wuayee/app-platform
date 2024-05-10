/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/08
 * Notes:       :
 */
#include "fit_registry_fitable_repository_asyn_decorator.h"
#include <algorithm>
#include <fit/fit_log.h>
#include <fit/stl/unordered_set.hpp>
#include <core/fit_registry_conf.h>
namespace Fit {
namespace Registry {
FitRegistryFitableRepositoryAsynDecorator::FitRegistryFitableRepositoryAsynDecorator(
    FitRegistryServiceRepositoryPtr serviceRepository)
    : FitRegistryRepositoryDecorator(std::move(serviceRepository))
{
}
FitRegistryFitableRepositoryAsynDecorator::~FitRegistryFitableRepositoryAsynDecorator()
{
    Stop();
}
bool FitRegistryFitableRepositoryAsynDecorator::Start()
{
    Stop();
    exit_.store(false);
    operatorSet_[static_cast<int32_t>(
        AsyncServiceInfo::ServiceInfoState::STATE_SAVE)]
        = [this](const AsyncServiceInfo &asyncServiceInfo) {
            SaveServiceSet(asyncServiceInfo.dbServiceSet);
        };
    operatorSet_[static_cast<int32_t>(
        AsyncServiceInfo::ServiceInfoState::STATE_REMOVE)]
        = [this](const AsyncServiceInfo &asyncServiceInfo) {
            RemoveServiceSet(asyncServiceInfo.dbServiceSet);
        };
    operatorSet_[static_cast<int32_t>(
        AsyncServiceInfo::ServiceInfoState::STATE_REMOVE_BY_WORKER_ADDRESS)]
        = [this](const AsyncServiceInfo &asyncServiceInfo) {
            RemoveServiceByWorkerAddress(asyncServiceInfo.workerAddress);
        };
    RunMemoryToDbTask();
    FIT_LOG_INFO("Service asynchronous is started.");
    return true;
}
bool FitRegistryFitableRepositoryAsynDecorator::Stop()
{
    exit_.store(true);
    asyncServiceInfoSetCondition_.notify_all();
    for (auto& it : consumer_) {
        if (it.joinable()) {
            it.join();
        }
    }
    FIT_LOG_INFO("Service async stop.");
    return true;
}
void FitRegistryFitableRepositoryAsynDecorator::CreateRunTask(uint64_t index)
{
    consumer_.emplace_back([index, this]() {
        while (true) {
            Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
            asyncServiceInfoSetCondition_.wait(lock, [this, index]() {
                return !asyncServiceInfoSets_[index].empty() || exit_.load();
            });
            if (exit_.load()) {
                return;
            }
            auto asyncService = std::move(asyncServiceInfoSets_[index].front());
            asyncServiceInfoSets_[index].pop_front();
            if (asyncService == nullptr) {
                continue;
            }
            // clear index
            for (const auto& it : asyncService->dbServiceSet) {
                RemoveAndGetAsyncServiceInfoPtrNoLock(it);
            }
            FIT_LOG_DEBUG("FitRegistryFitableRepositoryAsynDecorator left task num is %lu.",
                asyncServiceInfoSets_[index].size());
            lock.unlock();
            auto operatorIt = operatorSet_.find(static_cast<int32_t>(asyncService->state));
            if (operatorIt != operatorSet_.end()) {
                operatorIt->second(*asyncService);
            }
        }
    });
}
void FitRegistryFitableRepositoryAsynDecorator::RunMemoryToDbTask()
{
    Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
    asyncServiceInfoSets_.reserve(GetRegistryFitableMemoryToDbConsumerThreadNum());
    lock.unlock();
    for (size_t i = 0; i < GetRegistryFitableMemoryToDbConsumerThreadNum(); ++i) {
        Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
        asyncServiceInfoSets_.emplace_back(Fit::list<std::shared_ptr<AsyncServiceInfo>> {});
        lock.unlock();
        CreateRunTask(i);
    }
}
bool FitRegistryFitableRepositoryAsynDecorator::Save(const db_service_info_t &dbService)
{
    return AddSyncList(dbService, AsyncServiceInfo::ServiceInfoState::STATE_SAVE);
}
bool FitRegistryFitableRepositoryAsynDecorator::Save(const db_service_set &services)
{
    for (const auto& it : services) {
        AddSyncList(it, AsyncServiceInfo::ServiceInfoState::STATE_SAVE);
    }
    return true;
}
db_service_set FitRegistryFitableRepositoryAsynDecorator::Query(const fit_fitable_key_t &key)
{
    return FitRegistryRepositoryDecorator::Query(key);
}
bool FitRegistryFitableRepositoryAsynDecorator::Remove(const Fit::fitable_id &fitable,
    const Fit::fit_address &address)
{
    db_service_info_t serviceInfo;
    serviceInfo.service.fitable = fitable;
    serviceInfo.service.addresses.emplace_back(address);
    return AddSyncList(serviceInfo, AsyncServiceInfo::ServiceInfoState::STATE_REMOVE);
}

bool FitRegistryFitableRepositoryAsynDecorator::Remove(const db_service_set &services)
{
    for (const auto& it : services) {
        for (const auto& address : it.service.addresses) {
            Remove(it.service.fitable, address);
        }
    }
    return true;
}

using AsyncServiceInfoPtr = FitRegistryFitableRepositoryAsynDecorator::AsyncServiceInfoPtr;
AsyncServiceInfoPtr FitRegistryFitableRepositoryAsynDecorator::RemoveAndGetAsyncServiceInfoPtrNoLock(
    const db_service_info_t& serviceInfo)
{
    if (serviceInfo.service.addresses.empty()) {
        return nullptr;
    }
    fit_fitable_key_t fitableKey;
    fitableKey.generic_id = serviceInfo.service.fitable.generic_id;
    fitableKey.generic_version = serviceInfo.service.fitable.generic_version;
    fitableKey.fitable_id = serviceInfo.service.fitable.fitable_id;

    auto serviceMap = indexOfAsyncTaskByAddress_.find(serviceInfo.service.addresses.front().id);
    if (serviceMap == indexOfAsyncTaskByAddress_.end()) {
        return nullptr;
    }
    auto fitableIt = serviceMap->second.find(fitableKey);
    if (fitableIt == serviceMap->second.end()) {
        return nullptr;
    }

    auto AsyncServiceInfo = fitableIt->second.lock();
    serviceMap->second.erase(fitableIt);
    if (serviceMap->second.empty()) {
        indexOfAsyncTaskByAddress_.erase(serviceMap);
    }
    return AsyncServiceInfo;
}

void FitRegistryFitableRepositoryAsynDecorator::ClearSameFitable(const db_service_info_t& serviceInfo)
{
    Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
    auto AsyncServiceInfo = RemoveAndGetAsyncServiceInfoPtrNoLock(serviceInfo);
    if (AsyncServiceInfo == nullptr) {
        return;
    }

    auto it = std::find_if(
        AsyncServiceInfo->dbServiceSet.begin(),
        AsyncServiceInfo->dbServiceSet.end(),
        [&serviceInfo](const db_service_info_t& serviceInfoIn) {
            if (!fit_fitable_id_equal_to()(serviceInfo.service.fitable, serviceInfoIn.service.fitable)) {
                return false;
            }
            if (serviceInfo.service.addresses.empty() && serviceInfoIn.service.addresses.empty()) {
                return true;
            }
            if (serviceInfo.service.addresses.empty() || serviceInfoIn.service.addresses.empty()) {
                return false;
            }
            return serviceInfo.service.addresses.front().id == serviceInfoIn.service.addresses.front().id &&
                serviceInfo.service.addresses.front().port == serviceInfoIn.service.addresses.front().port;
        });
    if (it != AsyncServiceInfo->dbServiceSet.end()) {
        AsyncServiceInfo->dbServiceSet.erase(it);
        FIT_LOG_DEBUG("ClearSameFitable fitable id is %s, after size is %lu.",
            serviceInfo.service.fitable.fitable_id.c_str(),
            AsyncServiceInfo->dbServiceSet.size());
    }
}
bool FitRegistryFitableRepositoryAsynDecorator::CanMergeBack(
    Fit::vector<Fit::list<std::shared_ptr<AsyncServiceInfo>>>& asyncServiceInfoSets,
    uint64_t index,
    AsyncServiceInfo::ServiceInfoState state)
{
    return (!asyncServiceInfoSets[index].empty() &&
        asyncServiceInfoSets[index].back() != nullptr &&
        asyncServiceInfoSets[index].back()->state == state);
}
bool FitRegistryFitableRepositoryAsynDecorator::AddSyncList(const db_service_info_t &dbService,
    AsyncServiceInfo::ServiceInfoState state)
{
    ClearSameFitable(dbService);
    if (dbService.service.addresses.empty()) {
        return false;
    }
    fit_fitable_key_t fitableKey;
    fitableKey.generic_id = dbService.service.fitable.generic_id;
    fitableKey.generic_version = dbService.service.fitable.generic_version;
    fitableKey.fitable_id = dbService.service.fitable.fitable_id;
    auto index = GetIdByAddress(dbService.service.addresses.front());
    Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
    if (CanMergeBack(asyncServiceInfoSets_, index, state)) {
        asyncServiceInfoSets_[index].back()->dbServiceSet.push_back(dbService);
    } else {
        std::shared_ptr<AsyncServiceInfo> asyncServiceInfoPtr = std::make_shared<AsyncServiceInfo>();
        asyncServiceInfoPtr->state = state;
        asyncServiceInfoPtr->dbServiceSet.emplace_back(dbService);
        asyncServiceInfoSets_[index].emplace_back(asyncServiceInfoPtr);
    }
    indexOfAsyncTaskByAddress_[dbService.service.addresses.front().id][fitableKey]
        = asyncServiceInfoSets_[index].back();
    asyncServiceInfoSetCondition_.notify_one();
    lock.unlock();
    return true;
}

bool FitRegistryFitableRepositoryAsynDecorator::AddSyncList(std::shared_ptr<AsyncServiceInfo> asyncServiceInfo,
    uint64_t index)
{
    Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
    asyncServiceInfoSets_[index].emplace_back(std::move(asyncServiceInfo));
    asyncServiceInfoSetCondition_.notify_one();
    lock.unlock();
    FIT_LOG_DEBUG("Remove by address");
    return true;
}

void FitRegistryFitableRepositoryAsynDecorator::SaveServiceSet(const db_service_set &services)
{
    int times = RETRY_OPERATE_DB_TIMES;
    if (!FitRegistryRepositoryDecorator::Save(services)) {
        FIT_LOG_DEBUG("SaveServiceSet failed.");
    }
}
void FitRegistryFitableRepositoryAsynDecorator::RemoveServiceSet(const db_service_set &services)
{
    FIT_LOG_DEBUG("RemoveServiceSet.");
    FitRegistryRepositoryDecorator::Remove(services);
}
void FitRegistryFitableRepositoryAsynDecorator::RemoveServiceByWorkerAddress(const Fit::fit_address &address)
{
    FIT_LOG_DEBUG("RemoveServiceByWorkerAddress.");
    FitRegistryRepositoryDecorator::Remove(address);
}
db_service_set FitRegistryFitableRepositoryAsynDecorator::Remove(const Fit::fit_address &address)
{
    // 清理worker address相等的请求
    RemoveSyncList(address);
    std::shared_ptr<AsyncServiceInfo> asyncServiceInfo = std::make_shared<AsyncServiceInfo>();
    asyncServiceInfo->workerAddress = address;
    asyncServiceInfo->state = AsyncServiceInfo::ServiceInfoState::STATE_REMOVE_BY_WORKER_ADDRESS;

    // 将根据worker地址清理服务的请求加入队列
    AddSyncList(asyncServiceInfo, GetIdByAddress(address));
    return db_service_set();
}

// 无ip和port，只有id可用
bool FitRegistryFitableRepositoryAsynDecorator::RemoveSyncList(const Fit::fit_address &address)
{
    FIT_LOG_CORE("RemoveSyncList ip:port:id[%s,%u,%s].", address.ip.c_str(), address.port, address.id.c_str());
    Fit::unique_lock<Fit::mutex> lock(asyncServiceInfoSetMutex_);
    auto fitableKeyAsyncServiceInfoMap = indexOfAsyncTaskByAddress_.find(address.id);
    if (fitableKeyAsyncServiceInfoMap == indexOfAsyncTaskByAddress_.end()) {
        return true;
    }
    for (const auto& asyncServiceInfoIt : fitableKeyAsyncServiceInfoMap->second) {
        auto asyncServiceInfo = asyncServiceInfoIt.second.lock();
        if (asyncServiceInfo == nullptr) {
            continue;
        }
        auto it = asyncServiceInfo->dbServiceSet.begin();
        for (; it != asyncServiceInfo->dbServiceSet.end();) {
            if (it->service.addresses.empty()) {
                it = asyncServiceInfo->dbServiceSet.erase(it);
                continue;
            }
            if (address.id == it->service.addresses.front().id) {
                it = asyncServiceInfo->dbServiceSet.erase(it);
                continue;
            }
            ++it;
        }
    }
    indexOfAsyncTaskByAddress_.erase(fitableKeyAsyncServiceInfoMap);
    return true;
}

uint64_t FitRegistryFitableRepositoryAsynDecorator::GetIdByAddress(const Fit::fit_address &address)
{
    if (Fit::Registry::GetRegistryFitableMemoryToDbConsumerThreadNum() > 0) { // LCOV_EXCL_BR_LINE
        return std::hash<Fit::string>()(address.id) % Fit::Registry::GetRegistryFitableMemoryToDbConsumerThreadNum();
    }
    return 0;
}
}
} // LCOV_EXCL_BR_LINE