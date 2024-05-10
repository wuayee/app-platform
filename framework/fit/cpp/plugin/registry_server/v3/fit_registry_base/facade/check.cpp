/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        :
 */
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_check/1.0.0/cplusplus/check.hpp>
#include <v3/fit_registry_base/include/fit_base_strategy.h>
#include <v3/fit_registry_base/include/fit_strategy_factory.h>
#include <v3/fit_registry_base/include/fit_registry_context.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace {
/**
 * check the consistency of client and server data
 * @param checkElements
 */
FitCode Check(ContextObj ctx, const Fit::vector<::fit::hakuna::kernel::registry::shared::CheckElement> *checkElements)
{
    if (checkElements == nullptr) {
        FIT_LOG_ERROR("Element is nullptr.");
        return FIT_ERR_PARAM;
    }

    int32_t ret = FIT_OK;
    Fit::Registry::FitRegistryContext context {};
    for (const auto& element : *checkElements) {
        context.SetStrategy(Fit::Registry::FitStrategyFactory::GetStrategy(element.type));
        ret = context.DoCheck(element.kvs);
        if (ret != FIT_OK) {
            FIT_LOG_WARN("Not exist, type is : %s, result is %d.", element.type.c_str(), ret);
            return ret;
        }
    }
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::Check)
        .SetGenericId(fit::hakuna::kernel::registry::shared::check::GENERIC_ID)
        .SetFitableId("e3697a11fbd74d9f8af5d3b51a52ab5d");
}