/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/22 19:50
 */

#include "fit/internal/heartbeat/fit_scene_subscriber.h"

#include <utility>
#include "fit/internal/fit_address_utils.h"
#include "genericable/com_huawei_fit_heartbeat_heartbeat_address_change/1.0.0/cplusplus/heartbeatAddressChange.hpp"
#include "fit/internal/fit_scope_guard.h"

#include <fit/fit_log.h>

fit_scene_subscriber::fit_scene_subscriber(const Fit::Heartbeat::SubscribeBeatInfo &info)
    : subscribe_info_(info) {}

void fit_scene_subscriber::notify(const Fit::Heartbeat::AddressStatusInfo &changed_address) const
{
    if (changed_address.addressBeatInfo.id == subscribe_info_.id) {
        FIT_LOG_DEBUG("Self no need notify, id = %s.", subscribe_info_.id.c_str());
        return;
    }
    FIT_LOG_DEBUG("Notify, id = %s, scene = %s, callback address = [%s], callback fitid = %s, status = %u.",
        changed_address.addressBeatInfo.id.c_str(),
        changed_address.addressBeatInfo.beat_info.sceneType.c_str(),
        fit_address_utils::convert_to_string(subscribe_info_.callbackAddress).c_str(),
        subscribe_info_.callbackFitId.c_str(), static_cast<uint32_t>(changed_address.status));

    fit::heartbeat::heartbeatAddressChange heartbeat_address_change;

    Fit::vector<fit::heartbeat::HeartbeatEvent> events;
    fit::heartbeat::HeartbeatEvent event {};
    event.sceneType = changed_address.addressBeatInfo.beat_info.sceneType;
    event.eventType = (changed_address.status == Fit::Heartbeat::HeartbeatStatus::ALIVE ? Fit::Heartbeat::ONLINE
                                                                                        : Fit::Heartbeat::OFFLINE);
    event.address = Fit::Context::NewObj<fit::registry::Address>(heartbeat_address_change.ctx_);
    if (!changed_address.addressBeatInfo.addresses.empty()) {
        event.address->host = changed_address.addressBeatInfo.addresses.front().ip;
        event.address->port = changed_address.addressBeatInfo.addresses.front().port;
        event.address->protocol = (int32_t)changed_address.addressBeatInfo.addresses.front().protocol;
        for (auto format : changed_address.addressBeatInfo.addresses.front().formats) {
            event.address->formats.push_back(format);
        }
        event.address->environment = changed_address.addressBeatInfo.addresses.front().environment;
    }
    event.address->id = changed_address.addressBeatInfo.id;
    events.push_back(event);

    heartbeat_address_change.Route([this](const Fit::RouteFilterParam &param) -> bool {
        return param.fitableId == subscribe_info_.callbackFitId;
    }).Get([this](const Fit::LBFilterParam &param) -> bool {
        return param.workerId == subscribe_info_.id;
    }).Exec(&events, [](const Fit::CallBackInfo *cb, bool **out) {
        if (cb->code != FIT_ERR_SUCCESS || !out) {
            FIT_LOG_ERROR("Notify heartbeat address change error. ret = %X.", cb->code);
        }
        return FIT_ERR_SUCCESS;
    });
}

bool fit_scene_subscriber::operator==(const fit_scene_subscriber &other) const
{
    return subscribe_info_.sceneType == other.subscribe_info_.sceneType &&
        Fit::fit_address_equal_to()(subscribe_info_.callbackAddress, other.subscribe_info_.callbackAddress) &&
        subscribe_info_.callbackFitId == other.subscribe_info_.callbackFitId;
}

size_t fit_scene_subscriber::hash_value() const
{
    return std::hash<Fit::string>()(subscribe_info_.sceneType) ^
        std::hash<Fit::string>()(subscribe_info_.callbackFitId) ^
        Fit::fit_address_hash()(subscribe_info_.callbackAddress);
}
