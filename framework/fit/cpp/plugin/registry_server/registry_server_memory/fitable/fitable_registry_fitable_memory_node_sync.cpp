/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/08
 * Notes:       :
 */
#include "fitable_registry_fitable_memory_node_sync.h"
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <core/fit_registry_conf.h>
#include <registry_server_memory/common/util.h>
#include <registry_server_memory/common/registry_common_converter.hpp>
#include <fit/internal/registry/registry_util.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>
namespace Fit {
namespace Registry {
namespace {
using AppcationInstanceIndex = Fit::unordered_map<Fit::RegistryInfo::Application,
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance,
    Fit::RegistryInfo::ApplicationHash, Fit::RegistryInfo::ApplicationEqual>;
using AddressIndexByHost
    = Fit::unordered_map<Fit::string, ::fit::hakuna::kernel::registry::shared::Address>;
// 一个serviceInfo对应一个application
FitableRegistryFitableMemoryNodeSync::SyncFitableInstanceInnerPtr ConvertToFitableInstance(
    const db_service_info_t& serviceInfo, int operateType)
{
    FitableRegistryFitableMemoryNodeSync::SyncFitableInstanceInnerPtr syncFitableInstanceInnerPtr
        = std::make_shared<FitableRegistryFitableMemoryNodeSync::SyncFitableInstanceInner>();
    if (serviceInfo.service.addresses.empty()) {
        return nullptr;
    }

    FitableRegistryFitableMemoryNodeSync::FitableInstanceInner fitableInstanceInner {};
    fitableInstanceInner.fitable = RegistryUtil::ConvertFitableIdToHakunaFitable(serviceInfo.service.fitable);
    fitableInstanceInner.aliases = serviceInfo.service.aliases;
    fitableInstanceInner.tags = serviceInfo.service.tags;
    fitableInstanceInner.extensions = serviceInfo.service.extensions;

    FitableRegistryFitableMemoryNodeSync::ApplicationInstanceInner applicationInstanceInner {};
    applicationInstanceInner.application.name = serviceInfo.service.application.name;
    applicationInstanceInner.application.nameVersion = serviceInfo.service.application.nameVersion;
    TryFillApplicationMeta(applicationInstanceInner.application);

    Fit::vector<int32_t> formats;
    for (const auto& format : serviceInfo.service.addresses.front().formats) {
        formats.push_back(static_cast<int32_t>(format));
    }
    applicationInstanceInner.formats = formats;

    Fit::unordered_map<Fit::string, ::fit::hakuna::kernel::registry::shared::Worker> workerIndexById;
    Fit::unordered_map<Fit::string, AddressIndexByHost> addressIndexById;

    for (const auto& addressIn : serviceInfo.service.addresses) {
        ::fit::hakuna::kernel::registry::shared::Worker& worker = workerIndexById[addressIn.id];
        worker.id = addressIn.id;
        worker.expire = serviceInfo.service.timeoutSeconds;
        worker.environment = addressIn.environment;
        worker.extensions = addressIn.extensions;

        ::fit::hakuna::kernel::registry::shared::Endpoint endpoint;
        endpoint.port = addressIn.port;
        endpoint.protocol = static_cast<int32_t>(addressIn.protocol);

        ::fit::hakuna::kernel::registry::shared::Address& addressInTemp = addressIndexById[addressIn.id][addressIn.ip];
        addressInTemp.host = addressIn.ip;
        addressInTemp.endpoints.emplace_back(endpoint);
    }

    for (const auto& it : addressIndexById) {
        for (const auto& address : addressIndexById[it.first]) {
            workerIndexById[it.first].addresses.emplace_back(address.second);
        }
    }
    for (const auto& it : workerIndexById) {
        applicationInstanceInner.workers.emplace_back(it.second);
    }
    fitableInstanceInner.applicationInstances.emplace_back(applicationInstanceInner);
    syncFitableInstanceInnerPtr->fitableInstanceInner = fitableInstanceInner;
    syncFitableInstanceInnerPtr->operateType = operateType;
    return syncFitableInstanceInnerPtr;
}

Fit::vector<::fit::hakuna::kernel::registry::server::SyncSeviceAddress> BuildSyncAddresses(
    std::shared_ptr<Fit::vector<FitableRegistryFitableMemoryNodeSync::SyncFitableInstanceInnerPtr>>
    fitableInstancesPtr, ContextObj& ctx)
{
    // in param
    Fit::vector<::fit::hakuna::kernel::registry::server::SyncSeviceAddress> syncServiceAddresses;

    for (const auto& fitableInstancePtr : *fitableInstancesPtr) {
        if (fitableInstancePtr == nullptr) {
            continue;
        }
        ::fit::hakuna::kernel::registry::server::SyncSeviceAddress syncServiceAddress;
        syncServiceAddress.fitableInstance
            = Fit::Context::NewObj<::fit::hakuna::kernel::registry::shared::FitableInstance>(ctx);

        syncServiceAddress.operateType = fitableInstancePtr->operateType;
        syncServiceAddress.fitableInstance->fitable
            = Fit::Context::NewObj<::fit::hakuna::kernel::shared::Fitable>(ctx);

        *(syncServiceAddress.fitableInstance->fitable) = fitableInstancePtr->fitableInstanceInner.fitable;
        syncServiceAddress.fitableInstance->aliases = fitableInstancePtr->fitableInstanceInner.aliases;
        syncServiceAddress.fitableInstance->tags = fitableInstancePtr->fitableInstanceInner.tags;
        syncServiceAddress.fitableInstance->extensions = fitableInstancePtr->fitableInstanceInner.extensions;
        for (const auto& applicationInstance : fitableInstancePtr->fitableInstanceInner.applicationInstances) {
            ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstanceTemp;
            applicationInstanceTemp.application
                = Fit::Context::NewObj<::fit::hakuna::kernel::registry::shared::Application>(ctx);
            *(applicationInstanceTemp.application) = applicationInstance.application;
            applicationInstanceTemp.workers = applicationInstance.workers;
            applicationInstanceTemp.formats = applicationInstance.formats;
            syncServiceAddress.fitableInstance->applicationInstances.emplace_back(applicationInstanceTemp);
        }
        syncServiceAddresses.push_back(syncServiceAddress);
    }
    return syncServiceAddresses;
}

constexpr const int32_t MAX_SERVICE_PER_TIME = 100; // 满100个推一次
constexpr const int32_t MAX_INTERVAL_PER_TIME = 1000; // 不满100个，每秒推一次，单位ms
constexpr const int32_t SYNC_ADD = 0;
constexpr const int32_t SYNC_REMOVE = 1;
constexpr const int32_t TIMER_THREAD_POOL_NUM = 2; // 一个生产者，一个消费者
}
FitableRegistryFitableMemoryNodeSync::FitableRegistryFitableMemoryNodeSync()
{
}

FitableRegistryFitableMemoryNodeSync::~FitableRegistryFitableMemoryNodeSync()
{
    Stop();
}

bool FitableRegistryFitableMemoryNodeSync::Start()
{
    Stop();

    // 自定义单线程timer
    timeoutTimer_ = std::make_shared<Fit::timer>(std::make_shared<Fit::Thread::thread_pool>(TIMER_THREAD_POOL_NUM));
    syncWorker_ = std::make_shared<Fit::Thread::thread_pool>(GetRegistryFitableNodeSyncThreadNum());
    for (size_t i = 0; i < GetRegistryFitableNodeSyncThreadNum(); ++i) {
        serialExecutorSet_.emplace_back(std::make_shared<Fit::Thread::serial_executor>(syncWorker_));
    }
    if (timeoutTimer_ != nullptr) {
        taskId_ = timeoutTimer_->set_interval(MAX_INTERVAL_PER_TIME, [this]() {
            TimingSynchronizationService();
        });
    }
    FIT_LOG_INFO("Fitable replication is started.");
    return true;
}

bool FitableRegistryFitableMemoryNodeSync::Stop()
{
    if (timeoutTimer_ != nullptr) {
        timeoutTimer_->remove(taskId_);
        timeoutTimer_->stop();
    }

    if (syncWorker_ != nullptr) {
        syncWorker_->stop();
    }
    FIT_LOG_INFO("Fitable node sync stop.");
    return true;
}

int32_t FitableRegistryFitableMemoryNodeSync::Add(const db_service_set& serviceSet)
{
    return Sync(serviceSet, SYNC_ADD);
}
int32_t FitableRegistryFitableMemoryNodeSync::Remove(const db_service_set& serviceSet)
{
    return Sync(serviceSet, SYNC_REMOVE);
}
int32_t FitableRegistryFitableMemoryNodeSync::Sync(const db_service_set& serviceSet, int32_t operatorType)
{
    // get address list
    Fit::unique_lock<Fit::mutex> lock(fitableInstancesMutex_);
    // 如果满100个，推一次
    for (const db_service_info_t& it : serviceSet) {
        fitableInstances_.emplace_back(ConvertToFitableInstance(it, operatorType));
        FIT_LOG_DEBUG("Sync save [gid:fid] %s:%s  state %d, address size is %lu",
            it.service.fitable.generic_id.c_str(),
            it.service.fitable.fitable_id.c_str(), operatorType,
            it.service.addresses.size());
        if (fitableInstances_.size() != MAX_SERVICE_PER_TIME) {
            continue;
        }
        std::shared_ptr<Fit::vector<SyncFitableInstanceInnerPtr>> fitableInstancesPtr
            = std::make_shared<Fit::vector<SyncFitableInstanceInnerPtr>>();
        fitableInstancesPtr->swap(fitableInstances_);
        fitableInstances_.reserve(MAX_SERVICE_PER_TIME);
        SyncToOtherRegistryFitable(fitableInstancesPtr);
    }
    return REGISTRY_SUCCESS;
}

int32_t FitableRegistryFitableMemoryNodeSync::SyncToOtherRegistryFitable(
    std::shared_ptr<Fit::vector<SyncFitableInstanceInnerPtr>> fitableInstancesPtr)
{
    auto addressSet = GetRegistryAddresses(); // LCOV_EXCL_LINE
    for (const auto& address : addressSet) {
        auto executor = serialExecutorSet_[GetIdByAddress(address.workerId)];
        // 如果某个注册中心阻塞导致任务数超过上限，不再投递新的任务
        if (executor->task_num() >= GetMaxFitableNodeSyncTaskNum()) { // LCOV_EXCL_LINE
            FIT_LOG_ERROR("Task num is oversize %lu, ip is %s.", executor->task_num(), address.workerId.c_str());
            continue;
        }
        executor->execute(
            [address, fitableInstancesPtr, this]() {
                SyncFitable(fitableInstancesPtr, address);
            }
        );
    }
    return REGISTRY_SUCCESS;
}

int32_t FitableRegistryFitableMemoryNodeSync::SyncFitable(
    std::shared_ptr<Fit::vector<SyncFitableInstanceInnerPtr>> fitableInstancesPtr,
    const RegistryInfo::FlatAddress& address)
{
    fit::hakuna::kernel::registry::server::synchronizeFitService synchronizeFitServiceProxy;
    // in param
    Fit::vector<::fit::hakuna::kernel::registry::server::SyncSeviceAddress> syncServiceAddresses
        = BuildSyncAddresses(fitableInstancesPtr, synchronizeFitServiceProxy.ctx_);
    // call
    Context::TargetAddress targetAddress = RegistryCommonConverter::ConvertToTargetAddress(address);
    synchronizeFitServiceProxy.SetTargetAddress(&targetAddress);
    synchronizeFitServiceProxy.SetFitableId("202954b6897a4e2da49aa29ac572f5fb");
    int32_t *result {};
    auto ret = synchronizeFitServiceProxy(&syncServiceAddresses, &result);
    if (ret != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Failed to sync. ret=%x, ip:port:protocol=%s:%d:%d.", ret, address.host.c_str(), address.port,
            static_cast<int32_t>(address.protocol));
    }

    return ret;
}

void FitableRegistryFitableMemoryNodeSync::TimingSynchronizationService()
{
    Fit::unique_lock<Fit::mutex> lock(fitableInstancesMutex_);
    if (fitableInstances_.empty()) {
        return;
    }
    std::shared_ptr<Fit::vector<SyncFitableInstanceInnerPtr>> fitableInstancesPtr
        = std::make_shared<Fit::vector<SyncFitableInstanceInnerPtr>>();
    fitableInstancesPtr->swap(fitableInstances_);
    fitableInstances_.reserve(MAX_SERVICE_PER_TIME);
    lock.unlock();
    SyncToOtherRegistryFitable(fitableInstancesPtr);
    FIT_LOG_DEBUG("TimingSynchronizationService to other registry, size is %lu.", fitableInstancesPtr->size());
}

uint64_t FitableRegistryFitableMemoryNodeSync::GetIdByAddress(const string& workerId) const
{
    if (Fit::Registry::GetRegistryFitableNodeSyncThreadNum() > 0) {
        return std::hash<Fit::string>()(workerId) % Fit::Registry::GetRegistryFitableNodeSyncThreadNum();
    }
    return 0;
}
}
} // LCOV_EXCL_LINE