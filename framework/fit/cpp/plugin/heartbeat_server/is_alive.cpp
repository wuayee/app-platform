/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2024-01-08
 * Notes:       :
 */


#include <genericable/com_huawei_fit_heartbeat_is_alive/1.0.0/cplusplus/is_alive.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/fit_log.h>
#include "core/fit_heartbeat_manager.h"
#include "fit_code.h"

namespace {
using namespace fit::heartbeat;
static FitCode IsAliveImpl(ContextObj ctx, const Fit::string* id, const Fit::string* scene, bool** result)
{
    if (id == nullptr || scene == nullptr) {
        FIT_LOG_ERROR("Param is null.");
        return FIT_ERR_PARAM;
    }
    *result = Fit::Context::NewObj<bool>(ctx);
    if (*result == nullptr) {
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    Fit::Heartbeat::AddressStatusInfo status {};
    auto ret = fit_heartbeat_manager::instance()->get_heartbeat_service().get_beat(*id, *scene, status);
    if (ret != FIT_OK) {
        **result = false;
        return FIT_OK;
    }
    **result = status.status == Fit::Heartbeat::HeartbeatStatus::ALIVE;

    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(IsAliveImpl)
        .SetGenericId(fit::heartbeat::IsAlive::GENERIC_ID)
        .SetFitableId("default");
}
}
