/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : worker表操作
 * Author       : w00561424
 * Date       : 2021-11-24 14:27:59
 * Notes:       :
 */
#ifndef FIT_WORKER_TABLE_OPERATION_H
#define FIT_WORKER_TABLE_OPERATION_H

#include <fit/internal/registry/fit_registry_entities.h>
class FitWorkerTableOperation {
public:
    virtual ~FitWorkerTableOperation() = default;
    virtual bool Init() = 0;
    virtual int32_t Save(const Fit::RegistryInfo::Worker& worker) = 0;
    virtual int32_t Delete(const Fit::string& workerId) = 0;
    virtual int32_t Delete(const Fit::string& workerId, const Fit::RegistryInfo::Application& application) = 0;
    virtual Fit::vector<Fit::RegistryInfo::Worker> Query(const Fit::string& workerId) = 0;
    virtual Fit::vector<Fit::RegistryInfo::Worker> Query(const Fit::RegistryInfo::Application& application) = 0;
    virtual Fit::vector<Fit::RegistryInfo::Worker> QueryAll() = 0;
};

using FitWorkerTableOperationPtr = std::shared_ptr<FitWorkerTableOperation>;
class FitWorkerTableOperationFactory {
public:
    static FitWorkerTableOperationPtr Create();
};
#endif