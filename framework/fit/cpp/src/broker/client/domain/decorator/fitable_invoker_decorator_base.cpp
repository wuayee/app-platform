/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for base of fitable invoker decorators.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/04
 */

#include "fitable_invoker_decorator_base.hpp"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;

const ::Fit::FitableInvokerFactory* FitableInvokerDecoratorBase::GetFactory() const
{
    return GetDecorated().GetFactory();
}

const FitConfigPtr& FitableInvokerDecoratorBase::GetConfig() const
{
    return GetDecorated().GetConfig();
}

const FitableCoordinatePtr& FitableInvokerDecoratorBase::GetCoordinate() const
{
    return GetDecorated().GetCoordinate();
}

FitableType FitableInvokerDecoratorBase::GetFitableType() const
{
    return GetDecorated().GetFitableType();
}

FitCode FitableInvokerDecoratorBase::Invoke(ContextObj context, Arguments& in, Arguments& out) const
{
    return GetDecorated().Invoke(context, in, out);
}

FitableInvokerEmptyDecorator::FitableInvokerEmptyDecorator(std::unique_ptr<FitableInvoker> decorated)
    : decorated_(std::move(decorated))
{
}

const FitableInvoker& FitableInvokerEmptyDecorator::GetDecorated() const
{
    return *decorated_;
}
