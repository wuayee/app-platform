/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide strategy factory.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#include <v3/fit_registry_base/include/fit_strategy_factory.h>
#include <v3/fit_registry_base/include/fit_strategy_application.h>
#include <v3/fit_registry_base/include/fit_strategy_application_instance.h>
#include <core/fit_registry_mgr.h>
#include <fit/stl/unordered_map.hpp>
namespace Fit {
namespace Registry {
FitBaseStrategyPtr FitStrategyFactory::CreateApplicationStrategy()
{
    return Fit::make_shared<FitStrategyApplication>(
        Fit::Registry::fit_registry_mgr::instance()->get_fitable_meta_service());
}
FitBaseStrategyPtr FitStrategyFactory::CreateApplicationInstanceStrategy()
{
    return Fit::make_shared<FitStrategyApplicationInstance>(
        Fit::Registry::fit_registry_mgr::instance()->get_application_instance_service());
}

Fit::unordered_map<Fit::string, FitBaseStrategyPtr> FitStrategyFactory::Init()
{
    FitBaseStrategyPtr fitableMetaStrategy = CreateApplicationStrategy();
    FitBaseStrategyPtr applicationInstanceStrategy = CreateApplicationInstanceStrategy();
    Fit::unordered_map<Fit::string, FitBaseStrategyPtr> strategySet {
        { fitableMetaStrategy->Type(), fitableMetaStrategy },
        { applicationInstanceStrategy->Type(), applicationInstanceStrategy }
    };
    return strategySet;
}

FitBaseStrategyPtr FitStrategyFactory::GetStrategy(const Fit::string& type)
{
    static Fit::unordered_map<Fit::string, FitBaseStrategyPtr> strategySet = Init();
    auto it = strategySet.find(type);
    if (it == strategySet.end()) {
        return nullptr;
    }
    return it->second;
}
}
}