/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:04:49
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unsubscribe_application_instances/1.0.0/cplusplus/unsubscribe_application_instances.hpp>

namespace {
/**
 * 反订阅某个worker上的应用
 * @param applications
 * @param workerId
 * @param callbackId
 */
FitCode UnsubscribeApplicationInstances(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *applications,
    const Fit::string *workerId,
    const Fit::string *callbackId)
{
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::UnsubscribeApplicationInstances)
        .SetGenericId(fit::hakuna::kernel::registry::server::unsubscribeApplicationInstances::GENERIC_ID)
        .SetFitableId("0ff8d6a37189498bafe675de3f4ed085");
}
}