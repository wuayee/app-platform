/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/9/22 19:23
 * Notes:       :
 */

#ifndef FIT_HEARTBEAT_ENTITY_H
#define FIT_HEARTBEAT_ENTITY_H

#include <memory>
#include <map>
#include <fit/stl/vector.hpp>
#include "fit/internal/fit_fitable.h"

using scene_type_t = Fit::string;

static const char *ONLINE {"RUN_STATE_ONLINE"};
static const char *OFFLINE {"RUN_STATE_OFFLINE"};

static const char *MASTER {"ROLE_MASTER"};
static const char *SLAVE {"ROLE_SLAVE"};

static const char *SCENE_FIT_REGISTRY {"fit_registry"};
static const char *SCENE_FIT_REGISTRY_SERVER {"fit_registry_server"};

static const char *SUBSCRIBE_SCENE[] {SCENE_FIT_REGISTRY_SERVER, SCENE_FIT_REGISTRY};
static const uint32_t SUBSCRIBE_SCENE_SIZE {sizeof(SUBSCRIBE_SCENE) / sizeof(char *)};

enum class fit_heartbeat_status_t {
    UNKNOWN,
    ALIVE,
    LEAVE
};

struct fit_beat_info_t {
    scene_type_t scene_type;
    uint32_t alive_time;
    uint32_t interval;
    uint32_t init_delay;
    Fit::string callback_fitid;
};

struct fit_address_beat_info_t {
    Fit::string id;
    Fit::vector<Fit::fit_address> addresses;
    fit_beat_info_t beat_info;
};

struct fit_address_status_info_t {
    fit_address_beat_info_t address_beat_info;
    long long start_time {};
    long long last_heartbeat_time {};
    long long expired_time {};
    fit_heartbeat_status_t status {};
};

struct fit_subscribe_beat_info_t {
    scene_type_t scene_type;
    Fit::string callback_fitid;
    Fit::string id;
    Fit::fit_address callback_address;
};

struct fit_beat_status_info_t {
    fit_beat_info_t beat_info;
    fit_heartbeat_status_t status;
    uint32_t steady_count;
    bool is_steady;
};

struct fit_address_change_event_t {
    scene_type_t scene_type;
    fit_heartbeat_status_t status;
    Fit::fit_address address;
    Fit::string id;
};

using fit_address_beat_info_set = Fit::vector<fit_address_beat_info_t>;
using fit_address_beat_info_ptr = std::shared_ptr<fit_address_beat_info_t>;
using fit_address_status_set = Fit::vector<fit_address_status_info_t>;
using fit_id_with_address_status_set = std::map<Fit::string, fit_address_status_info_t>;
using fit_scene_type_with_address_status_set = std::map<scene_type_t, fit_id_with_address_status_set>;
using fit_subscribe_beat_info_set = Fit::vector<fit_subscribe_beat_info_t>;


#endif // FIT_HEARTBEAT_ENTITY_H
