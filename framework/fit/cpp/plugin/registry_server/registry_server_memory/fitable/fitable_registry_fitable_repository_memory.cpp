/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/08
 * Notes:       :
 */
#include "fitable_registry_fitable_repository_memory.h"
#include <fit/fit_log.h>
#include <utility>
#include <core/fit_registry_conf.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/registry/registry_util.h>
#include <registry_server_memory/common/util.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>

namespace Fit {
namespace Registry {
namespace {
constexpr const size_t INIT_MEMORY_TIMEOUT = 1; // 第一次从数据库初始化内存，时间为1s
constexpr const size_t SYNC_MEMORY_INTERVAL = 300; // 5分钟从数据库同步一次数据

bool IsSyncFitable(const db_service_info_t &service)
{
    Fit::fitable_id fitable;
    fitable.generic_id
        = fit::hakuna::kernel::registry::server::synchronizeFitService::GENERIC_ID;
    fitable.fitable_id = "202954b6897a4e2da49aa29ac572f5fb";
    fitable.generic_version = "1.0.0";
    fit::registry::Address localAddress = FitSystemPropertyUtils::Address();
    // 不是本地址的sync服务，不处理
    if (!(service.service.fitable.generic_id == fitable.generic_id &&
        service.service.fitable.fitable_id == fitable.fitable_id &&
        service.service.fitable.generic_version == fitable.generic_version &&
        service.service.addresses.front().id == localAddress.id)) {
        return false;
    }
    return true;
}
}
FitRegistryFitableRepositoryMemory::FitRegistryFitableRepositoryMemory(
    FitRegistryServiceRepositoryPtr serviceRepository,
    FitableRegistryFitableNodeSyncPtr fitableNodeSyncPtr,
    FitRegistryServiceRepositoryPtr syncServiceRepository,
    FitFitableMemoryRepositoryPtr serviceMemoryRepository)
    : serviceRepository_(std::move(serviceRepository)),
    registrySyncService_(std::move(fitableNodeSyncPtr)),
    syncServiceRepository_(std::move(syncServiceRepository)),
    serviceMemoryRepository_(std::move(serviceMemoryRepository))
{
}

FitRegistryFitableRepositoryMemory::~FitRegistryFitableRepositoryMemory()
{
    Stop();
}

bool FitRegistryFitableRepositoryMemory::Start()
{
    Stop();
    timer_ = Fit::timer_instance();
    InitMemory();

    if (serviceRepository_ != nullptr) {
        serviceRepository_->Start();
    }
    if (registrySyncService_ != nullptr) {
        registrySyncService_->Start();
    }
    if (syncServiceRepository_ != nullptr) {
        syncServiceRepository_->Start();
    }
    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->Start();
    }
    FIT_LOG_INFO("Fitable repo is started.");
    return true;
}

bool FitRegistryFitableRepositoryMemory::Stop()
{
    if (timer_ != nullptr) {
        timer_->remove(syncServiceInfoFromDbTaskTimeoutHandle_);
        timer_->remove(syncServiceInfoFromDbTaskHandle_);
    }
    if (serviceRepository_ != nullptr) {
        serviceRepository_->Stop();
    }
    if (registrySyncService_ != nullptr) {
        registrySyncService_->Stop();
    }
    if (syncServiceRepository_ != nullptr) {
        syncServiceRepository_->Stop();
    }
    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->Stop();
    }
    FIT_LOG_INFO("Fitable repo stop.");
    return true;
}

bool FitRegistryFitableRepositoryMemory::Save(const db_service_info_t &dbService)
{
    if (dbService.service.addresses.empty()) {
        FIT_LOG_ERROR("Address is empty, gid:fid [%s,%s].", dbService.service.fitable.generic_id.c_str(),
            dbService.service.fitable.fitable_id.c_str());
        return true;
    }

    // 在第一次注册服务前，初始化
    if (!ReadyForWorking(dbService)) {
        return true;
    }
    // 返回already exist
    auto retSyncSave = SaveServiceToMemory(dbService);

    // sync to other
    db_service_set serviceSet;
    serviceSet.push_back(dbService);
    if (registrySyncService_ != nullptr) {
        registrySyncService_->Add(serviceSet);
    }
    // 每次renew，都同步到其他节点，只有超时周期变更时，才同步到db
    if ((retSyncSave != REGISTRY_EXIST) && serviceRepository_ != nullptr) {
        serviceRepository_->Save(dbService);
    }
    return true;
}
bool FitRegistryFitableRepositoryMemory::Save(const db_service_set &services)
{
    {
        for (const db_service_info_t& it : services) {
            // 保存单个的服务
            Save(it);
        }
    }
    return true;
}

