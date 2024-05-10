/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020/04/26
 * Notes:       :
 */
#include <genericable/com_huawei_fit_sdk_system_get_system_property/1.0.0/cplusplus/getSystemProperty.hpp>
#include <genericable/com_huawei_fit_sdk_system_set_system_property/1.0.0/cplusplus/setSystemProperty.hpp>
#include <genericable/com_huawei_fit_sdk_system_get_local_addresses/1.0.0/cplusplus/getLocalAddresses.hpp>
#include <genericable/com_huawei_fit_sdk_system_set_local_addresses/1.0.0/cplusplus/setLocalAddresses.hpp>
#include <genericable/com_huawei_fit_sdk_system_get_registry_matched_address/1.0.0/cplusplus/getRegistryMatchedAddress.hpp>
#include <genericable/com_huawei_fit_sdk_system_set_registry_matched_address/1.0.0/cplusplus/setRegistryMatchedAddress.hpp>
#include <genericable/com_huawei_fit_sdk_system_get_external_addresses/1.0.0/cplusplus/get_external_addresses.hpp>

#include <fit/fit_log.h>
#include "fit/internal/fit_system_property_utils.h"

Fit::string FitSystemPropertyUtils::Get(const Fit::string &key)
{
    ::fit::sdk::system::getSystemProperty getSystemProperty;
    Fit::string* returnValue {nullptr};
    auto ret = getSystemProperty(&key, &returnValue);
    if (ret != FIT_ERR_SUCCESS || returnValue == nullptr) {
        FIT_LOG_ERROR("Fail to get key = %s.", key.c_str());
        return "";
    }
    return *returnValue;
}

bool FitSystemPropertyUtils::Set(const Fit::string &key, const Fit::string &value, bool isReadOnly)
{
    ::fit::sdk::system::setSystemProperty setSystemProperty;
    bool* Result {nullptr};
    auto ret = setSystemProperty(&key, &value, &isReadOnly, &Result);
    if (ret != FIT_ERR_SUCCESS || Result == nullptr || *Result == false) {
        FIT_LOG_ERROR("Fail to set, key = %s.", key.c_str());
        return false;
    }
    return *Result;
}

bool FitSystemPropertyUtils::SetAddresses(const Fit::vector<fit::registry::Address> &value)
{
    ::fit::sdk::system::setLocalAddresses setLocalAddresses;
    bool* Result {nullptr};
    auto ret = setLocalAddresses(&value, &Result);
    if (ret != FIT_ERR_SUCCESS || Result == nullptr || *Result == false) {
        FIT_LOG_ERROR("%s", "Fail to set addressed.");
        return false;
    }
    return *Result;
}

Fit::vector<fit::registry::Address> FitSystemPropertyUtils::Addresses()
{
    ::fit::sdk::system::getLocalAddresses getLocalAddresses;
    Fit::vector<fit::registry::Address>* value = nullptr;
    auto ret = getLocalAddresses(&value);
    if (ret != FIT_ERR_SUCCESS || value == nullptr) {
        FIT_LOG_ERROR("%s", "Fail to get addresses.");
        return Fit::vector<fit::registry::Address>();
    }
    return *value;
}

fit::registry::Address FitSystemPropertyUtils::Address()
{
    auto result = Addresses();
    if (result.empty()) {
        return {};
    }
    return result[0];
}

bool FitSystemPropertyUtils::SetRegistryMatchedAddress(const fit::registry::Address& address)
{
    ::fit::sdk::system::setRegistryMatchedAddress setRegistryMatchedAddress;
    bool* Result {nullptr};
    auto ret = setRegistryMatchedAddress(&address, &Result);
    if (ret != FIT_ERR_SUCCESS || Result == nullptr || *Result == false) {
        FIT_LOG_ERROR("%s", "Fail to set addressed.");
        return false;
    }
    return *Result;
}
fit::registry::Address FitSystemPropertyUtils::GetRegistryMatchedAddress()
{
    fit::registry::Address *address {nullptr};
    ::fit::sdk::system::getRegistryMatchedAddress getRegistryMatchedAddress;

    auto ret = getRegistryMatchedAddress(&address);
    if (ret != FIT_ERR_SUCCESS || address == nullptr) {
        FIT_LOG_ERROR("%s", "Fail to get addresses.");
        return fit::registry::Address();
    }
    return *address;
}
Fit::vector<fit::registry::Address> FitSystemPropertyUtils::GetExternalAddresses()
{
    ::fit::sdk::system::GetExternalAddresses client;
    Fit::vector<fit::registry::Address>* result {};
    auto ret = client(&result);
    if (ret != FIT_OK || result == nullptr) {
        FIT_LOG_ERROR("Fail to get addresses. (ret=%x).", ret);
        return {};
    }
    return *result;
}
