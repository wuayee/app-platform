/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/09
 * Notes:       :
 */
#include "fit_subscriber_repository_memory.h"
#include <core/fit_registry_conf.h>
#include <fit/fit_log.h>
namespace Fit {
namespace Registry {
namespace {
constexpr const size_t SECONDE_TO_MILLI_SENCOND = 1000;
constexpr const size_t INIT_MEMORY_TIMEOUT = 1; // 第一次从数据库初始化内存，时间为1s
constexpr const size_t SYNC_MEMORY_INTERVAL = 300; // 5分钟从数据库同步一次数据
}
FitSubscriberRepositoryMemory::FitSubscriberRepositoryMemory(fit_subscription_repository_ptr subscriptionRepository,
    FitSubscriptionNodeSyncPtr fitSubscriptionNodeSyncPtr)
    : subscriptionRepository_(std::move(subscriptionRepository)),
    fitSubscriptionNodeSyncPtr_(std::move(fitSubscriptionNodeSyncPtr))
{
}
FitSubscriberRepositoryMemory::~FitSubscriberRepositoryMemory()
{
    Stop();
}
bool FitSubscriberRepositoryMemory::Start()
{
    Stop();
    timer_ = Fit::timer_instance();
    InitMemory();

    if (subscriptionRepository_ != nullptr) {
        subscriptionRepository_->Start();
    }
    if (fitSubscriptionNodeSyncPtr_ != nullptr) {
        fitSubscriptionNodeSyncPtr_->Start();
    }
    FIT_LOG_INFO("Subscriber repo start.");
    return true;
}
bool FitSubscriberRepositoryMemory::Stop()
{
    if (timer_ != nullptr) {
        timer_->remove(initHandle_);
        timer_->remove(syncSubscriptionInfoFromDbTaskHandle_);
    }
    if (subscriptionRepository_ != nullptr) {
        subscriptionRepository_->Stop();
    }
    if (fitSubscriptionNodeSyncPtr_ != nullptr) {
        fitSubscriptionNodeSyncPtr_->Stop();
    }
    FIT_LOG_INFO("Subscriber repo stop.");
    return true;
}

int32_t FitSubscriberRepositoryMemory::insert_subscription_entry(const fit_fitable_key_t &key,
    const listener_t &listener)
{
    if (SyncInsertSubscriptionEntry(key, listener) == REGISTRY_EXIST) {
        return REGISTRY_SUCCESS;
    }
    // 同步到其他节点
    if (fitSubscriptionNodeSyncPtr_ != nullptr) {
        fitSubscriptionNodeSyncPtr_->Add(key, listener);
    }
    // 同步到持久化层db
    if (subscriptionRepository_ != nullptr) {
        subscriptionRepository_->insert_subscription_entry(key, listener);
    }
    return REGISTRY_SUCCESS;
}

int32_t FitSubscriberRepositoryMemory::remove_subscription_entry(const fit_fitable_key_t &key,
    const listener_t &listener)
{
    if (IsListenerExist(key, listener)) {
        SyncRemoveSubscriptionEntry(key, listener); // 删除内存
    }
    // 同步到其他节点
    if (fitSubscriptionNodeSyncPtr_ != nullptr) {
        FIT_LOG_INFO("remove_subscription_entry other node.");
        fitSubscriptionNodeSyncPtr_->Remove(key, listener);
    }
    // 同步到持久化
    if (subscriptionRepository_ != nullptr) {
        subscriptionRepository_->remove_subscription_entry(key, listener);
    }
    return REGISTRY_SUCCESS;
}

db_subscription_set FitSubscriberRepositoryMemory::query_subscription_set(const fit_fitable_key_t &key)
{
    db_subscription_set result;
    {
        Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
        auto listenerSet = fitableListenerMap_.find(key);
        if (listenerSet == fitableListenerMap_.end()) {
            return result;
        }

        db_subscription_entry_t subscriptionEntry;
        for (const auto& it : listenerSet->second) {
            subscriptionEntry.fitable_key = key;
            subscriptionEntry.listener = it;
            result.emplace_back(subscriptionEntry);
        }
    }
    if (result.empty() && subscriptionRepository_ != nullptr) {
        // 从持久化层查询
        result = subscriptionRepository_->query_subscription_set(key);
    }
    return result;
}

db_subscription_set FitSubscriberRepositoryMemory::query_all_subscriptions() const
{
    db_subscription_set result;
    Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
    for (const auto& listenerSet : fitableListenerMap_) {
        for (const auto& listener : listenerSet.second) {
            db_subscription_entry_t subscriptionEntry;
            subscriptionEntry.fitable_key = listenerSet.first;
            subscriptionEntry.listener = listener;
            result.emplace_back(subscriptionEntry);
        }
    }
    lock.unlock();
    return result;
}

listener_set FitSubscriberRepositoryMemory::query_listener_set(const fit_fitable_key_t &key)
{
    listener_set result;
    {
        Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
        auto listenerSet = fitableListenerMap_.find(key);
        if (listenerSet == fitableListenerMap_.end()) {
            return result;
        }

        for (const auto& it : listenerSet->second) {
            result.emplace_back(it);
        }
    }
    if (result.empty() && subscriptionRepository_ != nullptr) {
        // 从持久化层查询
        result = subscriptionRepository_->query_listener_set(key);
    }
    return result;
}

int32_t FitSubscriberRepositoryMemory::query_subscription_entry(const fit_fitable_key_t &key,
    const listener_t &listener,
    db_subscription_entry_t &resultSubscriptionEntry) const
{
    bool exist = false;
    {
        Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
        auto listenerSet = fitableListenerMap_.find(key);
        if (listenerSet != fitableListenerMap_.end()) {
            auto listenerIt = listenerSet->second.find(listener);
            if (listenerIt != listenerSet->second.end()) {
                resultSubscriptionEntry.fitable_key = key;
                resultSubscriptionEntry.listener = *listenerIt;
                exist = true;
            }
        }
    }
    if (!exist && subscriptionRepository_ != nullptr) {
        // 从db中查询
        return subscriptionRepository_->query_subscription_entry(key, listener, resultSubscriptionEntry);
    }
    return REGISTRY_SUCCESS;
}

int32_t FitSubscriberRepositoryMemory::SyncInsertSubscriptionEntry(
    const fit_fitable_key_t &key, const listener_t &listener)
{
    FIT_LOG_DEBUG("SyncInsertSubscriptionEntry gid:fid (%s:%s), listener ip:port:workerid (%s:%d:%s).",
        key.generic_id.c_str(), key.fitable_id.c_str(),
        listener.address.ip.c_str(), listener.address.port, listener.address.id.c_str());
    if (IsListenerExist(key, listener)) {
        FIT_LOG_DEBUG("SyncInsertSubscriptionEntry already exist.");
        UpdateSyncCount(key, listener, listener.syncCount);
        return REGISTRY_EXIST;
    }
    {
        Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
        fitableListenerMap_[key].insert(listener);
    }
    return REGISTRY_SUCCESS;
}
int32_t FitSubscriberRepositoryMemory::SyncRemoveSubscriptionEntry(
    const fit_fitable_key_t &key, const listener_t &listener)
{
    FIT_LOG_INFO("SyncRemoveSubscriptionEntry [gid:fid] %s:%s, listener [ip:port] %s:%d.",
        key.generic_id.c_str(), key.fitable_id.c_str(),
        listener.address.ip.c_str(), listener.address.port);
    {
        Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
        auto listenerSet = fitableListenerMap_.find(key);
        if (listenerSet == fitableListenerMap_.end()) {
            return REGISTRY_SUCCESS;
        }
        auto listenerIt = listenerSet->second.find(listener);
        if (listenerIt != listenerSet->second.end()) {
            listenerSet->second.erase(listenerIt);
        }
        if (listenerSet->second.empty()) {
            fitableListenerMap_.erase(listenerSet);
        }
    }
    return REGISTRY_SUCCESS;
}
bool FitSubscriberRepositoryMemory::IsListenerExist(const fit_fitable_key_t &key, const listener_t &listener)
{
    Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
    auto listenerSet = fitableListenerMap_.find(key);
    if (listenerSet == fitableListenerMap_.end()) {
        return false;
    }

    auto listenerIt = listenerSet->second.find(listener);
    if (listenerIt != listenerSet->second.end()) {
        return true;
    }
    return false;
}

void FitSubscriberRepositoryMemory::UpdateSyncCount(
    const fit_fitable_key_t &key, const listener_t &listener, uint64_t syncCount)
{
    Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
    auto listenerSet = fitableListenerMap_.find(key);
    if (listenerSet == fitableListenerMap_.end()) {
        return;
    }

    auto listenerIt = listenerSet->second.find(listener);
    if (listenerIt != listenerSet->second.end() && syncCount > listenerIt->syncCount) {
        listenerSet->second.erase(listenerIt);
        listener_t temp = listener;
        temp.syncCount = syncCount;
        listenerSet->second.emplace(temp);
    }
}

void FitSubscriberRepositoryMemory::InitMemory()
{
    if (timer_ != nullptr) {
        initHandle_ = timer_->set_timeout(INIT_MEMORY_TIMEOUT * SECONDE_TO_MILLI_SENCOND, [this]() {
            SyncSubscriptionInfoWithDb();
            CreateSyncSubscriptionWithDbTask();
        });
    }
}
void FitSubscriberRepositoryMemory::CreateSyncSubscriptionWithDbTask()
{
    if (timer_ != nullptr) {
        syncSubscriptionInfoFromDbTaskHandle_ = timer_->set_interval(
            SYNC_MEMORY_INTERVAL * SECONDE_TO_MILLI_SENCOND,
            [this]() {
                SyncSubscriptionInfoWithDb();
            });
    }
}
void FitSubscriberRepositoryMemory::MarkDBToMemoryOrder(db_subscription_set& subscriptionSet, uint64_t syncCount)
{
    for (auto& it : subscriptionSet) {
        it.listener.syncCount = syncCount;
    }
}
db_subscription_set FitSubscriberRepositoryMemory::GetSubscriptionExcludeDB(uint64_t syncCount)
{
    db_subscription_set subscriptionSet;
    {
        Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
        for (const auto& listenerSet : fitableListenerMap_) {
            for (const auto& listenerIt : listenerSet.second) {
                if (listenerIt.syncCount != syncCount) {
                    db_subscription_entry_t subscriptionEntry;
                    subscriptionEntry.listener = listenerIt;
                    subscriptionEntry.fitable_key = listenerSet.first;
                    subscriptionSet.emplace_back(subscriptionEntry);
                }
            }
        }
    }
    return subscriptionSet;
}
void FitSubscriberRepositoryMemory::SyncSubscriptionInfoWithDb()
{
    if (subscriptionRepository_ != nullptr) {
        // 从持久化层查询
        auto result = subscriptionRepository_->query_all_subscriptions();
        ++syncCount_;
        // 更新result
        MarkDBToMemoryOrder(result, syncCount_);
        FIT_LOG_DEBUG("result size is %lu.", result.size());
        for (const auto& it : result) {
            SyncInsertSubscriptionEntry(it.fitable_key, it.listener);
        }
    }
    // 如果不是主节点返回
    if (!IsMaster()) { // LCOV_EXCL_BR_LINE
        return;
    }
    // 如果是主节点，查找所有的打标不一致的lisenter
    db_subscription_set subscriptionSet = GetSubscriptionExcludeDB(syncCount_);
    for (const auto& it : subscriptionSet) {
        if (subscriptionRepository_ != nullptr) {
            subscriptionRepository_->insert_subscription_entry(it.fitable_key, it.listener);
        }
    }
}

Fit::unordered_set<Fit::string> FitSubscriberRepositoryMemory::query_all_listener_ids() const
{
    Fit::unordered_set<Fit::string> listenerIds;
    Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
    for (const auto& listenerSet : fitableListenerMap_) {
        for (const auto& listener : listenerSet.second) {
            listenerIds.insert(listener.address.id);
        }
    }
    return listenerIds;
}
ListenerSet FitSubscriberRepositoryMemory::query_all_listeners() const
{
    ListenerSet result;
    Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
    for (const auto& listenerSet : fitableListenerMap_) {
        for (const auto& listener : listenerSet.second) {
            result.insert(listener);
        }
    }
    return result;
}
db_subscription_set FitSubscriberRepositoryMemory::query_subscriptions_by_ids(
    const Fit::unordered_set<Fit::string>& listenerIds)
{
    db_subscription_set result;
    db_subscription_entry_t subscriptionEntry;
    Fit::unique_lock<Fit::mutex> lock(fitableListenerMapMutex_);
    for (const auto& listenerSet : fitableListenerMap_) {
        for (const auto& listener : listenerSet.second) {
            if (listenerIds.count(listener.address.id) != 0) {
                subscriptionEntry.fitable_key = listenerSet.first;
                subscriptionEntry.listener = listener;
                result.emplace_back(subscriptionEntry);
            }
        }
    }
    return result;
}
int32_t FitSubscriberRepositoryMemory::remove_subscription_entry(const Fit::unordered_set<Fit::string>& listenerIds)
{
    db_subscription_set subscriptionSet = query_subscriptions_by_ids(listenerIds);
    for (const auto& subscription : subscriptionSet) {
        if (IsListenerExist(subscription.fitable_key, subscription.listener)) {
            SyncRemoveSubscriptionEntry(subscription.fitable_key, subscription.listener); // 删除内存
        }
        if (!IsMaster()) {
            continue;
        }
        // 同步到持久化
        if (subscriptionRepository_ != nullptr) {
            subscriptionRepository_->remove_subscription_entry(subscription.fitable_key, subscription.listener);
        }
    }
    return FIT_OK;
}
}
} // LCOV_EXCL_BR_LINE