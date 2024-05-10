/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/31 20:02
 * Notes        :
 */
#include <genericable/com_huawei_fit_heartbeat_leave/1.0.0/cplusplus/leave.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "core/fit_heartbeat_manager.h"

namespace Fit {
namespace Heartbeat {
namespace Server {
static FitCode LeaveImpl(void *ctx,
    const Fit::vector<fit::heartbeat::BeatInfo> *beatInfo,
    const fit::registry::Address *address,
    bool **ret)
{
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
        info.beat_info.callbackFitId = item.callbackFitId;
        inner_address_beat_infos.push_back(info);
    }

    for (auto &item : inner_address_beat_infos) {
        auto retCode = fit_heartbeat_manager::instance()->get_heartbeat_service().leave(item);
        if (retCode != FIT_ERR_SUCCESS) {
            FIT_LOG_ERROR("Leave has error. ret = %d, id = %s, scene = %s.", retCode, item.id.c_str(),
                item.beat_info.sceneType.c_str());
        }
    }

    *ret = Fit::Context::NewObj<bool>(ctx);
    **ret = true;

    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(LeaveImpl)
        .SetGenericId(::fit::heartbeat::leave::GENERIC_ID)
        .SetFitableId("4C6DCDCCDEBC41F5B950D68C2BA50BE4");
}
}
}
}
