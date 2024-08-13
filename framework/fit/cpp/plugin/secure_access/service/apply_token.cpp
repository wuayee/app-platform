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
#include <genericable/com_huawei_fit_secure_access_apply_token/1.0.0/cplusplus/applyToken.hpp>
#include <include/secure_access.h>
#include <fit/stl/vector.hpp>
namespace {
using namespace Fit;
int32_t ApplyToken(ContextObj ctx, const Fit::string* accessKey, const Fit::string* timestamp,
    const Fit::string* signature, fit::secure::access::TokenInfo** ret)
{
    *ret = Fit::Context::NewObj<fit::secure::access::TokenInfo>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("New result failed.");
        return FIT_ERR_FAIL;
    }

    vector<AuthTokenRole> authTokenRoles = SecureAccess::Instance().GetTokenRole(*accessKey, *timestamp, *signature);
    if (authTokenRoles.empty()) {
        FIT_LOG_ERROR("Auth token is empty.");
        return FIT_ERR_FAIL;
    }

    for (const AuthTokenRole& tokenRole : authTokenRoles) {
        if (tokenRole.type == Fit::string(ACCESS_TOKEN_TYPE)) {
            (*ret)->accessToken = tokenRole.token;
            (*ret)->timeout = tokenRole.timeout;
        } else if (tokenRole.type == Fit::string(FRESH_TOKEN_TYPE)) {
            (*ret)->refreshToken = tokenRole.token;
        }
    }
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(ApplyToken)
        .SetGenericId(::fit::secure::access::ApplyToken::GENERIC_ID)
        .SetFitableId("apply_token");
}
} // LCOV_EXCL_LINE