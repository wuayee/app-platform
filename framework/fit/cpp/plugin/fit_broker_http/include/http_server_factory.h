/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide http server factory
 * Author       : w00561424
 * Date:        : 2024/03/12
 */
#ifndef HTTP_SERVER_FACTORY_H
#define HTTP_SERVER_FACTORY_H
#include <cpp-httplib/httplib.h>
#include <fit/stl/memory.hpp>
#include <fit/internal/runtime/crypto/crypto_manager.hpp>
namespace Fit {
class HttpServerFactory {
public:
static unique_ptr<httplib::Server> CreateHttpsServer(const HttpConfig* config)
{
    unique_ptr<httplib::Server> server {nullptr};
    if (config->IsUseSSL()) {
#ifdef CPPHTTPLIB_OPENSSL_SUPPORT
        Fit::string keyPwd = config->GetPrivateKeyPwd();
        CryptoManager::Instance().Get(config->GetCryptoType())->Decrypt(keyPwd.c_str(), keyPwd.length(), keyPwd);
        if (keyPwd.empty()) {
            keyPwd = config->GetPrivateKeyPwd();
        }
        server = make_unique<httplib::SSLServer>(config->GetCerPath().c_str(), config->GetPrivateKeyPath().c_str(),
            config->GetCaCrtPath().c_str(), nullptr, keyPwd.c_str());
        keyPwd = config->GetPrivateKeyPwd(); // 重置数据
        FIT_LOG_INFO("Cert:key:ca %s:%s:%s.", config->GetCerPath().c_str(), config->GetPrivateKeyPath().c_str(),
            config->GetCaCrtPath().c_str());
#endif
    } else {
        server = make_unique<httplib::Server>();
        FIT_LOG_INFO("Https server no ssl");
    }
    return server;
}

static unique_ptr<httplib::Server> CreateHttpServer()
{
    FIT_LOG_INFO("Https server no ssl");
    return make_unique<httplib::Server>();
}
};
}
#endif
