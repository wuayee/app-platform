/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供 Key 存储的接口定义。
 * Author       : 王攀博 w00561424
 * Date:        : 2024/05/16
 */
#ifndef AUTH_KEY_REPO_H
#define AUTH_KEY_REPO_H
#include <include/domain/auth_key.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/vector.hpp>
namespace Fit {
class AuthKeyRepo {
public:
    virtual ~AuthKeyRepo() = default;
    virtual int32_t Save(const vector<AuthKey>& authKeys) = 0;
    virtual int32_t Remove(const vector<string>& aks) = 0;
    virtual vector<AuthKey> Query(const vector<string>& aks) = 0;
};

using AuthKeyRepoPtr = Fit::shared_ptr<AuthKeyRepo>;

class AuthKeyRepoFactory {
public:
    static AuthKeyRepoPtr Create();
};
}
#endif