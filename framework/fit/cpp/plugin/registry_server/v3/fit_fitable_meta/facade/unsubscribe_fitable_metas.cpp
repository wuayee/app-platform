/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:29:20
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_unsubscribe_fitable_metas/1.0.0/cplusplus/unsubscribe_fitable_metas.hpp>

namespace {
/**
 * 反订阅元数据信息
 *
 * @param genericableIds
 * @param workerId
 * @param callbackId
 */
FitCode unsubscribeFitableMetas(ContextObj ctx,
    const Fit::vector<Fit::string> *genericableIds,
    const Fit::string *environment,
    const Fit::string *workerId,
    const Fit::string *callbackId)
{
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::unsubscribeFitableMetas)
        .SetGenericId(fit::hakuna::kernel::registry::shared::unsubscribeFitableMetas::GENERIC_ID)
        .SetFitableId("3467234f328e4450b7c284c1dbc103be");
}