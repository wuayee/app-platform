/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_GETSYSTEMPROPERTY_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_GETSYSTEMPROPERTY_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/string.hpp>

namespace fit {
namespace sdk {
namespace system {
struct __getSystemProperty {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::string **>;
};


/**
 * FitCode getSystemProperty(
 * const Fit::string *key,
 * Fit::string **result)
 */
class getSystemProperty
    : public ::Fit::Framework::ProxyClient<FitCode(__getSystemProperty::InType, __getSystemProperty::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "3212c92035dc402fa6e758225546ed6f";
    getSystemProperty()
        : ::Fit::Framework::ProxyClient<FitCode(__getSystemProperty::InType, __getSystemProperty::OutType)>(GENERIC_ID)
    {}
    ~getSystemProperty() = default;
};
}
}
}

#endif
