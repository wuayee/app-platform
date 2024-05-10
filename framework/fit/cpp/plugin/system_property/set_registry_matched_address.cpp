/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#include <genericable/com_huawei_fit_sdk_system_set_registry_matched_address/1.0.0/cplusplus/setRegistryMatchedAddress.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "core/fit_system_property_service.h"

namespace Fit {
namespace SDK {
namespace System {
int32_t SetRegistryMatchedAddress(ContextObj ctx, const fit::registry::Address* address, bool** ret)
{
    if (ctx == nullptr || address == nullptr) {
        FIT_LOG_ERROR("Param is nullptr.");
        return FIT_ERR_FAIL;
    }

    *ret = Fit::Context::NewObj<bool>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("New result failed.");
        return FIT_ERR_FAIL;
    }
    FitSystemPropertyService::GetService()->SetRegistryMatchedAddress(*address);
    **ret = true;
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(SetRegistryMatchedAddress)
        .SetGenericId(::fit::sdk::system::setRegistryMatchedAddress::GENERIC_ID)
        .SetFitableId("fit_sdk_system_set_registry_matched_address");
}
}
}
} // LCOV_EXCL_LINE