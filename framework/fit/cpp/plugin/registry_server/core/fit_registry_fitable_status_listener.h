/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/31 14:54
 * Notes:       :
 */

#ifndef FIT_REGISTRY_FITABLE_STATUS_LISTENER_H
#define FIT_REGISTRY_FITABLE_STATUS_LISTENER_H

#include "fit/internal/fit_fitable.h"
#include "service/fit_registry_service.h"
#include "service/fit_subscription_service.h"

#include <list>
#include <mutex>

namespace Fit {
namespace Registry {
class fit_registry_fitable_status_listener {
public:
    fit_registry_fitable_status_listener(fit_subscription_service_ptr subscription_service,
        fit_registry_service_ptr fitable_service);
    ~fit_registry_fitable_status_listener() = default;

    void add(const Fit::fitable_id &fitable);
    // @return true-has undo job, false-no job
    bool process();

protected:
    list<Fit::fitable_id> get_process_list();
    void process(const Fit::fitable_id &fitable);

private:
    std::mutex mt_;
    list<Fit::fitable_id> notify_wait_list_ {};

    fit_subscription_service_ptr subscription_service_ {};
    fit_registry_service_ptr fitable_service_ {};
};

using fitable_status_listener_ptr = std::shared_ptr<fit_registry_fitable_status_listener>;
}
}

#endif // FIT_REGISTRY_FITABLE_STATUS_LISTENER_H
