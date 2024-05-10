/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides trust decorator for fitable invokers.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/10
 */

#ifndef FIT_FITABLE_INVOKER_TRUST_DECORATOR_HPP
#define FIT_FITABLE_INVOKER_TRUST_DECORATOR_HPP

#include "fitable_invoker_decorator_base.hpp"

namespace Fit {
/**
 * 为服务实现的调用程序提供支持可信的装饰程序。
 */
class FitableInvokerTrustDecorator : public FitableInvokerEmptyDecorator {
public:
    explicit FitableInvokerTrustDecorator(std::unique_ptr<FitableInvoker> decorated);
    ~FitableInvokerTrustDecorator() override = default;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
private:
    FitCode Validate(ContextObj context, ::Fit::Framework::Arguments& in, ::Fit::Framework::Arguments& out) const;
    void Before(ContextObj context, ::Fit::Framework::Arguments& in) const;
    void Error(ContextObj context, ::Fit::Framework::Arguments& in) const;
    void After(ContextObj context, ::Fit::Framework::Arguments& in) const;
    FitCode InvokeTrust(ContextObj context, Fit::Framework::Annotation::FitableType fitableType,
        const ::Fit::string& fitableId, ::Fit::Framework::Arguments& in, ::Fit::Framework::Arguments& out) const;
};
}

#endif // FIT_FITABLE_INVOKER_TRUST_DECORATOR_HPP
