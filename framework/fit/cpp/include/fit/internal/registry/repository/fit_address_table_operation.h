/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : address表操作
 * Author       : w00561424
 * Date       : 2021-11-24 14:27:59
 * Notes:       :
 */
#ifndef FIT_ADDRESS_TABLE_OPERATION_H
#define FIT_ADDRESS_TABLE_OPERATION_H

#include <fit/internal/registry/fit_registry_entities.h>
class FitAddressTableOperation {
public:
    virtual ~FitAddressTableOperation() = default;
    virtual bool Init() = 0;
    virtual int32_t Save(const Fit::RegistryInfo::Address& address) = 0;
    virtual int32_t Save(const Fit::vector<Fit::RegistryInfo::Address>& addresses) = 0;

    virtual int32_t Delete(const Fit::string& workerId) = 0;

    virtual int32_t Query(const Fit::string& workerId, Fit::vector<Fit::RegistryInfo::Address>& addresses) = 0;
    virtual int32_t QueryAll(Fit::vector<Fit::RegistryInfo::Address>& addresses) = 0;
};

using FitAddressTableOperationPtr = std::shared_ptr<FitAddressTableOperation>;
class FitAddressTableOperationFactory {
public:
    static FitAddressTableOperationPtr Create();
};
#endif