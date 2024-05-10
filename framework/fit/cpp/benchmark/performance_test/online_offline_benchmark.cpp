/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/8/5 9:59
 * Notes        :
 */

#include <benchmark/benchmark.h>

#include <genericable/com_huawei_fit_heartbeat_online_heartbeat/1.0.0/cplusplus/onlineHeartbeat.hpp>
#include <genericable/com_huawei_fit_heartbeat_offline_heartbeat/1.0.0/cplusplus/offlineHeartbeat.hpp>
#include <fit/internal/heartbeat/heartbeat_entity.hpp>

#include "echo/echo.hpp"
#include "echo/echo_impl.hpp"
#include "echo/echo_converter.hpp"

class OnlineOfflineFixture : public benchmark::Fixture {
public:
    void SetUp(const benchmark::State &state)
    {
    }

    void TearDown(const benchmark::State &state)
    {
    }
};

BENCHMARK_DEFINE_F(OnlineOfflineFixture, OnlineOffline)(benchmark::State &state)
{
    fit::heartbeat::onlineHeartbeat onlineHeartbeatImpl;
    fit::heartbeat::offlineHeartbeat offlineHeartbeatImpl;
    fit::heartbeat::BeatInfo beatInfo {};
    beatInfo.sceneType = Fit::Heartbeat::SCENE_FIT_REGISTRY;
    constexpr int intervalMs = 3000;
    constexpr int aliveMs = 10000;
    constexpr int initDelayMs = 5000;
    beatInfo.interval = intervalMs;
    beatInfo.aliveTime = aliveMs;
    beatInfo.initDelay = initDelayMs;

    for (auto _ : state) {
        beatInfo.callbackFitId = std::to_string(state.range(0));
        bool *onlineHeartbeatResult {};
        auto ret = onlineHeartbeatImpl(&beatInfo, &onlineHeartbeatResult);
        if (ret != FIT_OK) {
            printf("onlineHeartbeat failed: ret = %X.\n", ret);
        }

        bool *offlineHeartbeatResult {};
        ret = offlineHeartbeatImpl(&beatInfo, &offlineHeartbeatResult);
        if (ret != FIT_OK) {
            printf("offlineHeartbeat failed: ret = %X.\n", ret);
        }
    }
}

BENCHMARK_REGISTER_F(OnlineOfflineFixture, OnlineOffline)
    ->RangeMultiplier(10)->Range(1, 100)
    ->Threads(1);
