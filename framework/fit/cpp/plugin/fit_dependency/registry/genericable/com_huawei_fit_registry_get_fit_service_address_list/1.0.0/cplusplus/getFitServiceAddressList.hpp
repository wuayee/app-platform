/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_REGISTRY_GETFITSERVICEADDRESSLIST_H
#define COM_HUAWEI_FIT_REGISTRY_GETFITSERVICEADDRESSLIST_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace registry {
struct __getFitServiceAddressList {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::registry::Fitable *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::registry::Address> **>;
};


/**
 * FitCode getFitServiceAddressList(
 * const ::fit::registry::Fitable *fitable,
 * Fit::vector<::fit::registry::Address> **result)
 */
class getFitServiceAddressList : public ::Fit::Framework::ProxyClient<FitCode(__getFitServiceAddressList::InType,
    __getFitServiceAddressList::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "564286f7db6349b2b29db49feac3b7da";
    getFitServiceAddressList()
        : ::Fit::Framework::ProxyClient<FitCode(__getFitServiceAddressList::InType,
        __getFitServiceAddressList::OutType)>(GENERIC_ID)
    {}
    ~getFitServiceAddressList() = default;
};
}
}

#endif
