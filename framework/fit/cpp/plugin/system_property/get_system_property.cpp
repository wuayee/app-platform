/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#include <fit/fit_log.h>
#include <fit/external/util/registration.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_sdk_system_get_system_property/1.0.0/cplusplus/getSystemProperty.hpp>
#include "core/fit_system_property_service.h"

namespace Fit {
namespace SDK {
namespace System {
int32_t GetSystemProperty(ContextObj ctx, const Fit::string* key, Fit::string** ret)
{
    *ret = Fit::Context::NewObj<Fit::string>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    **ret = FitSystemPropertyService::GetService()->Get(*key);
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(GetSystemProperty)
        .SetGenericId(::fit::sdk::system::getSystemProperty::GENERIC_ID)
        .SetFitableId("fit_sdk_system_property_get");
}
}
}
} // LCOV_EXCL_LINE