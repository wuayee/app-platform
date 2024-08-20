/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#include <mock/signature_mock.hpp>
#include <mock/auth_key_repo_mock.hpp>
#include <mock/token_role_repo_mock.hpp>
#include <mock/role_permissions_repo_mock.hpp>
#include <mock/time_util_by_repo_mock.hpp>
#include <secure_access/include/secure_access.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
constexpr const uint64_t ACCESS_TOKEN_CURRENT_TIME_S = 600;
constexpr const uint64_t FRESH_TOKEN_CURRENT_TIME_S = 700;
constexpr const uint64_t TOKEN_ROLE_SIZE = 2;
class SecureAccessTest : public ::testing::Test {
public:
    void SetUp() override
    {
        signature_ = make_shared<SignatureMock>();
        tokenRoleRepo_ = make_shared<TokenRoleRepoMock>();
        rolePermissionsRepo_ = make_shared<RolePermissionsRepoMock>();
        authKeyRepo_ = make_shared<AuthKeyRepoMock>();
        timeUtilByRepoMock_ = make_shared<TimeUtilByRepoMock>();
        secureAccess = new SecureAccess(signature_, tokenRoleRepo_, rolePermissionsRepo_, authKeyRepo_,
            timeUtilByRepoMock_.get());

        ak_ = "test_ak";
        sk_ = "test_sk";
        role_ = "provider";
        timestamp_ = "test_timestamp";
        signatureStr_ = "test_signature";
        authKey_.ak = ak_;
        authKey_.sk = sk_;
        authKey_.role = role_;

        accessTokenType_ = ACCESS_TOKEN_TYPE;
        freshTokenType_ = FRESH_TOKEN_TYPE;
        accessTokenTimeout_ = DEFAULT_ACCESS_TOKEN_EXPIRED_TIME_SECONDS;
        freshTokenTimeout_ = DEFAULT_FRESH_TOKEN_EXPIRED_TIME_SECONDS;

        accessTokenRole_ = AuthTokenRole("accessToken", ACCESS_TOKEN_TYPE, accessTokenTimeout_,
            ACCESS_TOKEN_CURRENT_TIME_S + accessTokenTimeout_ * SECOND_TO_MILLION_SECOND, role_);
        freshTokenRole_ = AuthTokenRole("freshToken", FRESH_TOKEN_TYPE, freshTokenTimeout_,
            ACCESS_TOKEN_CURRENT_TIME_S + freshTokenTimeout_ * SECOND_TO_MILLION_SECOND, role_);

        fit::hakuna::kernel::shared::Fitable fitableIn;
        fitableIn.genericableId = "test_gid";
        fitableIn.genericableVersion = "test_gversion";
        fitableIn.fitableId = "test_fid";
        fitableIn.fitableVersion = "test_fversion";
        permission_ = Permission(fitableIn);

        rolePermissions_ = RolePermissions(role_, {permission_});
    }

    void TearDown() override
    {
        delete secureAccess;
        secureAccess = nullptr;
    }
public:
    SecureAccess* secureAccess;
    Fit::shared_ptr<SignatureMock> signature_ {};
    Fit::shared_ptr<TokenRoleRepoMock> tokenRoleRepo_ {};
    Fit::shared_ptr<RolePermissionsRepoMock> rolePermissionsRepo_ {};
    Fit::shared_ptr<AuthKeyRepoMock> authKeyRepo_ {};
    Fit::shared_ptr<TimeUtilByRepoMock> timeUtilByRepoMock_ {};
    Fit::string ak_ {};
    Fit::string sk_ {};
    string role_ {};
    string timestamp_ {};
    string signatureStr_ {};
    AuthKey authKey_ {};
    string accessTokenType_;
    string freshTokenType_;
    uint64_t accessTokenTimeout_;
    uint64_t freshTokenTimeout_;
    string token_;
    AuthTokenRole accessTokenRole_ {};
    AuthTokenRole freshTokenRole_ {};
    Permission permission_ {};
    RolePermissions rolePermissions_ {};
};

