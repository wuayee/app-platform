/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供 secure access client
 * Author       : w00561424
 * Date:        : 2024/07/27
 */
#include <include/client/secure_access_client.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <fit/internal/secure_access/auth_token_role.h>
#include <include/secure_access.h>
#include <include/secure_access_config.h>
#include <fit/internal/registry/repository/util_by_repo.h>
namespace Fit {
int32_t SecureAccessClient::GetToken(Fit::string& token)
{
    FIT_LOG_DEBUG("Get token.");
    {
        Fit::unique_lock<Fit::mutex> lock(mutex_);
        if (!tokenInfo_.accessToken.empty() && !tokenInfo_.refreshToken.empty()) {
            token = tokenInfo_.accessToken;
            return FIT_OK;
        }
    }
    int32_t ret = ApplyToken();
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Apply token failed.");
        return ret;
    }
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    token = tokenInfo_.accessToken;
    return FIT_OK;
}

int32_t SecureAccessClient::UpdateToken(Fit::string& token)
{
    FIT_LOG_DEBUG("Update token.");
    Fit::string refreshToken;
    {
        Fit::unique_lock<Fit::mutex> lock(mutex_);
        refreshToken = tokenInfo_.refreshToken;
    }
    int32_t ret = FIT_OK;
    if (!refreshToken.empty()) {
        ret = RefreshToken(refreshToken);
    }

    // 刷新token失败，需要重新申请token
    if (ret == FIT_ERR_AUTHENTICATION_INVALID_FRESH_TOKEN || refreshToken.empty()) {
        FIT_LOG_WARN("Refresh token failed, error code is %d.", ret);
        ret = ApplyToken();
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Apply token failed %d.", ret);
            return ret;
        }
    }
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    token = tokenInfo_.accessToken;
    return FIT_OK;
}

int32_t SecureAccessClient::RefreshToken(const Fit::string& refreshToken)
{
    FIT_LOG_DEBUG("Refresh token.");
    vector<AuthTokenRole> authTokenRoles {};
    int32_t ret = SecureAccess::Instance().RefreshAccessToken(refreshToken, authTokenRoles);
    if (ret != FIT_OK) {
        FIT_LOG_INFO("Refresh token failed.");
        return ret;
    }

    Fit::unique_lock<Fit::mutex> lock(mutex_);
    AuthTokenRole tokenRole = authTokenRoles.front();
    if (tokenRole.type == Fit::string(ACCESS_TOKEN_TYPE)) {
        tokenInfo_.accessToken = tokenRole.token;
        tokenInfo_.timeout = tokenRole.timeout;
    }
    return FIT_OK;
}

int32_t SecureAccessClient::ApplyToken()
{
    FIT_LOG_DEBUG("Apply token.");
    Fit::string accessKey = SecureAccessConfig::Instance().AccessKey();
    uint64_t timestampInt;
    int32_t result = UtilByRepo::Instance().GetCurrentTimeMs(timestampInt);
    if (result != FIT_OK) {
        FIT_LOG_ERROR("Get current time failed.");
        return FIT_ERR_FAIL;
    }
    Fit::string timestamp = Fit::to_string(timestampInt);
    Fit::string signature;
    result = SecureAccess::Instance().Sign(accessKey, timestamp, signature);
    if (result != FIT_OK) {
        FIT_LOG_ERROR("Sign failed.");
        return FIT_ERR_FAIL;
    }

    vector<AuthTokenRole> authTokenRoles =
        SecureAccess::Instance().GetTokenRole(accessKey, timestamp, signature);
    if (authTokenRoles.empty()) {
        FIT_LOG_ERROR("Auth token is empty.");
        return FIT_ERR_FAIL;
    }

    Fit::unique_lock<Fit::mutex> lock(mutex_);
    for (const AuthTokenRole& tokenRole : authTokenRoles) {
        if (tokenRole.type == Fit::string(ACCESS_TOKEN_TYPE)) {
            tokenInfo_.accessToken = tokenRole.token;
            tokenInfo_.timeout = tokenRole.timeout;
        } else if (tokenRole.type == Fit::string(FRESH_TOKEN_TYPE)) {
            tokenInfo_.refreshToken = tokenRole.token;
        }
    }
    if (tokenInfo_.refreshToken.empty()) {
        tokenInfo_.accessToken.clear();
    }
    return FIT_OK;
}
}