/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供 Key 的定义。
 * Author       : w00561424
 * Date:        : 2024/05/16
 */
#ifndef AUTH_KEY_H
#define AUTH_KEY_H
#include <fit/stl/string.hpp>
namespace Fit {
class AuthKey {
public:
    AuthKey() = default;
    AuthKey(const string& akIn, const string& skIn, const string& roleIn) : ak(akIn), sk(skIn), role(roleIn) {}
    AuthKey(const AuthKey& authKey) : ak(authKey.ak), sk(authKey.sk), role(authKey.role) {}
    AuthKey& operator=(const AuthKey& authKey)
    {
        if (this == &authKey) {
            return *this;
        }
        this->ak = authKey.ak;
        this->sk = authKey.sk;
        this->role = authKey.role;
        return *this;
    }
    bool operator==(const AuthKey& authKey) const
    {
        return (this->ak == authKey.ak) && (this->sk == authKey.sk) && (this->role == authKey.role);
    }
public:
    string ak;
    string sk;
    string role;
};
}
#endif