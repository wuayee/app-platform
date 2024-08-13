/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/21
 */
#include <include/secure_access.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <include/repo/token_role_repo_composite.h>
namespace Fit {
std::once_flag initFlag;
SecureAccess::SecureAccess(SignaturePtr signature, TokenRoleRepoPtr tokenRoleRepo,
    RolePermissionsRepoPtr rolePermissionsRepo, AuthKeyRepoPtr authKeyRepo, UtilByRepo* timeUtilByRepo) :
    signature_(std::move(signature)), tokenRoleRepo_(std::move(tokenRoleRepo)),
    rolePermissionsRepo_(std::move(rolePermissionsRepo)), authKeyRepo_(std::move(authKeyRepo)),
    timeUtilByRepo_(timeUtilByRepo)
{
}

int32_t SecureAccess::Sign(const string& ak, const string& timestamp, string& signatureStr)
{
    signatureStr = signature_->Sign(ak, timestamp);
    return FIT_OK;
}

vector<AuthTokenRole> SecureAccess::GetTokenRole(const string& ak, const string& timestamp, const string& signature)
{
    if (ak.empty() || !Verify(ak, timestamp, signature)) {
        FIT_LOG_ERROR("No authorization, ak is %s.", ak.c_str());
        return {};
    }
    vector<AuthKey> authKeys = authKeyRepo_->Query({ak});
    if (authKeys.empty()) {
        FIT_LOG_ERROR("No authorization, ak is %s.", ak.c_str());
        return {};
    }
    string role = authKeys.front().role;
    vector<AuthTokenRole> tokens {};
    tokens.emplace_back(GetRefreshToken(role));
    tokens.emplace_back(GetAccessToken(role));
    return tokens;
}

int32_t SecureAccess::IsAuthorized(const string& token, const Permission& permission)
{
    vector<AuthTokenRole> tokenRoles = tokenRoleRepo_->Query({token});
    if (tokenRoles.empty()) {
        FIT_LOG_ERROR("Access token not found %s.", token.c_str());
        return FIT_ERR_AUTHENTICATION_INVALID_ACCESS_TOKEN;
    }
    if (tokenRoles.front().type != Fit::string(ACCESS_TOKEN_TYPE)) {
        FIT_LOG_ERROR("Invalid type, type (%s).", tokenRoles.front().type.c_str());
        return FIT_ERR_AUTHENTICATION_INVALID_ACCESS_TOKEN;
    }

    vector<RolePermissions> rolePermissionsSet = rolePermissionsRepo_->Query({tokenRoles.front().role});
    if (rolePermissionsSet.empty()) {
        FIT_LOG_ERROR("Role permission is empty, role is %s.", tokenRoles.front().role.c_str());
        return FIT_ERR_AUTHENTICATION_ROLE_NO_PERMISSION;
    }

    bool ret = rolePermissionsSet.front().ContainPermission(permission);
    if (!ret) {
        FIT_LOG_ERROR("Not authorize, gid is (%s).", permission.fitable.genericableId.c_str());
        return FIT_ERR_AUTHENTICATION_ROLE_NO_PERMISSION;
    }
    return FIT_OK;
}

int32_t SecureAccess::RefreshAccessToken(const string& refreshToken, vector<AuthTokenRole>& tokenRoles)
{
    vector<AuthTokenRole> refreshTokenRoles = tokenRoleRepo_->Query({refreshToken});
    if (refreshTokenRoles.empty()) {
        FIT_LOG_INFO("Refresh token is invalid.");
        return FIT_ERR_AUTHENTICATION_INVALID_FRESH_TOKEN;
    }
    tokenRoles = {GetAccessToken(refreshTokenRoles.front().role)};
    FIT_LOG_DEBUG("Refresh access token.");
    return FIT_OK;
}

bool SecureAccess::Verify(const string& ak, const string& timestamp, const string& signature)
{
    return signature_->Verify(ak, timestamp, signature);
}

AuthTokenRole SecureAccess::GetAccessToken(const Fit::string& role)
{
    uint64_t result {};
    timeUtilByRepo_->GetCurrentTimeMs(result);
    AuthTokenRole tokenRole = CreateTokenRole(ACCESS_TOKEN_TYPE, role,
        DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS, result);
    tokenRoleRepo_->Save({tokenRole});
    FIT_LOG_DEBUG("Get access token.");
    return tokenRole;
}

AuthTokenRole SecureAccess::GetRefreshToken(const Fit::string& role)
{
    uint64_t result {};
    timeUtilByRepo_->GetCurrentTimeMs(result);
    AuthTokenRole tokenRole = CreateTokenRole(FRESH_TOKEN_TYPE, role,
        DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS, result);
    tokenRoleRepo_->Save({tokenRole});
    FIT_LOG_DEBUG("Get refresh token.");
    return tokenRole;
}

void SecureAccess::Register(TokenLifeCycleObserver* tokenLifeCycleObserver)
{
    tokenLifeCycleObserver_ = tokenLifeCycleObserver;
}

TokenRoleRepoPtr SecureAccess::TokenRoleRepo()
{
    return tokenRoleRepo_;
}

UtilByRepo* SecureAccess::TimeUtil()
{
    return timeUtilByRepo_;
}

AuthKeyRepoPtr SecureAccess::AuthKeyRepo()
{
    return authKeyRepo_;
}

RolePermissionsRepoPtr SecureAccess::RolePermissionsRepo()
{
    return rolePermissionsRepo_;
}

SecureAccess SecureAccess::Create()
{
    AuthKeyRepoPtr authKeyRepo = AuthKeyRepoFactory::Create();
    return SecureAccess(SignatureFactory::Create(authKeyRepo),
        Fit::make_shared<TokenRoleRepoComposite>(TokenRoleRepoFactory::CreateMemoryRepo(),
        TokenRoleRepoFactory::CreateDbRepo()), RolePermissionsRepoFactory::Create(),
        authKeyRepo, &UtilByRepo::Instance());
}

SecureAccess& SecureAccess::Instance()
{
    static SecureAccess secureAccess = std::move(SecureAccess::Create());
    return secureAccess;
}
}