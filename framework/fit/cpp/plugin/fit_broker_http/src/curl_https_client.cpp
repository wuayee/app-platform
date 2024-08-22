/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供对的 https 客户端的实现
 * Author       : w00561424
 * Date:        : 2024/05/09
 */
#include <include/curl_https_client.h>
#include <curl/include/curl/easy.h>
#include <fit/internal/runtime/crypto/crypto_manager.hpp>
namespace Fit {

CurlHttpsClient::CurlHttpsClient(string contextPath, const HttpConfig* config, string hostAndPort)
    : CurlHttpClient(contextPath, config, hostAndPort), config_(config)
{
}

void CurlHttpsClient::OpenSsl(CURL* curl)
{
    // 设置 SSL 认证并忽略主机名校验
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 1L);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0L);

    // 设置客户端证书、私钥和密码
    curl_easy_setopt(curl, CURLOPT_CAINFO, config_->GetCaCrtPath().c_str());
    curl_easy_setopt(curl, CURLOPT_SSLCERT, config_->GetCerPath().c_str());
    curl_easy_setopt(curl, CURLOPT_SSLKEY, config_->GetPrivateKeyPath().c_str());
    Fit::string keyPwd = config_->GetPrivateKeyPwd();
    if (keyPwd.empty()) {
        return;
    }
    CryptoManager::Instance().Get(config_->GetCryptoType())->Decrypt(keyPwd.c_str(), keyPwd.length(), keyPwd);
    if (keyPwd.empty()) {
        keyPwd = config_->GetPrivateKeyPwd();
    }
    curl_easy_setopt(curl, CURLOPT_KEYPASSWD, keyPwd.c_str());
    keyPwd.clear(); // 重置数据
}

CURL* CurlHttpsClient::PreProcess(int64_t timeoutMs)
{
    CURL* curl = CurlHttpClient::PreProcess(timeoutMs);
    if (curl != nullptr) {
        OpenSsl(curl);
    }
    return curl;
}
FitCode CurlHttpsClient::RequestResponse(const fit::hakuna::kernel::broker::client::RequestParam& req, Response& result)
{
    return CurlHttpClient::RequestResponse(req, result);
}

FitCode CurlHttpsClient::AfterProcess(CURL* curl)
{
    return CurlHttpClient::AfterProcess(curl);
}
}