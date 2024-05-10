/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-31 10:54:27
 */

#ifndef FIT_SUBSCRIPTION_REPOSITORY_TEST_H
#define FIT_SUBSCRIPTION_REPOSITORY_TEST_H

#include "fit/internal/registry/repository/fit_subscription_repository.h"

#include <string>
#include <unordered_map>

class fit_subscription_repository_test : public fit_subscription_repository {
public:
    bool Start() override;
    bool Stop() override;
    int32_t insert_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener) override;

    int32_t remove_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener) override;

    db_subscription_set query_subscription_set(const fit_fitable_key_t &fitable_key) override;

    db_subscription_set query_all_subscriptions() const override;

    listener_set query_listener_set(const fit_fitable_key_t &fitable_key) override;

    int32_t query_subscription_entry(
        const fit_fitable_key_t &fitable_key, const listener_t &listener,
        db_subscription_entry_t &result_subscription_entry) const override;

    int32_t query_subscription_db_key(
        const fit_fitable_key_t &fitable, const listener_t &listener, Fit::string &db_key) const;

private:
    bool is_subscription_entry_exists(const fit_fitable_key_t &fitable, const listener_t &listener) const;

    using fit_fitable_to_listener_set =
    std::unordered_multimap<fit_fitable_key_t, listener_t, fitable_key_hasher>;
    fit_fitable_to_listener_set fitable_to_listener_set_ {};
};

#endif