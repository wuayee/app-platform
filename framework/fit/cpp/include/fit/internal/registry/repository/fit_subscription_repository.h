/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-08-31 10:52:03
 */

#ifndef FIT_SUBSCRIPTION_REPOSITORY_H
#define FIT_SUBSCRIPTION_REPOSITORY_H

#include "../fit_registry_entities.h"

class fit_subscription_repository {
public:
    virtual ~fit_subscription_repository()= default;
    virtual bool Start() = 0;
    virtual bool Stop() = 0;

    virtual listener_set query_listener_set(const fit_fitable_key_t &fitable_key) = 0;

    virtual db_subscription_set query_subscription_set(const fit_fitable_key_t &fitable_key) = 0;

    virtual db_subscription_set query_all_subscriptions() const
    {
        return {};
    }

    virtual int32_t query_subscription_entry(const fit_fitable_key_t &fitable_key, const listener_t &listener,
        db_subscription_entry_t &result_subscription_entry) const = 0;

    virtual int32_t insert_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener) = 0;

    virtual int32_t remove_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener) = 0;

    virtual Fit::unordered_set<Fit::string> query_all_listener_ids() const
    {
        return {};
    }
    virtual ListenerSet query_all_listeners() const
    {
        return {};
    }
    virtual int32_t remove_subscription_entry(const Fit::unordered_set<Fit::string>& listenerIds)
    {
        return FIT_OK;
    }
};

using fit_subscription_repository_ptr = std::shared_ptr<fit_subscription_repository>;

class fit_subscription_repository_factory final {
public:
    static fit_subscription_repository_ptr Create();
};
#endif