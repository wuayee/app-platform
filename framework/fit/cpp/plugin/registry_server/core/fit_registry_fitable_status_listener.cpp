/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/31 14:54
 * Notes:       :
 */

#include "fit_registry_fitable_status_listener.h"
#include <algorithm>
#include "fit_service_publisher.h"

namespace Fit {
namespace Registry {
fit_registry_fitable_status_listener::fit_registry_fitable_status_listener(
    fit_subscription_service_ptr subscription_service,
    fit_registry_service_ptr fitable_service)
    : subscription_service_(std::move(subscription_service)),
      fitable_service_(std::move(fitable_service)) {}

void fit_registry_fitable_status_listener::add(const Fit::fitable_id &fitable)
{
    std::lock_guard<std::mutex> guard(mt_);
    auto remove_iter = std::remove_if(notify_wait_list_.begin(), notify_wait_list_.end(),
        [&fitable](const Fit::fitable_id &item) { return fit_fitable_id_equal_to()(fitable, item); });
    notify_wait_list_.erase(remove_iter, notify_wait_list_.end());
    notify_wait_list_.push_back(fitable);
}

bool fit_registry_fitable_status_listener::process()
{
    list<Fit::fitable_id> process_events = get_process_list();
    for (const auto &item : process_events) {
        process(item);
    }

    return !notify_wait_list_.empty();
}

list<Fit::fitable_id> fit_registry_fitable_status_listener::get_process_list()
{
    list<Fit::fitable_id> result;
    std::lock_guard<std::mutex> guard(mt_);
    if (!notify_wait_list_.empty()) {
        result.push_back(notify_wait_list_.front());
        notify_wait_list_.pop_front();
    }

    return result;
}

void fit_registry_fitable_status_listener::process(const Fit::fitable_id &fitable)
{
    service_publisher(fitable, fitable_service_, subscription_service_).notify_all();
}
}
} // LCOV_EXCL_LINE