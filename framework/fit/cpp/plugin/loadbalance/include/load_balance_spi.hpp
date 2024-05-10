/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides SPIs for load balance.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#ifndef FIT_LOAD_BALANCE_SPI_HPP
#define FIT_LOAD_BALANCE_SPI_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

#include <memory>

namespace Fit {
namespace LoadBalance {
/**
 * 为负载均衡模块提供南向接口。
 */
class LoadBalanceSpi {
public:
    LoadBalanceSpi() = default;
    virtual ~LoadBalanceSpi() = default;

    LoadBalanceSpi(const LoadBalanceSpi&) = delete;
    LoadBalanceSpi(LoadBalanceSpi&&) = delete;
    LoadBalanceSpi& operator=(const LoadBalanceSpi&) = delete;
    LoadBalanceSpi& operator=(LoadBalanceSpi&&) = delete;

    /**
     * 获取本地工作进程的唯一标识。
     *
     * @return 表示工作进程唯一标识的字符串的引用。
     */
    virtual const ::Fit::string& GetWorkerId() const = 0;

    /**
     * 获取本地工作进程的环境标识。
     *
     * @return 表示环境标识的字符串的引用。
     */
    virtual const ::Fit::string& GetEnvironment() const = 0;

    /**
     * 获取本地工作进程支持的传输协议。
     *
     * @return 表示输出协议的集合的引用。
     */
    virtual const ::Fit::vector<int32_t>& GetProtocols() const = 0;

    /**
     * 获取环境调用链。
     *
     * @return 表示环境调用链中包含环境的集合。
     */
    virtual const ::Fit::vector<::Fit::string>& GetEnvironmentChain() const = 0;
};

/**
 * 为负载均衡模块南向接口提供共享指针。
 */
using LoadBalanceSpiPtr = std::shared_ptr<LoadBalanceSpi>;
}
}

#endif // FIT_LOAD_BALANCE_SPI_HPP
