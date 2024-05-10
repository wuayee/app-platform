/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2020/6/23
 * Notes:       :
 */

#ifndef FIT_HEARTBEAT_MANAGER_H
#define FIT_HEARTBEAT_MANAGER_H

#include "service/fit_heartbeat_notify_service.h"
#include "service/fit_heartbeat_service.h"

#include <memory>
#include <vector>
#include <thread>

using fit_heartbeat_notify_service_ptr = std::shared_ptr<fit_heartbeat_notify_service>;
using fit_heartbeat_service_ptr = std::shared_ptr<fit_heartbeat_service>;

class fit_heartbeat_manager;
using fit_heartbeat_manager_ptr = std::shared_ptr<fit_heartbeat_manager>;

class fit_heartbeat_manager {
public:
    ~fit_heartbeat_manager();

    static fit_heartbeat_manager_ptr &instance();
    fit_heartbeat_notify_service &get_notify_service();
    fit_heartbeat_service &get_heartbeat_service();

    fit_heartbeat_manager(fit_heartbeat_manager &) = delete;
    fit_heartbeat_manager &operator=(fit_heartbeat_manager &) = delete;

private:
    fit_heartbeat_manager();
    fit_heartbeat_service_ptr heartbeatService_{};
    fit_heartbeat_notify_service_ptr notifyService_{};

    std::thread check_thread_ {};
    volatile bool is_exit_ {false};

    static fit_heartbeat_manager_ptr instance_;
};

#endif  // FIT_HEARTBEAT_MANAGER_H
