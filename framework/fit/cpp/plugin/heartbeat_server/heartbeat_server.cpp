/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/31 16:16
 * Notes        :
 */


#include <genericable/com_huawei_fit_heartbeat_heartbeat/1.0.0/cplusplus/heartbeat.hpp>

#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/internal/registry/fit_registry_entity.h>
#include "core/fit_heartbeat_manager.h"

namespace Fit {
namespace Heartbeat {
namespace Server {
static FitCode HeartbeatImpl(void* ctx, const Fit::vector<fit::heartbeat::BeatInfo> *beatInfo,
    const fit::registry::Address *address, bool** ret)
{
    if (address->id.length() > MAX_WORKER_ID_LEN) {
        FIT_LOG_CORE("Worker id is longer than max len, id is %s, size is %lu.",
            address->id.c_str(), address->id.length());
        return FIT_ERR_PARAM;
    }
    fit_address addr;
    addr.ip = address->host;
    addr.port = address->port;
    addr.protocol = static_cast<fit_protocol_type>(address->protocol);
    for (auto item : address->formats) {
        addr.formats.push_back(static_cast<fit_format_type>(item));
    }
    addr.environment = address->environment;
    Fit::vector<Fit::Heartbeat::AddressBeatInfo> inner_address_beat_infos;
    for (const auto &item : *beatInfo) {
        Fit::Heartbeat::AddressBeatInfo info;
        info.addresses.push_back(addr);
        info.id = address->id;
        info.beat_info.sceneType = item.sceneType;
        info.beat_info.aliveTime = item.aliveTime;
        info.beat_info.interval = item.interval;
        info.beat_info.initDelay = item.initDelay;
        info.beat_info.callbackFitId = item.callbackFitId;
        inner_address_beat_infos.push_back(info);
    }

    for (auto &item : inner_address_beat_infos) {
        auto retCode = fit_heartbeat_manager::instance()->get_heartbeat_service().heartbeat(item);
        if (retCode != FIT_ERR_SUCCESS) {
            FIT_LOG_ERROR("Heartbeat has error. ret = %d, id = %s, scene = %s.", retCode, item.id.c_str(),
                item.beat_info.sceneType.c_str());
        }
    }

    *ret = Fit::Context::NewObj<bool>(ctx);
    **ret = true;
    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(HeartbeatImpl)
        .SetGenericId(::fit::heartbeat::heartbeat::GENERIC_ID)
        .SetFitableId("DBC9E2F7C0E443F1AC986BBC3D58C27B");
}

}  // namespace Server
}  // namespace Heartbeat
}  // namespace Fit