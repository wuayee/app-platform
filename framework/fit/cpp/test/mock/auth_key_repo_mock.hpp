/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef AUTH_KEY_REPO_MOCK_HPP
#define AUTH_KEY_REPO_MOCK_HPP
#include <gmock/gmock.h>
#include <secure_access/include/repo/auth_key_repo.h>
namespace Fit {
class AuthKeyRepoMock : public AuthKeyRepo {
public:
    MOCK_METHOD1(Save, int32_t(const vector<AuthKey>& authKeys));
    MOCK_METHOD1(Remove, int32_t(const vector<string>& aks));
    MOCK_METHOD1(Query, vector<AuthKey>(const vector<string>& aks));
};
}
#endif