/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for trust decorator.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/10
 */

#include "fitable_invoker_trust_decorator.hpp"

#include <fit/fit_log.h>
#include "fitable_invoker_factory.hpp"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;

FitableInvokerTrustDecorator::FitableInvokerTrustDecorator(std::unique_ptr<FitableInvoker> decorated)
    : FitableInvokerEmptyDecorator(std::move(decorated))
{
}

FitCode FitableInvokerTrustDecorator::Invoke(ContextObj context, Arguments& in, Arguments& out) const
{
    auto ret = Validate(context, in, out);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Validation failed. [genericable=%s, fitable=%s, error=%x]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(), ret);
        return ret;
    }
    Before(context, in);
    ret = FitableInvokerDecoratorBase::Invoke(context, in, out);
    if (ret != FIT_OK) {
        Error(context, in);
    } else {
        After(context, in);
    }
    return ret;
}

FitCode FitableInvokerTrustDecorator::Validate(ContextObj context, Arguments& in, Arguments& out) const
{
    return InvokeTrust(context, FitableType::VALIDATE, GetConfig()->GetValidate(), in, out);
}

void FitableInvokerTrustDecorator::Before(ContextObj context, Arguments& in) const
{
    Arguments out;
    InvokeTrust(context, FitableType::BEFORE, GetConfig()->GetBefore(), in, out);
}

void FitableInvokerTrustDecorator::Error(ContextObj context, Arguments& in) const
{
    Arguments out;
    InvokeTrust(context, FitableType::ERROR, GetConfig()->GetError(), in, out);
}

void FitableInvokerTrustDecorator::After(ContextObj context, Arguments& in) const
{
    Arguments out;
    InvokeTrust(context, FitableType::AFTER, GetConfig()->GetAfter(), in, out);
}

FitCode FitableInvokerTrustDecorator::InvokeTrust(ContextObj context,
    Fit::Framework::Annotation::FitableType fitableType,
    const string& fitableId, Arguments& in, Arguments& out) const
{
    if (!GetConfig()->EnableTrust() || fitableId.empty()) {
        return FIT_OK;
    }
    auto factory = GetFactory();
    FitableCoordinatePtr coordinate = FitableCoordinate::Custom()
        .SetGenericableId(GetCoordinate()->GetGenericableId())
        .SetGenericableVersion(GetCoordinate()->GetGenericableVersion())
        .SetFitableId(fitableId)
        .SetFitableVersion(GetCoordinate()->GetFitableVersion())
        .Build();
    auto invoker = factory->GetRawInvoker(std::move(coordinate), fitableType, GetConfig());
    return invoker->Invoke(context, in, out);
}
