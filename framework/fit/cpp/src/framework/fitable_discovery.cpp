/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/14
 * Notes:       :
 */

#include <fit/internal/framework/fitable_discovery.hpp>
#include "fitable_discovery_default_impl.hpp"

namespace Fit {
namespace Framework {
unique_ptr<FitableDiscovery> CreateFitableDiscovery()
{
    return make_unique<FitableDiscoveryDefaultImpl>();
}
}
} // LCOV_EXCL_LINE