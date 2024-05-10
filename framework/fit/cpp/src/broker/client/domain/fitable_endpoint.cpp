/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable endpoint.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#include "fitable_endpoint.hpp"
#include "fit_discovery.h"
#include "load_balance_endpoint_supplier.hpp"

#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <genericable/com_huawei_fit_sdk_system_get_system_property/1.0.0/cplusplus/getSystemProperty.hpp>

using namespace Fit;

namespace {
class DefaultFitableEndpoint : public FitableEndpointBase {
public:
    DefaultFitableEndpoint(string workerId, string environment, string host, uint16_t port, int32_t protocol,
        vector<int32_t> formats, Context context);
    ~DefaultFitableEndpoint() override = default;
    const ::Fit::string& GetWorkerId() const override;
    const ::Fit::string& GetEnvironment() const override;
    const ::Fit::string& GetHost() const override;
    uint16_t GetPort() const override;
    int32_t GetProtocol() const override;
    const vector<int32_t>& GetFormats() const override;
    const Context& GetContext() const override;

private:
    string workerId_ {};
    string environment_ {};
    string host_ {};
    uint16_t port_;
    int32_t protocol_;
    vector<int32_t> formats_ {};
    // fitable端点关联的信息，包含所属的application等信息, 目前只用于远程调用时使用, 其它场景设置为默认空对象即可
    Context context_ {};
};

class DirectEndpointSupplier : public FitableEndpointSupplier {
public:
    explicit DirectEndpointSupplier(::Fit::FitableEndpointPtr endpoint);
    ~DirectEndpointSupplier() override = default;
    FitableEndpointPtr Get() const override;
private:
    FitableEndpointPtr endpoint_ {};
};

class LocalFitableEndpointSupplier : public FitableEndpointSupplier {
public:
    explicit LocalFitableEndpointSupplier(FitableCoordinatePtr coordinate, BrokerFitableDiscoveryPtr discovery);
    ~LocalFitableEndpointSupplier() override = default;
    FitableEndpointPtr Get() const override;
private:
    FitableCoordinatePtr coordinate_;
    BrokerFitableDiscoveryPtr discovery_ {nullptr};
};

class FunctionalFitableEndpointPredicate : public FitableEndpointPredicate {
public:
    explicit FunctionalFitableEndpointPredicate(std::function<bool(const FitableEndpoint&)> func);
    ~FunctionalFitableEndpointPredicate() override = default;
    bool Test(const FitableEndpoint& endpoint) const override;
private:
    std::function<bool(const FitableEndpoint&)> func_;
};

class FitableEndpointPredicateComposite : public FitableEndpointPredicate {
public:
    explicit FitableEndpointPredicateComposite(
        std::unique_ptr<FitableEndpointPredicate> predicate1,
        std::unique_ptr<FitableEndpointPredicate> predicate2);
    ~FitableEndpointPredicateComposite() override = default;
    bool Test(const FitableEndpoint& endpoint) const override;
private:
    std::unique_ptr<FitableEndpointPredicate> predicate1_ {nullptr};
    std::unique_ptr<FitableEndpointPredicate> predicate2_ {nullptr};
};

class FitableEndpointSupplierComposite : public FitableEndpointSupplier {
public:
    explicit FitableEndpointSupplierComposite(
        std::unique_ptr<FitableEndpointSupplier> supplier1,
        std::unique_ptr<FitableEndpointSupplier> supplier2);
    ~FitableEndpointSupplierComposite() override = default;
    FitableEndpointPtr Get() const override;
private:
    std::unique_ptr<FitableEndpointSupplier> supplier1_ {nullptr};
    std::unique_ptr<FitableEndpointSupplier> supplier2_ {nullptr};
};
}

FitableEndpointSupplierComposite::FitableEndpointSupplierComposite(
    std::unique_ptr<FitableEndpointSupplier> supplier1,
    std::unique_ptr<FitableEndpointSupplier> supplier2)
    : supplier1_(std::move(supplier1)), supplier2_(std::move(supplier2))
{
}

