/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#include "fit/external/util/context/context_api.hpp"
#include "core/fit_system_property_service.h"

#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_sdk_system_get_local_addresses/1.0.0/cplusplus/getLocalAddresses.hpp>

namespace Fit {
namespace SDK {
namespace System {
int32_t GetLocalAddresses(ContextObj ctx, Fit::vector<fit::registry::Address>** addresses)
{
    if (ctx == nullptr) {
        FIT_LOG_ERROR("%s", "Param is nullptr.");
        return FIT_ERR_FAIL;
    }

    *addresses = Fit::Context::NewObj<Fit::vector<fit::registry::Address>>(ctx);
    if (*addresses == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    **addresses = FitSystemPropertyService::GetService()->GetLocalAddresses();
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(GetLocalAddresses)
        .SetGenericId(::fit::sdk::system::getLocalAddresses::GENERIC_ID)
        .SetFitableId("fit_sdk_system_get_local_addresses");
}
}
}
} // LCOV_EXCL_LINE