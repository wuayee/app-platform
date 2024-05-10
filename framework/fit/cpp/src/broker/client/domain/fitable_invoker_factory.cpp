/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  :
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#include "fitable_invoker_factory.hpp"

#include <fit/stl/memory.hpp>

#include "broker/client/adapter/south/gateway/broker_client_fit_config.h"
#include "decorator/fitable_invoker_degradation_decorator.hpp"
#include "decorator/fitable_invoker_retry_decorator.hpp"
#include "decorator/fitable_invoker_trust_decorator.hpp"
#include "fitable_invoker_shell.hpp"
#include "local_invoker.hpp"

using namespace Fit;
using namespace Fit::Configuration;
using namespace Fit::Framework::Annotation;
using namespace Fit::Framework::Formatter;

namespace {
class DefaultFitableInvokerFactory : public virtual FitableInvokerFactory {
public:
    explicit DefaultFitableInvokerFactory(string currentWorkerId,
        BrokerFitableDiscoveryPtr fitableDiscovery,
        FormatterServicePtr formatterService);
    ~DefaultFitableInvokerFactory() override = default;

    const ::Fit::string& GetCurrentWorkerId() const override;
    const ::Fit::BrokerFitableDiscoveryPtr& GetFitableDiscovery() const override;
    const ::Fit::Framework::Formatter::FormatterServicePtr& GetFormatterService() const override;
    std::unique_ptr<FitableInvoker> GetRawInvoker(
        FitableCoordinatePtr coordinate, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetRawInvoker(
        FitableCoordinatePtr coordinate,
        FitableType fitableType, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetRawInvoker(
        FitableCoordinatePtr coordinate,
        std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetRawInvoker(
        FitableCoordinatePtr coordinate,
        FitableType fitableType,
        std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetInvoker(
        FitableCoordinatePtr coordinate, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetInvoker(
        FitableCoordinatePtr coordinate,
        FitableType fitableType, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetInvoker(
        FitableCoordinatePtr coordinate,
        ::std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetInvoker(
        FitableCoordinatePtr coordinate,
        FitableType fitableType,
        std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetLocalInvoker(
        FitableCoordinatePtr coordinate, FitConfigPtr config) const override;
    std::unique_ptr<FitableInvoker> GetLocalInvoker(
        FitableCoordinatePtr coordinate,
        FitableType fitableType, FitConfigPtr config) const override;
private:
    string currentWorkerId_ {};
    BrokerFitableDiscoveryPtr fitableDiscovery_ {};
    FormatterServicePtr formatterService_ {};
};
}

DefaultFitableInvokerFactory::DefaultFitableInvokerFactory(string currentWorkerId,
    BrokerFitableDiscoveryPtr fitableDiscovery,
    FormatterServicePtr formatterService) : currentWorkerId_(std::move(currentWorkerId)),
    fitableDiscovery_(std::move(fitableDiscovery)),
    formatterService_(std::move(formatterService))
{
}

const ::Fit::string& DefaultFitableInvokerFactory::GetCurrentWorkerId() const
{
    return currentWorkerId_;
}

const ::Fit::BrokerFitableDiscoveryPtr& DefaultFitableInvokerFactory::GetFitableDiscovery() const
{
    return fitableDiscovery_;
}

const ::Fit::Framework::Formatter::FormatterServicePtr& DefaultFitableInvokerFactory::GetFormatterService() const
{
    return formatterService_;
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetInvoker(
    FitableCoordinatePtr coordinate, FitConfigPtr config) const
{
    return GetInvoker(move(coordinate), FitableType::MAIN, nullptr, move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetInvoker(
    FitableCoordinatePtr coordinate,
    FitableType fitableType, FitConfigPtr config) const
{
    return GetInvoker(move(coordinate), fitableType, nullptr, move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetInvoker(
    FitableCoordinatePtr coordinate,
    ::std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const
{
    return GetInvoker(move(coordinate), FitableType::MAIN, move(endpointSupplier), move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetInvoker(
    FitableCoordinatePtr coordinate,
    FitableType fitableType,
    std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const
{
    std::unique_ptr<FitableInvoker> invoker
        = GetRawInvoker(move(coordinate), fitableType, move(endpointSupplier), move(config));
    invoker = make_unique<FitableInvokerRetryDecorator>(move(invoker));
    invoker = make_unique<FitableInvokerDegradationDecorator>(move(invoker));
    invoker = make_unique<FitableInvokerTrustDecorator>(move(invoker));
    return invoker;
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetRawInvoker(
    FitableCoordinatePtr coordinate, FitConfigPtr config) const
{
    return GetRawInvoker(move(coordinate), FitableType::MAIN, nullptr, move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetRawInvoker(
    FitableCoordinatePtr coordinate,
    FitableType fitableType, FitConfigPtr config) const
{
    return GetRawInvoker(move(coordinate), fitableType, nullptr, move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetRawInvoker(
    FitableCoordinatePtr coordinate,
    std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const
{
    return GetRawInvoker(move(coordinate), FitableType::MAIN, move(endpointSupplier), move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetRawInvoker(
    FitableCoordinatePtr coordinate,
    FitableType fitableType,
    std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const
{
    return make_unique<FitableInvokerShell>(this, move(coordinate),
        fitableType, move(endpointSupplier), move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetLocalInvoker(
    FitableCoordinatePtr coordinate, FitConfigPtr config) const
{
    return GetLocalInvoker(move(coordinate), FitableType::MAIN, move(config));
}

std::unique_ptr<FitableInvoker> DefaultFitableInvokerFactory::GetLocalInvoker(
    FitableCoordinatePtr coordinate,
    FitableType fitableType, FitConfigPtr config) const
{
    return LocalInvokerBuilder()
        .SetFactory(this)
        .SetCoordinate(std::move(coordinate))
        .SetFitableType(fitableType)
        .SetFitConfig(move(config))
        .Build();
}

FitableInvokerFactoryBuilder& FitableInvokerFactoryBuilder::SetCurrentWorkerId(string workerId)
{
    workerId_ = std::move(workerId);
    return *this;
}

FitableInvokerFactoryBuilder& FitableInvokerFactoryBuilder::SetFitableDiscovery(
    BrokerFitableDiscoveryPtr fitableDiscovery)
{
    fitableDiscovery_ = std::move(fitableDiscovery);
    return *this;
}

FitableInvokerFactoryBuilder& FitableInvokerFactoryBuilder::SetFormatterService(
    ::Fit::Framework::Formatter::FormatterServicePtr formatterService)
{
    formatterService_ = std::move(formatterService);
    return *this;
}

FitableInvokerFactoryPtr FitableInvokerFactoryBuilder::Build()
{
    return std::make_shared<DefaultFitableInvokerFactory>(
        std::move(workerId_),
        std::move(fitableDiscovery_),
        std::move(formatterService_));
}

FitableInvokerFactoryBuilder FitableInvokerFactory::Custom()
{
    return {};
}
