/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :provide http and https client factory.
 * Author       : w00561424
 * Date:        : 2024/03/12
 */
#include <include/http_client_factory.h>
#include <fit/fit_log.h>
#include <httplib_util.hpp>
#include <fit/internal/runtime/crypto/crypto_manager.hpp>
namespace Fit {
#ifdef CPPHTTPLIB_OPENSSL_SUPPORT
class Certification {
public:
    X509* GetX05()
    {
        return x_;
    }

    EVP_PKEY* GetPrivateKey()
    {
        return pkey_;
    }

    X509* GetX05(const char *file)
    {
        BIO *in = BIO_new(BIO_s_file());
        if (in == NULL) {
            FIT_LOG_ERROR("Open crt file error.");
            return nullptr;
        }

        if (BIO_read_filename(in, file) <= 0) {
            BIO_free(in);
            FIT_LOG_ERROR("Read crt file error.");
            return nullptr;
        }

        X509* x = PEM_read_bio_X509(in, nullptr, nullptr, nullptr);
        BIO_free(in);
        FIT_LOG_CORE("Get x05 success.");
        return x;
    }

    EVP_PKEY* GetPrivateKey(const char *file, const char* password)
    {
        BIO *in = BIO_new(BIO_s_file());
        if (in == nullptr) {
            return nullptr;
        }

        if (BIO_read_filename(in, file) <= 0) {
            BIO_free(in);
            FIT_LOG_ERROR("Read pkey file error.");
            return nullptr;
        }

        EVP_PKEY *pkey = PEM_read_bio_PrivateKey(in, nullptr, nullptr, (void*)password);
        BIO_free(in);
        FIT_LOG_CORE("Get pkey success.");
        return pkey;
    }

    int32_t Init(HttpConfig* config)
    {
        x_ = GetX05(config->GetCerPath().c_str());
        if (x_ == nullptr) {
            FIT_LOG_ERROR("Get x005 failed.");
            return FIT_ERR_FAIL;
        }
        Fit::string keyPwd = config->GetPrivateKeyPwd();
        FIT_LOG_CORE("Encrypted pwd is (%s).", keyPwd.c_str());
        // 解密
        CryptoManager::Instance().Get(config->GetCryptoType())->Decrypt(keyPwd.c_str(), keyPwd.length(), keyPwd);
        FIT_LOG_INFO("Crypto type is %s.", config->GetCryptoType().c_str());
        if (keyPwd.empty()) {
            keyPwd = config->GetPrivateKeyPwd();
        }
        pkey_ = GetPrivateKey(config->GetPrivateKeyPath().c_str(), keyPwd.c_str());
        keyPwd = config->GetPrivateKeyPwd(); // 重置数据
        return FIT_OK;
    }
    static Certification* Instance()
    {
        static Certification* cert = new Certification();
        return cert;
    }
private:
    X509* x_ {nullptr};
    EVP_PKEY *pkey_ {nullptr};
};

Fit::shared_ptr<httplib::SSLClient> HttpClientFactory::CreateHttpsClient(
    const HttpConfig* config, Fit::string host, int32_t port)
{
    Fit::shared_ptr<httplib::SSLClient> client {nullptr};
    X509* x = Certification::Instance()->GetX05();
    EVP_PKEY* pKey = Certification::Instance()->GetPrivateKey();
    client = Fit::make_shared<httplib::SSLClient>(host, port, x, pKey);
    client->set_ca_cert_path(config->GetCaCrtPath().c_str());
    client->enable_server_certificate_verification(config->IsEnableSSLVerify());
    return client;
}
#endif
int32_t HttpClientFactory::InitHttps(HttpConfig* config)
{
    Certification::Instance()->Init(config);
    return FIT_OK;
}

HttpClientFactory* HttpClientFactory::Instance()
{
    static HttpClientFactory* factory = new HttpClientFactory();
    return factory;
}

Fit::shared_ptr<httplib::Client> HttpClientFactory::CreateHttpClient(
    const HttpConfig* config, Fit::string host, int32_t port)
{
    return Fit::make_shared<httplib::Client>(HttplibUtil::GetHttpAddress(host, port));
}
Fit::shared_ptr<httplib::Client> HttpClientFactory::CreateHttpsClientNoSSL(
    const HttpConfig* config, Fit::string host, int32_t port)
{
    return Fit::make_shared<httplib::Client>(HttplibUtil::GetHttpsAddress(host, port));
}
}