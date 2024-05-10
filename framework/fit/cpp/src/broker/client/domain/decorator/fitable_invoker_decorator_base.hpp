/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides base for fitable invoker decorators.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/04
 */

#ifndef FIT_FITABLE_INVOKER_DECORATOR_BASE_HPP
#define FIT_FITABLE_INVOKER_DECORATOR_BASE_HPP

#include "../fitable_invoker.hpp"
#include "../fitable_coordinate.hpp"

namespace Fit {
/**
 * 为服务实现的调用程序的装饰程序提供基类。
 */
class FitableInvokerDecoratorBase : public FitableInvoker {
public:
    const ::Fit::FitableInvokerFactory* GetFactory() const override;
    const FitConfigPtr& GetConfig() const override;
    const ::Fit::FitableCoordinatePtr& GetCoordinate() const override;
    ::Fit::Framework::Annotation::FitableType GetFitableType() const override;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
protected:
    virtual const FitableInvoker& GetDecorated() const = 0;
};

/**
 * 为服务调用的装饰程序提供空实现。
 */
class FitableInvokerEmptyDecorator : public FitableInvokerDecoratorBase {
public:
    explicit FitableInvokerEmptyDecorator(std::unique_ptr<FitableInvoker> decorated);
    ~FitableInvokerEmptyDecorator() override = default;
protected:
    const FitableInvoker& GetDecorated() const override;
private:
    std::unique_ptr<FitableInvoker> decorated_ {nullptr};
};
}

#endif // FIT_FITABLE_INVOKER_DECORATOR_BASE_HPP
