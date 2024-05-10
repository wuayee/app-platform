/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/05/28
 * Notes:       :
 */
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <genericable/com_huawei_fit_heartbeat_heartbeat_address_change/1.0.0/cplusplus/heartbeatAddressChange.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_remove_resgitry_address/1.0.0/cplusplus/removeResgitryAddress.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/fit_log.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/internal/heartbeat/heartbeat_entity.hpp>
#include <fit/stl/string.hpp>
#include "core/fit_registry_mgr.h"
#include "core/fit_registry_conf.h"
namespace {
static volatile bool is_registry_master = true;

bool is_current_node(const Fit::string &id)
{
    static const auto current_worker_id = FitSystemPropertyUtils::Get("fit_worker_id");
    return id == current_worker_id;
}

bool get_current_node_type(const fit::heartbeat::HeartbeatEvent &event, bool &isMaster);
void delete_offline_registry(const fit::heartbeat::HeartbeatEvent &event);
void process_fit_registry_server_scene(const Fit::vector<fit::heartbeat::HeartbeatEvent> &eventList)
{
    bool isMasterInThisTime = false;
    for (const auto &it : eventList) {
        if (it.address == nullptr) {
            FIT_LOG_ERROR("Addrss is nullptr.");
            continue;
        }

        if (it.sceneType != Fit::Heartbeat::SCENE_FIT_REGISTRY_SERVER) {
            FIT_LOG_DEBUG("Not process scene type = %s.", it.sceneType.c_str());
            continue;
        }
        // 由于角色类型数据和上下线数据一起过来，排除非角色状态数据，只处理主从类型数据
        if (it.eventType != Fit::Heartbeat::MASTER && it.eventType != Fit::Heartbeat::SLAVE &&
            it.eventType != Fit::Heartbeat::IDLE) {
            FIT_LOG_DEBUG("Not process event type = %s.", it.eventType.c_str());
            continue;
        }

        if (get_current_node_type(it, isMasterInThisTime)) {
            // set master
            Fit::Registry::SetMaster(isMasterInThisTime); // LCOV_EXCL_LINE
            Fit::string roleType = isMasterInThisTime ? Fit::Heartbeat::MASTER : Fit::Heartbeat::SLAVE;
            FIT_LOG_INFO("Role type change to = %s.", roleType.c_str());
            is_registry_master = isMasterInThisTime;
        }
    }
}
void remove_offline_fit_registry_server(const Fit::vector<fit::heartbeat::HeartbeatEvent> &eventList)
{
    for (const auto &it : eventList) {
        if (it.address == nullptr) {
            FIT_LOG_ERROR("Addrss is nullptr.");
            continue;
        }
        if (it.sceneType != Fit::Heartbeat::SCENE_FIT_REGISTRY_SERVER) {
            continue;
        }
        // 由于角色类型数据和上下线数据一起过来，排除非角色状态数据，只处理主从类型数据
        if (it.eventType != Fit::Heartbeat::OFFLINE) {
            continue;
        }

        if (Fit::Registry::IsMaster()) {
            // 将下线的注册中心地址从地址列表中删除
            delete_offline_registry(it);
        }
    }
}

void delete_offline_registry(const fit::heartbeat::HeartbeatEvent &event)
{
    if (event.eventType != Fit::Heartbeat::OFFLINE) {
        return;
    }
    fit::hakuna::kernel::registry::server::removeResgitryAddress removeResgitryAddressProxy;
    // in param
    ::fit::registry::Address address {};
    address.id = event.address->id;
    int32_t *removeResgitryAddressResult {nullptr};
    auto removeResgitryAddressRet = removeResgitryAddressProxy(&address, &removeResgitryAddressResult);
    if (removeResgitryAddressRet != FIT_OK || removeResgitryAddressResult == nullptr) {
        FIT_LOG_ERROR("removeResgitryAddressProxy failed, error code is %d.", removeResgitryAddressRet);
    }
    FIT_LOG_INFO("removeResgitryAddressProxy result %d, workId is %s..",
        removeResgitryAddressRet, address.id.c_str());
}

bool get_current_node_type(const fit::heartbeat::HeartbeatEvent &event, bool &isMaster)
{
    fit::registry::Address &address = *(event.address);
    auto &id = address.id;
    if (is_current_node(id)) {
        isMaster = (event.eventType == Fit::Heartbeat::MASTER);
        FIT_LOG_INFO("Current node role type = %s.", event.eventType.c_str());
        return true;
    } else {
        FIT_LOG_DEBUG("Not current node, id = %s.", id.c_str());
    }

    return false;
}

bool is_registry_client(const Fit::string &scene_type)
{
    for (auto item : Fit::Heartbeat::SUBSCRIBE_SCENE) {
        if (scene_type == item) {
            return true;
        }
    }
    return false;
}

void process_worker_node_event(const fit::heartbeat::HeartbeatEvent &event)
{
    Fit::string eventType = event.eventType;
    Fit::string id = event.address->id;
    bool isOnline = (eventType == Fit::Heartbeat::ONLINE);
    Fit::Registry::fit_registry_mgr::instance()->get_worker_status_listener()->add(
        Fit::Registry::worker_status_notify_t {
            .worker_id = id,
            .is_online = isOnline,
            .occur_time = std::chrono::steady_clock::now()
    });
}

void process_registry_client_scene(const Fit::vector<fit::heartbeat::HeartbeatEvent> &event_list)
{
    for (const auto &it : event_list) {
        if (it.address == nullptr) {
            FIT_LOG_ERROR("Addrss is nullptr.");
            continue;
        }
        auto &sceneType = it.sceneType;
        if (!is_registry_client(sceneType)) {
            FIT_LOG_DEBUG("Ignored, id = %s, scene type = %s, event type = %s.",
                it.address->id.c_str(), sceneType.c_str(), it.eventType.c_str());
            continue;
        }
        auto eventType = it.eventType;
        FIT_LOG_DEBUG("Id = %s, scene type = %s, event type = %s.",
            it.address->id.c_str(), sceneType.c_str(), eventType.c_str());
        // 由于角色类型数据和上下线数据一起过来，排除非在线状态数据，只处理在线状态数据
        if (eventType != Fit::Heartbeat::ONLINE && eventType != Fit::Heartbeat::OFFLINE) {
            continue;
        }
        // 需要存放到单任务队列处理，做一定的延迟，目前延迟1s，如果针对同一个进程有多个上下线消息则只保留最后一个；
        // 目的是为了减少不断重启进程场景短时间内发送多个上下线消息时，不断更新服务状态和进行服务状态订阅通知时的压力过大问题
        // 目前存储业务使用时，会造成ccdb查询压力多大，同时ccdb内部dpmm申请内存，造成繁忙时，dpmm内部分析锁由于有时长时间分配
        // 不到时间导致锁无法释放，其他访问dpmm的均会被堵住，造成系统不稳定，比如dntf线程堵住超过5s，会出发app_data自愈
        process_worker_node_event(it);
    }
}

FitCode heartbeat_address_change(ContextObj ctx,
    const Fit::vector<fit::heartbeat::HeartbeatEvent> *event_list,
    bool **result)
{
    if (event_list == nullptr || event_list->empty()) {
        FIT_LOG_ERROR("Param is empty or null.");
        return FIT_ERR_PARAM;
    }
    *result = Fit::Context::NewObj<bool>(ctx);
    if (*result == nullptr) {
        FIT_LOG_ERROR("Failed to new result.");
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    FIT_LOG_DEBUG("HeartbeatAddressChange");
    process_fit_registry_server_scene(*event_list);
    remove_offline_fit_registry_server(*event_list);
    process_registry_client_scene(*event_list);
    **result = true;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(heartbeat_address_change)
        .SetGenericId(fit::heartbeat::heartbeatAddressChange::GENERIC_ID)
        .SetFitableId("fit_registry_address_status_callback");
}
} // LCOV_EXCL_LINE