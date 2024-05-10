/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/15
 * Notes:       :
 */
#include "fit_subscriber_repository_memory_node_sync.h"
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <fit/internal/fit_system_property_utils.h>
#include <core/fit_registry_conf.h>
#include <registry_server_memory/common/util.h>
#include <registry_server_memory/common/registry_common_converter.hpp>
#include <genericable/com_huawei_fit_registry_get_registry_addresses/1.0.0/cplusplus/getRegistryAddresses.hpp>

namespace Fit {
namespace Registry {
namespace {
FitSubscriberRepositoryMemoryNodeSync::SyncSubscriptionServiceInner ConvertToSyncServiceAddress(
    const fit_fitable_key_t &key, const listener_t &listener, int32_t operatorType)
{
    FitSubscriberRepositoryMemoryNodeSync::SyncSubscriptionServiceInner syncSubscriptionService {};
    syncSubscriptionService.fitable.genericId = key.generic_id;
    syncSubscriptionService.fitable.genericVersion = key.generic_version;
    syncSubscriptionService.fitable.fitId = key.fitable_id;
    syncSubscriptionService.listenerAddress.host = listener.address.ip;
    syncSubscriptionService.listenerAddress.port = listener.address.port;
    syncSubscriptionService.listenerAddress.protocol = static_cast<int32_t>(listener.address.protocol);
    syncSubscriptionService.listenerAddress.id = listener.address.id;
    syncSubscriptionService.listenerAddress.environment = listener.address.environment;
    for (const auto& format : listener.address.formats) {
        syncSubscriptionService.listenerAddress.formats.push_back(static_cast<int32_t>(format));
    }
    syncSubscriptionService.callbackFitId = listener.fitable_id;
    syncSubscriptionService.operateType = operatorType;
    return syncSubscriptionService;
}

void BuildSyncSubscriptionServices(
    std::shared_ptr<Fit::vector<FitSubscriberRepositoryMemoryNodeSync::SyncSubscriptionServiceInner>>
    syncSubscriptionServicesPtr, ContextObj& ctx,
    Fit::vector<fit::hakuna::kernel::registry::server::SyncSubscriptionService>& syncSubscriptionServicesTemp)
{
    for (const auto &it : *syncSubscriptionServicesPtr) {
        fit::hakuna::kernel::registry::server::SyncSubscriptionService syncSubscriptionServiceTemp;
        syncSubscriptionServiceTemp.callbackFitId = it.callbackFitId;
        syncSubscriptionServiceTemp.fitable = Fit::Context::NewObj<::fit::registry::Fitable>(ctx);
        if (syncSubscriptionServiceTemp.fitable == nullptr) {
            FIT_LOG_ERROR("New result failed.");
            continue;
        }
        *(syncSubscriptionServiceTemp.fitable) = it.fitable;
        syncSubscriptionServiceTemp.listenerAddress = Fit::Context::NewObj<::fit::registry::Address>(ctx);
        if (syncSubscriptionServiceTemp.listenerAddress == nullptr) {
            FIT_LOG_ERROR("New result failed.");
            continue;
        }
        *(syncSubscriptionServiceTemp.listenerAddress) = it.listenerAddress;
        syncSubscriptionServiceTemp.operateType = it.operateType;
        syncSubscriptionServicesTemp.emplace_back(syncSubscriptionServiceTemp);
    }
}

constexpr const int32_t SYNC_ADD = 0;
constexpr const int32_t SYNC_REMOVE = 1;
constexpr const int32_t TIMER_THREAD_POOL_NUM = 2;
}
FitSubscriberRepositoryMemoryNodeSync::FitSubscriberRepositoryMemoryNodeSync(
    int32_t minServicePerTime, int32_t maxIntervalPerTime)
    : minServicePerTime_(minServicePerTime),
    maxIntervalPerTime_(maxIntervalPerTime)
{
}

FitSubscriberRepositoryMemoryNodeSync::~FitSubscriberRepositoryMemoryNodeSync()
{
    Stop();
}
bool FitSubscriberRepositoryMemoryNodeSync::Start()
{
    Stop();
    // 自定义单线程timer
    timerWorker_ = std::make_shared<Fit::Thread::thread_pool>(TIMER_THREAD_POOL_NUM);
    timeoutTimer_ = std::make_shared<Fit::timer>(timerWorker_);
    syncWorker_ = std::make_shared<Fit::Thread::thread_pool>(GetRegistrySubscriptionNodeSyncThreadNum());
    for (size_t i = 0; i < GetRegistrySubscriptionNodeSyncThreadNum(); ++i) {
        serialExecutorSet_.emplace_back(std::make_shared<Fit::Thread::serial_executor>(syncWorker_));
    }
    if (timeoutTimer_ != nullptr) {
        taskId_ = timeoutTimer_->set_interval(maxIntervalPerTime_, [this]() {
            TimeoutSync();
        });
    }
    FIT_LOG_INFO("Subscriber node sync start.");
    return true;
}
bool FitSubscriberRepositoryMemoryNodeSync::Stop()
{
    for (auto& it : serialExecutorSet_) {
        if (it != nullptr) {
            it->clear();
        }
    }
    if (timeoutTimer_ != nullptr) {
        timeoutTimer_->remove(taskId_);
        timeoutTimer_->stop();
    }
    if (timerWorker_ != nullptr) {
        timerWorker_->stop();
    }
    if (syncWorker_ != nullptr) {
        syncWorker_->stop();
    }
    FIT_LOG_INFO("Subscriber node sync stop.");
    return true;
}

int32_t FitSubscriberRepositoryMemoryNodeSync::Add(const fit_fitable_key_t &key, const listener_t &listener)
{
    FIT_LOG_DEBUG("Save [gid:fid] %s:%s, listener [ip:port] %s:%d.",
        key.generic_id.c_str(), key.fitable_id.c_str(),
        listener.address.ip.c_str(), listener.address.port);
    return Sync(key, listener, SYNC_ADD);
}
int32_t FitSubscriberRepositoryMemoryNodeSync::Remove(const fit_fitable_key_t &key, const listener_t &listener)
{
    FIT_LOG_DEBUG("Remove [gid:fid] %s:%s, listener [ip:port] %s:%d.",
        key.generic_id.c_str(), key.fitable_id.c_str(),
        listener.address.ip.c_str(), listener.address.port);
    return Sync(key, listener, SYNC_REMOVE);
}

// 将add和remove两种操作合并处理
int32_t FitSubscriberRepositoryMemoryNodeSync::Sync(
    const fit_fitable_key_t &key, const listener_t &listener, int32_t operatorType)
{
    Fit::unique_lock<Fit::mutex> lock(syncSubscriptionServiceSetMutex_);

    // 如果满100个，推一次
    syncSubscriptionServiceSet_.emplace_back(ConvertToSyncServiceAddress(key, listener, operatorType));
    if (static_cast<int32_t>(syncSubscriptionServiceSet_.size()) == minServicePerTime_) {
        std::shared_ptr<Fit::vector<SyncSubscriptionServiceInner>> syncSubscriptionServicesPtr
            = std::make_shared<Fit::vector<SyncSubscriptionServiceInner>>();
        syncSubscriptionServicesPtr->swap(syncSubscriptionServiceSet_);
        syncSubscriptionServiceSet_.reserve(minServicePerTime_);
        PushToQueue(syncSubscriptionServicesPtr);
    }
    return FIT_ERR_SUCCESS;
}
// 将数据推导队列中
int32_t FitSubscriberRepositoryMemoryNodeSync::PushToQueue(
    std::shared_ptr<Fit::vector<SyncSubscriptionServiceInner>> syncSubscriptionServicesPtr)
{
    auto addressSet = GetRegistryAddresses();
    for (const auto& address : addressSet) {
        auto executor = serialExecutorSet_[GetIdByAddress(address.workerId)];
        // 如果某个注册中心阻塞导致任务数超过上限，不再投递新的任务
        if (executor->task_num() >= GetMaxSubscriptionNodeSyncTaskNum()) { // LCOV_EXCL_BR_LINE
            FIT_LOG_ERROR("Task num is oversize %lu, ip is %s.", executor->task_num(), address.workerId.c_str());
            continue;
        }
        (executor)->execute(
            [this, syncSubscriptionServicesPtr, address]() {
                SyncToOtherRegistry(syncSubscriptionServicesPtr, address);
            }
        );
    }
    return FIT_ERR_SUCCESS;
}

int32_t FitSubscriberRepositoryMemoryNodeSync::SyncToOtherRegistry(
    std::shared_ptr<Fit::vector<SyncSubscriptionServiceInner>> syncSubscriptionServicesPtr,
    const RegistryInfo::FlatAddress& address)
{
    fit::hakuna::kernel::registry::server::syncSubscriptionFitService syncSubscriptionFitServiceProxy;
    Fit::vector<fit::hakuna::kernel::registry::server::SyncSubscriptionService> syncSubscriptionServicesTemp;

    BuildSyncSubscriptionServices(syncSubscriptionServicesPtr,
        syncSubscriptionFitServiceProxy.ctx_, syncSubscriptionServicesTemp);

    Context::TargetAddress targetAddress = RegistryCommonConverter::ConvertToTargetAddress(address);
    syncSubscriptionFitServiceProxy.SetTargetAddress(&targetAddress);
    syncSubscriptionFitServiceProxy.SetFitableId("3e259b5ed7f84aa692e297806b2f0901");
    int32_t *result {};
    auto ret = syncSubscriptionFitServiceProxy(&syncSubscriptionServicesTemp, &result);
    if (ret != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Failed to sync. ret=%x, ip:port:protocol=%s:%d:%d.", ret, address.host.c_str(), address.port,
            static_cast<int32_t>(address.protocol));
    }

