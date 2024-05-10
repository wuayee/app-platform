/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides retry decorator for fitable invokers.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#ifndef FIT_FITABLE_INVOKER_RETRY_DECORATOR_HPP
#define FIT_FITABLE_INVOKER_RETRY_DECORATOR_HPP

#include "fitable_invoker_decorator_base.hpp"

namespace Fit {
/**
 * 为服务实现的调用程序提供支持失败重试的装饰程序。
 */
class FitableInvokerRetryDecorator : public FitableInvokerEmptyDecorator {
public:
    explicit FitableInvokerRetryDecorator(std::unique_ptr<FitableInvoker> decorated);
    ~FitableInvokerRetryDecorator() override = default;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
};
}

#endif // FIT_FITABLE_INVOKER_RETRY_DECORATOR_HPP
