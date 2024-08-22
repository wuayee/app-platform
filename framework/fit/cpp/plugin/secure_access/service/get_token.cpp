/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024//
 */
#include <fit/fit_log.h>
#include <fit/external/util/registration.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_secure_access_get_token/1.0.0/cplusplus/getToken.hpp>
#include <fit/internal/secure_access/auth_token_role.h>
#include <include/secure_access.h>
#include <include/secure_access_config.h>
#include <include/client/secure_access_client.h>
#include <fit/internal/registry/repository/util_by_repo.h>
#include <fit/stl/vector.hpp>
namespace {
using namespace Fit;
int32_t GetToken(ContextObj ctx, const bool* isForceUpdate, Fit::string** token)
{
    *token = Fit::Context::NewObj<Fit::string>(ctx);
    if (*token == nullptr) {
        FIT_LOG_ERROR("New result failed.");
        return FIT_ERR_FAIL;
    }

    static SecureAccessClient secureAccessClient {};
    Fit::string tokenTemp;
    int32_t ret = FIT_OK;
    if (*isForceUpdate) {
        // 1：当 accessToken 失效时，刷新 token；【可以刷新accessToken，更新 refreshToken】
        // 1.1：当 refreshToken 不为空，基于 refreshToken 刷新 accessToken
        // 1.2：当 refreshToken 为空 或者 refreshToken 无效，申请新的 refreshToken 和 accessToken
        ret = secureAccessClient.UpdateToken(tokenTemp);
    } else {
        // 2：初次调用时申请 accessToken
        // 2.1：当 refreshToken 和 accessToken 都不为空，返回accessToken
        // 2.2：当 refreshToken 或 accessToken 为空，申请新的 refreshToken 和access token
        ret = secureAccessClient.GetToken(tokenTemp);
    }
    **token = tokenTemp;
    return ret;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(GetToken)
        .SetGenericId(::fit::secure::access::GetToken::GENERIC_ID)
        .SetFitableId("get_token");
}
} // LCOV_EXCL_LINE