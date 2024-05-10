/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/22 20:52
 */

#include "fit_heartbeat_notify_service.h"

#include <utility>

fit_heartbeat_notify_service::fit_heartbeat_notify_service(fit_scene_subscribe_repository_ptr repository)
    : repository_(std::move(repository))
{
}

int32_t fit_heartbeat_notify_service::subscribe(Fit::Heartbeat::SubscribeBeatInfo &subscribe_info)
{
    fit_scene_publisher publisher(subscribe_info.sceneType, repository_);

    return publisher.add_subscriber(fit_scene_subscriber(subscribe_info));
}

int32_t fit_heartbeat_notify_service::unsubscribe(Fit::Heartbeat::SubscribeBeatInfo &subscribe_info)
{
    fit_scene_publisher publisher(subscribe_info.sceneType, repository_);

    return publisher.remove_subscriber(fit_scene_subscriber(subscribe_info));
}

int32_t fit_heartbeat_notify_service::notify(Fit::Heartbeat::AddressStatusInfo &changed_address)
{
    fit_scene_publisher publisher(changed_address.addressBeatInfo.beat_info.sceneType, repository_);

    // remove subscribe with id, when it is offline
    if (changed_address.status == Fit::Heartbeat::HeartbeatStatus::LEAVE) {
        publisher.remove_subscriber(changed_address.addressBeatInfo.id);
    }

    publisher.notify(changed_address);

    return FIT_ERR_SUCCESS;
}

int32_t fit_heartbeat_notify_service::notify(const Fit::vector<Fit::Heartbeat::AddressStatusInfo>& changed_address)
{
    Fit::map<Fit::string, fit_scene_publisher> publishers;
    for (auto& item : changed_address) {
        auto iter = publishers.find(item.addressBeatInfo.beat_info.sceneType);
        if (iter == publishers.end()) {
            iter = publishers
                .emplace(item.addressBeatInfo.beat_info.sceneType,
                fit_scene_publisher(item.addressBeatInfo.beat_info.sceneType, repository_))
                .first;
        }
        if (item.status == Fit::Heartbeat::HeartbeatStatus::LEAVE) {
            iter->second.remove_subscriber(item.addressBeatInfo.id);
        }
    }
    for (auto& item : changed_address) {
        publishers.at(item.addressBeatInfo.beat_info.sceneType).notify(item);
    }
    return FIT_OK;
}
