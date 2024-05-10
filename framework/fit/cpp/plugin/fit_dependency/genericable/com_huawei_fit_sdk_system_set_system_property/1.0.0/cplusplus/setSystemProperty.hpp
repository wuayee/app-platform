/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_SETSYSTEMPROPERTY_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_SETSYSTEMPROPERTY_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/string.hpp>

namespace fit {
namespace sdk {
namespace system {
struct __setSystemProperty {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *, const Fit::string *, const bool *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};


/**
 * FitCode setSystemProperty(
 * const Fit::string *key,
 * const Fit::string *value,
 * const bool *readonly,
 * bool **result)
 */
class setSystemProperty
    : public ::Fit::Framework::ProxyClient<FitCode(__setSystemProperty::InType, __setSystemProperty::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "d3af91c10c334f26aaae9c619ce67453";
    setSystemProperty()
        : ::Fit::Framework::ProxyClient<FitCode(__setSystemProperty::InType, __setSystemProperty::OutType)>(GENERIC_ID)
    {}
    ~setSystemProperty() = default;
};
}
}
}

#endif
