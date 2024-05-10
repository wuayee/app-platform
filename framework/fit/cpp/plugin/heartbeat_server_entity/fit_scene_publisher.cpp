/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/9/22 20:52
 */

#include "fit/internal/heartbeat/fit_scene_publisher.h"

#include <utility>

#include <fit/fit_log.h>

fit_scene_publisher::fit_scene_publisher(Fit::string scene_type, fit_scene_subscribe_repository_ptr repository)
    : scene_type_(std::move(scene_type)),
      repository_(std::move(repository))
{
}

fit_scene_publisher::fit_scene_publisher(const fit_scene_publisher &other)
{
    scene_type_ = other.scene_type_;
    repository_ = other.repository_;
}

fit_scene_publisher::fit_scene_publisher(fit_scene_publisher &&other) noexcept
{
    scene_type_ = std::move(other.scene_type_);
    repository_ = std::move(other.repository_);
}

fit_scene_publisher &fit_scene_publisher::operator=(const fit_scene_publisher &other)
{
    if (&other == this) {
        return *this;
    }
    scene_type_ = other.scene_type_;
    repository_ = other.repository_;

    return *this;
}

int32_t fit_scene_publisher::add_subscriber(const fit_scene_subscriber &subscriber)
{
    if (!repository_) {
        return FIT_ERR_FAIL;
    }

    return repository_->add(subscriber);
}

int32_t fit_scene_publisher::remove_subscriber(const fit_scene_subscriber &subscriber)
{
    if (!repository_) {
        return FIT_ERR_FAIL;
    }

    return repository_->remove(subscriber);
}

void fit_scene_publisher::notify(const Fit::Heartbeat::AddressStatusInfo &changed_address)
{
    if (!repository_) {
        return;
    }

    auto subscribers = repository_->query(scene_type_);

    FIT_LOG_INFO("Notify size = %lu, scene type = %s, id = %s, status = %u.", subscribers.size(),
        changed_address.addressBeatInfo.beat_info.sceneType.c_str(),
        changed_address.addressBeatInfo.id.c_str(),
        static_cast<uint32_t>(changed_address.status));

    for (auto &item : subscribers) {
        item.notify(changed_address);
    }
}

size_t fit_scene_publisher::hash_value() const
{
    return std::hash<Fit::string>()(scene_type_);
}

bool fit_scene_publisher::operator==(const fit_scene_publisher &other) const
{
    return scene_type_ == other.scene_type_;
}

int32_t fit_scene_publisher::remove_subscriber(const Fit::string &id)
{
    if (!repository_) {
        return FIT_ERR_FAIL;
    }

    return repository_->remove(id);
}
