/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : null
 * Date         : 2021/1/31 13:33
 * Notes:       :
 */

#include <fit/external/broker/broker_client_external.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include "fit/fit_log.h"
#include "fit/fit_code.h"
namespace Fit {
int32_t GenericableInvoke(ContextObj context, const Fit::string& genericableId, Fit::vector<Fit::any>& in,
    Fit::vector<Fit::any>& out)
{
    Fit::Context::SetGenericableId(context, genericableId);

    if (GetBrokerClient() == nullptr) {
        FIT_LOG_ERROR("%s", "BrokerClient is nullptr");
        return FIT_ERR_FAIL;
    }
    return GetBrokerClient()->GenericableInvoke(context, genericableId, in, out);
}
} // LCOV_EXCL_LINE