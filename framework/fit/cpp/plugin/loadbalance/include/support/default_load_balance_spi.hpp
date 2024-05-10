/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides default implementation for SPIs of load balance.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#ifndef FIT_LOAD_BALANCE_DEFAULT_SPI_HPP
#define FIT_LOAD_BALANCE_DEFAULT_SPI_HPP

#include "../load_balance_spi.hpp"

namespace Fit {
namespace LoadBalance {
/**
 * 为 LoadBalanceSpi 提供默认实现。
 */
class DefaultLoadBalanceSpi : public virtual LoadBalanceSpi {
public:
    DefaultLoadBalanceSpi() = default;
    ~DefaultLoadBalanceSpi() override = default;
    const ::Fit::string& GetWorkerId() const override;
    const ::Fit::string& GetEnvironment() const override;
    const ::Fit::vector<int32_t>& GetProtocols() const override;
    const ::Fit::vector<::Fit::string>& GetEnvironmentChain() const override;
};
}
}

#endif // FIT_LOAD_BALANCE_DEFAULT_SPI_HPP
