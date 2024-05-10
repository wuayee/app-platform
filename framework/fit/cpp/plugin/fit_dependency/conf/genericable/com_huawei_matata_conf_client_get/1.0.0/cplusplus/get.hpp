/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_MATATA_CONF_CLIENT_GET_H
#define COM_HUAWEI_MATATA_CONF_CLIENT_GET_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/string.hpp>

namespace matata {
namespace conf {
namespace client {
struct __get {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::string **>;
};

/**
 * 获取配置的值。
 *
 * @param key 表示配置的键。配置的键将以“.”分割成路径。
 * @return 表示配置的值。
 */

/**
 * FitCode get(
 * const Fit::string *key,
 * Fit::string **result)
 */
class get : public ::Fit::Framework::ProxyClient<FitCode(__get::InType, __get::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "b670eca30b734604bdfd4529b9ce8d7f";
    get() : ::Fit::Framework::ProxyClient<FitCode(__get::InType, __get::OutType)>(GENERIC_ID) {}
    ~get() = default;
};
}
}
}

#endif
