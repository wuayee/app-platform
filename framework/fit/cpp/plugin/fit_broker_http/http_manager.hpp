/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http server manager
 * Author       : songyongtan
 * Create       : 2023-08-01
 * Notes:       :
 */

#ifndef FIT_HTTP_SERVER_MANAGER_HPP
#define FIT_HTTP_SERVER_MANAGER_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/mutex.hpp>

#include "http_server.hpp"
#include "http_client.hpp"
#include "http_config.hpp"

namespace Fit {
class HttpManager {
public:
    static HttpManager& Instance();

    void InitHttpServer(string host, int32_t port);
    void InitHttpsServer(string host, int32_t port);
    void SetHttpConfig(string contextPath, string workerPath, int32_t protocol);
    void SetHttpsConfig(string contextPath, string workerPath, int32_t protocol, bool sslVerify, string cerPath,
        string privateKeyPath, string privateKeyPwd, string caCrtPth, string keyPwdFilePath, string sccConfFilePath);
    void InitHttpClient();
    void UninitHttpClient();
    HttpServer* GetHttpServer();
    HttpServer* GetHttpsServer();
    HttpClientPtr GetClient(string host, int32_t port, string contextPath);
    HttpClientPtr GetHttpsClient(string host, int32_t port, string contextPath);

private:
    HttpConfig* GetHttpConfig();
    HttpConfig* GetHttpsConfig();
private:
    HttpManager() = default;
    HttpManager(HttpManager&&) = delete;
    HttpManager(const HttpManager&) = delete;
    HttpManager& operator=(HttpManager&&) = delete;
    HttpManager& operator=(const HttpManager&) = delete;

    unique_ptr<HttpServer> httpServer_;
    unique_ptr<HttpServer> httpsServer_;
    Fit::mutex mutex_ {};
    unique_ptr<HttpConfig> httpConfig_ {nullptr};
    unique_ptr<HttpConfig> httpsConfig_ {nullptr};
};
}

#endif