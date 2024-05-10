/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implement for fit http server manager
 * Author       : songyongtan
 * Create       : 2023-08-01
 * Notes:       :
 */

#include "http_manager.hpp"
#include <include/http_client_factory.h>
#include <include/http_server_factory.h>
#include <httplib_util.hpp>

namespace Fit {
HttpManager& HttpManager::Instance()
{
    static HttpManager instance;
    return instance;
}

void HttpManager::InitHttpServer(string host, int32_t port)
{
    httpServer_ = make_unique<HttpServer>(move(host), port, &httpConfig_, HttpServerFactory::CreateHttpServer());
}

void HttpManager::InitHttpsServer(string host, int32_t port)
{
    httpsServer_ = make_unique<HttpServer>(move(host), port, &httpsConfig_,
        HttpServerFactory::CreateHttpsServer(&httpsConfig_));
}

int32_t HttpManager::InitHttpsClient()
{
    return HttpClientFactory::Instance()->InitHttps(&httpsConfig_);
}
void HttpManager::SetHttpConfig(string contextPath, string workerPath, int32_t protocol)
{
    httpConfig_ = HttpConfig(move(contextPath), move(workerPath), protocol);
}

void HttpManager::SetHttpsConfig(string contextPath, string workerPath, int32_t protocol, bool sslVerify,
    string cerPath, string privateKeyPath, string privateKeyPwd, string caCrtPth, string keyPwdFilePath,
    string pwdCryptoType)
{
    httpsConfig_ = HttpConfig(move(contextPath), move(workerPath), protocol, sslVerify, move(cerPath),
        move(privateKeyPath), move(privateKeyPwd), move(caCrtPth), move(keyPwdFilePath), move(pwdCryptoType));
}

HttpServer* HttpManager::GetHttpServer()
{
    return httpServer_.get();
}

HttpServer* HttpManager::GetHttpsServer()
{
    return httpsServer_.get();
}

HttpClientPtr HttpManager::GetClient(string host, int32_t port, string contextPath)
{
    return Fit::make_shared<HttpClientTemp<Fit::shared_ptr<httplib::Client>>>(contextPath, &httpConfig_,
        HttpClientFactory::Instance()->CreateHttpClient(&httpConfig_, host, port),
        HttplibUtil::GetHttpAddress(host, port));
}

HttpClientPtr HttpManager::GetHttpsClient(string host, int32_t port, string contextPath)
{
#ifdef CPPHTTPLIB_OPENSSL_SUPPORT
    return Fit::make_shared<HttpClientTemp<Fit::shared_ptr<httplib::SSLClient>>>(contextPath, &httpsConfig_,
        HttpClientFactory::Instance()->CreateHttpsClient(&httpsConfig_, host, port),
        HttplibUtil::GetHttpsAddress(host, port));
#else
    return Fit::make_shared<HttpClientTemp<Fit::shared_ptr<httplib::Client>>>(contextPath, &httpsConfig_,
        HttpClientFactory::Instance()->CreateHttpsClientNoSSL(&httpsConfig_, host, port),
        HttplibUtil::GetHttpsAddress(host, port));
#endif
}
}