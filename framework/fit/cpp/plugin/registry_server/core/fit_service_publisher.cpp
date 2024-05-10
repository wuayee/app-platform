/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-28 14:47:14
 */

#include "fit_service_publisher.h"
#include <sstream>
#include <algorithm>
#include <fit/internal/fit_address_utils.h>
#include <fit/fit_log.h>
#include <fit/internal/fit_system_property_utils.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_notify_fitables/1.0.0/cplusplus/notifyFitables.hpp>
#include <registry_server_memory/common/util.h>

namespace Fit {
namespace Registry {
using ::fit::hakuna::kernel::registry::shared::FitableInstance;

int32_t service_publisher::update_subscription_entry()
{
    if (query_subscribers() != FIT_ERR_SUCCESS) {
        return FIT_ERR_FAIL;
    }

    query_services();

    return FIT_ERR_SUCCESS;
}

void service_publisher::notify_all()
{
    auto ret = update_subscription_entry();
    if (ret == FIT_ERR_SUCCESS) {
        std::for_each(
            listeners_.cbegin(), listeners_.cend(), [this](const listener_t& listener) {
                if (!is_valid_address(listener, fit::hakuna::kernel::registry::server::notifyFitables::GENERIC_ID)) {
                    FIT_LOG_INFO("Do not notify, invalid target address. (listenerAddress=%s, callbackFitId=%s, "
                                  "targetFitable=%s:%s:%s.",
                        fit_address_utils::convert_to_string(listener.address).c_str(), listener.fitable_id.c_str(),
                        fitable_.generic_id.c_str(), fitable_.fitable_id.c_str(), fitable_.fitable_version.c_str());
                    return;
                }
                notify_entry(std::bind(&service_publisher::notify, this, std::placeholders::_1), listener);
            });
    }
}

Fit::vector<FitableInstance> service_publisher::build_notify_fitable_addresses(
    ContextObj ctx) const
{
    ::fit::hakuna::kernel::shared::Fitable fitabletemp;
    fitabletemp.genericableId = fitable_.generic_id;
    fitabletemp.genericableVersion = fitable_.generic_version;
    fitabletemp.fitableId = fitable_.fitable_id;
    fitabletemp.fitableVersion = fitable_.fitable_version;

    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableInstance> result {};
    result.emplace_back(Aggregate(fitabletemp, serviceSet_, ctx));
    return result;
}
int32_t service_publisher::notify(const listener_t& listener)
{
    int ret = FIT_ERR_FAIL;
    fit::hakuna::kernel::registry::server::notifyFitables notifyFitablesProxy;

    // in param
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableInstance> targets
        = build_notify_fitable_addresses(notifyFitablesProxy.ctx_);
    // 指定地址调用
    notifyFitablesProxy.Route([listener](const Fit::RouteFilterParam &param) -> bool {
        return param.fitableId == listener.fitable_id;
    }).Get([listener](const Fit::LBFilterParam &param) -> bool {
        return param.workerId == listener.address.id;
    }).Exec(&targets, [this, &listener, &ret](const Fit::CallBackInfo *cb) ->FitCode {
        ret = cb->code;
        return ret;
    });

    return ret;
}

int32_t service_publisher::query_subscribers()
{
    if (!subscriber_service_) {
        FIT_LOG_ERROR("%s", "No subscriber service.");
        return FIT_ERR_FAIL;
    }

    auto listeners = subscriber_service_->query_listener_set(
        get_fitable_key_from_fitable(fitable_));
    listeners_.swap(listeners);

    if (listeners_.empty()) {
        return FIT_ERR_NOT_FOUND;
    }

    std::ostringstream result;
    result << "fitable = [" << fitable_.generic_id
        << ":" << fitable_.fitable_id << "]"
        << ", version = [" << fitable_.fitable_version
        << "]";
    FIT_LOG_DEBUG("Find listeners, size=%lu, %s.", listeners_.size(), result.str().c_str());
    return FIT_ERR_SUCCESS;
}

int32_t service_publisher::query_services()
{
    if (!fitable_service_) {
        return FIT_ERR_FAIL;
    }
    fit_fitable_key_t key;
    key.generic_id = fitable_.generic_id;
    key.generic_version = fitable_.generic_version;
    key.fitable_id = fitable_.fitable_id;
    auto serviceSet = fitable_service_->get_services(key);
    serviceSet_ = serviceSet;
    std::ostringstream result;
    result << "fitable = [" << fitable_.generic_id
        << ":" << fitable_.fitable_id << "]"
        << ", version = [" << fitable_.generic_version
        << "]";
    FIT_LOG_DEBUG("Find fitable service, size=%lu %s.", serviceSet_.size(),
        result.str().c_str());

    return FIT_ERR_SUCCESS;
}

bool service_publisher::is_valid_address(const listener_t& listener, const Fit::string& listener_generic_id) const
{
    if (!fitable_service_) {
        FIT_LOG_ERROR("%s", "No valid fitable service.");
        return false;
    }
    fit_fitable_key_t key;
    key.generic_id = listener_generic_id;
    key.fitable_id = listener.fitable_id;
    key.generic_version = "1.0.0";

    return !(fitable_service_->QueryService(key, listener.address).empty());
}
void service_publisher::notify_entry(std::function<int32_t(const listener_t&)> f, const listener_t& listener) const
{
    auto ret = f(listener);
    if (ret == FIT_OK) {
        FIT_LOG_DEBUG("Notify listener successfully. [listenerAddress=%s, callbackFitId=%s, targetFitable=(%s:%s:%s)]",
            fit_address_utils::convert_to_string(listener.address).c_str(), listener.fitable_id.c_str(),
            fitable_.generic_id.c_str(), fitable_.fitable_id.c_str(), fitable_.fitable_version.c_str());
    } else {
        FIT_LOG_ERROR(
            "Failed to notify listener. [ret=%x, listenerAddress=%s, callbackFitId=%s, targetFitable=(%s:%s:%s)]", ret,
            fit_address_utils::convert_to_string(listener.address).c_str(), listener.fitable_id.c_str(),
            fitable_.generic_id.c_str(), fitable_.fitable_id.c_str(), fitable_.fitable_version.c_str());
    }
}
}
} // LCOV_EXCL_BR_LINE