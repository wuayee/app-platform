/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 和worker表操作
 * Author       : w00561424
 * Date       : 2021-11-24 14:27:59
 * Notes:       :
 */
#ifndef REGISTRY_UTIL_H
#define REGISTRY_UTIL_H
#include <fit/internal/registry/fit_registry_entities.h>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <fit/fit_log.h>

class RegistryUtil {
public:
    static Fit::RegistryInfo::Worker GetWorkerFromServiceInfo(const db_service_info_t &service)
    {
        if (service.service.addresses.empty()) {
            return Fit::RegistryInfo::Worker();
        }
        Fit::RegistryInfo::Worker worker;
        worker.workerId = service.service.addresses.front().id;
        worker.application = service.service.application;
        worker.createTime = service.start_time;
        worker.expire = service.service.timeoutSeconds;
        worker.environment = service.service.addresses.front().environment;
        worker.extensions = service.service.addresses.front().extensions;
        return worker;
    }

    static Fit::vector<Fit::RegistryInfo::Address> GetAddressSetFromServiceInfo(const db_service_info_t &service)
    {
        Fit::vector<Fit::RegistryInfo::Address> result;
        for (const auto& it : service.service.addresses) {
            result.push_back(ConvertToRegistryInfoAddress(it));
        }
        return result;
    }

    static Fit::RegistryInfo::Address ConvertToRegistryInfoAddress(const Fit::fit_address &address)
    {
        Fit::RegistryInfo::Address result;
        result.host = address.ip;
        result.port = address.port;
        result.protocol = address.protocol;
        result.workerId = address.id;
        return result;
    }

    static Fit::RegistryInfo::Fitable ConvertFitableIdToRegistryInfoFitable(const Fit::fitable_id& fitableId)
    {
        Fit::RegistryInfo::Fitable fitable;
        fitable.fitableId = fitableId.fitable_id;
        fitable.fitableVersion = fitableId.fitable_version;
        fitable.genericableId = fitableId.generic_id;
        fitable.genericableVersion = fitableId.generic_version;
        return fitable;
    }

    static ::fit::hakuna::kernel::shared::Fitable ConvertFitableIdToHakunaFitable(const Fit::fitable_id& fitableId)
    {
        ::fit::hakuna::kernel::shared::Fitable fitable;
        fitable.fitableId = fitableId.fitable_id;
        fitable.fitableVersion = fitableId.fitable_version;
        fitable.genericableId = fitableId.generic_id;
        fitable.genericableVersion = fitableId.generic_version;
        return fitable;
    }

    static Fit::fitable_id ConvertHakunaFitableToFitableId(const ::fit::hakuna::kernel::shared::Fitable& fitableId)
    {
        Fit::fitable_id fitableIdOut;
        fitableIdOut.generic_id = fitableId.genericableId;
        fitableIdOut.generic_version = fitableId.genericableVersion;
        fitableIdOut.fitable_id = fitableId.fitableId;
        fitableIdOut.fitable_version = fitableId.fitableVersion;
        return fitableIdOut;
    }

    static fit_fitable_key_t ConvertFitableIdToFitableKey(const Fit::fitable_id& fitableId)
    {
        fit_fitable_key_t key;
        key.generic_id = fitableId.generic_id;
        key.generic_version = fitableId.generic_version;
        key.fitable_id = fitableId.fitable_id;
        return key;
    }

    static Fit::fitable_id ConvertRegistryInfoFitableToFitableId(const Fit::RegistryInfo::Fitable& fitable)
    {
        Fit::fitable_id fitableId;
        fitableId.fitable_id = fitable.fitableId;
        fitableId.fitable_version = fitable.fitableVersion;
        fitableId.generic_id = fitable.genericableId;
        fitableId.generic_version = fitable.genericableVersion;
        return fitableId;
    }

    static Fit::RegistryInfo::FitableMeta GetFitableMetaFromServiceInfo(const db_service_info_t &service)
    {
        Fit::RegistryInfo::FitableMeta fitableMeta;
        fitableMeta.fitable = ConvertFitableIdToRegistryInfoFitable(service.service.fitable);

        if (!service.service.addresses.empty()) {
            fitableMeta.formats = service.service.addresses.front().formats;
            fitableMeta.environment = service.service.addresses.front().environment;
        } else {
            FIT_LOG_WARN("Address is empty.");
        }

        fitableMeta.application = service.service.application;
        fitableMeta.aliases = service.service.aliases;
        fitableMeta.tags = service.service.tags;
        fitableMeta.extensions  = service.service.extensions;
        return fitableMeta;
    }

