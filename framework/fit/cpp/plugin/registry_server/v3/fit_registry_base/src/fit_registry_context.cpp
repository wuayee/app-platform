/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide context for fit registry strategy.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#include <v3/fit_registry_base/include/fit_registry_context.h>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
namespace Fit {
namespace Registry {
void FitRegistryContext::SetStrategy(FitBaseStrategyPtr registryStrategy)
{
    registryStrategy_ = std::move(registryStrategy);
}

int32_t FitRegistryContext::DoCheck(const Fit::map<Fit::string, Fit::string>& kvs)
{
    if (registryStrategy_ == nullptr) {
        FIT_LOG_ERROR("Strategy is null.");
        return FIT_ERR_FAIL;
    }
    return registryStrategy_->Check(kvs);
}
}
}