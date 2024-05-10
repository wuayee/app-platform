/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/26 17:40
 */

#include "multiplex_invoker_impl.h"
#include <fit/stl/memory.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include <fit/internal/broker/broker_client.h>
#include <fit/external/util/context/context_api.hpp>
#include <fit/fit_log.h>
#include "broker_client_fit_config.h"

namespace Fit {
MultiplexInvokerImpl::MultiplexInvokerImpl(
    Fit::string genericID,
    Fit::BrokerFitableDiscoveryPtr discovery,
    Configuration::ConfigurationServicePtr config,
    Fit::vector<uint8_t> supportProtocol)
    : genericID_(std::move(genericID)),
      discovery_(std::move(discovery)),
      config_(std::move(config)),
      supportClientProtocol_(std::move(supportProtocol)) {}

MultiplexInvoker &MultiplexInvokerImpl::Route(RouteFilter filter)
{
    if (config_ == nullptr) {
        FIT_LOG_ERROR("Config is null, gid is %s.", genericID_.c_str());
        return *this;
    }
    auto fitables = config_->GetFitables(genericID_);
    for (const auto &fitable : fitables) {
        Route(filter, fitable);
    }
    return *this;
}

void MultiplexInvokerImpl::Route(
    const RouteFilter &filter,
    const Configuration::FitableConfiguration &fitable)
{
    if (fitable.aliases.empty()) {
        RouteFilterParam param {
            fitable.fitableId
        };
        if (filter(param)) {
            routeResult_[fitable.fitableId].insert("");
        }
        return;
    }
    for (const auto &alias : fitable.aliases) {
        RouteFilterParam param {
            fitable.fitableId,
            alias
        };
        if (filter(param)) {
            routeResult_[fitable.fitableId].insert(alias);
            break;
        }
    }
}

MultiplexInvoker &MultiplexInvokerImpl::Get(LBFilter filter)
{
    Fit::vector<Framework::Fitable> fitables;
    for (const auto &fitableID : routeResult_) {
        fitables.push_back({genericID_, fitableID.first, "", "1.0.0"});
    }

    if (!fitables.empty()) {
        auto config = make_shared<BrokerClientFitConfig>(config_->GetGenericableConfigPtr(genericID_));
        Get(filter, discovery_->GetFitablesAddresses(*config, fitables));
    }
    return *this;
}

void MultiplexInvokerImpl::Get(const LBFilter &filter,
    const Fit::vector<Framework::ServiceAddress> &services)
{
    const auto &workerToService = Transform(FilterByProtocol(services));
    for (const auto &servicePair : workerToService) {
        for (const auto &service : servicePair.second) {
            if (!filter(LBFilterParam {
                service.address.host,
                static_cast<uint32_t>(service.address.port),
                static_cast<uint32_t>(service.address.protocol),
                service.address.workerId
                })) {
                continue;
            }

            FIT_LOG_DEBUG("Get one filter result! host:%s, port:%d, workerID:%s, fitableID:%s.",
                service.address.host.c_str(),
                service.address.port,
                service.address.workerId.c_str(),
                service.serviceMeta.fitable.fitableId.c_str());

            filterResult_.push_back(service);
            break;
        }
    }
}

FitCode MultiplexInvokerImpl::Exec(
    ContextObj ctx,
    Fit::Framework::Arguments &in,
    Fit::Framework::Arguments &out,
    CallBack cb)
{
    if (filterResult_.empty()) {
        FIT_LOG_ERROR("Filter target failed! genericID:%s.", genericID_.c_str());
        return FIT_ERR_FILTER_TARGET;
    }

    for (const auto &result : filterResult_) {
        Fit::Framework::Arguments tmpOut = out;
        FitCode ret {FIT_ERR_FAIL};
        if (Fit::GetBrokerClient() == nullptr) {
            FIT_LOG_ERROR("Runtime is not ready! [genericableId:%s]", genericID_.c_str());
            ret = FIT_ERR_NOT_READY;
        } else {
            ret = Fit::GetBrokerClient()->ServiceInvoker(result, ctx, in, tmpOut);
        }

        CallBackInfo info {
            ret,
            genericID_,
            result.serviceMeta.fitable.fitableId,
            GetAliasByID(result.serviceMeta.fitable.fitableId),
            result.address.host,
            static_cast<uint32_t>(result.address.port),
            result.address.workerId
        };
        cb(info, tmpOut);
    }

    return FIT_ERR_SUCCESS;
}

Fit::vector<Fit::string> MultiplexInvokerImpl::GetAliasByID(const Fit::string &fitableID)
{
    auto it = routeResult_.find(fitableID);
    if (it == routeResult_.end()) {
        return {};
    }

    Fit::vector<Fit::string> result;
    for (const auto &alias : it->second) {
        result.push_back(alias);
    }
    return result;
}

bool MultiplexInvokerImpl::IsSupportLocalProtocol(uint8_t protocol)
{
    auto it = std::find_if(supportClientProtocol_.begin(), supportClientProtocol_.end(),
        [protocol](uint32_t localProtocol) {
            return localProtocol == protocol;
        });
    return it != supportClientProtocol_.end();
}

Fit::vector<Framework::ServiceAddress> MultiplexInvokerImpl::FilterByProtocol(
    const Fit::vector<Framework::ServiceAddress> &services)
{
    Fit::vector<Framework::ServiceAddress> validServices = services;
    auto it = std::remove_if(validServices.begin(), validServices.end(),
        [this](const Fit::Framework::ServiceAddress &service) {
            return !IsSupportLocalProtocol(service.address.protocol);
        });
    validServices.erase(it, validServices.end());

    return validServices;
}

Fit::map<Fit::string, Fit::vector<Framework::ServiceAddress>> MultiplexInvokerImpl::Transform(
    const Fit::vector<Framework::ServiceAddress> &services)
{
    Fit::map<Fit::string, Fit::vector<Framework::ServiceAddress>> workerIdToService;
    std::for_each(services.begin(), services.end(),
        [&workerIdToService](const Framework::ServiceAddress &service) {
            workerIdToService[service.address.workerId].push_back(service);
        });
    return workerIdToService;
}

MultiInvokerPtr CreateMultiInvoker(const Fit::string &genericID)
{
    BrokerFitableDiscoveryPtr discovery;
    if (GetBrokerClient()) {
        discovery = GetBrokerClient()->GetFitableDiscovery();
    }
    return Fit::make_unique<MultiplexInvokerImpl>(
            genericID,
            move(discovery),
            GetConfigServiceInstance(),
            GetLocalProtocol());
}
} // LCOV_EXCL_LINE