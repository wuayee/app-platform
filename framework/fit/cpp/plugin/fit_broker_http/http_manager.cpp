/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implement for fit http server manager
 * Author       : songyongtan
 * Create       : 2023-08-01
 * Notes:       :
 */

#include "http_manager.hpp"
#include <include/http_server_factory.h>
#include <httplib_util.hpp>
#include <include/curl_http_client.h>
#include <include/curl_https_client.h>
namespace Fit {
HttpManager& HttpManager::Instance()
{
    static HttpManager instance;
    return instance;
}

void HttpManager::InitHttpServer(string host, int32_t port)
{
    httpServer_ = make_unique<HttpServer>(move(host), port, GetHttpConfig(), HttpServerFactory::CreateHttpServer());
}

void HttpManager::InitHttpsServer(string host, int32_t port)
{
    httpsServer_ = make_unique<HttpServer>(move(host), port, GetHttpsConfig(),
        HttpServerFactory::CreateHttpsServer(GetHttpsConfig()));
}

void HttpManager::InitHttpClient()
{
    CurlHttpClient::GlobalInit();
}

void HttpManager::UninitHttpClient()
{
    CurlHttpClient::GlobalUninit();
}

void HttpManager::SetHttpConfig(string contextPath, string workerPath, int32_t protocol)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    httpConfig_  = Fit::make_unique<HttpConfig>(move(contextPath), move(workerPath), protocol);
}

void HttpManager::SetHttpsConfig(string contextPath, string workerPath, int32_t protocol, bool sslVerify,
    string cerPath, string privateKeyPath, string privateKeyPwd, string caCrtPth, string keyPwdFilePath,
    string pwdCryptoType)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    httpsConfig_ = Fit::make_unique<HttpConfig>(move(contextPath), move(workerPath), protocol, sslVerify, move(cerPath),
        move(privateKeyPath), move(privateKeyPwd), move(caCrtPth), move(keyPwdFilePath), move(pwdCryptoType));
}

HttpConfig* HttpManager::GetHttpConfig()
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    if (httpConfig_ == nullptr) {
        FIT_LOG_ERROR("Http config is null.");
        return nullptr;
    }
    return httpConfig_.get();
}

HttpConfig* HttpManager::GetHttpsConfig()
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    if (httpsConfig_ == nullptr) {
        FIT_LOG_ERROR("Https config is null.");
        return nullptr;
    }
    return httpsConfig_.get();
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
    if (GetHttpConfig() == nullptr) {
        return nullptr;
    }
    return Fit::make_shared<CurlHttpClient>(contextPath, GetHttpConfig(), HttplibUtil::GetHttpAddress(host, port));
}

HttpClientPtr HttpManager::GetHttpsClient(string host, int32_t port, string contextPath)
{
    if (GetHttpsConfig() == nullptr) {
        return nullptr;
    }
#ifdef CPPHTTPLIB_OPENSSL_SUPPORT
    return Fit::make_shared<CurlHttpsClient>(contextPath, GetHttpsConfig(), HttplibUtil::GetHttpsAddress(host, port));
#else
    return Fit::make_shared<CurlHttpClient>(contextPath, GetHttpsConfig(), HttplibUtil::GetHttpsAddress(host, port));
#endif
}
}