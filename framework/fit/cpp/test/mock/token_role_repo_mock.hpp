/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef TOKEN_ROLE_REPO_MOCK_HPP
#define TOKEN_ROLE_REPO_MOCK_HPP
#include <gmock/gmock.h>
#include <fit/internal/secure_access/token_role_repo.h>
namespace Fit {
class TokenRoleRepoMock : public TokenRoleRepo {
public:
    MOCK_METHOD1(Save, int32_t(const vector<AuthTokenRole>& authTokenRoles));
    MOCK_METHOD1(Remove, int32_t(const vector<string>& tokens));
    MOCK_METHOD1(Query, vector<AuthTokenRole>(const vector<string>& tokens));
    MOCK_METHOD0(QueryAll, vector<AuthTokenRole>());
};
}
#endif