/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef SECURE_ACCESS_H
#define SECURE_ACCESS_H
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/memory.hpp>
#include <include/signature/signature.h>
#include <fit/internal/secure_access/token_role_repo.h>
#include <fit/internal/registry/repository/util_by_repo.h>
#include <include/repo/role_permissions_repo.h>
#include <include/repo/auth_key_repo.h>
#include <include/token_life_cycle_observer.h>
namespace Fit {
class SecureAccess {
public:
    SecureAccess(SignaturePtr signature, TokenRoleRepoPtr tokenRoleRepo, RolePermissionsRepoPtr rolePermissionsRepo,
        AuthKeyRepoPtr authKeyRepo, UtilByRepo* timeUtilByRepo);
    int32_t Sign(const string& ak, const string& timestamp, string& signatureStr);
    vector<AuthTokenRole> GetTokenRole(const string& ak, const string& timestamp, const string& signature);
    int32_t IsAuthorized(const string& token, const Permission& permission);
    int32_t RefreshAccessToken(const string& refreshToken, vector<AuthTokenRole>& tokenRoles);
    static SecureAccess& Instance();
    static SecureAccess Create();
    TokenRoleRepoPtr TokenRoleRepo();
    UtilByRepo* TimeUtil();
    AuthKeyRepoPtr AuthKeyRepo();
    RolePermissionsRepoPtr RolePermissionsRepo();
    void Register(TokenLifeCycleObserver* tokenLifeCycleObserver);
private:
    bool Verify(const string& ak, const string& timestamp, const string& signature);
    AuthTokenRole GetAccessToken(const Fit::string& role);
    AuthTokenRole GetRefreshToken(const Fit::string& role);
private:
    SignaturePtr signature_ {};
    TokenRoleRepoPtr tokenRoleRepo_ {};
    RolePermissionsRepoPtr rolePermissionsRepo_ {};
    AuthKeyRepoPtr authKeyRepo_ {};
    UtilByRepo* timeUtilByRepo_ {};
    TokenLifeCycleObserver* tokenLifeCycleObserver_ {};
};
}
#endif