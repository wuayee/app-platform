/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance implementation of strategy.
 * Author       : w00561424
 * Date:        : 2023/10/17
 */
#ifndef FIT_STRATEGY_APPLICATION_INSTANCE_H
#define FIT_STRATEGY_APPLICATION_INSTANCE_H
#include <v3/fit_registry_base/include/fit_base_strategy.h>
#include <v3/fit_application_instance/include/fit_application_instance_service.h>
namespace Fit {
namespace Registry {
class FitStrategyApplicationInstance : public FitBaseStrategy {
public:
    Fit::string Type() override;
    FitStrategyApplicationInstance(FitApplicationInstanceServicePtr applicationInstanceService);
    int32_t Check(const Fit::map<Fit::string, Fit::string>& kvs) override;
private:
    FitApplicationInstanceServicePtr applicationInstanceService_ {};
};
}
}
#endif