db_service_set FitRegistryFitableRepositoryMemory::Query(const fit_fitable_key_t &key)
{
    db_service_set result = serviceMemoryRepository_->Query(key);
    if (result.empty() && serviceRepository_ != nullptr) {
        result = serviceRepository_->Query(key);
    }
    FIT_LOG_DEBUG("Query by key [gid:fid] %s:%s, size is %lu.",
        key.generic_id.c_str(), key.fitable_id.c_str(), result.size());
    return result;
}

bool FitRegistryFitableRepositoryMemory::Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address)
{
    fit_fitable_key_t key;
    key.generic_id = fitable.generic_id;
    key.generic_version = fitable.generic_version;
    key.fitable_id = fitable.fitable_id;
    db_service_set serviceSetSync = QueryService(key, address); // 从内存中查询
    Remove(serviceSetSync);
    return true;
}

bool FitRegistryFitableRepositoryMemory::Remove(const db_service_set &services)
{
    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->Remove(services);
    }

    if (registrySyncService_ != nullptr) {
        registrySyncService_->Remove(services);
    }
    // 内存中没有，数据库中可能有
    if (serviceRepository_ != nullptr) {
        serviceRepository_->Remove(services);
    }
    return true;
}
db_service_set FitRegistryFitableRepositoryMemory::GetAllServices()
{
    return db_service_set();
}
int32_t FitRegistryFitableRepositoryMemory::SaveServiceToMemory(const db_service_info_t &dbService)
{
    if (serviceMemoryRepository_ == nullptr) {
        return FIT_ERR_FAIL;
    }

    int32_t ret = serviceMemoryRepository_->IsServiceAlreadyExist(dbService) ? REGISTRY_EXIST : REGISTRY_SUCCESS;
    serviceMemoryRepository_->Save(dbService);
    return ret;
}


int32_t FitRegistryFitableRepositoryMemory::SyncSave(const db_service_set &services)
{
    for (const auto& service : services) {
        SyncSave(service);
    }
    return REGISTRY_SUCCESS;
}

int32_t FitRegistryFitableRepositoryMemory::SyncRemove(const db_service_set &services)
{
    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->Remove(services);
    }
    return REGISTRY_SUCCESS;
}

int32_t FitRegistryFitableRepositoryMemory::SyncSave(const db_service_info_t &service)
{
    FIT_LOG_DEBUG("Sync save from other gid %s.", service.service.fitable.generic_id.c_str());
    int32_t ret = REGISTRY_SUCCESS;
    auto oldServiceSet = serviceMemoryRepository_->Query(service);
    if (!oldServiceSet.empty()) {
        auto serviceTemp = oldServiceSet.front();
        if (serviceTemp.service.timeoutSeconds == service.service.timeoutSeconds) {
            ret = REGISTRY_EXIST;
        }
    }

    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->Save(service);
    }
    return REGISTRY_SUCCESS;
}

int32_t FitRegistryFitableRepositoryMemory::SyncRemove(const db_service_info_t &service)
{
    if (service.service.addresses.empty()) {
        FIT_LOG_ERROR("Address is empty, gid:fid [%s,%s].", service.service.fitable.generic_id.c_str(),
            service.service.fitable.fitable_id.c_str());
        return REGISTRY_ERROR;
    }
    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->Remove(service.service.fitable, service.service.addresses.front());
    }
    return REGISTRY_SUCCESS;
}