FitableEndpointPtr FitableEndpointSupplierComposite::Get() const
{
    FitableEndpointPtr endpoint = supplier1_->Get();
    if (endpoint == nullptr) {
        endpoint = supplier2_->Get();
    }
    return endpoint;
}

bool FitableEndpointBase::IsLocal() const
{
    auto localEndpoint = GetLocalEndpoint();
    if (localEndpoint == nullptr) {
        return true;
    }
    return GetWorkerId() == localEndpoint->GetWorkerId();
}

DefaultFitableEndpoint::DefaultFitableEndpoint(string workerId, string environment, string host, uint16_t port,
    int32_t protocol, vector<int32_t> formats, Context context)
    : workerId_(move(workerId)), environment_(move(environment)), host_(move(host)), port_(port), protocol_(protocol),
      formats_(move(formats)), context_(move(context))
{
}

const ::Fit::string& DefaultFitableEndpoint::GetWorkerId() const
{
    return workerId_;
}

const ::Fit::string& DefaultFitableEndpoint::GetEnvironment() const
{
    return environment_;
}

const ::Fit::string& DefaultFitableEndpoint::GetHost() const
{
    return host_;
}

uint16_t DefaultFitableEndpoint::GetPort() const
{
    return port_;
}

int32_t DefaultFitableEndpoint::GetProtocol() const
{
    return protocol_;
}

const vector<int32_t>& DefaultFitableEndpoint::GetFormats() const
{
    return formats_;
}

const DefaultFitableEndpoint::Context& DefaultFitableEndpoint::GetContext() const
{
    return context_;
}

DirectEndpointSupplier::DirectEndpointSupplier(FitableEndpointPtr endpoint) : endpoint_(std::move(endpoint))
{
}

FitableEndpointPtr DirectEndpointSupplier::Get() const
{
    return endpoint_;
}

LocalFitableEndpointSupplier::LocalFitableEndpointSupplier(FitableCoordinatePtr coordinate,
    BrokerFitableDiscoveryPtr discovery) : coordinate_(std::move(coordinate)), discovery_(std::move(discovery))
{
}

FitableEndpointPtr LocalFitableEndpointSupplier::Get() const
{
    Framework::Fitable id;
    id.genericId = coordinate_->GetGenericableId();
    id.genericVersion = coordinate_->GetGenericableVersion();
    id.fitableId = coordinate_->GetFitableId();
    id.fitableVersion = coordinate_->GetFitableVersion();
    if (discovery_->GetLocalFitable(id).empty()) {
        return nullptr;
    } else {
        return FitableEndpoint::GetLocalEndpoint();
    }
}

FitableEndpointBuilder FitableEndpoint::Custom()
{
    return {};
}

FitableEndpointPtr FitableEndpoint::GetLocalEndpoint()
{
    static FitableEndpointPtr local = nullptr;
    if (local == nullptr) {
        ::fit::sdk::system::getSystemProperty getSystemProperty;
        Fit::string* workerId = nullptr;
        string key = "fit_worker_id";
        auto ret = getSystemProperty(&key, &workerId);
        if (ret != FIT_ERR_SUCCESS || workerId == nullptr) {
            FIT_LOG_ERROR("Fail to get key = %s.", key.c_str());
            return local;
        }
        local = Custom()
            .SetHost("localhost")
            .SetPort(0)
            .SetWorkerId(*workerId)
            .SetProtocol(0)
            .SetFormats(vector<int32_t> {0})
            .Build();
    }
    return local;
}

FitableEndpointBuilder& FitableEndpointBuilder::SetWorkerId(string workerId)
{
    workerId_ = std::move(workerId);
    return *this;
}

FitableEndpointBuilder& FitableEndpointBuilder::SetEnvironment(string environment)
{
    environment_ = std::move(environment);
    return *this;
}

FitableEndpointBuilder& FitableEndpointBuilder::SetHost(string host)
{
    host_ = std::move(host);
    return *this;
}

FitableEndpointBuilder& FitableEndpointBuilder::SetPort(uint16_t port)
{
    port_ = port;
    return *this;
}

