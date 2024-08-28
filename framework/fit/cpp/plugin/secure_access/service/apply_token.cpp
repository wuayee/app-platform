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
    const Fit::string* signature, vector<fit::secure::access::TokenInfo>** tokenInfos)
{
    *tokenInfos = Fit::Context::NewObj<vector<fit::secure::access::TokenInfo>>(ctx);
    if (*tokenInfos == nullptr) {
        FIT_LOG_ERROR("New result failed.");
        return FIT_ERR_FAIL;
    }

    vector<AuthTokenRole> authTokenRoles = SecureAccess::Instance().GetTokenRole(*accessKey, *timestamp, *signature);
    if (authTokenRoles.empty()) {
        FIT_LOG_ERROR("Auth token is empty.");
        return FIT_ERR_FAIL;
    }

    for (const auto& tokenRole : authTokenRoles) {
        fit::secure::access::TokenInfo tokenInfo {};
        tokenInfo.token = tokenRole.token;
        tokenInfo.timeout = tokenRole.timeout;
        tokenInfo.type = tokenRole.type;
        tokenInfo.status = TOKEN_STATUS_NORMAL;
        (**tokenInfos).emplace_back(tokenInfo);
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