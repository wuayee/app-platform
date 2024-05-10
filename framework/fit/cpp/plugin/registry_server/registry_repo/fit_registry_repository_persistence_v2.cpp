/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  : 和ccdb对接的实现
 * Author       : w00561424
 * Date       : 2021-11-24 14:27:59
 * Notes:       :
 */

#include "fit_registry_repository_persistence_v2.h"
#include <sstream>
#include <fit/fit_log.h>
#include <fit/internal/registry/registry_util.h>
#include <fit/stl/unordered_set.hpp>
#include <fit/stl/string.hpp>
namespace {
template<typename T>
void UpdateDataItemInSet(Fit::vector<T>& dataSet, T&& data,
    std::function<bool(const T&)> func)
{
    auto findIt = find_if(dataSet.begin(), dataSet.end(), func);
    if (findIt == dataSet.end()) {
        dataSet.emplace_back(std::forward<T>(data));
    }
}
}
namespace Fit {
FitRegistryRepositoryPersistenceV2::FitRegistryRepositoryPersistenceV2(
    FitWorkerTableOperationPtr fitWorkerTableOperationPtr,
    FitAddressTableOperationPtr fitAddressTableOperationPtr,
    FitFitableTableOperationPtr fitFitableTableOperationPtr)
    : fitWorkerTableOperationPtr_(std::move(fitWorkerTableOperationPtr)),
    fitAddressTableOperationPtr_(std::move(fitAddressTableOperationPtr)),
    fitFitableTableOperationPtr_(std::move(fitFitableTableOperationPtr))
{
    CheckDbHandle();
}
bool FitRegistryRepositoryPersistenceV2::Start()
{
    if (!CheckDbHandle()) {
        return false;
    }
    return true;
}
bool FitRegistryRepositoryPersistenceV2::Stop()
{
    return true;
}
void FitRegistryRepositoryPersistenceV2::UpdateWorker(const Fit::RegistryInfo::Worker& worker)
{
    Fit::vector<Fit::RegistryInfo::Worker> workers =
        fitWorkerTableOperationPtr_->Query(worker.workerId);
    bool hasSameWorker = false;
    for (const auto& it : workers) {
        if (worker.application.name != it.application.name ||
            worker.application.nameVersion != it.application.nameVersion) {
            fitWorkerTableOperationPtr_->Delete(worker.workerId); // 将旧的worker删除
            if (fitWorkerTableOperationPtr_->Query(it.application).empty()) { // worker和application的关系为空
                fitFitableTableOperationPtr_->Delete(it.application); // 根据旧的appInfo删除Fitable表
            }
        } else if (RegistryInfo::WorkerEqual()(worker, it)) {
            hasSameWorker = true;
        }
    }
    if (!hasSameWorker) {
        fitWorkerTableOperationPtr_->Save(worker);
    }
}

void FitRegistryRepositoryPersistenceV2::UpdateAddresses(Fit::vector<Fit::RegistryInfo::Address>& addressesIn)
{
    Fit::string workerId = addressesIn.front().workerId;
    Fit::vector<Fit::RegistryInfo::Address> addresses;
    fitAddressTableOperationPtr_->Query(workerId, addresses);
    bool isSame = RegistryUtil::CompareAddressSet(addresses, addressesIn);
    if (isSame) {
        return;
    }
    if (!addresses.empty()) {
        fitAddressTableOperationPtr_->Delete(workerId);
    }
    fitAddressTableOperationPtr_->Save(addressesIn);
}

void FitRegistryRepositoryPersistenceV2::UpdateFitableMeta(const Fit::RegistryInfo::FitableMeta& fitableMeta)
{
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas =
        fitFitableTableOperationPtr_->Query(fitableMeta);
    if (fitableMetas.empty()) {
        fitFitableTableOperationPtr_->Save(fitableMeta);
    }
}

bool FitRegistryRepositoryPersistenceV2::Save(const db_service_info_t &service)
{
    if (!CheckDbHandle()) {
        return false;
    }
    if (service.service.addresses.empty()) {
        return false;
    }
    Fit::RegistryInfo::Worker worker = RegistryUtil::GetWorkerFromServiceInfo(service);
    Fit::vector<Fit::RegistryInfo::Address> addressesIn = RegistryUtil::GetAddressSetFromServiceInfo(service);
    Fit::RegistryInfo::FitableMeta fitableMeta = RegistryUtil::GetFitableMetaFromServiceInfo(service);

    UpdateWorker(worker);
    UpdateFitableMeta(fitableMeta);
    UpdateAddresses(addressesIn);

    return true;
}

bool FitRegistryRepositoryPersistenceV2::Save(const db_service_set &services)
{
    if (!CheckDbHandle()) {
        return false;
    }
    FIT_LOG_INFO("Service size = %lu.", services.size());
    Fit::vector<Fit::RegistryInfo::Worker> workerSet;
    Fit::vector<Fit::vector<Fit::RegistryInfo::Address>> addressesSet;
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetaSet;
    for (const auto &service : services) {
        Fit::RegistryInfo::Worker worker = RegistryUtil::GetWorkerFromServiceInfo(service);
        auto workerCompare = [worker] (const Fit::RegistryInfo::Worker& workerItem) {
            return worker.workerId == workerItem.workerId &&
                worker.application.name == workerItem.application.name &&
                worker.application.nameVersion == workerItem.application.nameVersion;
        };
        UpdateDataItemInSet<Fit::RegistryInfo::Worker>(workerSet, std::move(worker), workerCompare);

        Fit::vector<Fit::RegistryInfo::Address> addressesIn = RegistryUtil::GetAddressSetFromServiceInfo(service);
        if (!addressesIn.empty()) {
            auto addressesCompare = [addressesIn] (const Fit::vector<Fit::RegistryInfo::Address>& addresses) {
                Fit::vector<Fit::RegistryInfo::Address> addressesInTemp {addressesIn};
                Fit::vector<Fit::RegistryInfo::Address> addressesTemp {addresses};
                return RegistryUtil::CompareAddressSet(addressesInTemp, addressesTemp);
            };
            UpdateDataItemInSet<Fit::vector<Fit::RegistryInfo::Address>>(
                addressesSet, std::move(addressesIn), addressesCompare);
        }

        Fit::RegistryInfo::FitableMeta fitableMetaIn = RegistryUtil::GetFitableMetaFromServiceInfo(service);
        auto fitableMetaCompare = [fitableMetaIn](const Fit::RegistryInfo::FitableMeta& fitableMeta) {
            return Fit::RegistryInfo::FitableEqual()(fitableMetaIn.fitable, fitableMeta.fitable) &&
                Fit::RegistryInfo::ApplicationEqual()(fitableMetaIn.application, fitableMeta.application);
        };
        UpdateDataItemInSet<Fit::RegistryInfo::FitableMeta>(
            fitableMetaSet, std::move(fitableMetaIn), fitableMetaCompare);
    }

    for (const auto& worker : workerSet) {
        UpdateWorker(worker);
    }
    for (const auto& fitableMeta : fitableMetaSet) {
        UpdateFitableMeta(fitableMeta);
    }
    for (auto& addressesIn : addressesSet) {
        UpdateAddresses(addressesIn);
    }
    return true;
}

db_service_set FitRegistryRepositoryPersistenceV2::Query(const fit_fitable_key_t &key)
{
    if (!CheckDbHandle()) {
        return {};
    }
    // 根据genericableId查询所有的fitable
    Fit::RegistryInfo::Fitable fitable;
    fitable.genericableId = key.generic_id;
    fitable.genericableVersion = key.generic_version;
    fitable.fitableId = key.fitable_id;
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas = fitFitableTableOperationPtr_->Query(fitable);

    return QueryServiceSetByFitableMetaSet(fitableMetas);
}

bool FitRegistryRepositoryPersistenceV2::Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address)
{
    if (!CheckDbHandle()) {
        return false;
    }
    DelServicesByAddress(address);
    return true;
}
bool FitRegistryRepositoryPersistenceV2::Remove(const db_service_set &services)
{
    if (!CheckDbHandle()) {
        return false;
    }
    for (const auto &it : services) {
        for (const auto& address : it.service.addresses) {
            Remove(it.service.fitable, address);
        }
    }
    return true;
}

