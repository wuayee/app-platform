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
#include <genericable/com_huawei_fit_secure_access_is_authorized/1.0.0/cplusplus/isAuthorized.hpp>
#include <include/secure_access.h>
#include <include/domain/permission.h>
namespace {
using namespace Fit;
int32_t IsAuthorized(ContextObj ctx, const Fit::string* token, const fit::secure::access::Permission* permission)
{
    Permission permissionIn(*(permission->fitable));
    return SecureAccess::Instance().IsAuthorized(*token, permissionIn);
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(IsAuthorized)
        .SetGenericId(::fit::secure::access::IsAuthorized::GENERIC_ID)
        .SetFitableId("is_authorized");
}
} // LCOV_EXCL_LINE