/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides degradation decorator for fitable invoker.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#ifndef FIT_FITABLE_INVOKER_DEGRADATION_DECORATOR_HPP
#define FIT_FITABLE_INVOKER_DEGRADATION_DECORATOR_HPP

#include "fitable_invoker_decorator_base.hpp"

namespace Fit {
/**
 * 为服务实现的调用程序提供支持降级调用能力的装饰程序。
 */
class FitableInvokerDegradationDecorator : public FitableInvokerEmptyDecorator {
public:
    explicit FitableInvokerDegradationDecorator(std::unique_ptr<FitableInvoker> decorated);
    ~FitableInvokerDegradationDecorator() override = default;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
};
}

#endif // FIT_FITABLE_INVOKER_DEGRADATION_DECORATOR_HPP
