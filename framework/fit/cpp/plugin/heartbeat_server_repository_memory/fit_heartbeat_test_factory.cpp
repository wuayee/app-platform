/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/10/9 17:26
 */

#ifndef FIT_HEARTBEAT_OCDB_FACTORY_H
#define FIT_HEARTBEAT_OCDB_FACTORY_H

#include <string>
#include <map>
#include <mutex>
#include <algorithm>
#include <fit/internal/heartbeat/fit_scene_subscribe_repository.h>
#include <fit/internal/heartbeat/fit_heartbeat_repository.h>
#include <fit/fit_log.h>

class fit_scene_subscribe_repository_test : public fit_scene_subscribe_repository {
public:
    int32_t add(const fit_scene_subscriber &info) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        auto iter = subscribers_.find(info.get_subscribe_info().sceneType);
        if (iter == subscribers_.end()) {
            iter = subscribers_.insert(
                std::make_pair(info.get_subscribe_info().sceneType, fit_scene_subscriber_set {})).first;
        }

        for (auto &item : iter->second) {
            if (fit_scene_subscriber_equal()(item, info)) {
                FIT_LOG_WARN("Already exist, scene type = %s, subscriber worker id = %s.",
                    info.get_subscribe_info().sceneType.c_str(), info.get_subscribe_info().id.c_str());
                return FIT_ERR_SUCCESS;
            }
        }

        FIT_LOG_CORE("Add, scene type = %s, subscriber worker id = %s.",
            info.get_subscribe_info().sceneType.c_str(), info.get_subscribe_info().id.c_str());
        iter->second.push_back(info);

        return FIT_ERR_SUCCESS;
    };

    int32_t remove(const fit_scene_subscriber &info) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        auto iter = subscribers_.find(info.get_subscribe_info().sceneType);
        if (iter == subscribers_.end()) {
            return FIT_ERR_SUCCESS;
        }

        auto remove_iter = std::remove_if(iter->second.begin(), iter->second.end(),
            [&info](const fit_scene_subscriber &item) {
                if (fit_scene_subscriber_equal()(item, info)) {
                    FIT_LOG_WARN("Remove, scene type = %s, subscriber worker id = %s.",
                        info.get_subscribe_info().sceneType.c_str(), info.get_subscribe_info().id.c_str());
                    return true;
                }

                return false;
            });
        iter->second.erase(remove_iter, iter->second.end());

        if (iter->second.empty()) {
            subscribers_.erase(iter);
        }

        return FIT_ERR_SUCCESS;
    };

    int32_t remove(const Fit::string &id) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        for (auto &item : subscribers_) {
            auto remove_iter = std::remove_if(item.second.begin(), item.second.end(),
                [&id](const fit_scene_subscriber &subscriber) {
                    if (id == subscriber.get_subscribe_info().id) {
                        FIT_LOG_WARN("Remove, scene type = %s, subscriber worker id = %s.",
                            subscriber.get_subscribe_info().sceneType.c_str(), id.c_str());
                        return true;
                    }

                    return false;
                });
            item.second.erase(remove_iter, item.second.end());
        }

        return FIT_ERR_SUCCESS;
    };

    fit_scene_subscriber_set query(const Fit::Heartbeat::SceneType &scene_type) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        auto iter = subscribers_.find(scene_type);
        if (iter == subscribers_.end()) {
            return {};
        }

        return iter->second;
    };

private:
    Fit::map<Fit::string, fit_scene_subscriber_set> subscribers_;
    std::mutex mt_;
};

class fit_heartbeat_repository_test : public fit_heartbeat_repository {
public:
    int32_t add_beat(const Fit::Heartbeat::AddressStatusInfo &info) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        for (auto &item : heartbeats_) {
            if (item.addressBeatInfo.id == info.addressBeatInfo.id) {
                item = info;
                return FIT_ERR_SUCCESS;
            }
        }
        heartbeats_.push_back(info);

        return FIT_ERR_SUCCESS;
    };

    int32_t modify_beat(const Fit::Heartbeat::AddressStatusInfo &info) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        for (auto &item : heartbeats_) {
            if (item.addressBeatInfo.id == info.addressBeatInfo.id) {
                item = info;
                return FIT_ERR_SUCCESS;
            }
        }

        return FIT_ERR_NOT_FOUND;
    };

    int32_t remove_beat(const Fit::Heartbeat::AddressBeatInfo &info) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        auto remove_iter = std::remove_if(heartbeats_.begin(), heartbeats_.end(),
            [&info](const Fit::Heartbeat::AddressStatusInfo &item) {
                if (item.addressBeatInfo.id == info.id) {
                    FIT_LOG_WARN("Remove, worker id = %s.", info.id.c_str());
                    return true;
                }
                return false;
            });

        heartbeats_.erase(remove_iter, heartbeats_.end());

        return FIT_ERR_SUCCESS;
    };

    int32_t query_beat(const Fit::Heartbeat::AddressBeatInfo &info,
        Fit::Heartbeat::AddressStatusInfo &result) override
    {
        std::lock_guard<std::mutex> guard(mt_);
        for (const auto &item : heartbeats_) {
            if (item.addressBeatInfo.id == info.id) {
                result = item;
                return FIT_ERR_SUCCESS;
            }
        }

        return FIT_ERR_NOT_FOUND;
    };

    Fit::Heartbeat::AddressStatusSet query_all_beat() override
    {
        std::lock_guard<std::mutex> guard(mt_);
        return heartbeats_;
    };

    FitCode get_current_time_ms(uint64_t& result) override
    {
        result = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::time_point_cast<std::chrono::milliseconds>(std::chrono::system_clock::now())
                .time_since_epoch())
            .count();
        return FIT_OK;
    }

private:
    Fit::Heartbeat::AddressStatusSet heartbeats_;
    std::mutex mt_;
};

fit_heartbeat_repository_ptr fit_heartbeat_repository_factory::create()
{
    return std::make_shared<fit_heartbeat_repository_test>();
}

fit_scene_subscribe_repository_ptr fit_scene_subscribe_repository_factory::create()
{
    return std::make_shared<fit_scene_subscribe_repository_test>();
}

#endif // FIT_HEARTBEAT_OCDB_FACTORY_H
