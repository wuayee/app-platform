/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-28 14:59:07
 */

#ifndef FIT_SUBSCRIBED_SERVICE_H
#define FIT_SUBSCRIBED_SERVICE_H

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>
#include <utility>
#include <vector>
#include <fit/external/util/context/context_api.hpp>

#include "fit/internal/registry/fit_registry_entities.h"
#include "service/fit_registry_service.h"
#include "service/fit_subscription_service.h"

namespace Fit {
namespace Registry {
class service_publisher {
public:
    service_publisher(const Fit::fitable_id& fitable,
        fit_registry_service_ptr service,
        fit_subscription_service_ptr subscriber_service)
        : fitable_(fitable),
          fitable_service_(std::move(service)),
          subscriber_service_(std::move(subscriber_service))
    {
    }

    ~service_publisher() = default;

    const listener_set& get_listeners() const
    {
        return listeners_;
    }

    int32_t update_subscription_entry();

    void notify_all();

protected:
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableInstance>
        build_notify_fitable_addresses(ContextObj ctx) const;
    bool is_valid_address(const listener_t& listener, const Fit::string& notifyFitableGenericableId) const;
    void notify_entry(std::function<int32_t(const listener_t&)> f, const listener_t& listener) const;
private:
    int32_t notify(const listener_t& listener);

    int32_t query_subscribers();

    int32_t query_services();

    listener_set listeners_ {};
    db_service_set serviceSet_ {};

    const Fit::fitable_id fitable_ {};
    fit_registry_service_ptr fitable_service_;
    fit_subscription_service_ptr subscriber_service_;
};
}
}

#endif