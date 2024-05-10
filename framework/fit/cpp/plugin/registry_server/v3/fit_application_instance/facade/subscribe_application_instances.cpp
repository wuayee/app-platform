/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:04:49
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_subscribe_application_instances/1.0.0/cplusplus/subscribe_application_instances.hpp>

namespace {
/**
 * 订阅应用实例信息
 * @param applications
 * @param workerId
 * @param callbackId
 * @return
 */
FitCode SubscribeApplicationInstances(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *applications,
    const Fit::string *workerId,
    const Fit::string *callbackId,
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **result)
{
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::SubscribeApplicationInstances)
        .SetGenericId(fit::hakuna::kernel::registry::server::subscribeApplicationInstances::GENERIC_ID)
        .SetFitableId("60bef964937e4e43b40b959dfea4c7dd");
}