db_service_set FitRegistryFitableRepositoryMemory::QueryService(const fit_fitable_key_t &key,
    const Fit::fit_address &address)
{
    db_service_set serviceSet;
    Fit::fitable_id fitable;
    fitable.generic_id = key.generic_id;
    fitable.generic_version = key.generic_version;
    fitable.fitable_id = key.fitable_id;
    fitable.fitable_version = "1.0.0";
    if (serviceMemoryRepository_ != nullptr) {
        serviceSet = serviceMemoryRepository_->Query(fitable, address);
    }
    return serviceSet;
}

bool FitRegistryFitableRepositoryMemory::InitTimeoutCallback(std::function<void(const db_service_set &)> callback)
{
    timeOutCallback_ = callback;
    if (serviceMemoryRepository_ != nullptr) {
        serviceMemoryRepository_->InitTimeoutCallback([this](const db_service_set &serviceInfo) {
            ServiceTimeoutCallback(serviceInfo);
        });
    }
    return true;
}

bool FitRegistryFitableRepositoryMemory::InsertServiceOrUpdateSyncCount(const db_service_set &services)
{
    if (serviceMemoryRepository_ != nullptr) {
        return serviceMemoryRepository_->InsertServiceOrUpdateSyncCount(services);
    }
    return false;
}

void FitRegistryFitableRepositoryMemory::ServiceTimeoutCallback(const db_service_set& serviceInfo)
{
    // 只有主节点删除db，并通知出去
    if (!IsMaster()) { // LCOV_EXCL_BR_LINE
        return;
    }
    // 删除数据库
    if (serviceRepository_ != nullptr) {
        serviceRepository_->Remove(db_service_set {serviceInfo});
    }
    // 通知给各listener
    if (timeOutCallback_ != nullptr) {
        timeOutCallback_(db_service_set {serviceInfo});
    }
    FIT_LOG_DEBUG("Memory callback timeout.");
}

db_service_set FitRegistryFitableRepositoryMemory::Remove(const Fit::fit_address &address)
{
    // 查找内存
    db_service_set serviceInfoSet;
    if (serviceMemoryRepository_ != nullptr) {
        serviceInfoSet = serviceMemoryRepository_->Remove(address);
    }
    // if is master 主节点
    // 删除db
    if (IsMaster() && serviceRepository_ != nullptr) {
        serviceRepository_->Remove(address);
    }
    FIT_LOG_INFO("Worker is offline, clear all fitable, ip: %s, port: %d, protocol: %d, id is %s.",
        address.ip.c_str(), address.port, static_cast<int32_t>(address.protocol), address.id.c_str());
    return serviceInfoSet;
}

bool FitRegistryFitableRepositoryMemory::IsAlreadyReadyForV3()
{
    fit_fitable_key_t key;
    key.generic_id
        = fit::hakuna::kernel::registry::server::synchronizeFitService::GENERIC_ID;
    key.fitable_id = "202954b6897a4e2da49aa29ac572f5fb";
    key.generic_version = "1.0.0";

    auto services = serviceMemoryRepository_->Query(key);
    for (const auto& service : services) {
        if (IsSyncFitable(service)) {
            return true;
        }
    }
    return false;
}

