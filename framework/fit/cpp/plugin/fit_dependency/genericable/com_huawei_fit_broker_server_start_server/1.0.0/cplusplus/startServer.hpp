/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_BROKER_SERVER_STARTSERVER_H
#define COM_HUAWEI_FIT_BROKER_SERVER_STARTSERVER_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
namespace fit {
namespace broker {
namespace server {
struct __startServer {
    using OutType = ::Fit::Framework::ArgumentsOut<::fit::registry::Address **>;
};


/**
 * FitCode startServer(::fit::registry::Address **result)
 */
class startServer : public ::Fit::Framework::ProxyClient<FitCode(__startServer::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "9289a2a4322d47d38f33fc32c47f04d2";
    startServer() : ::Fit::Framework::ProxyClient<FitCode(__startServer::OutType)>(GENERIC_ID) {}
    ~startServer() = default;
};
}
}
}

#endif
