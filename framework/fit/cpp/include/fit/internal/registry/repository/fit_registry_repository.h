/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020-06-16 11:40:55
 * @LastEditTime: 2022-05-26 15:03:29
 * @LastEditors: Please set LastEditors
 * Notes:       :
 */

#ifndef FIT_REGISTER_SERVICE_REPOSITORY_H
#define FIT_REGISTER_SERVICE_REPOSITORY_H
#include <fit/stl/list.hpp>
#include <fit/stl/vector.hpp>
#include <fit/fit_code.h>
#include <fit/internal/registry/repository/fit_worker_table_operation.h>
#include <fit/internal/registry/repository/fit_address_table_operation.h>
#include <fit/internal/registry/repository/fit_fitable_table_operation.h>
#include "../fit_registry_entities.h"
#include "../fit_registry_entity.h"
class FitRegistryServiceRepository {
public:
    using FitableAddress = Fit::RegistryInfo::FitableAddress;
    using FitableMetaAddress = Fit::RegistryInfo::FitableMetaAddress;
    FitRegistryServiceRepository()= default;

    virtual ~FitRegistryServiceRepository()= default;
    virtual bool Start() = 0;
    virtual bool Stop() = 0;
    virtual bool Save(const db_service_info_t &service) = 0;
    virtual bool Save(const db_service_set &services) = 0;

    virtual db_service_set Query(const fit_fitable_key_t &key) = 0;

    virtual bool Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address) = 0;
    virtual bool Remove(const db_service_set &services) = 0;

    virtual db_service_set GetAllServices() = 0;
    virtual db_service_set GetServicesByGenericId(const Fit::string &generic_id)
    {
        return {};
    }
    /**
     * @brief Get the fitable instances that belong the genericId. only for new registry
     *
     * @param genericId
     * @return Fit::vector<Fit::RegistryInfo::FitableAddress> fitable instances
     */
    virtual Fit::vector<FitableMetaAddress> GetFitableInstances(const Fit::string& genericId) const
    {
        return {};
    };

    virtual int32_t DelServicesByAddress(const Fit::fit_address &address)
    {
        return 0;
    }
    virtual db_service_set Remove(const Fit::fit_address &address) = 0;
    virtual FitCode QueryWorkerDetail(const Fit::string& workerId, Fit::RegistryInfo::WorkerDetail& result) const
    {
        return FIT_ERR_NOT_SUPPORT;
    }
    virtual Fit::vector<Fit::RegistryInfo::WorkerMeta> QueryAllWorkers() const
    {
        return {};
    }
};

using FitRegistryServiceRepositoryPtr = std::shared_ptr<FitRegistryServiceRepository>;

class FitRegistryServiceRepositoryFactoryV2 final {
public:
    static FitRegistryServiceRepositoryPtr Create();
};
#endif
