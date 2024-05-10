/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/9/22 19:50
 * Notes:       :
 */

#ifndef FIT_SCENE_SUBSCRIBER_H
#define FIT_SCENE_SUBSCRIBER_H

#include <fit/stl/unordered_set.hpp>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include "fit/internal/heartbeat/heartbeat_entity.hpp"

class fit_scene_subscriber final {
public:
    explicit fit_scene_subscriber(const Fit::Heartbeat::SubscribeBeatInfo &info);
    ~fit_scene_subscriber() = default;

    bool operator==(const fit_scene_subscriber &other) const;

    size_t hash_value() const;

    void notify(const Fit::Heartbeat::AddressStatusInfo &changed_address) const;

    const Fit::Heartbeat::SubscribeBeatInfo &get_subscribe_info() const
    {
        return subscribe_info_;
    }

private:
    Fit::Heartbeat::SubscribeBeatInfo subscribe_info_;
};

class fit_scene_subscriber_equal {
public:
    bool operator()(const fit_scene_subscriber &left, const fit_scene_subscriber &right) const
    {
        return left == right;
    }
};

class fit_scene_subscriber_hash {
public:
    size_t operator()(const fit_scene_subscriber &value) const
    {
        return value.hash_value();
    }
};

using fit_scene_subscriber_hash_set = Fit::unordered_set<fit_scene_subscriber,
    fit_scene_subscriber_hash, fit_scene_subscriber_equal>;
using fit_scene_subscriber_set = Fit::vector<fit_scene_subscriber>;

#endif // FIT_SCENE_SUBSCRIBER_H
