/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 订阅信息存储接口
 * Author       : s00558940
 * Create       : 2020/10/8 19:25
 * Notes:       :
 */

#ifndef FIT_SCENE_SUBSCRIBE_REPOSITORY_H
#define FIT_SCENE_SUBSCRIBE_REPOSITORY_H

#include "fit/internal/heartbeat/heartbeat_entity.hpp"
#include "fit/internal/heartbeat/fit_scene_subscriber.h"

class fit_scene_subscribe_repository {
public:
    fit_scene_subscribe_repository() = default;
    virtual ~fit_scene_subscribe_repository() = default;

    virtual int32_t add(const fit_scene_subscriber &info) = 0;
    virtual int32_t remove(const fit_scene_subscriber &info) = 0;
    virtual int32_t remove(const Fit::string &id) = 0;
    virtual fit_scene_subscriber_set query(const Fit::Heartbeat::SceneType &scene_type) = 0;
};

using fit_scene_subscribe_repository_ptr = std::shared_ptr<fit_scene_subscribe_repository>;

class fit_scene_subscribe_repository_factory final {
public:
    static fit_scene_subscribe_repository_ptr create();
};

#endif // FIT_SCENE_SUBSCRIBE_REPOSITORY_H
