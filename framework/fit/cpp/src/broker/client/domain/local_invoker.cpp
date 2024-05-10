/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for local invoker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#include "local_invoker.hpp"

#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>

#include "fitable_invoker_factory.hpp"

using namespace Fit;
using namespace Fit::Framework::Annotation;

LocalInvoker::LocalInvoker(const ::Fit::FitableInvokerFactory* factory,
    ::Fit::FitableCoordinatePtr coordinate, ::Fit::Framework::Annotation::FitableType fitableType, FitConfigPtr config)
    : FitableInvokerBase(factory, move(coordinate), fitableType, move(config))
{
}

FitCode LocalInvoker::Invoke(ContextObj context, vector<any>& in, vector<any>& out) const
{
    Framework::Fitable fitableId;
    fitableId.genericId = GetCoordinate()->GetGenericableId();
    fitableId.genericVersion = GetCoordinate()->GetGenericableVersion();
    fitableId.fitableId = GetCoordinate()->GetFitableId();
    fitableId.fitableVersion = GetCoordinate()->GetFitableVersion();
    auto fitables = GetFactory()->GetFitableDiscovery()->GetLocalFitable(fitableId);
    if (fitables.empty()) {
        FIT_LOG_ERROR("Failed to invoke local fitable. Local fitable not found. [genericable=%s, fitable=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str());
        return FIT_ERR_FAIL;
    }
    auto& fitable = fitables[0];
    if (fitable->GetFunctionProxy() == nullptr) {
        FIT_LOG_ERROR("Failed to invoke local fitable. The proxy function is nullptr. [genericable=%s, fitable=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str());
        return FIT_ERR_FAIL;
    }
    Framework::Arguments args;
    args.reserve(1 + in.size() + out.size());
    args.push_back(context);
    args.insert(args.end(), in.begin(), in.end());
    args.insert(args.end(), out.begin(), out.end());
    auto ret = fitable->GetFunctionProxy()(args);
    return ret;
}

TraceContextPtr LocalInvoker::CreateTraceContext(ContextObj context) const
{
    if (!GetConfig()->TraceIgnore() && Tracer::GetInstance()->IsLocalTraceEnabled()) {
        return TraceContext::Custom()
            .SetContext(context)
            .SetFitableCoordinate(GetCoordinate())
            .SetCallType(CallType::LOCAL)
            .SetTrustStage(GetTrustStage(GetFitableType()))
            .Build();
    } else {
        return nullptr;
    }
}

LocalInvokerBuilder& LocalInvokerBuilder::SetFactory(const ::Fit::FitableInvokerFactory* factory)
{
    factory_ = factory;
    return *this;
}

LocalInvokerBuilder& LocalInvokerBuilder::SetCoordinate(FitableCoordinatePtr coordinate)
{
    coordinate_ = std::move(coordinate);
    return *this;
}

LocalInvokerBuilder& LocalInvokerBuilder::SetFitableType(::Fit::Framework::Annotation::FitableType fitableType)
{
    fitableType_ = fitableType;
    return *this;
}

LocalInvokerBuilder& LocalInvokerBuilder::SetFitConfig(FitConfigPtr config)
{
    config_ = move(config);
    return *this;
}

std::unique_ptr<FitableInvoker> LocalInvokerBuilder::Build()
{
    auto invoker = make_unique<LocalInvoker>(factory_, move(coordinate_), fitableType_, move(config_));
    return make_unique<FitableInvokerTraceDecorator>(move(invoker));
}