bool FitRegistryFitableRepositoryMemory::ReadyForWorking(const db_service_info_t &service)
{
    if (isAlreadyInitSync_.load()) {
        return true;
    }

    if (IsAlreadyReadyForV3()) {
        isAlreadyInitSync_.store(true);
        return true;
    }

    Fit::unique_lock<Fit::mutex> lock(saveFitableCacheMutex_);
    if (isAlreadyInitSync_.load()) {
        return true;
    }

    PrintService(service, "ReadyForWorking");
    if (!IsSyncFitable(service)) {
        saveFitableCache_.emplace_back(service);
        return false;
    }

    SyncSave(service);
    db_service_set serviceSet;
    if (syncServiceRepository_ != nullptr) {
        // 同步将sync接口写入到db
        syncServiceRepository_->Save(service);
        // 同步将sync接口查询
        serviceSet = syncServiceRepository_->Query(RegistryUtil::ConvertFitableIdToFitableKey(service.service.fitable));
        // 将sync接口写入内存
        SyncSave(serviceSet);
    }
    FIT_LOG_INFO("Save and Query sync db, update to db, size is %lu.", serviceSet.size());
    // 将sync接口同步到其他节点
    if (registrySyncService_ != nullptr) {
        if (serviceSet.empty()) {
            serviceSet.push_back(service);
        }
        registrySyncService_->Add(serviceSet);
    }

    isAlreadyInitSync_.store(true);
    lock.unlock();

    // 将原有缓存的服务重新保存起来
    Save(saveFitableCache_);
    FIT_LOG_CORE("Cache size is %lu.", saveFitableCache_.size());
    saveFitableCache_.clear();
    return true;
}
void FitRegistryFitableRepositoryMemory::InitMemory()
{
    // 开启
    if (timer_ != nullptr) {
        syncServiceInfoFromDbTaskTimeoutHandle_ =
            timer_->set_timeout(INIT_MEMORY_TIMEOUT * MILLION_SECONDS_PER_SECOND, [this]() {
            SyncServiceBetweenMemoryAndDb();
            AddSyncServiceBetweenMemoryAndDbTask();
        });
    }
}
void FitRegistryFitableRepositoryMemory::AddSyncServiceBetweenMemoryAndDbTask()
{
    if (timer_ != nullptr) {
        syncServiceInfoFromDbTaskHandle_ = timer_->set_interval(
            SYNC_MEMORY_INTERVAL * MILLION_SECONDS_PER_SECOND, [this]() {
            SyncServiceBetweenMemoryAndDb();
        });
    }
}
void FitRegistryFitableRepositoryMemory::MarkDBToMemoryOrder(db_service_set &serviceSet, uint64_t syncCount)
{
    for (auto& it : serviceSet) {
        it.syncCount = syncCount;
    }
}

// 1.同步db数据到内存
// 2.内存里存在，db不存在，同步未标记的
void FitRegistryFitableRepositoryMemory::SyncServiceBetweenMemoryAndDb()
{
    if (serviceRepository_ != nullptr) {
        db_service_set serviceSet = serviceRepository_->GetAllServices();
        if (serviceSet.empty()) {
            FIT_LOG_INFO("Db info is empty.");
        }
        FIT_LOG_DEBUG("SyncServiceBetweenMemoryAndDb, serviceSet size is %lu.", serviceSet.size());
        // 更新标记
        ++syncCount_;
        // 标记从db中同步的数据
        MarkDBToMemoryOrder(serviceSet, syncCount_);
        InsertServiceOrUpdateSyncCount(serviceSet);
    }
    // 获取未标记的服务
    if (!IsMaster()) { // LCOV_EXCL_BR_LINE
        return;
    }
    Fit::vector<std::shared_ptr<db_service_info_t>> serviceSetNotMatchSyncCount;
    if (serviceMemoryRepository_ != nullptr) {
        serviceSetNotMatchSyncCount = serviceMemoryRepository_->GetServicesNotUpdated(syncCount_);
    }

    FIT_LOG_DEBUG("SyncCount %lu, synccount not match size is %lu.", syncCount_.load(),
        serviceSetNotMatchSyncCount.size());
    // 将服务同步回db
    for (auto& temp : serviceSetNotMatchSyncCount) {
        if (temp != nullptr && serviceRepository_ != nullptr) {
            serviceRepository_->Save(*temp);
        }
    }
}
vector<RegistryInfo::FitableMetaAddress> FitRegistryFitableRepositoryMemory::GetFitableInstances(
    const string& genericId) const
{
    if (serviceMemoryRepository_ == nullptr) {
        return {};
    }
    return serviceMemoryRepository_->GetFitableInstances(genericId);
}
FitCode FitRegistryFitableRepositoryMemory::QueryWorkerDetail(
    const Fit::string& workerId, Fit::RegistryInfo::WorkerDetail& result) const
{
    if (serviceMemoryRepository_ == nullptr) {
        return {};
    }
    return serviceMemoryRepository_->QueryWorkerDetail(workerId, result);
}
vector<RegistryInfo::WorkerMeta> FitRegistryFitableRepositoryMemory::QueryAllWorkers() const
{
    if (serviceMemoryRepository_ == nullptr) {
        return {};
    }
    return serviceMemoryRepository_->QueryAllWorkers();
}
}
} // LCOV_EXCL_BR_LINE