    return ret;
}
void FitSubscriberRepositoryMemoryNodeSync::TimeoutSync()
{
    Fit::unique_lock<Fit::mutex> lock(syncSubscriptionServiceSetMutex_);
    if (syncSubscriptionServiceSet_.empty()) {
        return;
    }
    std::shared_ptr<Fit::vector<SyncSubscriptionServiceInner>> syncSubscriptionServicesPtr
        = std::make_shared<Fit::vector<SyncSubscriptionServiceInner>>();
    syncSubscriptionServicesPtr->swap(syncSubscriptionServiceSet_);
    syncSubscriptionServiceSet_.reserve(minServicePerTime_);
    lock.unlock();
    PushToQueue(syncSubscriptionServicesPtr);
}
uint64_t FitSubscriberRepositoryMemoryNodeSync::GetIdByAddress(const string& workerId)
{
    if (Fit::Registry::GetRegistrySubscriptionNodeSyncThreadNum() > 0) { // LCOV_EXCL_LINE
        return std::hash<Fit::string>()(workerId.c_str()) % // LCOV_EXCL_LINE
            Fit::Registry::GetRegistrySubscriptionNodeSyncThreadNum(); // LCOV_EXCL_LINE
    }
    return 0;
}
}
} // LCOV_EXCL_BR_LINE