/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/9/22 20:52
 * Notes:       :
 */

#ifndef FIT_SCENE_PUBLISHER_H
#define FIT_SCENE_PUBLISHER_H

#include "fit/internal/heartbeat/heartbeat_entity.hpp"
#include "fit_scene_subscriber.h"

#include <fit/stl/unordered_set.hpp>
#include <fit/stl/unordered_map.hpp>
#include "fit/internal/heartbeat/fit_scene_subscribe_repository.h"

class fit_scene_publisher {
public:
    fit_scene_publisher(const fit_scene_publisher &other);
    fit_scene_publisher(fit_scene_publisher &&other) noexcept;
    explicit fit_scene_publisher(Fit::string scene_type, fit_scene_subscribe_repository_ptr repository);
    ~fit_scene_publisher() = default;
    fit_scene_publisher &operator = (const fit_scene_publisher &other);

    bool operator == (const fit_scene_publisher &other) const;

    size_t hash_value () const;

    int32_t add_subscriber(const fit_scene_subscriber &subscriber);
    int32_t remove_subscriber(const fit_scene_subscriber &subscriber);
    int32_t remove_subscriber(const Fit::string &id);
    void notify(const Fit::Heartbeat::AddressStatusInfo &changed_address);

private:
    Fit::string scene_type_;
    fit_scene_subscriber_hash_set subscribers_ {};
    fit_scene_subscribe_repository_ptr repository_;
};

class fit_scene_publisher_equal {
public:
    bool operator () (const fit_scene_publisher &left,  const fit_scene_publisher &right) const
    {
        return left == right;
    }
};

class fit_scene_publisher_hash {
public:
    size_t operator () (const fit_scene_publisher &value) const
    {
        return value.hash_value();
    }
};

using fit_scene_publisher_set = Fit::unordered_map<Fit::Heartbeat::SceneType, fit_scene_publisher>;

#endif // FIT_SCENE_PUBLISHER_H
