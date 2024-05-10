/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/26 17:47
 */
#ifndef MULTIPLEXINVOKERIMPL_H
#define MULTIPLEXINVOKERIMPL_H

#include <fit/external/broker/multiplex_invoker.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include "configuration_service.h"
#include "fit/stl/set.hpp"
#include "fit_discovery.h"

namespace Fit {
using FitableSet = Fit::map<Fit::string, Fit::set<Fit::string>>;
using FilterReusultSet = Fit::vector<Framework::ServiceAddress>;

class MultiplexInvokerImpl : public MultiplexInvoker {
public:
    MultiplexInvokerImpl(
        Fit::string genericID,
        Fit::BrokerFitableDiscoveryPtr discovery,
        Configuration::ConfigurationServicePtr config,
        Fit::vector<uint8_t> supportProtocol);

    ~MultiplexInvokerImpl() override = default;

    MultiplexInvoker &Route(RouteFilter filter) override;
    MultiplexInvoker &Get(LBFilter filter) override;
    FitCode Exec(
        ContextObj ctx,
        Fit::Framework::Arguments &in,
        Fit::Framework::Arguments &out,
        CallBack cb) override;

private:
    void Route(
        const RouteFilter &filter,
        const Configuration::FitableConfiguration &fitable);

    void Get(const LBFilter &filter,
        const Fit::vector<Framework::ServiceAddress> &addresses);

    Fit::map<Fit::string, Fit::vector<Framework::ServiceAddress>> Transform(
        const Fit::vector<Framework::ServiceAddress> &services);

    Fit::vector<Framework::ServiceAddress> FilterByProtocol(
        const Fit::vector<Framework::ServiceAddress> &services);

    bool IsSupportLocalProtocol(uint8_t protocol);

    Fit::vector<Fit::string> GetAliasByID(const Fit::string &fitableID);

private:
    const Fit::string genericID_;
    Fit::BrokerFitableDiscoveryPtr discovery_;
    Configuration::ConfigurationServicePtr config_;

    FitableSet routeResult_;
    FilterReusultSet filterResult_;
    Fit::vector<uint8_t> supportClientProtocol_;
};
}

#endif // MULTIPLEXINVOKERIMPL_H
