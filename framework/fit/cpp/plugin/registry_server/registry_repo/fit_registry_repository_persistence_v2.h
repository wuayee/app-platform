/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  : 和repo对接的实现
 * Author       : w00561424
 * Date       : 2021-11-24 14:27:59
 * Notes:       :
 */

#ifndef FIT_REGISTRY_REPOSITORY_PERSISTENCE_V2_H
#define FIT_REGISTRY_REPOSITORY_PERSISTENCE_V2_H

#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <fit/internal/registry/repository/fit_worker_table_operation.h>
#include <fit/internal/registry/repository/fit_address_table_operation.h>
#include <fit/internal/registry/repository/fit_fitable_table_operation.h>

namespace Fit {
class FitRegistryRepositoryPersistenceV2 : public FitRegistryServiceRepository {
public:
    FitRegistryRepositoryPersistenceV2();

    explicit FitRegistryRepositoryPersistenceV2(
        FitWorkerTableOperationPtr fitWorkerTableOperationPtr,
        FitAddressTableOperationPtr fitAddressTableOperationPtr,
        FitFitableTableOperationPtr fitFitableTableOperationPtr);

    ~FitRegistryRepositoryPersistenceV2() override = default;
    bool Start() override;
    bool Stop() override;
    bool Save(const db_service_info_t &service) override;
    bool Save(const db_service_set &services) override;

    db_service_set Query(const fit_fitable_key_t &key) override;

    bool Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address) override;
    bool Remove(const db_service_set &services) override;
    db_service_set Remove(const Fit::fit_address &address) override;

    db_service_set GetAllServices() override;
    db_service_set GetServicesByGenericId(const Fit::string &generic_id) override;

    int32_t DelServicesByAddress(const Fit::fit_address &address) override;

private:
    bool CheckDbHandle();
    db_service_set QueryServiceSetByFitableMetaSet(const Fit::vector<Fit::RegistryInfo::FitableMeta>& fitableMetas);
    void UpdateWorker(const Fit::RegistryInfo::Worker& worker);
    void UpdateAddresses(Fit::vector<Fit::RegistryInfo::Address>& addressesIn);
    void UpdateFitableMeta(const Fit::RegistryInfo::FitableMeta& fitableMeta);
private:
    FitWorkerTableOperationPtr fitWorkerTableOperationPtr_ {nullptr};
    FitAddressTableOperationPtr fitAddressTableOperationPtr_ {nullptr};
    FitFitableTableOperationPtr fitFitableTableOperationPtr_ {nullptr};
};
}
#endif // FIT_REGISTRY_REPOSITORY_PERSISTENCE_V2_H
