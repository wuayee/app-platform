/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供 Key 存储的接口定义。
 * Author       : 王攀博 w00561424
 * Date:        : 2024/05/20
 */
#ifndef TOKEN_REPO_H
#define TOKEN_REPO_H
#include <fit/internal/secure_access/auth_token_role.h>
#include <fit/stl/mutex.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/vector.hpp>
namespace Fit {
class TokenRoleRepo {
public:
    virtual ~TokenRoleRepo() = default;
    virtual int32_t Save(const vector<AuthTokenRole>& authTokenRoles) = 0;
    virtual int32_t Remove(const vector<string>& tokens) = 0;
    virtual vector<AuthTokenRole> Query(const vector<string>& tokens) = 0;
    virtual vector<AuthTokenRole> QueryAll() = 0;
};

using TokenRoleRepoPtr = shared_ptr<TokenRoleRepo>;

class TokenRoleRepoFactory {
public:
    static TokenRoleRepoPtr CreateDbRepo();
    static TokenRoleRepoPtr CreateMemoryRepo();
};
}
#endif