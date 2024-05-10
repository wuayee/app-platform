/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-31 12:48:00
 */

#ifndef FIT_SUBSCRIPTION_SERVICE_H
#define FIT_SUBSCRIPTION_SERVICE_H
#include <memory>
#include <utility>
#include <fit/internal/registry/repository/fit_subscription_memory_repository.h>
#include "fit_registry_service.h"

namespace Fit {
namespace Registry {
class fit_subscription_service {
public:
    explicit fit_subscription_service(FitSubscriptionMemoryRepositoryPtr repo_ptr)
        : subscription_repository_(std::move(repo_ptr))
    {
    }
    ~fit_subscription_service() = default;
    bool Start();
    bool Stop();
    int32_t query_subscription_entry(const fit_fitable_key_t& fitable, const listener_t& listener,
        db_subscription_entry_t& result_subscription_entry) const;

    db_subscription_set query_subscription_set(const fit_fitable_key_t& fitable) const;

    db_subscription_set query_all_subscriptions() const;

    listener_set query_listener_set(const fit_fitable_key_t& fitable) const;

    int32_t insert_subscription_entry(const fit_fitable_key_t& fitable, const listener_t& listener) const;

    int32_t remove_subscription_entry(const fit_fitable_key_t& fitable, const listener_t& listener) const;

    int32_t SyncInsertSubscriptionEntry(const fit_fitable_key_t &key, const listener_t &listener) const;
    int32_t SyncRemoveSubscriptionEntry(const fit_fitable_key_t &key, const listener_t &listener) const;

private:
    FitSubscriptionMemoryRepositoryPtr subscription_repository_ {};
};

using fit_subscription_service_ptr = std::shared_ptr<fit_subscription_service>;
}
}

#endif