/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/23
 */
#include <include/repo/token_role_repo_composite.h>
#include <fit/fit_code.h>
#include <algorithm>
namespace Fit {
TokenRoleRepoComposite::TokenRoleRepoComposite(TokenRoleRepoPtr memoryRepo, TokenRoleRepoPtr dbRepo)
    : memoryRepo_(std::move(memoryRepo)), dbRepo_(std::move(dbRepo))
{
}

int32_t TokenRoleRepoComposite::Save(const vector<AuthTokenRole>& authTokenRoles)
{
    int32_t ret = FIT_OK;
    if (dbRepo_ != nullptr) {
        ret = dbRepo_->Save(authTokenRoles);
    }
    if (memoryRepo_ != nullptr) {
        memoryRepo_->Save(authTokenRoles);
    }
    return ret;
}

int32_t TokenRoleRepoComposite::Remove(const vector<string>& tokens)
{
    int32_t ret = FIT_OK;
    if (dbRepo_ != nullptr) {
        ret = dbRepo_->Remove(tokens);
    }
    if (memoryRepo_ != nullptr) {
        memoryRepo_->Remove(tokens);
    }
    return ret;
}

vector<AuthTokenRole> TokenRoleRepoComposite::Query(const vector<string>& tokens)
{
    vector<AuthTokenRole> result {};
    vector<string> missingTokens {};
    if (memoryRepo_ != nullptr) {
        for (const auto& token : tokens) {
            vector<AuthTokenRole> tempTokenRoles = memoryRepo_->Query({token});
            if (tempTokenRoles.empty()) {
                missingTokens.emplace_back(token);
            } else {
                result.insert(result.end(), tempTokenRoles.begin(), tempTokenRoles.end());
            }
        }
    } else {
        missingTokens = tokens;
    }
    // token对应的值不会更新
    if (!missingTokens.empty() && dbRepo_ != nullptr) {
        vector<AuthTokenRole> tempTokenRoles = dbRepo_->Query(missingTokens);
        result.insert(result.end(), tempTokenRoles.begin(), tempTokenRoles.end());
        if (!tempTokenRoles.empty() && memoryRepo_ != nullptr) {
            memoryRepo_->Save(tempTokenRoles);
        }
    }

    return result;
}

vector<AuthTokenRole> TokenRoleRepoComposite::QueryAll()
{
    vector<AuthTokenRole> result {};
    if (memoryRepo_ != nullptr) {
        result = memoryRepo_->QueryAll();
    }
    if (dbRepo_ != nullptr) {
        vector<AuthTokenRole> tokenRoles = dbRepo_->QueryAll();
        if (result.empty()) {
            result = tokenRoles;
        } else {
            for (const auto& tokenRole : tokenRoles) {
                if (std::find(result.begin(), result.end(), tokenRole) == result.end()) {
                    result.insert(result.end(), tokenRole);
                }
            }
        }
    }
    return result;
}
}