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
#include <genericable/com_huawei_fit_secure_access_sign/1.0.0/cplusplus/sign.hpp>
#include <include/secure_access.h>

namespace {
using namespace Fit;
int32_t Sign(ContextObj ctx, const Fit::string* key, const Fit::string* timestamp, Fit::string** ret)
{
    *ret = Fit::Context::NewObj<Fit::string>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    int32_t result = SecureAccess::Instance().Sign(*key, *timestamp, **ret);
    if (result != FIT_OK) {
        FIT_LOG_ERROR("Sign failed.");
        return FIT_ERR_FAIL;
    }
    return result;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(Sign)
        .SetGenericId(::fit::secure::access::Sign::GENERIC_ID)
        .SetFitableId("sign_for_cpp");
}
} // LCOV_EXCL_LINE