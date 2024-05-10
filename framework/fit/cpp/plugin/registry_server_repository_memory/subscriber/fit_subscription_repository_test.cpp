/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-31 11:09:53
 */

#include "fit_subscription_repository_test.h"

#include "fit/fit_log.h"

#include <algorithm>
bool fit_subscription_repository_test::Start()
{
    return true;
}
bool fit_subscription_repository_test::Stop()
{
    return true;
}
int32_t fit_subscription_repository_test::insert_subscription_entry(
    const fit_fitable_key_t &fitable, const listener_t &listener)
{
    if (!is_subscription_entry_exists(fitable, listener)) {
        fitable_to_listener_set_.emplace(fitable, listener);

        FIT_LOG_INFO(
            "Insert subscription entry: [Fitable: %s, Listener: {%s, %s}]",
            fitable.fitable_id.c_str(),
            listener.fitable_id.c_str(),
            listener.address.ip.c_str());
    }

    return FIT_ERR_SUCCESS;
}

int32_t fit_subscription_repository_test::remove_subscription_entry(
    const fit_fitable_key_t &fitable, const listener_t &listener)
{
    auto result_range = fitable_to_listener_set_.equal_range(fitable);

    for (auto it = result_range.first; it != result_range.second; it++) {
        if (it->second == listener) {
            fitable_to_listener_set_.erase(it);

            FIT_LOG_INFO(
                "Remove subscription entry: [Fitable: %s, Listener: {%s, %s}]",
                fitable.fitable_id.c_str(),
                listener.fitable_id.c_str(),
                listener.address.ip.c_str());
            break;
        }
    }

    return FIT_ERR_SUCCESS;
}

db_subscription_set fit_subscription_repository_test::query_subscription_set(
    const fit_fitable_key_t &fitable_key)
{
    const auto result_range = fitable_to_listener_set_.equal_range(fitable_key);
    const auto result_size = std::distance(result_range.first, result_range.second);

    db_subscription_set subscriptions {};
    subscriptions.reserve(result_size);
    std::transform(
        result_range.first,
        result_range.second,
        std::back_inserter(subscriptions),
        [](const std::pair<fit_fitable_key_t, listener_t> &pair) -> db_subscription_entry_t {
            db_subscription_entry_t subscription;
            subscription.fitable_key = pair.first;
            subscription.listener = pair.second;
            return subscription;
        });

    return subscriptions;
}

db_subscription_set fit_subscription_repository_test::query_all_subscriptions() const
{
    db_subscription_set subscriptions {};
    subscriptions.reserve(fitable_to_listener_set_.size());
    std::transform(
        fitable_to_listener_set_.cbegin(),
        fitable_to_listener_set_.cend(),
        std::back_inserter(subscriptions),
        [](const std::pair<fit_fitable_key_t, listener_t> &pair) -> db_subscription_entry_t {
            db_subscription_entry_t subscription;
            subscription.fitable_key = pair.first;
            subscription.listener = pair.second;
            return subscription;
        });

    return subscriptions;
}

listener_set fit_subscription_repository_test::query_listener_set(const fit_fitable_key_t &fitable_key)
{
    auto result_range = fitable_to_listener_set_.equal_range(fitable_key);
    auto result_size = std::distance(result_range.first, result_range.second);

    listener_set listeners {};
    listeners.reserve(result_size);
    std::transform(
        result_range.first,
        result_range.second,
        std::back_inserter(listeners),
        [](const std::pair<fit_fitable_key_t, listener_t> &pair) -> listener_t { return pair.second; });

    return listeners;
}

int32_t fit_subscription_repository_test::query_subscription_entry(
    const fit_fitable_key_t &fitable_key, const listener_t &listener,
    db_subscription_entry_t &result_subscription_entry) const
{
    auto result_range = fitable_to_listener_set_.equal_range(fitable_key);
    auto result_iter = std::find_if(
        result_range.first,
        result_range.second,
        [&listener](const std::pair<fit_fitable_key_t, listener_t> &pair) {
            return pair.second == listener;
        });
    if (result_iter != result_range.second) {
        result_subscription_entry.fitable_key = result_iter->first;
        result_subscription_entry.listener = result_iter->second;

        FIT_LOG_INFO(
            "Find subscription entry for fitable %s, listener {%s,%s}",
            fitable_key.fitable_id.c_str(),
            listener.fitable_id.c_str(),
            listener.address.ip.c_str());
        return FIT_ERR_SUCCESS;
    }

    return FIT_ERR_FAIL;
}

int32_t fit_subscription_repository_test::query_subscription_db_key(
    const fit_fitable_key_t &fitable, const listener_t &listener, Fit::string &db_key) const
{
    db_key = "key";
    return FIT_ERR_SUCCESS;
}

bool fit_subscription_repository_test::is_subscription_entry_exists(
    const fit_fitable_key_t &fitable, const listener_t &listener) const
{
    db_subscription_entry_t _;

    return query_subscription_entry(fitable, listener, _) == FIT_ERR_SUCCESS;
}
