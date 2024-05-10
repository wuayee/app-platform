/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides trace decorator for fitable invoker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/30
 */

#ifndef FIT_FITABLE_INVOKER_TRACE_DECORATOR_HPP
#define FIT_FITABLE_INVOKER_TRACE_DECORATOR_HPP

#include "fitable_invoker_decorator_base.hpp"

#include "../trace/tracer.hpp"

#include <functional>

namespace Fit {
/**
 * 表示支持调用跟踪的服务实现。
 */
class TraceAbleFitableInvoker : public virtual FitableInvoker {
public:
    /**
     * 为当前的服务提供调用跟踪程序。
     *
     * @param context 表示服务实现执行时所关联的上下文。
     * @return 表示指向当前服务实现的服务跟踪程序的指针。
     */
    virtual TraceContextPtr CreateTraceContext(ContextObj context) const = 0;
};

using TraceAbleFitableInvokerPtr = std::shared_ptr<TraceAbleFitableInvoker>;

/**
 * 为服务实现的调用程序提供调用跟踪的装饰程序。
 */
class FitableInvokerTraceDecorator : public FitableInvokerDecoratorBase {
public:
    explicit FitableInvokerTraceDecorator(std::unique_ptr<TraceAbleFitableInvoker> decorated);
    ~FitableInvokerTraceDecorator() override = default;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
protected:
    const FitableInvoker& GetDecorated() const override;
private:
    TraceAbleFitableInvokerPtr decorated_;
};
}

#endif // FIT_FITABLE_INVOKER_TRACE_DECORATOR_HPP