db_service_set FitRegistryRepositoryPersistenceV2::GetAllServices()
{
    if (!CheckDbHandle()) {
        return db_service_set();
    }
    // 根据genericableId查询所有的fitable
    Fit::vector<Fit::RegistryInfo::Worker> workers = fitWorkerTableOperationPtr_->QueryAll();
    Fit::vector<Fit::RegistryInfo::Address> addresses;
    fitAddressTableOperationPtr_->QueryAll(addresses);
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas = fitFitableTableOperationPtr_->QueryAll();
    db_service_set dbServices = RegistryUtil::ConvertToServiceSet(workers, addresses, fitableMetas);
    return dbServices;
}

bool FitRegistryRepositoryPersistenceV2::CheckDbHandle()
{
    if (fitWorkerTableOperationPtr_ == nullptr ||
        fitAddressTableOperationPtr_ == nullptr ||
        fitFitableTableOperationPtr_ == nullptr) {
        FIT_LOG_DEBUG("Operation is null worker:address:fitable[%d:%d:%d].",
            int32_t(fitWorkerTableOperationPtr_ != nullptr),
            int32_t(fitAddressTableOperationPtr_ != nullptr),
            int32_t(fitFitableTableOperationPtr_ != nullptr));
        return false;
    }

    if (!fitWorkerTableOperationPtr_->Init() ||
        !fitAddressTableOperationPtr_->Init() ||
        !fitFitableTableOperationPtr_->Init()) {
        FIT_LOG_DEBUG("Init worker:address:fitable[%d:%d:%d].",
            int32_t(fitWorkerTableOperationPtr_->Init()),
            int32_t(fitAddressTableOperationPtr_->Init()),
            int32_t(fitFitableTableOperationPtr_->Init()));
        return false;
    }
    return true;
}

