/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <genericable/com_huawei_fit_secure_access_apply_token/1.0.0/cplusplus/applyToken.hpp>
#include <genericable/com_huawei_fit_secure_access_is_authorized/1.0.0/cplusplus/isAuthorized.hpp>
#include <genericable/com_huawei_fit_secure_access_refresh_token/1.0.0/cplusplus/refreshToken.hpp>
#include <genericable/com_huawei_fit_secure_access_sign/1.0.0/cplusplus/sign.hpp>
#include <genericable/com_huawei_fit_secure_access_get_token/1.0.0/cplusplus/getToken.hpp>
#include <fit/stl/memory.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class SecureAccessInterfaceTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
public:
};

TEST_F(SecureAccessInterfaceTest, should_return_true_when_apply_token_and_check_auth_given_param)
{
    // given
    fit::secure::access::ApplyToken applyTokenExec;
    fit::secure::access::IsAuthorized isAuthorizedExec;
    fit::secure::access::RefreshToken refreshTokenExec;
    fit::secure::access::Sign signExec;

    Fit::string accessKey = "test_ak1";
    Fit::string timestamp = "test_timestamp";
    Fit::string signature = "45387cf6b1a6e70e10f62895c02079b20c8a182b9a9752f57a6d11131a3a6461";

    ::fit::hakuna::kernel::shared::Fitable fitable;
    fitable.genericableId = "provider_gid";
    fitable.genericableVersion = "1.0.0";
    fitable.fitableId = "provider_fid";
    fitable.fitableVersion = "1.0.0";
    fit::secure::access::Permission permission;
    permission.fitable = &fitable;

    // when
    Fit::string* signatureOut = new Fit::string();
    auto signRet = signExec(&accessKey, &timestamp, &signatureOut);

    fit::secure::access::TokenInfo* tokenInfoRet = new fit::secure::access::TokenInfo();
    auto applyTokenRet = applyTokenExec(&accessKey, &timestamp, signatureOut, &tokenInfoRet);
    auto isAuthorizedRet = isAuthorizedExec(&tokenInfoRet->accessToken, &permission);

    fit::secure::access::TokenInfo* refreshTokenInfo = new fit::secure::access::TokenInfo();
    auto refreshTokenRet = refreshTokenExec(&tokenInfoRet->refreshToken, &refreshTokenInfo);

    // then
    EXPECT_EQ(signRet, FIT_OK);
    EXPECT_EQ(*signatureOut, signature);
    EXPECT_EQ(applyTokenRet, FIT_OK);
    EXPECT_EQ(tokenInfoRet->accessToken.empty(), false);
    EXPECT_EQ(tokenInfoRet->refreshToken.empty(), false);
    EXPECT_EQ(tokenInfoRet->timeout, 3600);
    EXPECT_EQ(isAuthorizedRet, FIT_OK);

    EXPECT_EQ(refreshTokenRet, FIT_OK);
    EXPECT_EQ(refreshTokenInfo->accessToken.empty(), false);
    EXPECT_NE(refreshTokenInfo->accessToken, refreshTokenInfo->refreshToken);
    EXPECT_EQ(refreshTokenInfo->refreshToken, tokenInfoRet->refreshToken);
    EXPECT_EQ(refreshTokenInfo->timeout, 3600);
}

TEST_F(SecureAccessInterfaceTest, should_return_error_when_get_token_given_param)
{
    // given
    fit::secure::access::GetToken getToken;
    Fit::string* token;
    bool isForceUpdate = false;

    // when
    auto getTokenRet = getToken(&isForceUpdate, &token);

    // then
    EXPECT_EQ(getTokenRet, FIT_ERR_FAIL);
}