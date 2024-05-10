/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/31 13:33
 * Notes:       :
 */

#ifndef FIT_REGISTRY_WORKER_STATUS_LISTENER_H
#define FIT_REGISTRY_WORKER_STATUS_LISTENER_H

#include "service/fit_registry_service.h"

#include <chrono>
#include <fit/internal/registry/fit_registry_entities.h>
#include <fit/stl/string.hpp>
#include <mutex>
#include <fit/stl/list.hpp>

namespace Fit {
namespace Registry {
struct worker_status_notify_t {
    Fit::string worker_id;
    bool is_online;
    std::chrono::steady_clock::time_point occur_time;
};
class fit_registry_worker_status_listener {
public:
    explicit fit_registry_worker_status_listener(fit_registry_service_ptr fitable_service);
    ~fit_registry_worker_status_listener() = default;

    void add(const worker_status_notify_t &event);
    void process(uint32_t delay_time_ms);

protected:
    Fit::list<worker_status_notify_t> get_process_list(uint32_t delay_time_ms);
    void process(const worker_status_notify_t &event);
private:
    std::mutex mt_;
    Fit::list<worker_status_notify_t> notify_wait_list_ {};
    fit_registry_service_ptr fitable_service_ {};
};

using worker_status_listener_ptr = std::shared_ptr<fit_registry_worker_status_listener>;
}
}

#endif // FIT_REGISTRY_WORKER_STATUS_LISTENER_H
