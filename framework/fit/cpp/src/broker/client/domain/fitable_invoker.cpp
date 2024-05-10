/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable invoker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#include "fitable_invoker.hpp"

#include "fitable_invoker_factory.hpp"
#include "broker/client/adapter/south/gateway/broker_client_fit_config.h"

using namespace Fit;
using namespace Fit::Framework::Annotation;

FitableInvokerBase::FitableInvokerBase(const ::Fit::FitableInvokerFactory* factory,
    FitableCoordinatePtr coordinate, FitableType fitableType, FitConfigPtr config) : factory_(factory),
    coordinate_(std::move(coordinate)),
    fitableType_(fitableType), config_(move(config)) {}

const ::Fit::FitableInvokerFactory* FitableInvokerBase::GetFactory() const
{
    return factory_;
}

const FitConfigPtr& FitableInvokerBase::GetConfig() const
{
    return config_;
}

const FitableCoordinatePtr& FitableInvokerBase::GetCoordinate() const
{
    return coordinate_;
}

FitableType FitableInvokerBase::GetFitableType() const
{
    return fitableType_;
}
