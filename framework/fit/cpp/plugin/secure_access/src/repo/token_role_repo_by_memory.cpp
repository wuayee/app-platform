/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/23
 */
#include <include/repo/token_role_repo_by_memory.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
namespace Fit {
int32_t TokenRoleRepoByMemory::Save(const vector<AuthTokenRole>& authTokenRoles)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    for (const auto& tokenRole : authTokenRoles) {
        auto it = tokenRoles_.find(tokenRole.token);
        if (it == tokenRoles_.end()) {
            tokenRoles_[tokenRole.token] = tokenRole;
        } else if (!(tokenRole == it->second)) {
            FIT_LOG_ERROR("Token role update, token is %s.", tokenRole.token.c_str());
            return FIT_ERR_FAIL;
        }
    }
    return FIT_OK;
}

int32_t TokenRoleRepoByMemory::Remove(const vector<string>& tokens)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    for (const auto& token : tokens) {
        tokenRoles_.erase(token);
    }
    return FIT_OK;
}

vector<AuthTokenRole> TokenRoleRepoByMemory::Query(const vector<string>& tokens)
{
    vector<AuthTokenRole> tokenRoles;
    tokenRoles.reserve(tokens.size());
    unique_lock<mutex> lock(mutex_);
    for (const auto& token : tokens) {
        auto it = tokenRoles_.find(token);
        if (it != tokenRoles_.end()) {
            tokenRoles.emplace_back(it->second);
        }
    }
    return tokenRoles;
}

vector<AuthTokenRole> TokenRoleRepoByMemory::QueryAll()
{
    vector<AuthTokenRole> tokenRoles;
    unique_lock<mutex> lock(mutex_);
    tokenRoles.reserve(tokenRoles_.size());
    for (const auto& tokenRole : tokenRoles_) {
        tokenRoles.emplace_back(tokenRole.second);
    }
    return tokenRoles;
}

TokenRoleRepoPtr TokenRoleRepoFactory::CreateMemoryRepo()
{
    return Fit::make_shared<TokenRoleRepoByMemory>();
}
}