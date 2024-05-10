/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http config
 * Author       : songyongtan
 * Create       : 2023-08-01
 * Notes:       :
 */

#ifndef FIT_HTTP_CONFIG_HPP
#define FIT_HTTP_CONFIG_HPP

#include <fit/stl/string.hpp>

namespace Fit {
class HttpConfig {
public:
    HttpConfig() = default;
    HttpConfig(string contextPath, string workerPath, int32_t protocol)
        : contextPath_(move(contextPath)), workerPath_(move(workerPath)), protocol_(protocol)
    {
        serverPath_ = contextPath_ + workerPath_;
    }
    HttpConfig(string contextPath, string workerPath, int32_t protocol, bool sslVerify, string cerPath,
        string privateKeyPath, string privateKeyPwd, string caCrtPath, string privateKeyPwdFilePath,
        string pwdCryptoType)
        : contextPath_(move(contextPath)), workerPath_(move(workerPath)), protocol_(protocol),
        isEnableSslVerify_(sslVerify), cerPath_(move(cerPath)), privateKeyPath_(move(privateKeyPath)),
        privateKeyPwd_(move(privateKeyPwd)), caCrtPath_(move(caCrtPath)),
        privateKeyPwdFilePath_(move(privateKeyPwdFilePath)), pwdCryptoType_(move(pwdCryptoType))
    {
        serverPath_ = contextPath_ + workerPath_;
        isUseSsl_ = true;
    }
    string GetClientPath(const string& contextPath, const string& genericableId, const string& fitableId) const
    {
        return contextPath + workerPath_ + '/' + genericableId + '/' + fitableId;
    }
    const string& GetServerPath() const noexcept
    {
        return serverPath_;
    }
    int32_t GetProtocol() const noexcept
    {
        return protocol_;
    }
    bool IsEnableSSLVerify() const noexcept
    {
        return isEnableSslVerify_;
    }
    const string& GetCerPath() const noexcept
    {
        return cerPath_;
    }
    const string& GetPrivateKeyPath() const noexcept
    {
        return privateKeyPath_;
    }
    const string& GetPrivateKeyPwd() const noexcept
    {
        return privateKeyPwd_;
    }

    bool IsUseSSL() const noexcept
    {
        return isUseSsl_;
    }
    const string& GetCaCrtPath() const noexcept
    {
        return caCrtPath_;
    }
    const string& GetPrivateKeyPwdFilePath() const noexcept
    {
        return privateKeyPwdFilePath_;
    }
    const string& GetCryptoType() const noexcept
    {
        return pwdCryptoType_;
    }
private:
    string contextPath_;
    string workerPath_;
    string serverPath_;
    int32_t protocol_ {-1};
    bool isEnableSslVerify_ {false};
    string cerPath_;
    string privateKeyPath_;
    string privateKeyPwd_;
    bool isUseSsl_ {false};
    string caCrtPath_;
    string privateKeyPwdFilePath_;
    string pwdCryptoType_;
};
}
#endif