/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020-12-06
 * Notes:       :
 */
#ifndef FIT_FITABLE_MEMORY_REPOSITORY_H
#define FIT_FITABLE_MEMORY_REPOSITORY_H
#include <fit/internal/registry/repository/fit_registry_repository.h>
class FitFitableMemoryRepository : public FitRegistryServiceRepository {
public:
    virtual ~FitFitableMemoryRepository()= default;
    virtual db_service_set Query(const fit_fitable_key_t &key) = 0;
    // 按照workerId查询出的服务，不消费host、port
    virtual db_service_set Query(const db_service_info_t &service) = 0;
    // 按照workerId查询出的服务，不消费host、port
    virtual db_service_set Query(const Fit::fitable_id &fitable, const Fit::fit_address &address) = 0;
    virtual Fit::vector<std::shared_ptr<db_service_info_t>> GetServicesNotUpdated(uint64_t syncCount) = 0;
    virtual bool InitTimeoutCallback(std::function<void(const db_service_set &)> callback) = 0;
    virtual bool IsServiceAlreadyExist(const db_service_info_t &service) = 0;
    virtual bool InsertServiceOrUpdateSyncCount(const db_service_set &services) = 0;
};

using FitFitableMemoryRepositoryPtr = std::shared_ptr<FitFitableMemoryRepository>;
#endif
