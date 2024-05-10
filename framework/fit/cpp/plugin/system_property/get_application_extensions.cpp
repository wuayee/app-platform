/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application extensions.
 * Author       : w00561424
 * Date:        : 2023/10/30
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_system_shared_get_application_extensions/1.0.0/cplusplus/get_application_extensions.hpp>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include "core/fit_system_property_service.h"
namespace {
FitCode GetApplicationExtensions(ContextObj ctx, Fit::map<Fit::string, Fit::string> **result)
{
    if (ctx == nullptr) {
        FIT_LOG_ERROR("%s", "Param is nullptr.");
        return FIT_ERR_FAIL;
    }

    *result = Fit::Context::NewObj<Fit::map<Fit::string, Fit::string>>(ctx);
    if (*result == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    **result = Fit::SDK::System::FitSystemPropertyService::GetService()->GetApplicationExtensions();
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::GetApplicationExtensions)
        .SetGenericId(fit::hakuna::system::shared::getApplicationExtensions::GENERIC_ID)
        .SetFitableId("5f2e7f5e928d45068bcff70e188e9694");
}