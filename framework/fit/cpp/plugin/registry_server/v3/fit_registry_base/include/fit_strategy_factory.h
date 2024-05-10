/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide strategy factory.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#ifndef FIT_STRATEGY_FACTORY_H
#define FIT_STRATEGY_FACTORY_H
#include <v3/fit_registry_base/include/fit_base_strategy.h>
#include <fit/stl/unordered_map.hpp>
namespace Fit {
namespace Registry {
class FitStrategyFactory {
public:
    static FitBaseStrategyPtr CreateApplicationStrategy();
    static FitBaseStrategyPtr CreateApplicationInstanceStrategy();
    static Fit::unordered_map<Fit::string, FitBaseStrategyPtr> Init();
    static FitBaseStrategyPtr GetStrategy(const Fit::string& type);
};
}
}
#endif