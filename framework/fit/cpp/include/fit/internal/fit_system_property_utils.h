/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020/04/26
 * Notes:       :
 */

#ifndef FIT_SYSTEM_PROPERTY_UTILS_H
#define FIT_SYSTEM_PROPERTY_UTILS_H

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

namespace FitSystemPropertyKey {
static const char *LOCAL_PROTOCOL_KEY = "fit.broker.client.protocols";
}

class FitSystemPropertyUtils {
public:
    static Fit::string Get(const Fit::string &key);
    static bool Set(const Fit::string &key, const Fit::string &value, bool isReadOnly);
    static bool SetAddresses(const Fit::vector<fit::registry::Address> &value);
    static Fit::vector<fit::registry::Address> Addresses();
    static fit::registry::Address Address();
    static bool SetRegistryMatchedAddress(const fit::registry::Address& address);
    static fit::registry::Address GetRegistryMatchedAddress();
    /**
     * get the external address. such as,  the container's internal port is not the same as its external port
     *
     * @return Fit::vector<fit::registry::Address>
     */
    static Fit::vector<fit::registry::Address> GetExternalAddresses();
};
#endif // FIT_CPP_FIT_LOCAL_CALL_CONTEXT_H