    static db_service_info_t BuildServiceSet(const Fit::RegistryInfo::Worker& worker,
        const Fit::RegistryInfo::FitableMeta fitableMeta,
        const Fit::vector<Fit::RegistryInfo::Address>& addresses)
    {
        db_service_info_t serviceInfo;
        serviceInfo.service.fitable = ConvertRegistryInfoFitableToFitableId(fitableMeta.fitable);
        serviceInfo.service.aliases = fitableMeta.aliases;
        serviceInfo.service.tags = fitableMeta.tags;
        serviceInfo.service.extensions = fitableMeta.extensions;
        serviceInfo.service.timeoutSeconds = worker.expire;
        serviceInfo.start_time = worker.createTime;
        serviceInfo.service.application = worker.application;
        for (const auto& it : addresses) {
            Fit::fit_address addressOut;
            addressOut.ip = it.host;
            addressOut.port = it.port;
            addressOut.protocol = it.protocol;
            addressOut.formats = fitableMeta.formats;
            addressOut.environment = worker.environment;
            addressOut.id = worker.workerId;
            addressOut.extensions = worker.extensions;
            serviceInfo.service.addresses.push_back(addressOut);
        }
        return serviceInfo;
    }

    static Fit::RegistryInfo::WorkerMap BuildWorkerMap(const Fit::vector<Fit::RegistryInfo::Worker>& workers)
    {
        Fit::RegistryInfo::WorkerMap workerMap;
        for (const auto& it : workers) {
            workerMap[it.workerId] = it;
        }

        return workerMap;
    }
    static Fit::RegistryInfo::AddressMap BuildAddressMap(const Fit::vector<Fit::RegistryInfo::Address>& addresses)
    {
        Fit::RegistryInfo::AddressMap addressMap;
        for (const auto& it : addresses) {
            addressMap[it.workerId].emplace_back(it);
        }

        return addressMap;
    }
    static Fit::RegistryInfo::FitableMetaMap BuildFitableMetaMap(
        const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas)
    {
        Fit::RegistryInfo::FitableMetaMap fitableMap;
        for (const auto& it : fitableMetas) {
            fitableMap[it.application].emplace_back(it);
        }
        return fitableMap;
    }
    static db_service_set ConvertToServiceSet(const Fit::vector<Fit::RegistryInfo::Worker>& workers,
        const Fit::vector<Fit::RegistryInfo::Address>& addresses,
        const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas)
    {
        Fit::RegistryInfo::WorkerMap workerIndexById = BuildWorkerMap(workers);
        auto addressSetIndexById = BuildAddressMap(addresses);
        auto fitableMetaIndexByApp = BuildFitableMetaMap(fitableMetas);

        Fit::RegistryInfo::WorkerMetaMap workerMetaIndexByApp;
        for (const auto& it : workerIndexById) {
            // don't process worker, if address is empty under worker
            auto addressesIt = addressSetIndexById.find(it.first);
            if (addressesIt == addressSetIndexById.end() ||
                it.second.application.name.empty() ||
                it.second.application.nameVersion.empty()) {
                FIT_LOG_ERROR("Address or app is empty, workerId is %s, appname:appversion %s:%s.", it.first.c_str(),
                    it.second.application.name.c_str(), it.second.application.nameVersion.c_str());
                continue;
            }

            Fit::RegistryInfo::WorkerMeta workerMeta;
            workerMeta.worker = it.second;
            workerMeta.addresses = addressesIt->second; // get addresses by workerId
            workerMetaIndexByApp[it.second.application] = workerMeta;
        }

        db_service_set result;
        for (const auto& it : fitableMetaIndexByApp) {
            auto workerIt = workerMetaIndexByApp.find(it.first);
            if (workerIt == workerMetaIndexByApp.end()) {
                FIT_LOG_ERROR("Worker is empty when query worker by app, app:version : (%s:%s).",
                    it.first.name.c_str(), it.first.nameVersion.c_str());
                continue;
            }
            auto workerMeta = workerIt->second;
            for (const auto& fitableMeta : it.second) {
                result.emplace_back(BuildServiceSet(workerMeta.worker, fitableMeta, workerMeta.addresses));
            }
        }
        return result;
    }
    template<typename T>
    static void DbTypeConvertToType(Fit::vector<T>& out,
        const Fit::vector<Fit::RegistryInfo::DbType<T>>& dbInfoIn)
    {
        for (const auto& it : dbInfoIn) {
            out.emplace_back(it.value);
        }
    }

    static bool CompareAddressSet(Fit::vector<Fit::RegistryInfo::Address>& addressesLeft,
        Fit::vector<Fit::RegistryInfo::Address>& addressesRight)
    {
        if (addressesLeft.size() != addressesRight.size()) {
            return false;
        }
        sort(addressesLeft.begin(), addressesLeft.end(), Fit::RegistryInfo::AddressCompare());
        sort(addressesRight.begin(), addressesRight.end(), Fit::RegistryInfo::AddressCompare());
        for (size_t pos = 0; pos != addressesLeft.size(); ++pos) {
            if (!Fit::RegistryInfo::AddressEqual()(addressesLeft[pos], addressesRight[pos])) {
                return false;
            }
        }
        return true;
    }
};
#endif