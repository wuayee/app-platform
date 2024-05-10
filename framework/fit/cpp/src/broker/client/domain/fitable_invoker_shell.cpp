/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable invoker shell.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/06
 */

#include "fitable_invoker_shell.hpp"

#include <fit/fit_log.h>
#include "local_invoker.hpp"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;
using namespace Fit::Framework::Formatter;

FitableInvokerShell::FitableInvokerShell(const ::Fit::FitableInvokerFactory* factory,
    FitableCoordinatePtr coordinate, FitableType fitableType, std::unique_ptr<FitableEndpointSupplier> endpointSupplier,
    FitConfigPtr config)
    : FitableInvokerBase(factory, move(coordinate), fitableType, move(config)),
    endpointSupplier_(move(endpointSupplier))
{
}

FitCode FitableInvokerShell::Invoke(ContextObj context, Arguments& in, Arguments& out) const
{
    std::unique_ptr<FitableInvoker> invoker;
    if (GetConfig()->LocalOnly()) {
        invoker = CreateLocalInvoker();
    } else {
        FitableEndpointPtr endpoint;
        if (endpointSupplier_ == nullptr) {
            endpoint = CreateDefaultEndpointSupplier()->Get();
        } else {
            endpoint = endpointSupplier_->Get();
        }
        if (endpoint == nullptr) {
            FIT_LOG_ERROR("No available fitable endpoint found. "
                          "[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
                GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetGenericableVersion().c_str(),
                GetCoordinate()->GetFitableId().c_str(), GetCoordinate()->GetFitableVersion().c_str());
            return FIT_ERR_NOT_FOUND;
        } else {
            if (endpoint->IsLocal()) {
                invoker = CreateLocalInvoker();
            } else {
                invoker = CreateRemoteInvoker(std::move(endpoint));
            }
        }
    }
    return invoker->Invoke(context, in, out);
}

std::unique_ptr<FitableInvoker> FitableInvokerShell::CreateLocalInvoker() const
{
    return LocalInvokerBuilder()
        .SetCoordinate(GetCoordinate())
        .SetFactory(GetFactory())
        .SetFitableType(GetFitableType())
        .SetFitConfig(GetConfig())
        .Build();
}

std::unique_ptr<FitableInvoker> FitableInvokerShell::CreateRemoteInvoker(FitableEndpointPtr endpoint) const
{
    return RemoteInvokerBuilder()
        .SetCoordinate(GetCoordinate())
        .SetFactory(GetFactory())
        .SetFitableType(GetFitableType())
        .SetEndpoint(std::move(endpoint))
        .SetFitConfig(GetConfig())
        .Build();
}

std::unique_ptr<FitableEndpointSupplier> FitableInvokerShell::CreateDefaultEndpointSupplier() const
{
    return FitableEndpointSupplier::CreateLoadBalanceSupplier(
        GetCoordinate(), GetConfig(), GetFactory()->GetFitableDiscovery());
}
