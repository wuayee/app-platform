/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-05-07 15:17:41
 */
#include <genericable/com_huawei_fit_heartbeat_offline_heartbeat/1.0.0/cplusplus/offlineHeartbeat.hpp>
#include <fit/fit_code.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/util/registration.hpp>
#include <fit/internal/heartbeat/heartbeat_entity.hpp>
#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include "Core/ClientManager.hpp"
namespace fit {
namespace heartbeat {

static FitCode OfflineHeartbeatImpl(void* ctx, const fit::heartbeat::BeatInfo* beatInfo, bool** ret)
{
    Fit::Heartbeat::BeatInfo rawBeatInfo{beatInfo->sceneType,
        beatInfo->interval,
        beatInfo->aliveTime,
        beatInfo->initDelay,
        beatInfo->callbackFitId};

    *ret = Fit::Context::NewObj<bool>(ctx);
    **ret = Fit::Heartbeat::Client::ClientManager::Instance()
        ->GetHeartbeatService().Offline(rawBeatInfo) == FIT_ERR_SUCCESS;
    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(OfflineHeartbeatImpl)
        .SetGenericId(fit::heartbeat::offlineHeartbeat::GENERIC_ID)
        .SetFitableId("offline_heartbeat_impl");
}
}  // namespace Heartbeat
}  // namespace Fit