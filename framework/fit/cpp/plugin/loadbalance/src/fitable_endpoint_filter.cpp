/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable endpoint filter.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#include <fitable_endpoint_filter.hpp>
#include <fit/stl/memory.hpp>
#include <fitable_endpoint.hpp>

#include <fit/fit_log.h>
#include <fit/internal/util/vector_utils.hpp>

#include <algorithm>

using namespace Fit;
using namespace Fit::LoadBalance;
using namespace Fit::Util;

namespace {
using ApplicationInstance = ::fit::hakuna::kernel::registry::shared::ApplicationInstance;
using Fitable = ::fit::hakuna::kernel::shared::Fitable;

class DefaultFitableEndpointFilter : public virtual FitableEndpointFilter {
public:
    explicit DefaultFitableEndpointFilter(LoadBalanceSpiPtr spi, ContextObj context,
        const vector<ApplicationInstance>& instances);
    ~DefaultFitableEndpointFilter() override = default;
    FitCode Filter() override;
    vector<ApplicationInstance>* GetResult() override;
private:
    bool Predicate(const FitableEndpoint& endpoint);
    bool PredicateProtocol(const FitableEndpoint& endpoint);
    bool PredicateEnvironment(const FitableEndpoint& endpoint);
    LoadBalanceSpiPtr spi_ {nullptr};
    ContextObj context_ {nullptr};
    const vector<ApplicationInstance>& instances_;
    vector<ApplicationInstance>* result_ {nullptr};
};
}

std::unique_ptr<FitableEndpointFilter> FitableEndpointFilter::Create(LoadBalanceSpiPtr spi, ContextObj context,
    const vector<ApplicationInstance>& instances)
{
    return make_unique<DefaultFitableEndpointFilter>(move(spi), context, instances);
}

DefaultFitableEndpointFilter::DefaultFitableEndpointFilter(LoadBalanceSpiPtr spi, ContextObj context,
    const vector<ApplicationInstance>& instances)
    : spi_(std::move(spi)), context_(context), instances_(instances)
{
}

FitCode DefaultFitableEndpointFilter::Filter()
{
    auto endpoints = FitableEndpoint::Flat(instances_);
    size_t before = endpoints.size();
    for (auto iter = endpoints.begin(); iter != endpoints.end();) {
        if (Predicate(*iter)) {
            FIT_LOG_DEBUG("The endpoint is available. [worker=%s, environment=%s, host=%s, port=%d, protocol=%d]",
                iter->GetWorker()->id.c_str(), iter->GetWorker()->environment.c_str(),
                iter->GetAddress()->host.c_str(), iter->GetEndpoint()->port, iter->GetEndpoint()->protocol);
            iter++;
        } else {
            iter = endpoints.erase(iter);
        }
    }
    FIT_LOG_DEBUG("Endpoints filtered by protocol and environment. [before=%zu, after=%zu]", before, endpoints.size());
    result_ = FitableEndpoint::Aggregate(context_, endpoints);
    return FIT_OK;
}

vector<ApplicationInstance>* DefaultFitableEndpointFilter::GetResult()
{
    return result_;
}

bool DefaultFitableEndpointFilter::Predicate(const FitableEndpoint& endpoint)
{
    return PredicateProtocol(endpoint) && PredicateEnvironment(endpoint);
}

bool DefaultFitableEndpointFilter::PredicateProtocol(const FitableEndpoint& endpoint)
{
    const vector<int32_t>& protocols = spi_->GetProtocols();
    if (VectorUtils::BinarySearch<int32_t>(protocols, [&endpoint](const int32_t& existing) -> int32_t {
        return existing - endpoint.GetEndpoint()->protocol;
        }) > -1) {
        return true;
    } else {
        FIT_LOG_DEBUG("The endpoint is unavailable because the protocol is not supported. "
                      "[worker=%s, environment=%s, host=%s, port=%d, protocol=%d]",
            endpoint.GetWorker()->id.c_str(), endpoint.GetWorker()->environment.c_str(),
            endpoint.GetAddress()->host.c_str(), endpoint.GetEndpoint()->port, endpoint.GetEndpoint()->protocol);
        return false;
    }
}

bool DefaultFitableEndpointFilter::PredicateEnvironment(const FitableEndpoint& endpoint)
{
    const vector<string>& environments = spi_->GetEnvironmentChain();
    if (std::any_of(environments.begin(), environments.end(), [&endpoint](const string& environment) -> bool {
        return environment == endpoint.GetWorker()->environment;
        })) {
        return true;
    } else {
        FIT_LOG_DEBUG("The endpoint is unavailable because The environment is not supported. "
                      "[worker=%s, environment=%s, host=%s, port=%d, protocol=%d]",
            endpoint.GetWorker()->id.c_str(), endpoint.GetWorker()->environment.c_str(),
            endpoint.GetAddress()->host.c_str(), endpoint.GetEndpoint()->port, endpoint.GetEndpoint()->protocol);
        return false;
    }
}
