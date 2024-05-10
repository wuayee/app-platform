/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides filter for fitable endpoints.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#ifndef FIT_LOAD_BALANCE_FITABLE_ENDPOINT_FILTER_HPP
#define FIT_LOAD_BALANCE_FITABLE_ENDPOINT_FILTER_HPP

#include "load_balance_spi.hpp"

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>

#include <fit/fit_code.h>
#include <fit/external/util/context/context_api.hpp>

#include <memory>

namespace Fit {
namespace LoadBalance {
class FitableEndpointFilter {
public:
    FitableEndpointFilter() = default;
    virtual ~FitableEndpointFilter() = default;

    FitableEndpointFilter(const FitableEndpointFilter&) = delete;
    FitableEndpointFilter(FitableEndpointFilter&&) = delete;
    FitableEndpointFilter& operator=(const FitableEndpointFilter&) = delete;
    FitableEndpointFilter& operator=(FitableEndpointFilter&&) = delete;

    virtual FitCode Filter() = 0;

    virtual ::Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* GetResult() = 0;

    static std::unique_ptr<FitableEndpointFilter> Create(LoadBalanceSpiPtr spi, ContextObj context,
        const ::Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>& instances);
};
}
}

#endif // FIT_LOAD_BALANCE_FITABLE_ENDPOINT_FILTER_HPP
