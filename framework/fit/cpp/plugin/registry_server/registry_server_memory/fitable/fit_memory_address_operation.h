/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#ifndef FIT_MEMORY_ADDRESS_OPERATION_H
#define FIT_MEMORY_ADDRESS_OPERATION_H
#include <cstdint>
#include <fit/stl/mutex.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/registry/fit_registry_entities.h>

namespace Fit {
namespace Registry {
class FitMemoryAddressOperation {
public:
    using AddressPtrSet = Fit::vector<std::shared_ptr<Fit::RegistryInfo::Address>>;
    using AddressIndexById = Fit::unordered_map<Fit::string, AddressPtrSet>;
public:
    int32_t Save(const AddressPtrSet& addresses);
    AddressPtrSet Query(const Fit::string& workerId);
    std::shared_ptr<Fit::RegistryInfo::Address> Query(const Fit::RegistryInfo::Address& address);
    int32_t Remove(const Fit::string& workerId);
    static Fit::shared_ptr<FitMemoryAddressOperation> Create();
private:
    Fit::mutex addressIndexByIdMutex_ {};
    AddressIndexById addressIndexById_ {};
};
}
}
#endif