TEST_F(SecureAccessTest, should_return_signature_when_sign_given_param)
{
    // given
    int32_t expectedRet = FIT_OK;
    string expectedSignatureReturn = signatureStr_;
    EXPECT_CALL(*signature_, Sign(testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(expectedSignatureReturn));

    // when
    string signatureStr;
    int32_t actualRet = secureAccess->Sign(ak_, sk_, signatureStr);

    // then
    EXPECT_EQ(actualRet, expectedRet);
    EXPECT_EQ(signatureStr, expectedSignatureReturn);
}

TEST_F(SecureAccessTest, should_return_empty_when_get_token_role_given_verify_failed)
{
    // given
    EXPECT_CALL(*signature_, Verify(testing::_, testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(false));
    // when
    vector<AuthTokenRole> authTokenRoles = secureAccess->GetTokenRole(ak_, timestamp_, signatureStr_);

    // then
    EXPECT_EQ(authTokenRoles.empty(), true);
}

TEST_F(SecureAccessTest, should_return_empty_when_get_token_role_given_query_auth_key_repo)
{
    // given
    EXPECT_CALL(*signature_, Verify(testing::_, testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(true));
    vector<AuthKey> authKeys {};
    EXPECT_CALL(*authKeyRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(authKeys));
    // when
    vector<AuthTokenRole> authTokenRoles = secureAccess->GetTokenRole(ak_, timestamp_, signatureStr_);

    // then
    EXPECT_EQ(authTokenRoles.empty(), true);
}

TEST_F(SecureAccessTest, should_return_auth_token_role_when_get_token_role_given_query_auth_key_repo)
{
    // given
    EXPECT_CALL(*signature_, Verify(testing::_, testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(true));
    vector<AuthKey> authKeys {authKey_};
    EXPECT_CALL(*authKeyRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(authKeys));
    EXPECT_CALL(*tokenRoleRepo_, Save(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    uint64_t accTokenCurTime = ACCESS_TOKEN_CURRENT_TIME_S;
    uint64_t freshTokenCurTime = FRESH_TOKEN_CURRENT_TIME_S;
    EXPECT_CALL(*timeUtilByRepoMock_, GetCurrentTimeMs(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<0>(freshTokenCurTime), testing::Return(FIT_OK)))
        .WillOnce(testing::DoAll(testing::SetArgReferee<0>(accTokenCurTime), testing::Return(FIT_OK)));

    // when
    vector<AuthTokenRole> authTokenRoles = secureAccess->GetTokenRole(ak_, timestamp_, signatureStr_);

    // then
    EXPECT_EQ(authTokenRoles.empty(), false);
    EXPECT_EQ(authTokenRoles.size(), TOKEN_ROLE_SIZE);
    EXPECT_EQ(authTokenRoles[0].token.empty(), false);
    EXPECT_EQ(authTokenRoles[0].type, freshTokenType_);
    EXPECT_EQ(authTokenRoles[0].timeout, freshTokenTimeout_);
    EXPECT_EQ(authTokenRoles[0].endTime, freshTokenTimeout_ * SECOND_TO_MILLION_SECOND + FRESH_TOKEN_CURRENT_TIME_S);
    EXPECT_EQ(authTokenRoles[1].token.empty(), false);
    EXPECT_EQ(authTokenRoles[1].type, accessTokenType_);
    EXPECT_EQ(authTokenRoles[1].timeout, accessTokenTimeout_);
    EXPECT_EQ(authTokenRoles[1].endTime, accessTokenTimeout_ * SECOND_TO_MILLION_SECOND + ACCESS_TOKEN_CURRENT_TIME_S);
}

TEST_F(SecureAccessTest, should_return_false_when_call_is_authorized_given_mock_query_empty)
{
    // given
    vector<AuthTokenRole> tokenRoles {};
    EXPECT_CALL(*tokenRoleRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    Permission permission {};
    // when
    int32_t isAuthorized = secureAccess->IsAuthorized(token_, permission);

    // then
    EXPECT_EQ(isAuthorized, FIT_ERR_AUTHENTICATION_INVALID_ACCESS_TOKEN);
}

TEST_F(SecureAccessTest, should_return_false_when_call_is_authorized_given_invalid_type)
{
    // given
    vector<AuthTokenRole> tokenRoles {freshTokenRole_};
    EXPECT_CALL(*tokenRoleRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    // when
    int32_t isAuthorized = secureAccess->IsAuthorized(freshTokenRole_.token, permission_);

    // then
    EXPECT_EQ(isAuthorized, FIT_ERR_AUTHENTICATION_INVALID_ACCESS_TOKEN);
}

TEST_F(SecureAccessTest, should_return_false_when_call_is_authorized_given_mock_query_permission_empty)
{
    // given
    vector<AuthTokenRole> tokenRoles {accessTokenRole_};
    EXPECT_CALL(*tokenRoleRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    vector<RolePermissions> rolePermissionsSet {};
    EXPECT_CALL(*rolePermissionsRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(rolePermissionsSet));
    // when
    int32_t isAuthorized = secureAccess->IsAuthorized(freshTokenRole_.token, permission_);

    // then
    EXPECT_EQ(isAuthorized, FIT_ERR_AUTHENTICATION_ROLE_NO_PERMISSION);
}

TEST_F(SecureAccessTest, should_return_false_when_call_is_authorized_given_mock_query_permission)
{
    // given
    vector<AuthTokenRole> tokenRoles {accessTokenRole_};
    EXPECT_CALL(*tokenRoleRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    vector<RolePermissions> rolePermissionsSet {rolePermissions_};
    EXPECT_CALL(*rolePermissionsRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(rolePermissionsSet));
    // when
    int32_t isAuthorized = secureAccess->IsAuthorized(freshTokenRole_.token, permission_);

    // then
    EXPECT_EQ(isAuthorized, FIT_OK);
}

TEST_F(SecureAccessTest, should_return_auth_tokens_when_call_refresh_access_token_given_mock_query_token_role)
{
    // given
    vector<AuthTokenRole> tokenRoles {accessTokenRole_};
    EXPECT_CALL(*tokenRoleRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));
    EXPECT_CALL(*tokenRoleRepo_, Save(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));

    EXPECT_CALL(*timeUtilByRepoMock_, GetCurrentTimeMs(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<0>(ACCESS_TOKEN_CURRENT_TIME_S), testing::Return(FIT_OK)));

    // when
    vector<AuthTokenRole> authTokenRoles {};
    int32_t ret = secureAccess->RefreshAccessToken(freshTokenRole_.token, authTokenRoles);

    // then
    EXPECT_EQ(authTokenRoles.empty(), false);
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(authTokenRoles[0].token.empty(), false);
    EXPECT_EQ(authTokenRoles[0].type, accessTokenType_);
    EXPECT_EQ(authTokenRoles[0].timeout, accessTokenTimeout_);
    EXPECT_EQ(authTokenRoles[0].endTime, accessTokenTimeout_ * SECOND_TO_MILLION_SECOND + ACCESS_TOKEN_CURRENT_TIME_S);
}

TEST_F(SecureAccessTest, should_return_empty_when_call_refresh_access_token_given_mock_query_token_role_empty)
{
    // given
    vector<AuthTokenRole> tokenRoles {};
    EXPECT_CALL(*tokenRoleRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(tokenRoles));

    // when
    vector<AuthTokenRole> authTokenRoles {};
    int32_t ret = secureAccess->RefreshAccessToken(freshTokenRole_.token, authTokenRoles);

    // then
    EXPECT_EQ(authTokenRoles.empty(), true);
    EXPECT_EQ(ret, FIT_ERR_AUTHENTICATION_INVALID_FRESH_TOKEN);
}

TEST_F(SecureAccessTest, should_return_object_when_call_instance_given_empty)
{
    // given
    // when
    SecureAccess secureAccess = SecureAccess::Instance();
    secureAccess.Register(nullptr);
    TokenRoleRepoPtr tokenRoleRepo = secureAccess.TokenRoleRepo();
    UtilByRepo* timeUtilByRepo = secureAccess.TimeUtil();
    AuthKeyRepoPtr authKeyRepo = secureAccess.AuthKeyRepo();
    RolePermissionsRepoPtr rolePermissionsRepo  = secureAccess.RolePermissionsRepo();

    // then
    EXPECT_NE(tokenRoleRepo, nullptr);
    EXPECT_NE(timeUtilByRepo, nullptr);
    EXPECT_NE(authKeyRepo, nullptr);
    EXPECT_NE(rolePermissionsRepo, nullptr);
}