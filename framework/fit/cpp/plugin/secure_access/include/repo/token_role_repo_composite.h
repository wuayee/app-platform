/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/23
 */
#ifndef TOKEN_ROLE_REPO_COMPOSITE_H
#define TOKEN_ROLE_REPO_COMPOSITE_H
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/internal/secure_access/token_role_repo.h>
namespace Fit {
class TokenRoleRepoComposite : public TokenRoleRepo {
public:
    TokenRoleRepoComposite(TokenRoleRepoPtr memoryRepo, TokenRoleRepoPtr dbRepo);
    int32_t Save(const vector<AuthTokenRole>& authTokenRoles) override;
    int32_t Remove(const vector<string>& tokens) override;
    vector<AuthTokenRole> Query(const vector<string>& tokens) override;
    vector<AuthTokenRole> QueryAll() override;
private:
    TokenRoleRepoPtr memoryRepo_ {};
    TokenRoleRepoPtr dbRepo_ {};
};
}
#endif