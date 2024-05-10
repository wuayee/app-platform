/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide context for fit registry strategy.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#ifndef FIT_REGISTRY_CONTEXT_H
#define FIT_REGISTRY_CONTEXT_H
#include <v3/fit_registry_base/include/fit_base_strategy.h>
#include <fit/stl/map.hpp>
namespace Fit {
namespace Registry {
class FitRegistryContext {
public:
    void SetStrategy(FitBaseStrategyPtr registryStrategy);
    int32_t DoCheck(const Fit::map<Fit::string, Fit::string>& kvs);
private:
    FitBaseStrategyPtr registryStrategy_ {nullptr};
};
}
}
#endif