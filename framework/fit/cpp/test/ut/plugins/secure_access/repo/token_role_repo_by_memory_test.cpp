/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <secure_access/include/repo/token_role_repo_by_memory.h>
#include <fit/fit_code.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class TokenRoleRepoByMemoryTest : public ::testing::Test {
public:
    void SetUp() override
    {
        tokenRoleRepoByMemory_ = make_shared<TokenRoleRepoByMemory>();
        role_ = "provider";
        accessTokenRole_ = AuthTokenRole("accessToken", ACCESS_TOKEN_TYPE, DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS,
            600 + DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS, role_);
        accessTokenRole2_ = AuthTokenRole("accessToken", FRESH_TOKEN_TYPE, DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS,
            700 + DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS, role_);
    }

    void TearDown() override
    {
    }
public:
    Fit::shared_ptr<TokenRoleRepoByMemory> tokenRoleRepoByMemory_ {};
    string role_ {};
    AuthTokenRole accessTokenRole_ {};
    AuthTokenRole accessTokenRole2_ {};
};

TEST_F(TokenRoleRepoByMemoryTest, should_return_token_roles_when_save_and_query_given_param)
{
    // given
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};

    // when
    int32_t saveRet = tokenRoleRepoByMemory_->Save(authTokenRoles);
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoByMemory_->Query({accessTokenRole_.token});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
}

TEST_F(TokenRoleRepoByMemoryTest, should_return_token_roles_when_save_and_query_all_given_param)
{
    // given
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};

    // when
    int32_t saveRet = tokenRoleRepoByMemory_->Save(authTokenRoles);
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoByMemory_->QueryAll();
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
}

TEST_F(TokenRoleRepoByMemoryTest, should_return_empty_when_save_remove_and_query_given_param)
{
    // given
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};

    // when
    int32_t saveRet = tokenRoleRepoByMemory_->Save(authTokenRoles);
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoByMemory_->Query({accessTokenRole_.token});
    int32_t removeRet = tokenRoleRepoByMemory_->Remove({accessTokenRole_.token});
    vector<AuthTokenRole> actualAuthTokenRolesAfterRemove = tokenRoleRepoByMemory_->Query({accessTokenRole_.token});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
    EXPECT_EQ(removeRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRolesAfterRemove.empty(), true);
}

TEST_F(TokenRoleRepoByMemoryTest, should_return_error_when_save_and_update_and_query_given_param)
{
    // given
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};
    vector<AuthTokenRole> authTokenRoles2 {accessTokenRole2_};

    // when
    int32_t saveRet = tokenRoleRepoByMemory_->Save(authTokenRoles);
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoByMemory_->Query({accessTokenRole_.token});
    int32_t saveRet2 = tokenRoleRepoByMemory_->Save(authTokenRoles2);
    vector<AuthTokenRole> actualAuthTokenRolesAfterUpdate = tokenRoleRepoByMemory_->Query({accessTokenRole_.token});

    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
    EXPECT_EQ(saveRet2, FIT_ERR_FAIL);
    EXPECT_EQ(actualAuthTokenRolesAfterUpdate.front(), accessTokenRole_);
}