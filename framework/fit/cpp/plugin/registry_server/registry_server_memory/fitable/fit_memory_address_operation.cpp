/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#include "fit_memory_address_operation.h"
#include <fit/fit_log.h>

namespace Fit {
namespace Registry {
void PrintAddress(const Fit::RegistryInfo::Address& address, Fit::string type = "")
{
    FIT_LOG_DEBUG("Address %s, id:ip:port:protocol[%s:%s:%d:%d].", type.c_str(),
        address.workerId.c_str(), address.host.c_str(),
        address.port, static_cast<int>(address.protocol));
}
FitMemoryAddressOperation::AddressPtrSet FitMemoryAddressOperation::Query(const Fit::string& workerId)
{
    Fit::unique_lock<Fit::mutex> lock(addressIndexByIdMutex_);
    auto it = addressIndexById_.find(workerId);
    if (it == addressIndexById_.end()) {
        return FitMemoryAddressOperation::AddressPtrSet();
    }
    return it->second;
}
std::shared_ptr<Fit::RegistryInfo::Address> FitMemoryAddressOperation::Query(const Fit::RegistryInfo::Address& address)
{
    Fit::unique_lock<Fit::mutex> lock(addressIndexByIdMutex_);
    auto it = addressIndexById_.find(address.workerId);
    if (it == addressIndexById_.end()) {
        return nullptr;
    }

    AddressPtrSet addressSet = it->second;
    auto addressIt = std::find_if(addressSet.begin(), addressSet.end(),
        [&address](const std::shared_ptr<Fit::RegistryInfo::Address>& addressPtr) {
        if (addressPtr != nullptr) {
            return addressPtr->host == address.host &&
                addressPtr->port == address.port &&
                addressPtr->protocol == address.protocol;
        }
        return false;
    });
    if (addressIt != addressSet.end()) {
        PrintAddress(**addressIt, "Query");
        return *addressIt;
    }
    return nullptr;
}
int32_t FitMemoryAddressOperation::Save(const FitMemoryAddressOperation::AddressPtrSet& addresses)
{
    if (addresses.empty()) {
        return FIT_ERR_FAIL;
    }
    Fit::unique_lock<Fit::mutex> lock(addressIndexByIdMutex_);
    auto address = addresses.front();
    if (address != nullptr && !address->workerId.empty()) {
        addressIndexById_[address->workerId] = addresses;
    }
    Fit::string tag = "save, size is " + Fit::to_string(addressIndexById_.size());
    for (const auto& it : addresses) {
        if (it != nullptr) {
            PrintAddress(*it, tag);
        }
    }
    return FIT_ERR_SUCCESS;
}
int32_t FitMemoryAddressOperation::Remove(const Fit::string& workerId)
{
    Fit::unique_lock<Fit::mutex> lock(addressIndexByIdMutex_);
    addressIndexById_.erase(workerId);
    FIT_LOG_DEBUG("Address type is remove, id:%s.", workerId.c_str());
    return FIT_ERR_SUCCESS;
}
Fit::shared_ptr<FitMemoryAddressOperation> FitMemoryAddressOperation::Create()
{
    return Fit::make_shared<FitMemoryAddressOperation>();
}
}
} // LCOV_EXCL_BR_LINE