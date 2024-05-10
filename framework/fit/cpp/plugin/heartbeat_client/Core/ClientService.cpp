/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#include "ClientService.hpp"

#include <algorithm>
#include <chrono>
#include <fit/fit_log.h>
#include <genericable/com_huawei_fit_heartbeat_heartbeat/1.0.0/cplusplus/heartbeat.hpp>
#include "fit/internal/fit_system_property_utils.h"
#include "fit/internal/fit_scope_guard.h"
#include "ClientConfig.hpp"

namespace Fit {
namespace Heartbeat {
namespace Client {

ClientService::ClientService(string id)
    : id_(std::move(id))
{
}

int32_t ClientService::Online(const BeatInfo &beatInfo)
{
    lock_guard<mutex> guard(mt_);

    auto iter = sceneBeatInfo_.find(beatInfo.sceneType);
    if (iter == sceneBeatInfo_.end()) {
        iter = sceneBeatInfo_.insert(make_pair(beatInfo.sceneType, BeatStatusInfoSet())).first;
    }

    for (auto &item : iter->second) {
        if (item.beatInfo.callbackFitId == beatInfo.callbackFitId) {
            FIT_LOG_ERROR("Callback fitid is already exist, scene = %s, callback fitid = %s.",
                beatInfo.sceneType.c_str(),
                beatInfo.callbackFitId.c_str());
            return FIT_ERR_EXIST;
        }
    }

    BeatStatusInfo statusInfo {};
    statusInfo.beatInfo = beatInfo;
    statusInfo.status = HeartbeatStatus::ALIVE;
    statusInfo.steadyCount = 0;
    statusInfo.isSteady = true;
    statusInfo.tickCount = 0;
    iter->second.emplace_back(move(statusInfo));
    FIT_LOG_INFO("Online scene = %s successfully, callback fitid = %s.",
        beatInfo.sceneType.c_str(),
        beatInfo.callbackFitId.c_str());

    return FIT_ERR_SUCCESS;
}

int32_t ClientService::Offline(const BeatInfo &beatInfo)
{
    bool isClear = false;
    if (Remove(beatInfo, isClear) != FIT_ERR_SUCCESS) {
        return FIT_ERR_FAIL;
    }

    if (isClear) {
        Leave(beatInfo);
    }

    return FIT_ERR_SUCCESS;
}

void ClientService::UpdateHeartbeatStatus(BeatStatusInfo &beatStatusInfo, HeartbeatStatus newStatus)
{
    FIT_LOG_DEBUG("Heartbeat, scene = %s, callback fitid = %s.",
        beatStatusInfo.beatInfo.sceneType.c_str(),
        beatStatusInfo.beatInfo.callbackFitId.c_str());
    if (beatStatusInfo.status != newStatus) {
        beatStatusInfo.isSteady = false;
        beatStatusInfo.status = newStatus;
    }

    if (!beatStatusInfo.isSteady) {
        beatStatusInfo.steadyCount++;
        if (beatStatusInfo.steadyCount >= HEARTBEAT_STEADY_COUNT) {
            beatStatusInfo.steadyCount = 0;
            beatStatusInfo.isSteady = true;
            Notify(beatStatusInfo.beatInfo, beatStatusInfo.status);
        }
    }
}

void ClientService::Notify(const BeatInfo &beatInfo, HeartbeatStatus status)
{
    FIT_LOG_INFO("Heartbeat change, scene = %s, callback fitid = %s, status = %u.",
        beatInfo.sceneType.c_str(),
        beatInfo.callbackFitId.c_str(),
        static_cast<uint32_t>(status));

    FIT_LOG_INFO("Notify, id = %s, scene = %s, callback fitid = %s, status = %u.",
        id_.c_str(),
        beatInfo.sceneType.c_str(),
        beatInfo.callbackFitId.c_str(),
        static_cast<uint32_t>(status));
}

void ClientService::Heartbeat()
{
    lock_guard<mutex> guard(mt_);
    if (sceneBeatInfo_.empty()) {
        return;
    }

    Fit::vector<fit::heartbeat::BeatInfo> rawBeatinfos {};
    for (auto &item : sceneBeatInfo_) {
        if (item.second.empty()) {
            continue;
        }
        auto &currentBeatStatus = item.second.front();
        const auto &currentBeatInfo = currentBeatStatus.beatInfo;
        if (currentBeatStatus.tickCount > 1) {
            --currentBeatStatus.tickCount;
            continue;
        }
        constexpr uint32_t msPerSecond = 1000;
        currentBeatStatus.tickCount = currentBeatInfo.interval / msPerSecond;
        fit::heartbeat::BeatInfo beatInfo {};
        beatInfo.sceneType = currentBeatInfo.sceneType;
        beatInfo.interval = currentBeatInfo.interval;
        beatInfo.aliveTime = currentBeatInfo.aliveTime;
        beatInfo.initDelay = currentBeatInfo.initDelay;
        beatInfo.callbackFitId = currentBeatInfo.callbackFitId;
        rawBeatinfos.push_back(beatInfo);

        FIT_LOG_DEBUG("Heartbeat. scene = %s, id = %s, alive time = %ld, interval = %ld.",
            item.second.front().beatInfo.sceneType.c_str(),
            id_.c_str(),
            item.second.front().beatInfo.aliveTime,
            item.second.front().beatInfo.interval);
    }

    if (rawBeatinfos.empty()) {
        return;
    }

    fit::registry::Address address{};
    auto externalAddress = FitSystemPropertyUtils::GetExternalAddresses();
    if (!externalAddress.empty()) {
        address = move(externalAddress.front());
    }
    address.id = id_;
    bool *result {};
    ::fit::heartbeat::heartbeat heartbeat;
    auto ret = heartbeat(&rawBeatinfos, &address, &result);
}

void ClientService::Leave(const BeatInfo &beatInfo)
{
    vector<BeatInfo> beatInfos {beatInfo};

    FIT_LOG_DEBUG("Leave. scene = %s, id = %s, alive time = %ld, interval = %ld.",
        beatInfo.sceneType.c_str(),
        id_.c_str(),
        beatInfo.aliveTime,
        beatInfo.interval);
}

int32_t ClientService::Remove(const BeatInfo &beatInfo, bool &isClear)
{
    lock_guard<mutex> guard(mt_);

    auto iter = sceneBeatInfo_.find(beatInfo.sceneType);
    if (iter == sceneBeatInfo_.end()) {
        FIT_LOG_ERROR("Not exist scene = %s, caller callback fitid = %s.",
            beatInfo.sceneType.c_str(),
            beatInfo.callbackFitId.c_str());
        return FIT_ERR_EXIST;
    }

    auto removeIter = remove_if(iter->second.begin(), iter->second.end(), [&beatInfo](BeatStatusInfo &info) {
        if (info.beatInfo.callbackFitId == beatInfo.callbackFitId) {
            FIT_LOG_INFO("Remove, scene = %s, callback fitid = %s.",
                beatInfo.sceneType.c_str(),
                beatInfo.callbackFitId.c_str());
            return true;
        }
        return false;
    });

    iter->second.erase(removeIter, iter->second.end());
    isClear = iter->second.empty();

    return FIT_ERR_SUCCESS;
}
}  // namespace Client
}  // namespace Heartbeat
}  // namespace Fit
