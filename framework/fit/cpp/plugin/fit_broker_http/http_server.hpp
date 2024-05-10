/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef FIT_HTTP_SERVER_HPP
#define FIT_HTTP_SERVER_HPP

#include <thread>
#include <cpp-httplib/httplib.h>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/network/network_define.h>

#include "http_config.hpp"

namespace Fit {
class HttpServer {
public:
    HttpServer(string host, int32_t port, const HttpConfig* config, unique_ptr<httplib::Server> svr);
    ~HttpServer();
    using Handler = FitCode(const Network::Request&, Network::Response&);
    struct Result {
        uint32_t status;
        string msg;
    };
    const string& GetHost() const noexcept;
    int32_t GetPort() const noexcept;
    int32_t GetProtocol() const noexcept;
    string GetPattern() const;

    int32_t Start(Handler handler);
    int32_t Stop();
private:
    std::thread serverThread_ {};
    string host_;
    int32_t port_ {};
    const HttpConfig* config_ {};
    unique_ptr<httplib::Server> svr_;
};
}

#endif