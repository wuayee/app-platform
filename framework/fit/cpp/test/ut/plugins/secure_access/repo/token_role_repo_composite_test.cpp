/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/29
 */
#include <secure_access/include/repo/token_role_repo_by_memory.h>
#include <secure_access/include/repo/token_role_repo_composite.h>
#include <mock/token_role_repo_mock.hpp>
#include <fit/fit_code.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class TokenRoleRepoCompositeTest : public ::testing::Test {
public:
    void SetUp() override
    {
        tokenRoleRepoByMemory_ = make_shared<TokenRoleRepoByMemory>();
        role_ = "provider";
        accessTokenRole_ = AuthTokenRole("accessToken", ACCESS_TOKEN_TYPE, DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS,
            600 + DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS, role_);
        accessTokenRole2_ = AuthTokenRole("accessToken", FRESH_TOKEN_TYPE, DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS,
            700 + DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS, role_);
        tokenRoleRepoByDb_ = make_shared<TokenRoleRepoMock>();
        tokenRoleRepoComposite_ = make_shared<TokenRoleRepoComposite>(tokenRoleRepoByMemory_, tokenRoleRepoByDb_);
    }

    void TearDown() override
    {
    }
public:
    Fit::shared_ptr<TokenRoleRepoByMemory> tokenRoleRepoByMemory_ {};
    Fit::shared_ptr<TokenRoleRepoMock> tokenRoleRepoByDb_ {};
    Fit::shared_ptr<TokenRoleRepoComposite> tokenRoleRepoComposite_ {};
    string role_ {};
    AuthTokenRole accessTokenRole_ {};
    AuthTokenRole accessTokenRole2_ {};
};

TEST_F(TokenRoleRepoCompositeTest, should_return_token_roles_when_save_remove_and_query_given_param)
{
    // given
    EXPECT_CALL(*tokenRoleRepoByDb_, Save(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));

    vector<AuthTokenRole> tokenRoles {};
    EXPECT_CALL(*tokenRoleRepoByDb_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    EXPECT_CALL(*tokenRoleRepoByDb_, Remove(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};

    // when
    int32_t saveRet = tokenRoleRepoComposite_->Save(authTokenRoles);
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoComposite_->Query({accessTokenRole_.token});
    int32_t removeRet = tokenRoleRepoComposite_->Remove({accessTokenRole_.token});
    vector<AuthTokenRole> actualAuthTokenRolesAfterRemove = tokenRoleRepoComposite_->Query({accessTokenRole_.token});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
    EXPECT_EQ(removeRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRolesAfterRemove.empty(), true);
}

TEST_F(TokenRoleRepoCompositeTest, should_return_token_roles_when_save_and_query_all_given_param)
{
    // given
    EXPECT_CALL(*tokenRoleRepoByDb_, Save(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    vector<AuthTokenRole> tokenRoles {};
    EXPECT_CALL(*tokenRoleRepoByDb_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    EXPECT_CALL(*tokenRoleRepoByDb_, Remove(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};

    // when
    int32_t saveRet = tokenRoleRepoComposite_->Save(authTokenRoles);
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoComposite_->QueryAll();
    int32_t removeRet = tokenRoleRepoComposite_->Remove({accessTokenRole_.token});
    vector<AuthTokenRole> actualAuthTokenRolesAfterRemove = tokenRoleRepoComposite_->Query({accessTokenRole_.token});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
    EXPECT_EQ(removeRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRolesAfterRemove.empty(), true);
}


TEST_F(TokenRoleRepoCompositeTest, should_return_token_roles_when_query_all_memory_not_found_given_param)
{
    // given
    vector<AuthTokenRole> tokenRoles {accessTokenRole_};
    EXPECT_CALL(*tokenRoleRepoByDb_, QueryAll())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));
    vector<AuthTokenRole> tokenRoles2 {};
    EXPECT_CALL(*tokenRoleRepoByDb_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles2));

    EXPECT_CALL(*tokenRoleRepoByDb_, Remove(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    vector<AuthTokenRole> authTokenRoles {accessTokenRole_};

    // when
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoComposite_->QueryAll();
    int32_t removeRet = tokenRoleRepoComposite_->Remove({accessTokenRole_.token});
    vector<AuthTokenRole> actualAuthTokenRolesAfterRemove = tokenRoleRepoComposite_->Query({accessTokenRole_.token});
    // then
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
    EXPECT_EQ(removeRet, FIT_OK);
    EXPECT_EQ(actualAuthTokenRolesAfterRemove.empty(), true);
}


TEST_F(TokenRoleRepoCompositeTest, should_return_token_roles_when_query_memory_not_exist_and_db_exist_given_param)
{
    // given
    vector<AuthTokenRole> tokenRoles {accessTokenRole_};
    EXPECT_CALL(*tokenRoleRepoByDb_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    // when
    vector<AuthTokenRole> actualAuthTokenRoles = tokenRoleRepoComposite_->Query({accessTokenRole_.token});

    // then
    EXPECT_EQ(actualAuthTokenRoles.front(), accessTokenRole_);
}