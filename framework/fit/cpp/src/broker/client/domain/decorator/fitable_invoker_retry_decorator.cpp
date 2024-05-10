/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable invoker retry decorator.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#include "fitable_invoker_retry_decorator.hpp"

#include <fit/fit_code_helper.h>
#include <fit/fit_log.h>

using namespace Fit;
using namespace Fit::Framework;

FitableInvokerRetryDecorator::FitableInvokerRetryDecorator(std::unique_ptr<FitableInvoker> decorated)
    : FitableInvokerEmptyDecorator(std::move(decorated))
{
}

FitCode FitableInvokerRetryDecorator::Invoke(ContextObj context, Arguments& in, Arguments& out) const
{
    uint32_t maximum = ContextGetRetry(context);
    uint32_t retry = 0;
    FitCode ret = FitableInvokerEmptyDecorator::Invoke(context, in, out);
    while (IsNetErrorCode(ret) && retry < maximum) {
        retry++;
        FIT_LOG_WARN("Network error occurs. Retry fitable invocation. "
                      "[genericableId=%s, genericableVersion=%s, "
                      "fitableId=%s, fitableVersion=%s, retry=%u, maximum=%u]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableVersion().c_str(),
            GetCoordinate()->GetFitableId().c_str(), GetCoordinate()->GetFitableVersion().c_str(),
            retry, maximum);
        ret = FitableInvokerEmptyDecorator::Invoke(context, in, out);
    }
    return ret;
}