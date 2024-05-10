/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2020/6/16
 * Notes:       :
 */

#include "fit_registry_service.h"
#include <algorithm>
#include <cstdlib>
#include <ctime>
#include <random>
#include <utility>
#include <fit/fit_log.h>
#include "fit_subscription_service.h"

namespace Fit {
namespace Registry {
fit_registry_service::fit_registry_service(FitRegistryMemoryRepositoryPtr repo,
    fit_fitable_changed_publisher_ptr publisher)
    : fit_registry_service_repo_(std::move(repo)),
    publisher_(std::move(publisher))
{
    InitTimeoutCallback([this](const db_service_set &services) {
        TimeoutCallback(services);
    });
}

fit_registry_service::~fit_registry_service() = default;

bool fit_registry_service::Start()
{
    if (fit_registry_service_repo_ != nullptr) {
        fit_registry_service_repo_->Start();
    }
    return true;
}

bool fit_registry_service::Stop()
{
    if (fit_registry_service_repo_ != nullptr) {
        fit_registry_service_repo_->Stop();
    }
    return true;
}

bool fit_registry_service::register_services(const fit_service_instance_set &services)
{
    if (!fit_registry_service_repo_) {
        return false;
    }

    const time_t current_time = time(nullptr);

    db_service_set db_services;
    for (const auto &service : services) {
        db_service_info_t db_service;
        db_service.is_online = true;
        db_service.start_time = current_time;
        db_service.service = service;
        db_service.syncCount = DEFAULT_SYNC_COUNT;
        db_services.push_back(db_service);
    }

    auto is_success = fit_registry_service_repo_->Save(db_services);
    if (is_success) {
        notify(db_services, fit_data_changed_type::ADD);
    }

    return is_success;
}

db_service_set fit_registry_service::get_all_services() const
{
    if (!fit_registry_service_repo_) {
        return {};
    }
    return fit_registry_service_repo_->GetAllServices();
}

bool fit_registry_service::unregister_services(
    const fit_service_instance_set &services, const fit_worker_info_t &worker)
{
    if (!fit_registry_service_repo_) {
        return false;
    }
    for (auto &item : services) {
        fit_registry_service_repo_->Remove(item.fitable, worker.address);
    }

    db_service_set notify_data;
    for (auto &item : services) {
        db_service_info_t db_service;
        db_service.service = item;
        db_service.is_online = false;
        notify_data.emplace_back(db_service);
    }
    notify(notify_data, fit_data_changed_type::REMOVE);

    return true;
}

db_service_set fit_registry_service::get_services(const fit_fitable_key_t &key) const
{
    if (!fit_registry_service_repo_) {
        return {};
    }
    return fit_registry_service_repo_->Query(key);
}

address_set fit_registry_service::get_addresses(const Fit::fitable_id &fitable) const
{
    const auto service_set = get_services(get_fitable_key_from_fitable(fitable));
    return get_addresses_from_service_set(service_set);
}

address_set fit_registry_service::get_addresses_from_service_set(const db_service_set &service_set) const
{
    address_set addresses;
    for (const auto& service : service_set) {
        addresses.insert(addresses.end(), service.service.addresses.begin(), service.service.addresses.end());
    }
    return addresses;
}

db_service_set fit_registry_service::get_services_by_generic_id(const Fit::string &generic_id)
{
    if (!fit_registry_service_repo_) {
        return {};
    }
    return fit_registry_service_repo_->GetServicesByGenericId(generic_id);
}

int32_t fit_registry_service::SyncSave(const db_service_info_t &service)
{
    if (!fit_registry_service_repo_) {
        return FIT_ERR_FAIL;
    }
    return fit_registry_service_repo_->SyncSave(service);
}

int32_t fit_registry_service::SyncRemove(const db_service_info_t &service)
{
    if (!fit_registry_service_repo_) {
        return FIT_ERR_FAIL;
    }
    return fit_registry_service_repo_->SyncRemove(service);
}

int32_t fit_registry_service::SyncSave(const db_service_set &services)
{
    if (!fit_registry_service_repo_) {
        return FIT_ERR_FAIL;
    }
    return fit_registry_service_repo_->SyncSave(services);
}
int32_t fit_registry_service::SyncRemove(const db_service_set &services)
{
    if (!fit_registry_service_repo_) {
        return FIT_ERR_FAIL;
    }
    return fit_registry_service_repo_->SyncRemove(services);
}
void fit_registry_service::Remove(const Fit::fit_address &address)
{
    if (!fit_registry_service_repo_) {
        return;
    }

    auto effect_services = fit_registry_service_repo_->Remove(address);

    notify(effect_services, fit_data_changed_type::REMOVE);
}

db_service_set fit_registry_service::QueryService(
    const fit_fitable_key_t &key, const Fit::fit_address &address)
{
    if (!fit_registry_service_repo_) {
        return db_service_set();
    }
    return fit_registry_service_repo_->QueryService(key, address);
}

void fit_registry_service::TimeoutCallback(const db_service_set &services)
{
    FIT_LOG_INFO("Service timeout happened, size = %lu.", services.size());
    for (const auto& it : services) {
        FIT_LOG_INFO("Service timeout, [gid:fid]:(%s:%s).",
            it.service.fitable.generic_id.c_str(), it.service.fitable.fitable_id.c_str());
        for (const auto& address : it.service.addresses) {
            FIT_LOG_INFO("Address [ip:port:workerId]: [%s:%d:%s].",
                address.ip.c_str(), address.port, address.id.c_str());
        }
    }
    notify(services, fit_data_changed_type::REMOVE);
}
bool fit_registry_service::InitTimeoutCallback(std::function<void(const db_service_set &)> callback)
{
    if (fit_registry_service_repo_ == nullptr) {
        FIT_LOG_ERROR("Fit registry service repo is nullptr.");
        return false;
    }
    return fit_registry_service_repo_->InitTimeoutCallback(std::move(callback));
}
void fit_registry_service::notify(const db_service_set &services, fit_data_changed_type type)
{
    if (!publisher_) {
        FIT_LOG_ERROR("Fit publish is nullptr.");
        return;
    }

    for (auto &item : services) {
        fitable_changed_data_t changed_data;
        changed_data.data = item;
        changed_data.type = type;
        publisher_->notify(changed_data);
    }
}
vector<RegistryInfo::FitableMetaAddress> fit_registry_service::GetFitableInstances(const string& genericId) const
{
    if (!fit_registry_service_repo_) {
        return {};
    }
    return fit_registry_service_repo_->GetFitableInstances(genericId);
}
FitCode fit_registry_service::QueryWorkerDetail(
    const Fit::string& workerId, Fit::RegistryInfo::WorkerDetail& result) const
{
    if (!fit_registry_service_repo_) {
        return FIT_ERR_FAIL;
    }
    return fit_registry_service_repo_->QueryWorkerDetail(workerId, result);
}
vector<RegistryInfo::WorkerMeta> fit_registry_service::QueryAllWorkers() const
{
    if (!fit_registry_service_repo_) {
        return {};
    }
    return fit_registry_service_repo_->QueryAllWorkers();
}
}
} // LCOV_EXCL_BR_LINE
