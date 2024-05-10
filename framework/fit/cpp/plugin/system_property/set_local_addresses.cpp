/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#include <genericable/com_huawei_fit_sdk_system_set_local_addresses/1.0.0/cplusplus/setLocalAddresses.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "core/fit_system_property_service.h"

namespace Fit {
namespace SDK {
namespace System {
int32_t SetLocalAddresses(ContextObj ctx, const Fit::vector<fit::registry::Address>* addresses, bool** ret)
{
    if (ctx == nullptr || addresses == nullptr) {
        FIT_LOG_ERROR("%s", "Param is nullptr.");
        return FIT_ERR_FAIL;
    }

    *ret = Fit::Context::NewObj<bool>(ctx);
    if (*ret == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    FitSystemPropertyService::GetService()->SetLocalAddresses(*addresses);
    **ret = true;
    return FIT_OK;
}
FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(SetLocalAddresses)
        .SetGenericId(::fit::sdk::system::setLocalAddresses::GENERIC_ID)
        .SetFitableId("fit_sdk_system_set_local_addresses");
}
}
}
} // LCOV_EXCL_LINE