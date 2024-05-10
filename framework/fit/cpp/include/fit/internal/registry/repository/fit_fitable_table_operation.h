/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 、fitable表操作
 * Author       : w00561424
 * Date       : 2021-11-24 14:27:59
 * Notes:       :
 */
#ifndef FIT_FITABLE_TABLE_OPERATION_H
#define FIT_FITABLE_TABLE_OPERATION_H

#include <fit/internal/registry/fit_registry_entities.h>
class FitFitableTableOperation {
public:
    virtual bool Init() = 0;
    virtual ~FitFitableTableOperation() = default;
    virtual int32_t Save(const Fit::RegistryInfo::FitableMeta& fitableMeta) = 0;

    virtual int32_t Delete(const Fit::RegistryInfo::Application& application) = 0;
    virtual int32_t Delete(const Fit::RegistryInfo::FitableMeta& fitableMeta) = 0;

    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::RegistryInfo::Application& application) = 0;
    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::RegistryInfo::FitableMeta& fitableMeta) = 0;
    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::string& genericableId) = 0;
    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> Query(const Fit::RegistryInfo::Fitable& fitable) = 0;
    virtual Fit::vector<Fit::RegistryInfo::FitableMeta> QueryAll() = 0;
};

using FitFitableTableOperationPtr = std::shared_ptr<FitFitableTableOperation>;
class FitFitableTableOperationFactory {
public:
    static FitFitableTableOperationPtr Create();
};
#endif