db_service_set FitRegistryRepositoryPersistenceV2::QueryServiceSetByFitableMetaSet(
    const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas)
{
    db_service_set dbServices;
    dbServices.reserve(fitableMetas.size());
    Fit::unordered_set<Fit::string> applicationNameSet;
    // 遍历genericableId，并按application获取worker
    Fit::vector<Fit::RegistryInfo::Worker> workers;
    Fit::vector<Fit::RegistryInfo::Address> addresses;
    for (const auto& fitableMeta : fitableMetas) {
        Fit::string key = fitableMeta.application.name + fitableMeta.application.nameVersion;
        if (applicationNameSet.count(key) != 0) {
            continue;
        }
        applicationNameSet.insert(key);

        workers = fitWorkerTableOperationPtr_->Query(fitableMeta.application);
        if (workers.empty()) {
            continue;
        }

        // 根据workerId获取，worker下的地址
        for (const auto& worker : workers) {
            fitAddressTableOperationPtr_->Query(worker.workerId, addresses);
            if (addresses.empty()) {
                continue;
            }

            dbServices.push_back(std::move(RegistryUtil::BuildServiceSet(worker, fitableMeta, addresses)));
        }
    }
    return dbServices;
}
db_service_set FitRegistryRepositoryPersistenceV2::GetServicesByGenericId(const Fit::string &generic_id)
{
    if (!CheckDbHandle()) {
        return db_service_set();
    }
    // 根据genericableId查询所有的fitable
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas = fitFitableTableOperationPtr_->Query(generic_id);
    return QueryServiceSetByFitableMetaSet(fitableMetas);
}

int32_t FitRegistryRepositoryPersistenceV2::DelServicesByAddress(const Fit::fit_address &address)
{
    if (!CheckDbHandle()) {
        return REGISTRY_ERROR;
    }
    Fit::vector<Fit::RegistryInfo::Worker> workers =
        fitWorkerTableOperationPtr_->Query(address.id);
    if (!workers.empty()) {
        fitWorkerTableOperationPtr_->Delete(address.id);
        if (fitWorkerTableOperationPtr_->Query(workers.front().application).empty()) {
            // 根据appName和旧的appNameVersionOld删除Fitable表
            fitFitableTableOperationPtr_->Delete(workers.front().application);
        }
    }

    fitAddressTableOperationPtr_->Delete(address.id);
    return REGISTRY_SUCCESS;
}

db_service_set FitRegistryRepositoryPersistenceV2::Remove(const Fit::fit_address &address)
{
    if (!CheckDbHandle()) {
        return db_service_set();
    }
    FIT_LOG_DEBUG("Remove by address, id is %s.", address.id.c_str());
    db_service_set dbServices {};
    Fit::vector<Fit::RegistryInfo::Worker> workers =
        fitWorkerTableOperationPtr_->Query(address.id);
    if (!workers.empty()) {
        fitWorkerTableOperationPtr_->Delete(address.id);
        if (fitWorkerTableOperationPtr_->Query(workers.front().application).empty()) {
            // 根据appName和旧的appNameVersionOld删除Fitable表
            fitFitableTableOperationPtr_->Delete(workers.front().application);
            FIT_LOG_DEBUG("Delete application, appname:appversion is %s:%s.",
                workers.front().application.name.c_str(), workers.front().application.nameVersion.c_str());
        }
        FIT_LOG_DEBUG("Delete worker by id, id is %s.", address.id.c_str());
    } else {
        FIT_LOG_DEBUG("Worker is not exist, id is %s.", address.id.c_str());
        return dbServices;
    }

    Fit::vector<Fit::RegistryInfo::Address> addresses;
    fitAddressTableOperationPtr_->Query(address.id, addresses);
    if (!addresses.empty()) {
        fitAddressTableOperationPtr_->Delete(address.id);
        FIT_LOG_DEBUG("Delete address by id, id is %s.", address.id.c_str());
    }

    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetas =
        fitFitableTableOperationPtr_->Query(workers.front().application);

    if (addresses.empty() || fitableMetas.empty()) {
        return dbServices;
    }

    for (const auto& fitableMeta : fitableMetas) {
        dbServices.push_back(
            std::move(RegistryUtil::BuildServiceSet(workers.front(), fitableMetas.front(), addresses)));
    }

    return dbServices;
} // LCOV_EXCL_BR_LINE
}