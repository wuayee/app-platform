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
int32_t RefreshToken(ContextObj ctx, const Fit::string* refreshToken, fit::secure::access::TokenInfo** ret)
{
    if (refreshToken == nullptr) {
        FIT_LOG_ERROR("Refresh token is null.");
        return FIT_ERR_FAIL;
    }
    *ret = Fit::Context::NewObj<fit::secure::access::TokenInfo>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    vector<AuthTokenRole> authTokenRoles {};
    int32_t result = SecureAccess::Instance().RefreshAccessToken(*refreshToken, authTokenRoles);
    if (result != FIT_OK) {
        FIT_LOG_ERROR("Refresh token is invalid.");
        return FIT_ERR_AUTHENTICATION_INVALID_FRESH_TOKEN;
    }
    (*ret)->refreshToken = *refreshToken;
    AuthTokenRole tokenRole = authTokenRoles.front();
    if (tokenRole.type == Fit::string(ACCESS_TOKEN_TYPE)) {
        (*ret)->accessToken = tokenRole.token;
        (*ret)->timeout = tokenRole.timeout;
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