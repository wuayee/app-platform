/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: null
 * Date: 2020-04-19
 */

#include "broker_client_impl.h"

#include <fit/stl/memory.hpp>
#include <fit/internal/util/vector_utils.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/runtime/config/system_config.hpp>

#include "broker_client_fit_config.h"
#include "broker_fitable_discovery.h"
#include "router/router_factory.hpp"
#include "default_registry_listener_api.h"

using namespace ::Fit;
using namespace ::Fit::Configuration;
using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Annotation;
using namespace ::Fit::Framework::Formatter;
using namespace ::Fit::Framework::ParamJsonFormatter;
using namespace ::Fit::Util;
using ::Fit::Config::SystemConfig;

using FitableInfo = fit::registry::Fitable;

namespace {
bool PredicateEndpointWorker(const FitableEndpoint& endpoint, const string& workerId)
{
    if (endpoint.GetWorkerId() == workerId) {
        FIT_LOG_DEBUG("The endpoint is available because target worker matched. "
                      "(environment=%s, worker=%s, protocol=%d, host=%s, port=%d, requiredWorker=%s).",
            endpoint.GetEnvironment().c_str(), endpoint.GetWorkerId().c_str(), endpoint.GetProtocol(),
            endpoint.GetHost().c_str(), (int32_t)endpoint.GetPort(), workerId.c_str());
        return true;
    }
    FIT_LOG_DEBUG("The endpoint is unavailable because target worker unmatched. "
                    "(environment=%s, worker=%s, protocol=%d, host=%s, port=%d, requiredWorker=%s).",
        endpoint.GetEnvironment().c_str(), endpoint.GetWorkerId().c_str(), endpoint.GetProtocol(),
        endpoint.GetHost().c_str(), (int32_t)endpoint.GetPort(), workerId.c_str());
    return false;
}
}

BrokerClient::BrokerClient(Runtime* runtime) : IBrokerClient(runtime) {}

BrokerClient::BrokerClient(Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr,
    ConfigurationServicePtr configurationServicePtr,
    std::shared_ptr<FormatterService> formatterService,
    ParamJsonFormatterPtr paramJsonFormatterService,
    string environment, string workerId,
    Fit::FitableInvokerFactoryPtr fitableInvokerFactoryPtr)
    : IBrokerClient(nullptr),
      fitableDiscoveryPtr_(move(fitableDiscoveryPtr)),
      configurationServicePtr_(std::move(configurationServicePtr)),
      formatterService_(std::move(formatterService)), paramJsonFormatterService_(std::move(paramJsonFormatterService)),
      environment_(std::move(environment))
{
    if (fitableInvokerFactoryPtr != nullptr) {
        factory_ = fitableInvokerFactoryPtr;
    } else {
        factory_ = FitableInvokerFactory::Custom()
            .SetCurrentWorkerId(std::move(workerId))
            .SetFitableDiscovery(make_shared<BrokerFitableDiscovery>(fitableDiscoveryPtr_,
                make_unique<DefaultRegistryListenerApi>()))
            .SetFormatterService(formatterService_)
            .Build();
    }
}

const std::shared_ptr<FormatterService>& BrokerClient::GetFormatterService() const
{
    return formatterService_;
}
const BrokerFitableDiscoveryPtr& BrokerClient::GetFitableDiscovery() const
{
    return factory_->GetFitableDiscovery();
}

std::unique_ptr<Fit::FitableEndpointSupplier> BrokerClient::CreateEndpointSupplier(ContextObj context,
    Fit::FitableCoordinatePtr coordinate, Fit::FitableInvokerFactoryPtr factory,
    Fit::Context::TargetAddress* targetAddressPtr,
    FitConfigPtr fitableConfig)
{
    std::unique_ptr<Fit::FitableEndpointSupplier> endpointSupplier;
    if (targetAddressPtr != nullptr) {
        auto formats = formatterService_->GetFormats(coordinate->GetGenericableId());
        endpointSupplier = FitableEndpointSupplier::CreateDirectSupplier(FitableEndpoint::Custom()
                .SetWorkerId((*targetAddressPtr).workerId)
                .SetEnvironment("debug")
                .SetHost((*targetAddressPtr).host)
                .SetPort((*targetAddressPtr).port)
                .SetProtocol((*targetAddressPtr).protocol)
                .SetFormats(formats)
                .SetContext({{(*targetAddressPtr).workerId, targetAddressPtr->extensions}, {}})
                .Build());
    } else {
        auto localSupplier = FitableEndpointSupplier::CreateLocalSupplier(coordinate, factory->GetFitableDiscovery());
        auto loadBalanceSupplier = FitableEndpointSupplier::CreateLoadBalanceSupplier(
            coordinate, fitableConfig, factory->GetFitableDiscovery(), CreateEndpointPredicate(context));
        endpointSupplier
            = FitableEndpointSupplier::Combine(std::move(localSupplier), std::move(loadBalanceSupplier));
    }
    return endpointSupplier;
}

