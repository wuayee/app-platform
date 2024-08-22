/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#include <include/repo/default_auth_key_repo.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
namespace Fit {
int32_t DefaultAuthKeyRepo::Save(const vector<AuthKey>& authKeys)
{
    unique_lock<mutex> lock(mutex_);
    for (const auto& authKey : authKeys) {
        auto it = authKeys_.find(authKey.ak);
        if (it == authKeys_.end()) {
            authKeys_[authKey.ak] = authKey;
        } else if (!(authKey == it->second)) {
            it->second = authKey;
        }
    }
    return FIT_OK;
}

int32_t DefaultAuthKeyRepo::Remove(const vector<string>& accessKeys)
{
    unique_lock<mutex> lock(mutex_);
    for (const auto& accessKey : accessKeys) {
        authKeys_.erase(accessKey);
    }
    return FIT_OK;
}

vector<AuthKey> DefaultAuthKeyRepo::Query(const vector<string>& accessKeys)
{
    vector<AuthKey> aks;
    aks.reserve(accessKeys.size());
    unique_lock<mutex> lock(mutex_);
    for (const auto& accessKey : accessKeys) {
        auto authKeyIt = authKeys_.find(accessKey);
        if (authKeyIt != authKeys_.end()) {
            aks.emplace_back(authKeyIt->second);
        }
    }
    return aks;
}

AuthKeyRepoPtr AuthKeyRepoFactory::Create()
{
    static AuthKeyRepoPtr authKeyRepo = Fit::make_shared<DefaultAuthKeyRepo>();
    return authKeyRepo;
}
}