/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#include <include/secure_access_config.h>
#include <fit/fit_log.h>
namespace Fit {
using namespace Fit::Config;
namespace {
constexpr const char* CRYPTO_TYPE = "secure-access.crypto-type";
constexpr const char* ACCESS_KEY_TYPE = "secure-access.local-ak";
constexpr const char* ROLE_INFO_TYPE = "secure-access.role-info";

int32_t ParsePermissions(Value& permissions, vector<Permission>& permissionsIn)
{
    permissionsIn.reserve(permissions.Size());
    for (int32_t i = 0; i < permissions.Size(); ++i) {
        Value& permission = permissions[i];
        if (!permission.IsObject()) {
            FIT_LOG_ERROR("Role permission not object.");
            return FIT_ERR_FAIL;
        }

        fit::hakuna::kernel::shared::Fitable fitable;
        fitable.genericableId = permission["genericableId"].AsString("");
        fitable.genericableVersion = permission["genericableVersion"].AsString("");
        fitable.fitableId = permission["fitableId"].AsString("");
        fitable.fitableVersion = permission["fitableVersion"].AsString("");
        FIT_LOG_INFO("gid:gversion:fid:fversion (%s:%s:%s:%s).", fitable.genericableId.c_str(),
            fitable.genericableVersion.c_str(), fitable.fitableId.c_str(), fitable.fitableVersion.c_str());
        Permission permissionValue(fitable);
        permissionsIn.emplace_back(permissionValue);
    }
    return FIT_OK;
}
}

FitCode SecureAccessConfig::InitConfig(Fit::Plugin::PluginConfigPtr pluginConfig)
{
    cryptoType_ = pluginConfig->Get(CRYPTO_TYPE).AsString("");
    accessKey_ = pluginConfig->Get(ACCESS_KEY_TYPE).AsString(""); // todo 解密，或者ak不加密
    FIT_LOG_INFO("Crypto type : access key (%s:%s).", cryptoType_.c_str(), accessKey_.c_str());

    Value& roleInfos = pluginConfig->Get(ROLE_INFO_TYPE);
    if (!roleInfos.IsArray()) {
        FIT_LOG_ERROR("Role infos not array.");
        return FIT_ERR_FAIL;
    }
    for (int32_t i = 0; i < roleInfos.Size(); ++i) {
        Value& roleInfo = roleInfos[i];
        if (!roleInfo.IsObject()) {
            FIT_LOG_ERROR("Role info not object.");
            return FIT_ERR_FAIL;
        }

        AuthKey authKey;
        authKey.role = roleInfo["role"].AsString("");
        authKey.ak = roleInfo["ak"].AsString("");
        authKey.sk = roleInfo["sk"].AsString("");
        authKeys_.emplace_back(authKey);
        FIT_LOG_CORE("Role is (%s).", authKey.role.c_str());

        Value& permissions = roleInfo["permissions"];
        if (!permissions.IsArray()) {
            FIT_LOG_ERROR("Role permissions not array.");
            return FIT_ERR_FAIL;
        }

        vector<Permission> permissionsIn;
        int32_t ret = ParsePermissions(permissions, permissionsIn);
        if (ret != FIT_OK) {
            return ret;
        }

        RolePermissions rolePermissions(authKey.role, permissionsIn);
        rolePermissionsSet_.emplace_back(rolePermissions);
    }
    return FIT_OK;
}

const Fit::string& SecureAccessConfig::AccessKey()
{
    return accessKey_;
}

const Fit::string& SecureAccessConfig::CryptoType()
{
    return cryptoType_;
}

const Fit::vector<AuthKey>& SecureAccessConfig::AuthKeys()
{
    return authKeys_;
}

const Fit::vector<RolePermissions>& SecureAccessConfig::RolePermissionsSet()
{
    return rolePermissionsSet_;
}

SecureAccessConfig& SecureAccessConfig::Instance()
{
    static SecureAccessConfig secureAccessConfig;
    return secureAccessConfig;
}
}