/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:28:52
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_subscribe_fitable_metas/1.0.0/cplusplus/subscribe_fitable_metas.hpp>

namespace {
/**
 * 根据genericableId订阅元数据
 *
 * @param genericableIds
 * @param workerId
 * @param callbackId
 * @return FitableMeta
 */
FitCode subscribeFitableMetas(ContextObj ctx,
    const Fit::vector<Fit::string> *genericableIds,
    const Fit::string *environment,
    const Fit::string *workerId,
    const Fit::string *callbackId,
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> **result)
{
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::subscribeFitableMetas)
        .SetGenericId(fit::hakuna::kernel::registry::shared::subscribeFitableMetas::GENERIC_ID)
        .SetFitableId("d1a2a1260a714f738e6a42b85945ef13");
}