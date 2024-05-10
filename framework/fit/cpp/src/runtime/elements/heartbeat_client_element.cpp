/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : implement for heartbeat client runtime element
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#include "heartbeat_client_element.hpp"
#include <genericable/com_huawei_fit_heartbeat_subscribe_heartbeat/1.0.0/cplusplus/subscribeHeartbeat.hpp>
#include <genericable/com_huawei_fit_heartbeat_online_heartbeat/1.0.0/cplusplus/onlineHeartbeat.hpp>
#include <genericable/com_huawei_fit_heartbeat_offline_heartbeat/1.0.0/cplusplus/offlineHeartbeat.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <fit/fit_log.h>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/internal/runtime/config/system_config.hpp>

using namespace Fit;
using ::fit::hakuna::kernel::registry::server::registerFitables;
using ::fit::heartbeat::onlineHeartbeat;
using Framework::FitableDiscovery;
using Config::SystemConfig;

namespace Fit {
HeartbeatClientElement::HeartbeatClientElement() : RuntimeElementBase("heartbeatClientStarter") {}
HeartbeatClientElement::~HeartbeatClientElement() = default;

bool HeartbeatClientElement::Start()
{
    if (!HasHeartbeatClient()) {
        FIT_LOG_INFO("Working with no heartbeat client.");
        return true;
    }
    auto ret = Online();
    if (ret != FIT_OK) {
        return false;
    }

    return ObserveHeartbeatChanged() == FIT_OK;
}

bool HeartbeatClientElement::Stop()
{
    if (!HasHeartbeatClient()) {
        return true;
    }
    fit::heartbeat::offlineHeartbeat offline;
    fit::heartbeat::BeatInfo beatInfo {};
    beatInfo.sceneType = "fit_registry";
    if (HasRegistry()) {
        beatInfo.sceneType = "fit_registry_server";
    }
    bool *result {};
    offline(&beatInfo, &result);
    return true;
}

FitCode HeartbeatClientElement::Online()
{
    fit::heartbeat::onlineHeartbeat online;
    fit::heartbeat::BeatInfo beatInfo {};
    beatInfo.sceneType = "fit_registry";
    if (HasRegistry()) {
        beatInfo.sceneType = "fit_registry_server";
    }
    constexpr int intervalMs = 3000;
    constexpr int aliveMs = 10000;
    constexpr int initDelayMs = 5000;
    beatInfo.interval = intervalMs;
    beatInfo.aliveTime = aliveMs;
    beatInfo.initDelay = initDelayMs;
    bool *result {};
    auto ret = online(&beatInfo, &result);
    if (ret != FIT_OK || !result || !*result) {
        FIT_LOG_ERROR("Failed to online. [ret=%X]", ret);
        return FIT_ERR_FAIL;
    }
    return FIT_OK;
}

bool HeartbeatClientElement::HasRegistry()
{
    return !GetRuntime().GetElementIs<FitableDiscovery>()->GetLocalFitableByGenericId(
        registerFitables::GENERIC_ID).empty();
}

bool HeartbeatClientElement::HasHeartbeatClient()
{
    return !GetRuntime().GetElementIs<FitableDiscovery>()->GetLocalFitableByGenericId(
        onlineHeartbeat::GENERIC_ID).empty();
}

FitCode HeartbeatClientElement::ObserveHeartbeatChanged()
{
    if (!HasRegistry()) {
        return FIT_OK;
    }
    static auto observedSceneTypes = {"fit_registry_server", "fit_registry"};
    fit::registry::Address address {};
    address.id = GetRuntime().GetElementIs<SystemConfig>()->GetWorkerId();

    fit::heartbeat::subscribeHeartbeat subscribe;
    Fit::vector<fit::heartbeat::SubscribeBeatInfo> subscribeBeatInfos;
    for (auto &subscribeSceneType : observedSceneTypes) {
        fit::heartbeat::SubscribeBeatInfo subscribeBeatInfo {};
        subscribeBeatInfo.callbackFitId = "fit_registry_address_status_callback";
        subscribeBeatInfo.sceneType = subscribeSceneType;
        subscribeBeatInfos.push_back(subscribeBeatInfo);
    }

    bool *result {};
    auto ret = subscribe(&subscribeBeatInfos, &address, &result);
    if (ret != FIT_OK || !result || !*result) {
        FIT_LOG_ERROR("Failed to subscribe heartbeat. [ret=%X]", ret);
        return FIT_ERR_FAIL;
    }

    FIT_LOG_INFO("Subscribe heartbeat successfully.");
    return FIT_OK;
}
}