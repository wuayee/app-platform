/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Heartbeat
 * Author       : s00558940
 * Create       : 2020/9/22 20:52
 */

#include "fit_heartbeat_service.h"
#include "fit_code.h"

#include <utility>
#include <algorithm>
#include <chrono>

#include <fit/fit_log.h>

fit_heartbeat_service::fit_heartbeat_service(fit_heartbeat_notify_service_ptr notifier,
    fit_heartbeat_repository_ptr repository, uint64_t startup_time)
    : notifier_(std::move(notifier)),
      repository_(std::move(repository)), startup_time_(startup_time)
{
}

uint64_t fit_heartbeat_service::get_current_time_ms()
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::time_point_cast<std::chrono::milliseconds>(std::chrono::system_clock::now()).time_since_epoch())
        .count();
}

int32_t fit_heartbeat_service::heartbeat(const Fit::Heartbeat::AddressBeatInfo &beat_info)
{
    if (!repository_) {
        return FIT_ERR_FAIL;
    }

    uint64_t query_before_time = get_current_time_ms();

    Fit::Heartbeat::AddressStatusInfo address_status;
    auto ret = repository_->query_beat(beat_info, address_status);
    // 使用数据库时间，增强多实例
    uint64_t current_time_ms = 0;
    if (repository_->get_current_time_ms(current_time_ms) != FIT_OK) {
        FIT_LOG_ERROR("Can not get the repo current timestamp.");
        return FIT_ERR_FAIL;
    }
    auto query_take_time = get_current_time_ms() - query_before_time;
    if (ret != FIT_ERR_SUCCESS) {
        address_status.addressBeatInfo = beat_info;
        address_status.start_time = current_time_ms;
        address_status.last_heartbeat_time = current_time_ms;
        address_status.status = Fit::Heartbeat::HeartbeatStatus::ALIVE;
        address_status.expired_time = current_time_ms + beat_info.beat_info.aliveTime + beat_info.beat_info.initDelay;

        notify(address_status);
        FIT_LOG_INFO("Add heartbeat. id = %s, scene = %s. callback fitid = %s, aliveTime=%ld, interval = %ld.",
            beat_info.id.c_str(), beat_info.beat_info.sceneType.c_str(), beat_info.beat_info.callbackFitId.c_str(),
            beat_info.beat_info.aliveTime, beat_info.beat_info.interval);
        return repository_->add_beat(address_status);
    } else {
        auto interval = current_time_ms - address_status.last_heartbeat_time;
        if (interval > (uint64_t)(beat_info.beat_info.aliveTime + 1) / 2) {
            FIT_LOG_WARN("The heartbeat is unstable. (id=%s, scene=%s. aliveTime=%ld, interval=%ld, "
                         "realInterval=%lu, queryTakeTime=%lu, lastTime=%lu, currentTime=%lu).",
                beat_info.id.c_str(), beat_info.beat_info.sceneType.c_str(), beat_info.beat_info.aliveTime,
                beat_info.beat_info.interval, interval, query_take_time, address_status.last_heartbeat_time,
                current_time_ms);
        } else if (interval < 1000) {
            FIT_LOG_WARN("The heartbeat is frequently. (id=%s, scene=%s. aliveTime=%ld, interval=%ld, "
                         "realInterval=%lu, queryTakeTime=%lu, lastTime=%lu, currentTime=%lu).",
                beat_info.id.c_str(), beat_info.beat_info.sceneType.c_str(), beat_info.beat_info.aliveTime,
                beat_info.beat_info.interval, interval, query_take_time, address_status.last_heartbeat_time,
                current_time_ms);
        }
        address_status.expired_time = current_time_ms + beat_info.beat_info.aliveTime;
        address_status.last_heartbeat_time = current_time_ms;
        address_status.status = Fit::Heartbeat::HeartbeatStatus::ALIVE;
        address_status.addressBeatInfo = beat_info;
        FIT_LOG_DEBUG("Modify heartbeat. id = %s, scene = %s. callback fitid = %s, aliveTime=%ld, interval = %ld.",
            beat_info.id.c_str(), beat_info.beat_info.sceneType.c_str(), beat_info.beat_info.callbackFitId.c_str(),
            beat_info.beat_info.aliveTime, beat_info.beat_info.interval);
        return repository_->modify_beat(address_status);
    }
}

