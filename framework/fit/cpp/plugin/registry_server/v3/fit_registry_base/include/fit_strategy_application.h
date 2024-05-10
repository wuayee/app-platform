/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : application implementation of strategy.
 * Author       : w00561424
 * Date:        : 2023/10/17
 */
#ifndef FIT_STRATEGY_APPLICATION_H
#define FIT_STRATEGY_APPLICATION_H
#include <v3/fit_registry_base/include/fit_base_strategy.h>
#include <v3/fit_fitable_meta/include/fit_fitable_meta_service.h>
namespace Fit {
namespace Registry {
class FitStrategyApplication : public FitBaseStrategy {
public:
    Fit::string Type() override;
    FitStrategyApplication(FitFitableMetaServicePtr fitableMetaService);
    int32_t Check(const Fit::map<Fit::string, Fit::string>& kvs) override;
private:
    FitFitableMetaServicePtr fitableMetaService_ {};
};
}
}
#endif
