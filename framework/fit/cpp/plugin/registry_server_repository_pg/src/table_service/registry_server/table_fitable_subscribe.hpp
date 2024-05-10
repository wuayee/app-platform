/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for t_fitable_subscribe table in registry server
 * Author       : x00649642
 * Create       : 2023-11-24
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_TABLE_FITABLE_SUBSCRIBE_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_TABLE_FITABLE_SUBSCRIBE_HPP

#include "fit/fit_code.h"
#include "fit/internal/registry/repository/fit_subscription_repository.h"

#include "abstract_sql_exec_result.hpp"
#include "sql_wrapper/sql_builder.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class TableFitableSubscribe : public fit_subscription_repository {
    using Subscription = db_subscription_entry_t;
    using Listener = fit_listener_info_t;
    using Fitable = fit_fitable_key_t;

public:
    TableFitableSubscribe() = default;
    ~TableFitableSubscribe() override = default;

    bool Start() override;
    bool Stop() override;

    FitCode insert_subscription_entry(const Fitable& fitable, const Listener& listener) override;

    FitCode remove_subscription_entry(const Fitable& fitable, const Listener& listener) override;

    Fit::vector<Subscription> query_subscription_set(const Fitable& fitable) override;
    Fit::vector<Subscription> query_all_subscriptions() const override;
    Fit::vector<Listener> query_listener_set(const Fitable& fitable) override;
    FitCode query_subscription_entry(const Fitable& fitable, const Listener& listener,
                                     Subscription& resultCollector) const override;

private:
    FitCode QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                         Fit::vector<Subscription>& resultCollector) const;
    FitCode QueryHandler(const Fit::Pg::SqlCmd& sqlCmd, const std::function<void(const char*)>& logFunc,
                         const std::function<Fit::vector<Subscription>(SqlExecResultPtr&)>& parser,
                         Fit::vector<Subscription>& resultCollector) const;
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_TABLE_FITABLE_SUBSCRIBE_HPP