FitableEndpointBuilder& FitableEndpointBuilder::SetProtocol(int32_t protocol)
{
    protocol_ = protocol;
    return *this;
}

FitableEndpointBuilder& FitableEndpointBuilder::SetFormats(vector<int32_t> formats)
{
    formats_ = std::move(formats);
    return *this;
}
FitableEndpointBuilder& FitableEndpointBuilder::SetContext(FitableEndpoint::Context context)
{
    context_ = move(context);
    return *this;
}

FitableEndpointPtr FitableEndpointBuilder::Build()
{
    return std::make_shared<DefaultFitableEndpoint>(std::move(workerId_), std::move(environment_), std::move(host_),
        port_, protocol_, std::move(formats_), move(context_));
}

std::unique_ptr<FitableEndpointSupplier> FitableEndpointSupplier::CreateDirectSupplier(FitableEndpointPtr endpoint)
{
    return make_unique<DirectEndpointSupplier>(move(endpoint));
}

std::unique_ptr<FitableEndpointSupplier> FitableEndpointSupplier::CreateLoadBalanceSupplier(
    FitableCoordinatePtr coordinate, FitConfigPtr config, BrokerFitableDiscoveryPtr discovery)
{
    return CreateLoadBalanceSupplier(std::move(coordinate), std::move(config), std::move(discovery), nullptr);
}

std::unique_ptr<FitableEndpointSupplier> FitableEndpointSupplier::CreateLoadBalanceSupplier(
    FitableCoordinatePtr coordinate, FitConfigPtr config, BrokerFitableDiscoveryPtr discovery,
    std::unique_ptr<FitableEndpointPredicate> predicate)
{
    return make_unique<LoadBalanceEndpointSupplier>(move(coordinate),
        move(config), move(discovery), move(predicate));
}

std::unique_ptr<FitableEndpointSupplier> FitableEndpointSupplier::CreateLocalSupplier(FitableCoordinatePtr coordinate,
    ::Fit::BrokerFitableDiscoveryPtr discovery)
{
    return make_unique<LocalFitableEndpointSupplier>(move(coordinate), move(discovery));
}

std::unique_ptr<FitableEndpointSupplier> FitableEndpointSupplier::Combine(
    std::unique_ptr<FitableEndpointSupplier> supplier1,
    std::unique_ptr<FitableEndpointSupplier> supplier2)
{
    if (supplier1 == nullptr) {
        return supplier2;
    } else if (supplier2 == nullptr) {
        return supplier1;
    } else {
        return make_unique<FitableEndpointSupplierComposite>(move(supplier1), move(supplier2));
    }
}

FunctionalFitableEndpointPredicate::FunctionalFitableEndpointPredicate(
    std::function<bool(const FitableEndpoint&)> func) : func_(std::move(func))
{
}

bool FunctionalFitableEndpointPredicate::Test(const FitableEndpoint& endpoint) const
{
    return func_(endpoint);
}

std::unique_ptr<FitableEndpointPredicate> FitableEndpointPredicate::Create(
    std::function<bool(const FitableEndpoint&)> func)
{
    return make_unique<FunctionalFitableEndpointPredicate>(move(func));
}

FitableEndpointPredicateComposite::FitableEndpointPredicateComposite(
    std::unique_ptr<FitableEndpointPredicate> predicate1, std::unique_ptr<FitableEndpointPredicate> predicate2)
    : predicate1_(std::move(predicate1)), predicate2_(std::move(predicate2))
{
}

bool FitableEndpointPredicateComposite::Test(const FitableEndpoint& endpoint) const
{
    return predicate1_->Test(endpoint) && predicate2_->Test(endpoint);
}

std::unique_ptr<FitableEndpointPredicate> FitableEndpointPredicate::Combine(
    std::unique_ptr<FitableEndpointPredicate> predicate1, std::unique_ptr<FitableEndpointPredicate> predicate2)
{
    if (predicate1 == nullptr) {
        return predicate2;
    } else if (predicate2 == nullptr) {
        return predicate1;
    } else {
        return make_unique<FitableEndpointPredicateComposite>(move(predicate1), move(predicate2));
    }
}
