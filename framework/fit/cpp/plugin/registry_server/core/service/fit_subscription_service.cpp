/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-31 15:08:16
 */

#include "fit_subscription_service.h"

#include "fit/fit_log.h"

#include <algorithm>
#include <thread>
#include <utility>

namespace Fit {
namespace Registry {
bool fit_subscription_service::Start()
{
    if (subscription_repository_ != nullptr) {
        subscription_repository_->Start();
    }
    return true;
}
bool fit_subscription_service::Stop()
{
    if (subscription_repository_ != nullptr) {
        subscription_repository_->Stop();
    }
    return true;
}
int32_t fit_subscription_service::query_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener,
    db_subscription_entry_t &result_subscription_entry) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return FIT_ERR_FAIL;
    }
    return subscription_repository_->query_subscription_entry(fitable, listener, result_subscription_entry);
}

db_subscription_set fit_subscription_service::query_subscription_set(const fit_fitable_key_t &fitable) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return {};
    }

    return subscription_repository_->query_subscription_set(fitable);
}

db_subscription_set fit_subscription_service::query_all_subscriptions() const
{
    if (subscription_repository_ == nullptr) {
        return {};
    }

    return subscription_repository_->query_all_subscriptions();
}

listener_set fit_subscription_service::query_listener_set(const fit_fitable_key_t& fitable) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return {};
    }

    return subscription_repository_->query_listener_set(fitable);
}

int32_t fit_subscription_service::insert_subscription_entry(
    const fit_fitable_key_t &fitable, const listener_t &listener) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return FIT_ERR_FAIL;
    }

    return subscription_repository_->insert_subscription_entry(fitable, listener);
}

int32_t fit_subscription_service::remove_subscription_entry(
    const fit_fitable_key_t &fitable, const listener_t &listener) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return FIT_ERR_FAIL;
    }

    return subscription_repository_->remove_subscription_entry(fitable, listener);
}
int32_t fit_subscription_service::SyncInsertSubscriptionEntry(
    const fit_fitable_key_t &key, const listener_t &listener) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return FIT_ERR_FAIL;
    }

    return subscription_repository_->SyncInsertSubscriptionEntry(key, listener);
}
int32_t fit_subscription_service::SyncRemoveSubscriptionEntry(
    const fit_fitable_key_t &key, const listener_t &listener) const
{
    if (subscription_repository_ == nullptr) {
        FIT_LOG_ERROR("%s", "No valid subscription repository");
        return FIT_ERR_FAIL;
    }

    return subscription_repository_->SyncRemoveSubscriptionEntry(key, listener);
}
}
} // LCOV_EXCL_BR_LINE