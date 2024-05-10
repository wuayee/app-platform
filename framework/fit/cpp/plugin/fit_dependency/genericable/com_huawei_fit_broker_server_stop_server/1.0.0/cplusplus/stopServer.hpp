/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_BROKER_SERVER_STOPSERVER_H
#define COM_HUAWEI_FIT_BROKER_SERVER_STOPSERVER_H

#include <fit/external/framework/proxy_client.hpp>

namespace fit {
namespace broker {
namespace server {
/**
 * FitCode stopServer()
 */
class stopServer : public ::Fit::Framework::ProxyClient<FitCode()> {
public:
    static constexpr const char *GENERIC_ID = "1b9bfc4a2b2141d5b31aa06791d645b4";
    stopServer() : ::Fit::Framework::ProxyClient<FitCode()>(GENERIC_ID) {}
    ~stopServer() = default;
};
}
}
}

#endif
