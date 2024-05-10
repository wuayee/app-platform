/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#include "fitable_memory_repository.h"
#include <fit/fit_log.h>
#include <registry_server_memory/common/util.h>
#include <fit/internal/registry/registry_util.h>

namespace Fit {
namespace Registry {
FitableMemoryRepository::FitableMemoryRepository(
    std::shared_ptr<FitMemoryWorkerOperation> memoryWorkerOperation,
    std::shared_ptr<FitMemoryAddressOperation> memoryAddressOperation,
    std::shared_ptr<FitMemoryFitableOperation> memoryFitableOperation)
{
    memoryWorkerOperation_ = memoryWorkerOperation;
    memoryAddressOperation_ = memoryAddressOperation;
    memoryFitableOperation_ = memoryFitableOperation;
}

FitableMemoryRepository::~FitableMemoryRepository()
{
}
bool FitableMemoryRepository::Start()
{
    Stop();
    if (memoryWorkerOperation_ != nullptr) {
        memoryWorkerOperation_->Init([this](std::weak_ptr<WorkerWithMeta> workerWithMeta) {
            ServiceTimeoutCallback(workerWithMeta);
        });
    }
    FIT_LOG_INFO("Fitable memory repo is started.");
    return true;
}

bool FitableMemoryRepository::Stop()
{
    if (memoryWorkerOperation_ != nullptr) {
        memoryWorkerOperation_->Stop();
    }
    FIT_LOG_INFO("Fitable memory stop.");
    return true;
}

bool FitableMemoryRepository::Save(const db_service_info_t &service)
{
    if (!CheckStatus()) {
        return false;
    }

    auto workerWithMeta = std::make_shared<WorkerWithMeta>();
    workerWithMeta->syncCount = service.syncCount;
    workerWithMeta->worker = RegistryUtil::GetWorkerFromServiceInfo(service);

    auto addressSetTemp = RegistryUtil::GetAddressSetFromServiceInfo(service);
    Fit::vector<std::shared_ptr<Fit::RegistryInfo::Address>> addressSet;
    addressSet.reserve(addressSetTemp.size());
    for (const auto& it : addressSetTemp) {
        addressSet.emplace_back(std::make_shared<Fit::RegistryInfo::Address>(it));
    }

    auto fitableMetaInfo = std::make_shared<Fit::RegistryInfo::FitableMeta>(
        RegistryUtil::GetFitableMetaFromServiceInfo(service));

    UpdateWorkerInfo(workerWithMeta);
    memoryAddressOperation_->Save(addressSet); // 一个workerId只对应一组address，直接覆盖
    memoryFitableOperation_->Save(fitableMetaInfo);

    return true;
}

bool FitableMemoryRepository::Save(const db_service_set &services)
{
    for (const auto& it : services) {
        Save(it);
    }
    return true;
}
int32_t FitableMemoryRepository::UpdateWorkerInfo(std::shared_ptr<WorkerWithMeta> workerWithMeta)
{
    std::shared_ptr<WorkerWithMeta> workerWithMetaPtr = memoryWorkerOperation_->Query(workerWithMeta->worker.workerId);
    if (workerWithMetaPtr != nullptr) {
        // app不一致时
        if (workerWithMetaPtr->worker.application.name != workerWithMeta->worker.application.name ||
            workerWithMetaPtr->worker.application.nameVersion != workerWithMeta->worker.application.nameVersion) {
            memoryWorkerOperation_->Remove(workerWithMeta->worker.workerId); // 将旧的worker删除
            // worker和application的关系为空
            if (memoryWorkerOperation_->Query(workerWithMetaPtr->worker.application).empty()) {
                memoryFitableOperation_->Remove(workerWithMetaPtr->worker.application); // 根据旧的appInfo删除Fitable列表
            }
        }
    }
    return memoryWorkerOperation_->Save(workerWithMeta);
}
db_service_set FitableMemoryRepository::Query(const fit_fitable_key_t &key)
{
    if (!CheckStatus()) {
        return db_service_set {};
    }

    Fit::RegistryInfo::Fitable fitable;
    fitable.genericableId = key.generic_id;
    fitable.genericableVersion = key.generic_version;
    fitable.fitableId = key.fitable_id;
    fitable.fitableVersion = "1.0.0";
    FitMemoryFitableOperation::FitableMetaPtrSet fitableMetas = memoryFitableOperation_->Query(fitable);
    db_service_set result;
    for (const auto& fitableMeta : fitableMetas) {
        if (fitableMeta == nullptr) {
            continue;
        }
        Fit::vector<std::shared_ptr<WorkerWithMeta>> workerWithMetaPtrSet
            = memoryWorkerOperation_->Query(fitableMeta->application);
        for (const auto& workerMetaPtr : workerWithMetaPtrSet) {
            if (workerMetaPtr == nullptr) {
                continue;
            }
            FitMemoryAddressOperation::AddressPtrSet addressSet =
                memoryAddressOperation_->Query(workerMetaPtr->worker.workerId);
            Fit::vector<Fit::RegistryInfo::Address> addresses;
            addresses.reserve(addressSet.size());
            for (const auto& address : addressSet) {
                addresses.emplace_back(*address);
            }
            auto serviceInfo = RegistryUtil::BuildServiceSet(workerMetaPtr->worker, *fitableMeta, addresses);
            result.emplace_back(serviceInfo);
        }
    }
    return result;
}

db_service_set FitableMemoryRepository::Query(const db_service_info_t &service)
{
    if (service.service.addresses.empty()) {
        return db_service_set {};
    }
    // 一个worker多地址时，相同的workerId，查询的数据未去重，会得到多个一样的服务地址；因此只需要查询一遍
    return Query(service.service.fitable, service.service.addresses.front());
}

db_service_set FitableMemoryRepository::Query(const Fit::fitable_id &fitable, const Fit::fit_address &address)
{
    if (!CheckStatus()) {
        return db_service_set {};
    }

    std::shared_ptr<WorkerWithMeta> workerMetaPtr = memoryWorkerOperation_->Query(address.id);
    if (workerMetaPtr == nullptr) {
        return db_service_set {};
    }
    Fit::RegistryInfo::Fitable fitableInput;
    fitableInput.genericableId = fitable.generic_id;
    fitableInput.genericableVersion = fitable.generic_version;
    fitableInput.fitableId = fitable.fitable_id;
    fitableInput.fitableVersion = fitable.fitable_version;

    auto fitableMetaPtrSet = memoryFitableOperation_->Query(fitableInput);
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr {nullptr};
    for (const auto& fitableMetaPtrTemp : fitableMetaPtrSet) {
        if (Fit::RegistryInfo::ApplicationEqual()(fitableMetaPtrTemp->application, workerMetaPtr->worker.application)) {
            fitableMetaPtr = fitableMetaPtrTemp;
            break;
        }
    }
    if (fitableMetaPtr == nullptr) {
        return db_service_set {};
    }

    FitMemoryAddressOperation::AddressPtrSet addressPtrSet = memoryAddressOperation_->Query(address.id);
    db_service_set result;
    Fit::vector<Fit::RegistryInfo::Address> addresses;
    addresses.reserve(addressPtrSet.size());
    for (const auto& address : addressPtrSet) {
        addresses.emplace_back(*address);
    }
    auto serviceInfo = RegistryUtil::BuildServiceSet(workerMetaPtr->worker, *fitableMetaPtr, addresses);
    serviceInfo.syncCount = workerMetaPtr->syncCount;
    result.emplace_back(serviceInfo);

    return result;
}

bool FitableMemoryRepository::Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address)
{
    if (!CheckStatus()) {
        return false;
    }

    auto workerWithMetaPtr = memoryWorkerOperation_->Query(address.id);
    memoryWorkerOperation_->Remove(address.id);
    if (workerWithMetaPtr != nullptr) {
        // 从定时器删除
        if (memoryWorkerOperation_->Query(workerWithMetaPtr->worker.application).empty()) {
            // 删除fitable
            memoryFitableOperation_->Remove(workerWithMetaPtr->worker.application);
        }
    }

    memoryAddressOperation_->Remove(address.id);
    return true;
}
bool FitableMemoryRepository::Remove(const db_service_set &services)
{
    for (const auto& it : services) {
        for (const auto& address : it.service.addresses) {
            Remove(it.service.fitable, address);
        }
    }
    return true;
}

db_service_set FitableMemoryRepository::GetAllServices()
{
    return db_service_set {};
}

db_service_set FitableMemoryRepository::Remove(const Fit::fit_address &address)
{
    if (!CheckStatus()) {
        return db_service_set {};
    }

    std::shared_ptr<WorkerWithMeta> workerMetaPtr = memoryWorkerOperation_->Query(address.id);
    if (workerMetaPtr == nullptr) {
        return db_service_set {};
    }

    FitMemoryAddressOperation::AddressPtrSet addressSet =
        memoryAddressOperation_->Query(workerMetaPtr->worker.workerId);
    FitMemoryFitableOperation::FitableMetaPtrSet fitableMetaPtrSet
        = memoryFitableOperation_->Query(workerMetaPtr->worker.application);

    memoryWorkerOperation_->Remove(address.id);
    memoryAddressOperation_->Remove(workerMetaPtr->worker.workerId);
    if (memoryWorkerOperation_->Query(workerMetaPtr->worker.application).empty()) {
        memoryFitableOperation_->Remove(workerMetaPtr->worker.application);
    }

    Fit::vector<Fit::RegistryInfo::Address> addresses;
    for (const auto& address : addressSet) {
        addresses.emplace_back(*address);
    }
    db_service_set result;
    for (const auto& fitableMeta : fitableMetaPtrSet) {
        if (fitableMeta == nullptr) {
            continue;
        }
        auto serviceInfo = RegistryUtil::BuildServiceSet(workerMetaPtr->worker, *fitableMeta, addresses);
        result.emplace_back(serviceInfo);
    }
    return result;
}

bool FitableMemoryRepository::InitTimeoutCallback(std::function<void(const db_service_set &)> callback)
{
    timeOutCallback_ = callback;
    return true;
}

void FitableMemoryRepository::ServiceTimeoutCallback(std::weak_ptr<WorkerWithMeta> workerWithMeta)
{
    auto workerWithMetaPtr = workerWithMeta.lock();
    if (workerWithMetaPtr != nullptr && memoryAddressOperation_ != nullptr) {
        // 删除内存
        Fit::fit_address address;
        address.id = workerWithMetaPtr->worker.workerId;
        address.environment = workerWithMetaPtr->worker.environment;

        auto addressSet = memoryAddressOperation_->Query(workerWithMetaPtr->worker.workerId);
        for (const auto& addressTemp : addressSet) {
            if (addressTemp == nullptr) {
                continue;
            }
            address.ip = addressTemp->host;
            address.port = addressTemp->port;
            address.protocol = addressTemp->protocol;

            db_service_set serviceSet = Remove(address);
            // 将超时删除的消息回调出去
            if (timeOutCallback_ != nullptr) {
                timeOutCallback_(serviceSet);
            }
        }
        FIT_LOG_DEBUG("Fitable callback timeout workerId is %s.", workerWithMetaPtr->worker.workerId.c_str());
    }
    FIT_LOG_DEBUG("Fitable callback timeout.");
}

bool FitableMemoryRepository::CheckStatus()
{
    return (memoryFitableOperation_ != nullptr &&
        memoryAddressOperation_ != nullptr &&
        memoryWorkerOperation_ != nullptr);
}

Fit::vector<std::shared_ptr<db_service_info_t>> FitableMemoryRepository::GetServicesNotUpdated(uint64_t syncCount)
{
    if (!CheckStatus()) {
        return Fit::vector<std::shared_ptr<db_service_info_t>> {};
    }
    Fit::vector<std::shared_ptr<db_service_info_t>> result;
    Fit::vector<std::shared_ptr<WorkerWithMeta>> workerWithMetaPtrResult = memoryWorkerOperation_->QueryAll();
    for (const auto& workerWithMetaPtr : workerWithMetaPtrResult) {
        if (workerWithMetaPtr == nullptr || workerWithMetaPtr->syncCount == syncCount) {
            continue;
        }
        auto fitableMetaPtrSet = memoryFitableOperation_->Query(workerWithMetaPtr->worker.application);
        auto addressSet = memoryAddressOperation_->Query(workerWithMetaPtr->worker.workerId);

        Fit::vector<Fit::RegistryInfo::Address> addresses;
        for (const auto& address : addressSet) {
            addresses.emplace_back(*address);
        }

        for (const auto& fitableMeta : fitableMetaPtrSet) {
            if (fitableMeta == nullptr) {
                continue;
            }
            auto serviceInfo = RegistryUtil::BuildServiceSet(workerWithMetaPtr->worker, *fitableMeta, addresses);
            result.emplace_back(std::make_shared<db_service_info_t>(serviceInfo));
        }
    }
    return result;
}
bool FitableMemoryRepository::IsServiceAlreadyExist(const db_service_info_t &service)
{
    db_service_set serviceSet = Query(service);
    if (serviceSet.empty() ||
        (service.service.addresses.size() != serviceSet.front().service.addresses.size()) ||
        !RegistryInfo::ApplicationEqual()(service.service.application, serviceSet.front().service.application)) {
        return false;
    }
    // 数量相等时，service.service.addresses是serviceSet.front().service.addresses的子集，等价于相等
    // A 属于 B，并且A.size 等于 B.size() => A == B
    if (!Fit::Registry::IsSubsetOfBaseAddresses(serviceSet.front().service.addresses, service.service.addresses)) {
        return false;
    }
    // 判断worker信息是否有变更
    auto worker = RegistryUtil::GetWorkerFromServiceInfo(service);
    auto currentWorkerMeta = memoryWorkerOperation_->Query(worker.workerId);
    if (!currentWorkerMeta) {
        return false;
    }
    if (!RegistryInfo::WorkerEqual()(worker, currentWorkerMeta->worker)) {
        return false;
    }
    return serviceSet.front().service.timeoutSeconds == service.service.timeoutSeconds;
}
// 从数据库中查询到的数据为一个服务多地址
bool FitableMemoryRepository::InsertServiceOrUpdateSyncCount(const db_service_set &services)
{
    if (!CheckStatus()) {
        return false;
    }
    for (const auto& service : services) {
        if (Query(service).empty()) { // 如果查询为空，直接save
            Save(service);
        } else {
            // 如果查询到，更新worker count
            memoryWorkerOperation_->UpdateWorkerSyncCount(service.service.addresses.front().id, service.syncCount);
        }
    }
    return true;
}
vector<RegistryInfo::FitableMetaAddress> FitableMemoryRepository::GetFitableInstances(const string& genericId) const
{
    auto fitableMetas = memoryFitableOperation_->Query(genericId);
    vector<RegistryInfo::FitableMetaAddress> result;
    result.reserve(fitableMetas.size());
    for (auto& fitableMeta : fitableMetas) {
        vector<std::shared_ptr<WorkerWithMeta>> workerMetas = memoryWorkerOperation_->Query(fitableMeta->application);
        if (workerMetas.empty()) {
            continue;
        }
        RegistryInfo::FitableMetaAddress fitableMetaAddress;
        fitableMetaAddress.fitableMeta = *fitableMeta;
        fitableMetaAddress.workers.reserve(workerMetas.size());
        for (auto& workerMeta : workerMetas) {
            fitableMetaAddress.workers.emplace_back(workerMeta->worker);
        }
        result.emplace_back(std::move(fitableMetaAddress));
    }
    return result;
}
FitCode FitableMemoryRepository::QueryWorkerDetail(
    const Fit::string& workerId, Fit::RegistryInfo::WorkerDetail& result) const
{
    auto workerMeta = memoryWorkerOperation_->Query(workerId);
    if (!workerMeta) {
        return FIT_ERR_NOT_FOUND;
    }
    result.worker = workerMeta->worker;
    auto fitableMetas = memoryFitableOperation_->Query(workerMeta->worker.application);
    result.fitables.reserve(fitableMetas.size());
    for (auto& fitableMeta : fitableMetas) {
        result.fitables.emplace_back(*fitableMeta);
    }
    auto addresses = memoryAddressOperation_->Query(workerMeta->worker.workerId);
    result.addresses.reserve(addresses.size());
    for (auto& address : addresses) {
        result.addresses.emplace_back(*address);
    }
    result.app.id = workerMeta->worker.application;

    return FIT_OK;
}
vector<RegistryInfo::WorkerMeta> FitableMemoryRepository::QueryAllWorkers() const
{
    vector<RegistryInfo::WorkerMeta> result;
    auto workerMetas = memoryWorkerOperation_->QueryAll();
    result.reserve(workerMetas.size());
    for (auto& src : workerMetas) {
        result.emplace_back();
        auto& meta = result.back();
        meta.worker = src->worker;
        auto addresses = memoryAddressOperation_->Query(src->worker.workerId);
        meta.addresses.reserve(addresses.size());
        for (auto& address : addresses) {
            meta.addresses.emplace_back(*address);
        }
    }
    return result;
}
}
} // LCOV_EXCL_BR_LINE