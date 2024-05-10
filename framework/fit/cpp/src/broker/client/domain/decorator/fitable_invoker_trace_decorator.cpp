/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable invoker trace decorator.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/30
 */

#include "fitable_invoker_trace_decorator.hpp"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;

FitableInvokerTraceDecorator::FitableInvokerTraceDecorator(std::unique_ptr<TraceAbleFitableInvoker> decoratedInvoker)
    : decorated_(std::move(decoratedInvoker))
{
}

FitCode FitableInvokerTraceDecorator::Invoke(ContextObj context, Arguments& in, Arguments& out) const
{
    TraceContextPtr traceContext = decorated_->CreateTraceContext(context);
    if (traceContext != nullptr) {
        traceContext->OnFitableInvoking();
    }
    auto ret = FitableInvokerDecoratorBase::Invoke(context, in, out);
    if (traceContext != nullptr) {
        traceContext->OnFitableInvoked((ret == FIT_OK) ? "OK" : to_string(ret));
    }
    return ret;
}

const FitableInvoker& FitableInvokerTraceDecorator::GetDecorated() const
{
    return *decorated_;
}
