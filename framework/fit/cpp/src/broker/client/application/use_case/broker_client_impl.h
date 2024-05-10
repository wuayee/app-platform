/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2021-04-19
 */
#ifndef BROKER_CLIENT_H
#define BROKER_CLIENT_H

#include <memory>
#include <fit/stl/vector.hpp>
#include <fit/stl/any.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <src/broker/client/adapter/south/gateway/broker_client_fit_config.h>
#include "fit_discovery.h"
#include "fit_config.h"
#include "configuration_service.h"
#include "fit/internal/framework/formatter_service.hpp"
#include "fit/internal/broker/broker_client.h"
#include "fit/internal/framework/param_json_formatter_service.hpp"
#include "broker/client/domain/fitable_invoker_factory.hpp"

namespace Fit {
class BrokerClient : public Fit::IBrokerClient {
public:
    explicit BrokerClient(Runtime* runtime);
    explicit BrokerClient(
        Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr,
        Configuration::ConfigurationServicePtr configurationServicePtr,
        std::shared_ptr<Fit::Framework::Formatter::FormatterService> formatterService,
        Framework::ParamJsonFormatter::ParamJsonFormatterPtr paramJsonFormatterService,
        Fit::string environment,
        Fit::string workerId,
        Fit::FitableInvokerFactoryPtr fitableInvokerFactoryPtr = nullptr);
    ~BrokerClient() override = default;

    bool Start() override;
    bool Stop() override;

    const std::shared_ptr<::Fit::Framework::Formatter::FormatterService>& GetFormatterService() const override;
    const BrokerFitableDiscoveryPtr& GetFitableDiscovery() const override;

    int32_t GenericableInvoke(ContextObj context,
        const Fit::string &genericableId,
        Fit::vector<Fit::any> &in, Fit::vector<Fit::any> &out) override;

    int32_t ServiceInvoker(
        const Framework::ServiceAddress &service,
        ContextObj context,
        Fit::vector<Fit::any> &in,
        Fit::vector<Fit::any> &out) override;

    int32_t LocalInvoke(ContextObj context,
        const fit::registry::Fitable &fitable,
        Fit::vector<Fit::any> &in, Fit::vector<Fit::any> &out,
        Fit::Framework::Annotation::FitableType fitableType) override;

private:
    static std::unique_ptr<FitableEndpointPredicate> CreateEndpointPredicate(ContextObj context);
    std::unique_ptr<Fit::FitableEndpointSupplier> CreateEndpointSupplier(ContextObj context,
        Fit::FitableCoordinatePtr coordinate, Fit::FitableInvokerFactoryPtr factory,
        Fit::Context::TargetAddress* targetAddressPtr,
        FitConfigPtr fitableConfig);
    Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr_ {};
    Configuration::ConfigurationServicePtr configurationServicePtr_ {};
    std::shared_ptr<Fit::Framework::Formatter::FormatterService> formatterService_ {};
    Framework::ParamJsonFormatter::ParamJsonFormatterPtr paramJsonFormatterService_ {};
    Fit::string environment_ {};
    ::Fit::FitableInvokerFactoryPtr factory_ {nullptr};
};
}
#endif