int32_t fit_heartbeat_service::leave(const Fit::Heartbeat::AddressBeatInfo &beat_info)
{
    FIT_LOG_INFO("Heartbeat leave. id = %s, scene = %s. callback fitid = %s.", beat_info.id.c_str(),
        beat_info.beat_info.sceneType.c_str(),
        beat_info.beat_info.callbackFitId.c_str());
    if (!repository_) {
        return FIT_ERR_FAIL;
    }

    Fit::Heartbeat::AddressStatusInfo address_status;
    address_status.addressBeatInfo = beat_info;
    address_status.status = Fit::Heartbeat::HeartbeatStatus::LEAVE;

    notify(address_status);

    return repository_->remove_beat(beat_info);
}

void fit_heartbeat_service::check()
{
    Fit::vector<Fit::Heartbeat::AddressStatusInfo> removed_heartbeat;
    remove_expired_heartbeat(removed_heartbeat);

    if (notifier_) {
        notifier_->notify(removed_heartbeat);
    }
}

Fit::Heartbeat::AddressStatusSet fit_heartbeat_service::query_all_beat()
{
    return repository_->query_all_beat();
}

bool fit_heartbeat_service::is_alive_in_startup(
    const Fit::Heartbeat::AddressStatusInfo& target, uint64_t check_time) const
{
    // no longer checks after 60 seconds of startup or startup time is invalid
    uint64_t ignore_ms = 60000;
    if (startup_time_ > check_time || check_time - startup_time_ > ignore_ms) {
        return false;
    }
    return (startup_time_ + target.addressBeatInfo.beat_info.aliveTime > check_time);
}

void fit_heartbeat_service::remove_expired_heartbeat(Fit::Heartbeat::AddressStatusSet& removed_heartbeat)
{
    if (!repository_) {
        return;
    }

    auto query_before_time = get_current_time_ms();
    auto all_beats = repository_->query_all_beat();
    // 使用数据库时间，增强多实例
    uint64_t check_time = 0;
    if (repository_->get_current_time_ms(check_time) != FIT_OK) {
        FIT_LOG_ERROR("Can not get the repo current timestamp.");
        return;
    }
    auto take_time = get_current_time_ms() - query_before_time;
    for (auto &beat : all_beats) {
        if (beat.expired_time + take_time < check_time && !is_alive_in_startup(beat, check_time)) {
            FIT_LOG_WARN("Heartbeat expired, id = %s, scene = %s, take = %lu(ms), expired_time=%lu, start_time=%lu, "
                         "last_heartbeat_time=%lu.",
                beat.addressBeatInfo.id.c_str(), beat.addressBeatInfo.beat_info.sceneType.c_str(), take_time,
                beat.expired_time, beat.start_time, beat.last_heartbeat_time);

            beat.status = Fit::Heartbeat::HeartbeatStatus::LEAVE;
            removed_heartbeat.push_back(beat);
            repository_->remove_beat(beat.addressBeatInfo);
        } else {
            FIT_LOG_DEBUG("Heartbeat is healthy, id = %s, scene = %s.", beat.addressBeatInfo.id.c_str(),
                beat.addressBeatInfo.beat_info.sceneType.c_str());
        }
    }
}

void fit_heartbeat_service::notify(Fit::Heartbeat::AddressStatusInfo &changed_address)
{
    if (notifier_) {
        notifier_->notify(changed_address);
    }
}

FitCode fit_heartbeat_service::get_beat(
    const Fit::string& id, const Fit::string& scene, Fit::Heartbeat::AddressStatusInfo& result) const
{
    Fit::Heartbeat::AddressBeatInfo param {};
    param.id = id;
    param.beat_info.sceneType = scene;
    auto ret = repository_->query_beat(param, result);
    if (ret != FIT_OK) {
        FIT_LOG_WARN("Failed to get beat. (ret=%x, id=%s, scene=%s).", ret, id.c_str(), scene.c_str());
    }
    return ret;
}
