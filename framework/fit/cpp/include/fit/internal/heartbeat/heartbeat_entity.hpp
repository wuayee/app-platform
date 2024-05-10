/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/9/22 19:23
 * Notes:       :
 */

#ifndef FIT_HEARTBEAT_ENTITY_HPP
#define FIT_HEARTBEAT_ENTITY_HPP

#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

#include <cstdint>
#include <cstdint>
#include <memory>
#include "fit/internal/fit_fitable.h"

namespace Fit {
namespace Heartbeat {
using SceneType = Fit::string;

constexpr const char *ONLINE{"RUN_STATE_ONLINE"};
constexpr const char *OFFLINE{"RUN_STATE_OFFLINE"};

constexpr const char *MASTER{"ROLE_MASTER"};
constexpr const char *SLAVE{"ROLE_SLAVE"};
constexpr const char *IDLE{"ROLE_IDLE"};

constexpr const char *SCENE_FIT_REGISTRY{"fit_registry"};
constexpr const char *SCENE_FIT_REGISTRY_SERVER{"fit_registry_server"};

constexpr const char *SUBSCRIBE_SCENE[]{SCENE_FIT_REGISTRY_SERVER, SCENE_FIT_REGISTRY};
constexpr const uint32_t SUBSCRIBE_SCENE_SIZE{sizeof(SUBSCRIBE_SCENE) / sizeof(char *)};

constexpr const char *REGISTRY_ROLE_KEY {"registry_role"};

enum class HeartbeatStatus { UNKNOWN, ALIVE, LEAVE };

struct BeatInfo {
    SceneType sceneType;
    int64_t aliveTime;
    int64_t interval;
    int64_t initDelay;
    string callbackFitId;
};

struct AddressBeatInfo {
    string id;
    vector<fit_address> addresses;
    BeatInfo beat_info;
};

struct AddressStatusInfo {
    AddressBeatInfo addressBeatInfo;
    uint64_t start_time{};
    uint64_t last_heartbeat_time{};
    uint64_t expired_time{};
    HeartbeatStatus status{};
};

struct SubscribeBeatInfo {
    SceneType sceneType;
    string callbackFitId;
    string id;
    fit_address callbackAddress;
};

struct BeatStatusInfo {
    BeatInfo beatInfo;
    HeartbeatStatus status;
    uint32_t steadyCount;
    bool isSteady;
    uint64_t tickCount;
};

struct AddressChangeEvent {
    SceneType sceneType;
    HeartbeatStatus status;
    fit_address address;
    string id;
};

using AddressBeatInfoSet = vector<AddressBeatInfo>;
using AddressBeatInfoPtr = std::shared_ptr<AddressBeatInfo>;
using AddressStatusSet = vector<AddressStatusInfo>;
using IdWithAddressStatusSet = map<string, AddressStatusInfo>;
using SceneTypeWithAddressStatusSet = map<SceneType, IdWithAddressStatusSet>;
using SubscribeBeatInfoSet = vector<SubscribeBeatInfo>;
}  // namespace Heartbeat
}  // namespace Fit

#endif  // FIT_HEARTBEAT_ENTITY_H
