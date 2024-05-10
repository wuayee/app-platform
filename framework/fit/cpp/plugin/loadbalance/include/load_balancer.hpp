/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for load balancer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/27
 */

#ifndef FIT_LOAD_BALANCER_HPP
#define FIT_LOAD_BALANCER_HPP

#include "load_balance_spi.hpp"

#include <fit/external/util/context/context_api.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>

#include <memory>

namespace Fit {
namespace LoadBalance {
/**
 * 为服务调用提供负载均衡程序。
 */
class LoadBalancer {
public:
    LoadBalancer() = default;
    virtual ~LoadBalancer() = default;

    LoadBalancer(const LoadBalancer&) = delete;
    LoadBalancer(LoadBalancer&&) = delete;
    LoadBalancer& operator=(const LoadBalancer&) = delete;
    LoadBalancer& operator=(LoadBalancer&&) = delete;

    /**
     * 进行负载均衡计算。
     *
     * @return 若为 FIT_OK，则计算成功；否则计算失败。
     */
    virtual FitCode LoadBalance() = 0;

    /**
     * 获取计算得到的负载结果。
     *
     * @return 表示指向负载结果的指针。
     */
    virtual ::fit::hakuna::kernel::registry::shared::ApplicationInstance* GetResult() = 0;

    /**
     * 使用待调用的服务实现、源应用程序实例信息、目标应用程序实例信息的集合创建负载均衡程序的新实例。
     *
     * @param context 表示进行负载的上下文。
     * @param fitable 表示待调用的服务实现。
     * @param targets 表示可选的目标应用程序实例。
     * @return 表示负载均衡程序的唯一指针。
     */
    static std::unique_ptr<LoadBalancer> Create(ContextObj context,
        const ::fit::hakuna::kernel::shared::Fitable& fitable,
        const ::Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>& targets);
};
}
}

#endif // FIT_LOAD_BALANCER_HPP