int32_t BrokerClient::GenericableInvoke(ContextObj context, const string &genericableId, Arguments &in, Arguments &out)
{
    FitConfigPtr config = Fit::BrokerClientFitConfig::BuildConfig(genericableId, configurationServicePtr_);
    if (config == nullptr) {
        FIT_LOG_ERROR("Genericable configuration not found. (genericable=%s).", genericableId.c_str());
        return FIT_ERR_NOT_FOUND;
    }
    auto router = RouterFactory::Create(context, config, paramJsonFormatterService_, in, environment_);
    auto fitableId = router->Route();
    if (fitableId.empty()) {
        FIT_LOG_ERROR("Route error! genericableID:%s.", genericableId.c_str());
        return FIT_ERR_ROUTE;
    }
    FIT_LOG_DEBUG("Route successfully. (fitable=%s:%s).", genericableId.c_str(), fitableId.c_str());

    auto coordinate = FitableCoordinate::Custom()
        .SetGenericableId(genericableId)
        .SetGenericableVersion("1.0.0")
        .SetFitableId(std::move(fitableId))
        .SetFitableVersion("1.0.0")
        .Build();
    std::unique_ptr<Fit::FitableEndpointSupplier> endpointSupplier;
    std::unique_ptr<Fit::FitableInvoker> invoker;
    Fit::Context::TargetAddress* targetAddressPtr = Fit::Context::ContextGetTargetAddress(context);
    endpointSupplier = CreateEndpointSupplier(context, coordinate, factory_, targetAddressPtr, config);
    invoker = factory_->GetInvoker(std::move(coordinate), std::move(endpointSupplier), std::move(config));
    return invoker->Invoke(context, in, out);
}

int32_t BrokerClient::ServiceInvoker(const ServiceAddress &service, ContextObj context, Arguments &in, Arguments &out)
{
    auto config = BrokerClientFitConfig::BuildConfig(service.serviceMeta.fitable.genericId, configurationServicePtr_);
    if (config == nullptr) {
        FIT_LOG_ERROR("Genericable config not found. [genericable=%s]", service.serviceMeta.fitable.genericId.c_str());
        return FIT_ERR_FAIL;
    }

    auto coordinate = FitableCoordinate::Custom()
        .SetGenericableId(service.serviceMeta.fitable.genericId)
        .SetGenericableVersion(service.serviceMeta.fitable.genericVersion)
        .SetFitableId(service.serviceMeta.fitable.fitableId)
        .SetFitableVersion(service.serviceMeta.fitable.fitableVersion)
        .Build();
    auto fitableType = FitableType::MAIN;
    auto endpointSupplier = FitableEndpointSupplier::CreateDirectSupplier(FitableEndpoint::Custom()
        .SetWorkerId(service.address.workerId)
        .SetEnvironment(service.address.environment)
        .SetHost(service.address.host)
        .SetPort(service.address.port)
        .SetProtocol((int32_t)service.address.protocol)
        .SetFormats(service.address.formats)
        .SetContext({{service.address.workerId, service.address.extensions}, {service.serviceMeta.application}})
        .Build());

    auto invoker = factory_->GetInvoker(
        std::move(coordinate),
        fitableType,
        std::move(endpointSupplier), std::move(config));
    return invoker->Invoke(context, in, out);
}

int32_t BrokerClient::LocalInvoke(ContextObj context, const FitableInfo &fitable, Arguments &in, Arguments &out,
    FitableType fitableType)
{
    FitConfigPtr config = Fit::BrokerClientFitConfig::BuildConfig(fitable.genericId, configurationServicePtr_);
    if (config == nullptr) {
        FIT_LOG_ERROR("Genericable configuration not found. (genericable=%s).", fitable.genericId.c_str());
        return FIT_ERR_NOT_FOUND;
    }
    return factory_->GetLocalInvoker(FitableCoordinate::Custom()
        .SetGenericableId(fitable.genericId)
        .SetGenericableVersion(fitable.genericVersion)
        .SetFitableId(fitable.fitId)
        .SetFitableVersion(fitable.fitVersion)
        .Build(),
        fitableType, move(config))
        ->Invoke(context, in, out);
}

std::unique_ptr<FitableEndpointPredicate> BrokerClient::CreateEndpointPredicate(ContextObj context)
{
    std::unique_ptr<FitableEndpointPredicate> predicate {nullptr};
    string targetWorker = ContextGetTargetWorker(context);
    if (!targetWorker.empty()) {
        auto workerPredicate = std::bind(::PredicateEndpointWorker, std::placeholders::_1, targetWorker);
        predicate = FitableEndpointPredicate::Combine(std::move(predicate),
            FitableEndpointPredicate::Create(std::move(workerPredicate)));
    }
    return predicate;
}

bool BrokerClient::Start()
{
    fitableDiscoveryPtr_ = (FitableDiscoveryPtr(GetRuntime().GetElementIs<FitableDiscovery>(),
        [](FitableDiscovery*) {}));
    configurationServicePtr_ = Configuration::ConfigurationServicePtr(
        GetRuntime().GetElementIs<Configuration::ConfigurationService>(),
        [](Configuration::ConfigurationService* p) {});
    formatterService_ = Framework::Formatter::FormatterServicePtr(GetRuntime().GetElementIs<FormatterService>(),
        [](FormatterService* p) {});
    paramJsonFormatterService_ = ParamJsonFormatterPtr(GetRuntime().GetElementIs<ParamJsonFormatterService>(),
        [](ParamJsonFormatterService* p) {});
    environment_ = GetRuntime().GetElementIs<SystemConfig>()->GetEnvName();

    factory_ = FitableInvokerFactory::Custom()
        .SetCurrentWorkerId(GetRuntime().GetElementIs<SystemConfig>()->GetWorkerId())
        .SetFitableDiscovery(make_shared<BrokerFitableDiscovery>(fitableDiscoveryPtr_,
            make_unique<DefaultRegistryListenerApi>()))
        .SetFormatterService(formatterService_)
        .Build();
    return true;
}

bool BrokerClient::Stop()
{
    return true;
}
