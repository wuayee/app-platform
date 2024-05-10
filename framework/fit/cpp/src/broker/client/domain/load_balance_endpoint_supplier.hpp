/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides endpoint supplier based on load balance.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#ifndef FIT_LOAD_BALANCE_ENDPOINT_SUPPLIER_HPP
#define FIT_LOAD_BALANCE_ENDPOINT_SUPPLIER_HPP

#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_get_fitable_addresses/1.0.0/cplusplus/getFitableAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_filter_v3/1.0.0/cplusplus/filterV3.hpp>
#include "fitable_endpoint.hpp"
#include "fitable_coordinate.hpp"
#include "broker/client/application/gateway/fit_config.h"
#include "broker/client/application/gateway/fit_discovery.h"
namespace Fit {
/**
 * 为服务终结点的供应程序提供基于负载均衡的实现。
 */
class LoadBalanceEndpointSupplier : public FitableEndpointSupplier {
public:
    explicit LoadBalanceEndpointSupplier(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::FitConfigPtr config,
        Fit::BrokerFitableDiscoveryPtr discovery,
        std::unique_ptr<FitableEndpointPredicate> predicate);
    ~LoadBalanceEndpointSupplier() override = default;
    ::Fit::FitableEndpointPtr Get() const override;
private:
    bool Filter(::fit::hakuna::kernel::loadbalance::filterV3 &filter,
        const ::fit::hakuna::kernel::shared::Fitable& fitable,
        ::fit::hakuna::kernel::registry::shared::FitableInstance* fitableInstance,
        vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>*& filtered) const;
private:
    ::Fit::FitableCoordinatePtr coordinate_;
    ::Fit::FitConfigPtr config_ {nullptr};
    Fit::BrokerFitableDiscoveryPtr discovery_ {};
    std::unique_ptr<FitableEndpointPredicate> predicate_ {nullptr};
};
}

#endif // FIT_LOAD_BALANCE_ENDPOINT_SUPPLIER_HPP
