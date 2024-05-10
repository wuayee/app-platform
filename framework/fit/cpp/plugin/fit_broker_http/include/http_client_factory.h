/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :provide http and https client factory.
 * Author       : w00561424
 * Date:        : 2024/03/12
 */
#ifndef HTTP_CLIENT_FACTORY_H
#define HTTP_CLIENT_FACTORY_H
#include <cpp-httplib/httplib.h>
#include <http_config.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
namespace Fit {
class HttpClientFactory {
public:
#ifdef CPPHTTPLIB_OPENSSL_SUPPORT
    Fit::shared_ptr<httplib::SSLClient> CreateHttpsClient(
        const HttpConfig* config, Fit::string host, int32_t port);
#endif
    Fit::shared_ptr<httplib::Client> CreateHttpClient(
        const HttpConfig* config, Fit::string host, int32_t port);
    Fit::shared_ptr<httplib::Client> CreateHttpsClientNoSSL(
        const HttpConfig* config, Fit::string host, int32_t port);
    int32_t InitHttps(HttpConfig* config);

    static HttpClientFactory* Instance();
};
}
#endif