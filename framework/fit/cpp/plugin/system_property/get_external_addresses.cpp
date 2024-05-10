/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#include <genericable/com_huawei_fit_sdk_system_get_external_addresses/1.0.0/cplusplus/get_external_addresses.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/util/context/context_api.hpp>

#include "core/fit_system_property_service.h"
#include "fit_code.h"

namespace {
using namespace Fit::SDK::System;
int32_t GetExternalAddressesImpl(ContextObj ctx, Fit::vector<fit::registry::Address>** addresses)
{
    *addresses = Fit::Context::NewObj<Fit::vector<fit::registry::Address>>(ctx);
    if (*addresses == nullptr) {
        FIT_LOG_ERROR("Failed to new result.");
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    **addresses = FitSystemPropertyService::GetService()->GetExternalAddresses();
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(GetExternalAddressesImpl)
        .SetGenericId(::fit::sdk::system::GetExternalAddresses::GENERIC_ID)
        .SetFitableId("getExternalAddresses");
}
} // LCOV_EXCL_LINE
