/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/17
 */
#include <fit/fit_log.h>
#include <fit/external/util/registration.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_secure_access_refresh_token/1.0.0/cplusplus/refreshToken.hpp>
#include <include/secure_access.h>
#include <fit/stl/vector.hpp>
namespace {
using namespace Fit;
void InsertTokenInfo(vector<fit::secure::access::TokenInfo>& tokenInfos,
    Fit::string token, int64_t timeout, Fit::string type, Fit::string status)
{
    fit::secure::access::TokenInfo tokenInfo {};
    tokenInfo.token = token;
    tokenInfo.timeout = timeout;
    tokenInfo.type = type;
    tokenInfo.status = status;
    tokenInfos.emplace_back(tokenInfo);
}
int32_t RefreshToken(ContextObj ctx, const Fit::string* refreshToken,
    vector<fit::secure::access::TokenInfo>** tokenInfos)
{
    if (refreshToken == nullptr) {
        FIT_LOG_ERROR("Refresh token is null.");
        return FIT_ERR_FAIL;
    }
    *tokenInfos = Fit::Context::NewObj<vector<fit::secure::access::TokenInfo>>(ctx);
    if (*tokenInfos == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    vector<AuthTokenRole> authTokenRoles {};
    int32_t result = SecureAccess::Instance().RefreshAccessToken(*refreshToken, authTokenRoles);
    if (result != FIT_OK) {
        FIT_LOG_ERROR("Refresh token is invalid.");
        InsertTokenInfo(**tokenInfos, *refreshToken, 0, FRESH_TOKEN_TYPE, TOKEN_STATUS_INVALID);
        InsertTokenInfo(**tokenInfos, INVALID_TOKEN, 0, ACCESS_TOKEN_TYPE, TOKEN_STATUS_INVALID);
        return FIT_OK;
    }
    for (const auto& tokenRole : authTokenRoles) {
        InsertTokenInfo(**tokenInfos, tokenRole.token, tokenRole.timeout, tokenRole.type, TOKEN_STATUS_NORMAL);
    }
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(RefreshToken)
        .SetGenericId(::fit::secure::access::RefreshToken::GENERIC_ID)
        .SetFitableId("refresh_token_for_registry_server");
}
} // LCOV_EXCL_LINE