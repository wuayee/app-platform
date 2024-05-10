/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-05-07 17:07:20
 */

#include <genericable/com_huawei_fit_heartbeat_online_heartbeat/1.0.0/cplusplus/onlineHeartbeat.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "Core/ClientManager.hpp"

namespace fit {
namespace heartbeat {
static FitCode OnlineHeartbeatImpl(void* ctx, const fit::heartbeat::BeatInfo* beatInfo, bool** result)
{
    Fit::Heartbeat::BeatInfo rawBeatInfo{beatInfo->sceneType,
        beatInfo->aliveTime,
        beatInfo->interval,
        beatInfo->initDelay,
        beatInfo->callbackFitId};

    *result = Fit::Context::NewObj<bool>(ctx);
    **result = Fit::Heartbeat::Client::ClientManager::Instance()
        ->GetHeartbeatService().Online(rawBeatInfo) == FIT_ERR_SUCCESS;

    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(OnlineHeartbeatImpl)
        .SetGenericId(fit::heartbeat::onlineHeartbeat::GENERIC_ID)
        .SetFitableId("online_heartbeat_impl");
}
}  // namespace Heartbeat
}  // namespace Fit