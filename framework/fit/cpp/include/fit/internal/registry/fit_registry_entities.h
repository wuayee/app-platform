/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/7/17 16:07
 * Notes:       :
 */

#ifndef FIT_REGISTRY_ENTITIES_H
#define FIT_REGISTRY_ENTITIES_H

#include <fit/internal/fit_fitable.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/stl/string.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/memory/fit_base.hpp>
#include <fit/stl/unordered_set.hpp>
#include "fit_heartbeat_entity.h"
#include "fit_registry_entity.h"

struct fit_generic_key_t : public FitBase {
    Fit::string generic_id;
    Fit::string generic_version;
};

struct fit_listener_info_t : public FitBase {
    Fit::string fitable_id;
    Fit::fit_address address;
    uint64_t syncCount {DEFAULT_SYNC_COUNT};
};
using listener_t = fit_listener_info_t;

struct fit_fitable_key_t : public FitBase {
    Fit::string generic_id;
    Fit::string generic_version;
    Fit::string fitable_id;
};

struct db_worker_info_t : public FitBase {
    Fit::string db_key; // ccdb使用
    Fit::string token;
    Fit::fit_address address;
    time_t start_time;
    time_t update_time;
    bool is_online;
    Fit::string id;
};

struct db_service_info_t : public FitBase {
    Fit::string db_key; // ccdb使用
    fit_service_instance_t service;
    time_t start_time;
    bool is_online {false};
    Fit::timer::timer_handle_t handle { Fit::timer::INVALID_TASK_ID };
    uint64_t syncCount {DEFAULT_SYNC_COUNT};
};

struct db_subscription_entry_t : public FitBase {
    Fit::string db_key;
    fit_fitable_key_t fitable_key;
    listener_t listener;
    time_t start_time;
};

using registry_query_conditions = Fit::vector<std::pair<Fit::string, Fit::string>>;
using db_service_set      = Fit::vector<db_service_info_t>;
using db_worker_set       = Fit::vector<db_worker_info_t>;
using db_subscription_set = Fit::vector<db_subscription_entry_t>;

using address_set  = Fit::vector<Fit::fit_address>;
using listener_set = Fit::vector<listener_t>;

using db_query_subscription_entry_result = std::pair<db_subscription_entry_t, int32_t>;
using db_query_subscription_set_result   = std::pair<db_subscription_set, int32_t>;
using db_query_listener_set_result       = std::pair<listener_set, int32_t>;
using db_query_subscription_key          = std::pair<Fit::string, int32_t>;

class fit_generic_key_compare {
public:
    bool operator()(const fit_generic_key_t &a, const fit_generic_key_t &b) const
    {
        if (a.generic_id == b.generic_id && a.generic_version == b.generic_version) {
            return false;
        }
        return a.generic_id + a.generic_version < b.generic_id + b.generic_version;
    }
};

class FitableKeyEqual {
public:
    size_t operator()(const fit_fitable_key_t &keyLeft, const fit_fitable_key_t &keyRight) const
    {
        return (keyLeft.fitable_id == keyRight.fitable_id) &&
            (keyLeft.generic_id == keyRight.generic_id) &&
            (keyLeft.generic_version == keyRight.generic_version);
    }
};

class fitable_key_hasher {
public:
    size_t operator()(const fit_fitable_key_t &key) const
    {
        return std::hash<Fit::string>()(key.generic_id) ^
            std::hash<Fit::string>()(key.generic_version) ^
            std::hash<Fit::string>()(key.fitable_id);
    }
};

class FitableAddressEqual {
public:
    size_t operator()(const Fit::fit_address& addressLeft, const Fit::fit_address& addressRight) const
    {
        return (addressLeft.ip == addressRight.ip) &&
            (addressLeft.port == addressRight.port) &&
            (addressLeft.protocol == addressRight.protocol) &&
            (addressLeft.id == addressRight.id);
    }
};

class FitableAddressHasher {
public:
    size_t operator()(const Fit::fit_address& address) const
    {
        return std::hash<Fit::string>()(address.ip) ^ std::hash<uint32_t>()(address.port) ^
            std::hash<uint32_t>()(static_cast<uint32_t>(address.protocol)) ^
            std::hash<Fit::string>()(address.id);
    }
};

class fit_fitable_id_equal_to {
public:
    bool operator()(const Fit::fitable_id &a, const Fit::fitable_id &b) const
    {
        return a.generic_id == b.generic_id &&
            a.generic_version == b.generic_version &&
            a.fitable_id == b.fitable_id &&
            a.fitable_version == b.fitable_version;
    }
};

class FitListenerHasher {
public:
    size_t operator()(const listener_t& listener) const
    {
        return std::hash<Fit::string>()(listener.fitable_id) ^
            std::hash<Fit::string>()(listener.address.id);
    }
};
class FitListenerEqualTo {
public:
    bool operator()(const listener_t &a, const listener_t &b) const
    {
        return a.fitable_id == b.fitable_id &&
            a.address.id == b.address.id;
    }
};

using ListenerSet = Fit::unordered_set<listener_t, FitListenerHasher, FitListenerEqualTo>;
using FitableListenerMap = Fit::unordered_map<fit_fitable_key_t, ListenerSet,
    fitable_key_hasher, FitableKeyEqual>;

bool inline operator==(const Fit::fit_address &address_x, const Fit::fit_address &address_y)
{
    return address_x.ip == address_y.ip && address_x.port == address_y.port && address_x.protocol == address_y.protocol;
}
bool inline operator==(const listener_t &listener_x, const listener_t &listener_y)
{
    return listener_x.fitable_id == listener_y.fitable_id && listener_x.address == listener_y.address;
}
bool inline operator==(const fit_fitable_key_t &key_x, const fit_fitable_key_t &key_y)
{
    return key_x.fitable_id == key_y.fitable_id && key_x.generic_id == key_y.generic_id &&
        key_x.generic_version == key_y.generic_version;
}
bool inline operator==(const Fit::fitable_id &key_x, const Fit::fitable_id &key_y)
{
    return key_x.fitable_id == key_y.fitable_id && key_x.generic_id == key_y.generic_id &&
        key_x.generic_version == key_y.generic_version && key_x.fitable_version == key_y.fitable_version;
}

inline fit_fitable_key_t get_fitable_key_from_fitable(const Fit::fitable_id &fitable)
{
    fit_fitable_key_t key;
    key.generic_id = fitable.generic_id;
    key.generic_version = fitable.generic_version;
    key.fitable_id = fitable.fitable_id;
    return key;
}

#endif // FIT_REGISTRY_ENTITIES_H
