/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef DEFAULT_AUTH_KEY_REPO_H
#define DEFAULT_AUTH_KEY_REPO_H
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/mutex.hpp>
#include "auth_key_repo.h"
namespace Fit {
class DefaultAuthKeyRepo : public AuthKeyRepo {
public:
    int32_t Save(const vector<AuthKey>& authKeys) override;
    int32_t Remove(const vector<string>& accessKeys) override;
    vector<AuthKey> Query(const vector<string>& accessKeys) override;
private:
    Fit::mutex mutex_ {};
    Fit::unordered_map<Fit::string, AuthKey> authKeys_ {};
};
}
#endif
