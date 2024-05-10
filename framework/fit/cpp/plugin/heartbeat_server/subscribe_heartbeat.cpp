/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/5/31 19:48
 * Notes        :
 */
#include <genericable/com_huawei_fit_heartbeat_subscribe_heartbeat/1.0.0/cplusplus/subscribeHeartbeat.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/fit_log.h>
#include <fit/internal/fit_address_utils.h>
#include <core/fit_heartbeat_manager.h>
#include <fit/internal/registry/fit_registry_entity.h>
namespace fit {
namespace heartbeat {
static FitCode SubscribeHeartbeatImpl(void *ctx,
    const Fit::vector<fit::heartbeat::SubscribeBeatInfo> *beatInfo,
    const fit::registry::Address *address,
    bool **ret)
{
    if (address->id.length() > MAX_WORKER_ID_LEN) {
        FIT_LOG_CORE("Worker id is longer than max len, id is %s, size is %lu.",
            address->id.c_str(), address->id.length());
        return FIT_ERR_PARAM;
    }

    Fit::fit_address addr;
    addr.ip = address->host;
    addr.port = address->port;
    addr.protocol = static_cast<Fit::fit_protocol_type>(address->protocol);
    for (auto item : address->formats) {
        addr.formats.push_back(static_cast<Fit::fit_format_type>(item));
    }
    addr.environment = address->environment;

    Fit::vector<Fit::Heartbeat::SubscribeBeatInfo> innerAddressBeatInfos;
    for (const auto &item : *beatInfo) {
        Fit::Heartbeat::SubscribeBeatInfo info;

        info.sceneType = item.sceneType;
        info.callbackFitId = item.callbackFitId;
        info.id = address->id;
        info.callbackAddress = addr;
        innerAddressBeatInfos.push_back(info);
    }

    for (auto &item : innerAddressBeatInfos) {
        auto retCode = fit_heartbeat_manager::instance()->get_notify_service().subscribe(item);
        if (retCode != FIT_ERR_SUCCESS) {
            FIT_LOG_ERROR(
                "Subscribe has error. ret = %d, id = %s, scene = %s, callback fitid = %s, callback address = [%s].",
                retCode, item.id.c_str(), item.sceneType.c_str(), item.callbackFitId.c_str(),
                fit_address_utils::convert_to_string(item.callbackAddress).c_str());
        } else {
            FIT_LOG_INFO("Subscribe success. id = %s, scene = %s, callback fitid = %s, callback address = [%s].",
                item.id.c_str(), item.sceneType.c_str(), item.callbackFitId.c_str(),
                fit_address_utils::convert_to_string(item.callbackAddress).c_str());
        }
    }

    *ret = Fit::Context::NewObj<bool>(ctx);
    **ret = true;

    return FIT_ERR_SUCCESS;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(SubscribeHeartbeatImpl)
        .SetGenericId(fit::heartbeat::subscribeHeartbeat::GENERIC_ID)
        .SetFitableId("subscribe_heartbeat_cpp");
}
}
}
