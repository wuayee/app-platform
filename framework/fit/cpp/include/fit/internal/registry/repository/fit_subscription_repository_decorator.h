/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020-09-15
 * Notes:       :
 */
#ifndef FIT_SUBSCRIPTION_REPOSITORY_DECORATOR_H
#define FIT_SUBSCRIPTION_REPOSITORY_DECORATOR_H
#include <fit/fit_log.h>
#include "fit_subscription_repository.h"
class FitSubscriptionRepositoryDecorator : public fit_subscription_repository {
public:
    explicit FitSubscriptionRepositoryDecorator(fit_subscription_repository_ptr subscriptionRepo)
        : subscriptionRepo_(subscriptionRepo)
    {}
    virtual ~FitSubscriptionRepositoryDecorator() = default;
    listener_set query_listener_set(const fit_fitable_key_t &fitableKey) override
    {
        if (subscriptionRepo_ == nullptr) {
            FIT_LOG_ERROR("SubscriptionRepo_ is nullptr.");
            return listener_set {};
        }
        return subscriptionRepo_->query_listener_set(fitableKey);
    }

    db_subscription_set query_subscription_set(const fit_fitable_key_t &fitableKey)  override
    {
        if (subscriptionRepo_ == nullptr) {
            FIT_LOG_ERROR("SubscriptionRepo_ is nullptr.");
            return db_subscription_set {};
        }
        return subscriptionRepo_->query_subscription_set(fitableKey);
    }

    db_subscription_set query_all_subscriptions() const
    {
        if (subscriptionRepo_ == nullptr) {
            FIT_LOG_ERROR("SubscriptionRepo_ is nullptr.");
            return db_subscription_set {};
        }
        return subscriptionRepo_->query_all_subscriptions();
    }

    int32_t query_subscription_entry(const fit_fitable_key_t &fitableKey, const listener_t &listener,
        db_subscription_entry_t &result_subscription_entry) const override
    {
        if (subscriptionRepo_ == nullptr) {
            FIT_LOG_ERROR("SubscriptionRepo_ is nullptr.");
            return FIT_ERR_FAIL;
        }
        return subscriptionRepo_->query_subscription_entry(fitableKey, listener, result_subscription_entry);
    }

    int32_t insert_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener) override
    {
        if (subscriptionRepo_ == nullptr) {
            FIT_LOG_ERROR("SubscriptionRepo_ is nullptr.");
            return FIT_ERR_FAIL;
        }
        return subscriptionRepo_->insert_subscription_entry(fitable, listener);
    }

    int32_t remove_subscription_entry(const fit_fitable_key_t &fitable, const listener_t &listener) override
    {
        if (subscriptionRepo_ == nullptr) {
            FIT_LOG_ERROR("SubscriptionRepo_ is nullptr.");
            return FIT_ERR_FAIL;
        }
        return subscriptionRepo_->remove_subscription_entry(fitable, listener);
    }

private:
    fit_subscription_repository_ptr subscriptionRepo_ {nullptr};
};

using FitSubscriptionRepositoryDecoratorPtr = std::shared_ptr<FitSubscriptionRepositoryDecorator>;
class FitSubscriptionRepositoryDecoratorFactory final {
public:
    static FitSubscriptionRepositoryDecoratorPtr Create(fit_subscription_repository_ptr subscriptionRepository);
};
#endif
