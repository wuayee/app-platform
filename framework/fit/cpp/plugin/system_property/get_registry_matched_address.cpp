/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#include <genericable/com_huawei_fit_sdk_system_get_registry_matched_address/1.0.0/cplusplus/getRegistryMatchedAddress.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "core/fit_system_property_service.h"

namespace Fit {
namespace SDK {
namespace System {
int32_t GetRegistryMatchedAddress(ContextObj ctx, fit::registry::Address** address)
{
    if (ctx == nullptr) {
        FIT_LOG_ERROR("%s", "Param is nullptr.");
        return FIT_ERR_FAIL;
    }

    *address = Fit::Context::NewObj<fit::registry::Address>(ctx);
    if (*address == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    **address = FitSystemPropertyService::GetService()->GetRegistryMatchedAddress();
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(GetRegistryMatchedAddress)
        .SetGenericId(::fit::sdk::system::getRegistryMatchedAddress::GENERIC_ID)
        .SetFitableId("fit_sdk_system_get_registry_matched_address");
}
}
}
} // LCOV_EXCL_LINE