/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : heartbeat_manager
 * Author       : songyongtan
 * Date         : 2020/6/23
 */

#include "fit_heartbeat_manager.h"

#include <fit/fit_log.h>
#include "fit_heartbeat_server_conf.h"

fit_heartbeat_manager_ptr fit_heartbeat_manager::instance_;

fit_heartbeat_manager_ptr &fit_heartbeat_manager::instance()
{
    static std::once_flag once;
    std::call_once(once, [] {
        instance_ = std::shared_ptr<fit_heartbeat_manager>(new fit_heartbeat_manager);
    });
    return instance_;
}

fit_heartbeat_manager::fit_heartbeat_manager()
{
    auto heartbeatRepository = fit_heartbeat_repository_factory::create();
    auto scene_subscribe_repository = fit_scene_subscribe_repository_factory::create();

    notifyService_ = std::make_shared<fit_heartbeat_notify_service>(scene_subscribe_repository);
    heartbeatService_ = std::make_shared<fit_heartbeat_service>(
        notifyService_, heartbeatRepository, fit_heartbeat_service::get_current_time_ms());

    check_thread_ = std::thread([this]() {
        while (!is_exit_) {
            std::this_thread::sleep_for(
                std::chrono::milliseconds(fit_heartbeat_server_conf::HEARTBEAT_CHECK_WAIT_TIME_MS));
            heartbeatService_->check();
        }
    });
}

fit_heartbeat_manager::~fit_heartbeat_manager()
{
    is_exit_ = true;
    if (check_thread_.joinable()) {
        check_thread_.join();
    }
}

fit_heartbeat_notify_service &fit_heartbeat_manager::get_notify_service()
{
    return *notifyService_;
}

fit_heartbeat_service &fit_heartbeat_manager::get_heartbeat_service()
{
    return *heartbeatService_;
}
