/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#include <genericable/com_huawei_fit_sdk_system_set_system_property/1.0.0/cplusplus/setSystemProperty.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "core/fit_system_property_service.h"

namespace Fit {
namespace SDK {
namespace System {
int32_t SetSystemProperty(ContextObj ctx, const Fit::string* key, const Fit::string* value, const bool* readyOnly,
    bool** ret)
{
    *ret = Fit::Context::NewObj<bool>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    **ret = FitSystemPropertyService::GetService()->Put(*key, *value, *readyOnly);
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(SetSystemProperty)
        .SetGenericId(::fit::sdk::system::setSystemProperty::GENERIC_ID)
        .SetFitableId("fit_sdk_system_property_set");
}
}
}
} // LCOV_EXCL_LINE