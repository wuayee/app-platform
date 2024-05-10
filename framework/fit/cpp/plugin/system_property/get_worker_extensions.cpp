/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide Worker extensions.
 * Author       : s00558940
 * Date:        : 2023/11/03
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_system_shared_get_worker_extensions/1.0.0/cplusplus/get_worker_extensions.hpp>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include "core/fit_system_property_service.h"
namespace {
FitCode GetWorkerExtensions(ContextObj ctx, Fit::map<Fit::string, Fit::string> **result)
{
    *result = Fit::Context::NewObj<Fit::map<Fit::string, Fit::string>>(ctx);
    if (*result == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    **result = Fit::SDK::System::FitSystemPropertyService::GetService()->GetWorkerExtensions();
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::GetWorkerExtensions)
        .SetGenericId(fit::hakuna::system::shared::getWorkerExtensions::GENERIC_ID)
        .SetFitableId("36e3d397c5a8433cb1a63e5f397dd00d");
}