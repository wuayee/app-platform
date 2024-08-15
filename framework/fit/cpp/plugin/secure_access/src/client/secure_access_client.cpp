/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供 secure access client
 * Author       : w00561424
 * Date:        : 2024/07/27
 */
#include <include/client/secure_access_client.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <include/secure_access.h>
#include <include/secure_access_config.h>
#include <fit/internal/registry/repository/util_by_repo.h>
namespace Fit {
int32_t SecureAccessClient::GetToken(Fit::string& token)
{
    FIT_LOG_DEBUG("Get token.");
    {
        Fit::unique_lock<Fit::mutex> lock(mutex_);
        if (!accessToken_.token.empty() && accessToken_.status == TOKEN_STATUS_NORMAL
            && !refreshToken_.token.empty() && refreshToken_.status == TOKEN_STATUS_NORMAL) {
            token = accessToken_.token;
            return FIT_OK;
        }
    }
    int32_t ret = ApplyToken();
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Apply token failed.");
        return ret;
    }
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    token = accessToken_.token;
    return FIT_OK;
}

int32_t SecureAccessClient::UpdateToken(Fit::string& token)
{
    FIT_LOG_DEBUG("Update token.");
    ::fit::secure::access::TokenInfo refreshTokenInfo;
    {
        Fit::unique_lock<Fit::mutex> lock(mutex_);
        refreshTokenInfo = refreshToken_;
    }
    int32_t ret = FIT_OK;
    if (!refreshTokenInfo.token.empty() && refreshTokenInfo.status == TOKEN_STATUS_NORMAL) {
        ret = RefreshToken(refreshTokenInfo.token);
    }

    // 刷新token失败，需要重新申请token
    if (ret == FIT_ERR_AUTHENTICATION_INVALID_FRESH_TOKEN || refreshTokenInfo.token.empty()
        || refreshTokenInfo.status != TOKEN_STATUS_NORMAL) {
        FIT_LOG_WARN("Refresh token failed, error code is %d.", ret);
        ret = ApplyToken();
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Apply token failed %d.", ret);
            return ret;
        }
    }
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    token = accessToken_.token;
    return FIT_OK;
}

void SecureAccessClient::UpdateTokenInfo(const vector<AuthTokenRole>& authTokenRoles)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    for (const AuthTokenRole& tokenRole : authTokenRoles) {
        if (tokenRole.type == Fit::string(ACCESS_TOKEN_TYPE)) {
            accessToken_.token = tokenRole.token;
            accessToken_.status = TOKEN_STATUS_NORMAL;
        } else if (tokenRole.type == Fit::string(FRESH_TOKEN_TYPE)) {
            refreshToken_.token = tokenRole.token;
            refreshToken_.status = TOKEN_STATUS_NORMAL;
        }
    }
    if (refreshToken_.token.empty() || refreshToken_.status != TOKEN_STATUS_NORMAL) {
        accessToken_.token.clear();
        accessToken_.status = TOKEN_STATUS_INVALID;
        refreshToken_.token.clear();
        refreshToken_.status = TOKEN_STATUS_INVALID;
    }
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
    UpdateTokenInfo(authTokenRoles);
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
    UpdateTokenInfo(authTokenRoles);
    return FIT_OK;
}
}