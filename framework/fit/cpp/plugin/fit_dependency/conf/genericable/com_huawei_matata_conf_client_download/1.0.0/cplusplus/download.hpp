/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_MATATA_CONF_CLIENT_DOWNLOAD_H
#define COM_HUAWEI_MATATA_CONF_CLIENT_DOWNLOAD_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>

namespace matata {
namespace conf {
namespace client {
struct __download {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::map<Fit::string, Fit::string> **>;
};

/**
 * 下载配置。
 *
 * @param key 表示配置的键。
 * @return 表示配置的值。
 */

/**
 * FitCode download(
 * const Fit::string *key,
 * Fit::map<Fit::string, Fit::string> **result)
 */
class download : public ::Fit::Framework::ProxyClient<FitCode(__download::InType, __download::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "918fbf55d64b4de8910d08cfcb41d31c";
    download() : ::Fit::Framework::ProxyClient<FitCode(__download::InType, __download::OutType)>(GENERIC_ID) {}
    ~download() = default;
};
}
}
}

#endif
