/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/22 20:52
 */

#ifndef FIT_HEARTBEAT_NOTIFY_SERVICE_H
#define FIT_HEARTBEAT_NOTIFY_SERVICE_H

#include "fit/internal/heartbeat/fit_scene_publisher.h"
#include "fit/internal/heartbeat/fit_scene_subscribe_repository.h"

#include <mutex>

class fit_heartbeat_notify_service {
public:
    explicit fit_heartbeat_notify_service(fit_scene_subscribe_repository_ptr repository);
    ~fit_heartbeat_notify_service() = default;

    int32_t subscribe(Fit::Heartbeat::SubscribeBeatInfo &subscribe_info);
    int32_t unsubscribe(Fit::Heartbeat::SubscribeBeatInfo &subscribe_info);

    int32_t notify(Fit::Heartbeat::AddressStatusInfo &changed_address);
    int32_t notify(const Fit::vector<Fit::Heartbeat::AddressStatusInfo>& changed_address);

private:
    fit_scene_subscribe_repository_ptr repository_ {};
};

using fit_heartbeat_notify_service_ptr = std::shared_ptr<fit_heartbeat_notify_service>;

#endif // FIT_HEARTBEAT_NOTIFY_SERVICE_H
