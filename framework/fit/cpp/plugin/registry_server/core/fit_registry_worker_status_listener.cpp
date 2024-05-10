/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/31 13:33
 * Notes:       :
 */

#include "fit_registry_worker_status_listener.h"

#include <algorithm>
#include <utility>
#include "fit/fit_log.h"

namespace Fit {
namespace Registry {
using std::chrono::duration_cast;
using std::chrono::milliseconds;
using std::chrono::steady_clock;

fit_registry_worker_status_listener::fit_registry_worker_status_listener(
    fit_registry_service_ptr fitable_service)
    : fitable_service_(std::move(fitable_service)) {}

void fit_registry_worker_status_listener::add(const worker_status_notify_t &event)
{
    if (event.is_online) {
        return;
    }
    std::lock_guard<std::mutex> guard(mt_);
    auto remove_iter = std::remove_if(notify_wait_list_.begin(), notify_wait_list_.end(),
        [&event](const worker_status_notify_t &item) { return item.worker_id == event.worker_id; });
    notify_wait_list_.erase(remove_iter, notify_wait_list_.end());
    notify_wait_list_.push_back(event);
}

void fit_registry_worker_status_listener::process(uint32_t delay_time_ms)
{
    list<worker_status_notify_t> process_events = get_process_list(delay_time_ms);
    for (const auto &item : process_events) {
        process(item);
    }
}

Fit::list<worker_status_notify_t> fit_registry_worker_status_listener::get_process_list(uint32_t delay_time_ms)
{
    auto current = steady_clock::now();
    list<worker_status_notify_t> result;
    std::lock_guard<std::mutex> guard(mt_);
    for (auto &item : notify_wait_list_) {
        if (duration_cast<milliseconds>(current - item.occur_time).count() >= delay_time_ms) {
            result.push_back(item);
        }
    }

    notify_wait_list_.erase(std::remove_if(notify_wait_list_.begin(), notify_wait_list_.end(),
        [&delay_time_ms, &current](const worker_status_notify_t &item) {
            return duration_cast<milliseconds>(current - item.occur_time).count() >= delay_time_ms;
        }),
        notify_wait_list_.end());

    return result;
}

void fit_registry_worker_status_listener::process(const worker_status_notify_t &event)
{
    FIT_LOG_CORE("Online status is %d, workerId is %s.",
        static_cast<int32_t>(event.is_online), event.worker_id.c_str());
    if (event.is_online) {
        return;
    }
    if (!fitable_service_) {
        FIT_LOG_ERROR("null fitable service");
        return;
    }
    Fit::fit_address address;
    address.id = event.worker_id;
    fitable_service_->Remove(address);
}
}
} // LCOV_EXCL_LINE