/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides fitable suite for load balance.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/27
 */

#include <load_balancer.hpp>
#include <fitable_endpoint_filter.hpp>
#include <support/default_load_balance_spi.hpp>

#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_load_balance_v2/1.0.0/cplusplus/loadBalanceV2.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_filter_v3/1.0.0/cplusplus/filterV3.hpp>

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/fit_log.h>

namespace Fit {
namespace LoadBalance {
LoadBalanceSpiPtr spi;

FitCode Start(::Fit::Framework::PluginContext* context)
{
    spi = std::make_shared<DefaultLoadBalanceSpi>();
    FIT_LOG_INFO("Load balance started.");
    return FIT_OK;
}

FitCode Stop()
{
    spi = nullptr;
    return FIT_OK;
}

/**
 * 过滤从注册中心获取到的目标应用实例列表。
 *
 * @param fitable 表示fitable的信息。
 * @param instances 表示待过滤的应用实例列表。
 * @return 过滤后的应用实例。
 */
FitCode Filter(ContextObj ctx,
    const ::fit::hakuna::kernel::shared::Fitable* fitable,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* instances,
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>** result)
{
    if (fitable == nullptr) {
        FIT_LOG_ERROR("The fitable to filter endpoints cannot be nullptr.");
        return FIT_ERR_FAIL;
    } else if (instances == nullptr) {
        FIT_LOG_ERROR("The application instances to filter cannot be nullptr.");
        return FIT_ERR_FAIL;
    } else if (instances->empty()) {
        FIT_LOG_DEBUG("The application instances to filter is empty. Skip to filter.");
        *result = Fit::Context::NewObj<Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>>(ctx);
        return FIT_OK;
    } else {
        auto filter = FitableEndpointFilter::Create(spi, ctx, *instances);
        auto ret = filter->Filter();
        *result = filter->GetResult();
        return ret;
    }
}

/**
 * 负载均衡接口。
 *
 * @param fitable 表示fitable信息。
 * @param sourceInstance 表示调用者的应用实例。
 * @param targetInstances 表示被调用方的应用实例列表。
 * @return 选中的应用实例。
 */
FitCode LoadBalance(ContextObj ctx,
    const ::fit::hakuna::kernel::shared::Fitable* fitable,
    const ::fit::hakuna::kernel::registry::shared::ApplicationInstance* sourceInstance,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* targetInstances,
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance** result)
{
    if (fitable == nullptr) {
        FIT_LOG_ERROR("The fitable to load balance cannot be nullptr.");
        return FIT_ERR_FAIL;
    } else if (targetInstances == nullptr) {
        FIT_LOG_ERROR("The target application instances to load balance cannot be nullptr.");
        return FIT_ERR_FAIL;
    } else {
        *result = ::Fit::Context::NewObj<::fit::hakuna::kernel::registry::shared::ApplicationInstance>(ctx);
        if (*result == nullptr) {
            FIT_LOG_ERROR("Failed to allocate memory for load balance result.");
            return FIT_ERR_FAIL;
        } else {
            auto balancer = LoadBalancer::Create(ctx, *fitable, *targetInstances);
            FitCode ret = balancer->LoadBalance();
            *result = balancer->GetResult();
            return ret;
        }
    }
}
}
}

namespace {
FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::Fit::LoadBalance::Filter)
        .SetGenericId(fit::hakuna::kernel::loadbalance::filterV3::GENERIC_ID)
        .SetFitableId("94bb2f1b39514bcb8512eb6d7f020803");
    ::Fit::Framework::Annotation::Fitable(::Fit::LoadBalance::LoadBalance)
        .SetGenericId(fit::hakuna::kernel::loadbalance::loadBalanceV2::GENERIC_ID)
        .SetFitableId("dad46ea5362647819137135f301360ff");

    ::Fit::Framework::PluginActivatorRegistrar()
        .SetStart(::Fit::LoadBalance::Start)
        .SetStop(::Fit::LoadBalance::Stop);
}
} // LCOV_EXCL_LINE