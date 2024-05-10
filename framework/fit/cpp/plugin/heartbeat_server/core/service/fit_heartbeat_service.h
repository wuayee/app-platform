/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/22 20:52
 */

#ifndef FIT_HEARTBEAT_SERVICE_H
#define FIT_HEARTBEAT_SERVICE_H

#include <map>

#include "fit/internal/heartbeat/fit_heartbeat_repository.h"
#include "fit_heartbeat_notify_service.h"

class fit_heartbeat_service final {
public:
    explicit fit_heartbeat_service(fit_heartbeat_notify_service_ptr notifier,
        fit_heartbeat_repository_ptr repository, uint64_t startup_time);
    ~fit_heartbeat_service() = default;

    int32_t heartbeat(const Fit::Heartbeat::AddressBeatInfo &beat_info);
    int32_t leave(const Fit::Heartbeat::AddressBeatInfo &beat_info);
    void check();
    Fit::Heartbeat::AddressStatusSet query_all_beat();
    FitCode get_beat(const Fit::string& id, const Fit::string& scene, Fit::Heartbeat::AddressStatusInfo& result) const;
    static uint64_t get_current_time_ms();

protected:
    void notify(Fit::Heartbeat::AddressStatusInfo &changed_address);
    void remove_expired_heartbeat(Fit::vector<Fit::Heartbeat::AddressStatusInfo>& removed_heartbeat);
    bool is_alive_in_startup(const Fit::Heartbeat::AddressStatusInfo& target, uint64_t check_time) const;

private:
    fit_heartbeat_notify_service_ptr notifier_ {};
    fit_heartbeat_repository_ptr repository_ {};
    uint64_t startup_time_ {};
};

#endif // FIT_HEARTBEAT_SERVICE_H
