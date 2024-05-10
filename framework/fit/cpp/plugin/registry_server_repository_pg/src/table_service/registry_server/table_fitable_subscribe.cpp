/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : table operation for t_fitable_subscribe table in registry server
 * Author       : x00649642
 * Create       : 2023-11-24
 * Notes:       :
 */
#include "table_fitable_subscribe.hpp"

#include "fit/fit_log.h"

#include "connection_pool.hpp"
#include "table_service/utils/entity_convertor.hpp"
#include "table_service/utils/field_declare_helper.hpp"

using namespace Fit::Repository::Pg;
namespace {
constexpr const char* TABLE_NAME = "registry_fitable_subscribe";
using Subscription = db_subscription_entry_t;
using Listener = fit_listener_info_t;
using Fitable = fit_fitable_key_t;
using SqlBuilderT = Fit::Pg::SqlBuilder<Subscription>;
using ColumnDescT = SqlBuilderT::ColumnDescT;
namespace Column {
constexpr const char* ID = "subscriber_id";
constexpr const char* HOST = "subscriber_host";
constexpr const char* PORT = "subscriber_port";
constexpr const char* PROTOCOL = "subscriber_protocol";
constexpr const char* CALLBACK_FITABLE_ID = "subscriber_callback_fitid";
constexpr const char* SUBSCRIBED_GENERIC_ID = "subscribed_generic_id";
constexpr const char* SUBSCRIBED_GENERIC_VERSION = "subscribed_generic_version";
constexpr const char* SUBSCRIBED_FITABLE_ID = "subscribed_fitid";
constexpr const char* CONSTRAINT_INDEX = "registry_fitable_subscribe_index";

ColumnDescT idDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(ID, Subscription, listener.address.id);
ColumnDescT hostDesc = PQ_HELPER_TYPE_VARCHAR_DECLARE(HOST, Subscription, listener.address.ip);
ColumnDescT portDesc = PQ_HELPER_TYPE_INT_DECLARE(PORT, Subscription, listener.address.port);
ColumnDescT callbackFitableIdDesc =
    PQ_HELPER_TYPE_VARCHAR_DECLARE(CALLBACK_FITABLE_ID, Subscription, listener.fitable_id);
ColumnDescT subscribedGenericIdDesc =
    PQ_HELPER_TYPE_VARCHAR_DECLARE(SUBSCRIBED_GENERIC_ID, Subscription, fitable_key.generic_id);
ColumnDescT subscribedGenericVersionDesc =
    PQ_HELPER_TYPE_VARCHAR_DECLARE(SUBSCRIBED_GENERIC_VERSION, Subscription, fitable_key.generic_version);
ColumnDescT subscribedFitableIdDesc =
    PQ_HELPER_TYPE_VARCHAR_DECLARE(SUBSCRIBED_FITABLE_ID, Subscription, fitable_key.fitable_id);
ColumnDescT protocolDesc{
    Column::PROTOCOL, Fit::Pg::TYPE_INT,
    [](const Subscription& subscription, Fit::vector<Fit::string>& holder) {
        holder.emplace_back(Fit::to_string(static_cast<uint32_t>(subscription.listener.address.protocol)));
        return holder.back().c_str();
    },
    [](Fit::string value, Subscription& subscription) {
        subscription.listener.address.protocol = static_cast<Fit::fit_protocol_type>(stoull(value));
    }};
}  // namespace Column

struct SubscriptionConvertor : public EntityConvertor<Subscription> {
    Fit::vector<ColumnDescT> GetAllColumns()
    {
        return {Column::hostDesc,
                Column::portDesc,
                Column::protocolDesc,
                Column::callbackFitableIdDesc,
                Column::subscribedGenericIdDesc,
                Column::subscribedGenericVersionDesc,
                Column::subscribedFitableIdDesc,
                Column::idDesc};
    }
};
SubscriptionConvertor g_convertor;
}  // namespace

bool TableFitableSubscribe::Start()
{
    return true;
}

bool TableFitableSubscribe::Stop()
{
    return true;
}

FitCode TableFitableSubscribe::insert_subscription_entry(const Fitable& fitable, const Listener& listener)
{
    Subscription entity;
    entity.fitable_key = fitable;
    entity.listener = listener;
    auto sqlCmd =
        SqlBuilderT::BuildInsertOrUpdate(TABLE_NAME, g_convertor.GetAllColumns(), Column::CONSTRAINT_INDEX, {}, entity);
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Save failed, error message: %s params: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr",
                      g_convertor.ToLogString(entity).c_str());
        return FIT_ERR_FAIL;
    }
    return (result->CountAffected() == 1) ? FIT_ERR_SUCCESS : FIT_ERR_FAIL;
}

FitCode TableFitableSubscribe::remove_subscription_entry(const Fitable& fitable, const listener_t& listener)
{
    Subscription entity;
    entity.fitable_key = fitable;
    entity.listener = listener;
    vector<ColumnDescT> columns{Column::idDesc, Column::callbackFitableIdDesc, Column::subscribedGenericIdDesc,
                                Column::subscribedGenericVersionDesc, Column::subscribedFitableIdDesc};
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, columns, entity);
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        FIT_LOG_ERROR("Delete failed, error message: %s params: %s.",
                      result ? result->GetErrorMessage() : "result is nullptr",
                      g_convertor.ToLogString(entity, columns).c_str());
        return FIT_ERR_FAIL;
    }
    return FIT_ERR_SUCCESS;
}

Fit::vector<Subscription> TableFitableSubscribe::query_subscription_set(const Fitable& fitable)
{
    Fit::vector<Subscription> resultCollector;
    Subscription inParam;
    inParam.fitable_key = fitable;
    Fit::vector<ColumnDescT> columns{Column::subscribedGenericIdDesc, Column::subscribedGenericVersionDesc,
                                     Column::subscribedFitableIdDesc};
    (void)QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), columns, inParam),
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query by fitable failed, error message: %s params: %s.", errorMsg,
                          g_convertor.ToLogString(inParam, columns).c_str());
        },
        resultCollector);
    return resultCollector;
}

Fit::vector<Subscription> TableFitableSubscribe::query_all_subscriptions() const
{
    Fit::vector<Subscription> resultCollector;
    (void)QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), {}, {}),
        [&](const char* errorMsg) { FIT_LOG_ERROR("Query all failed, error message: %s.", errorMsg); },
        resultCollector);
    return resultCollector;
}

Fit::vector<Listener> TableFitableSubscribe::query_listener_set(const Fitable& fitable)
{
    Fit::vector<Subscription> resultCollector;
    Fit::vector<Listener> finalResults;
    Subscription inParam;
    inParam.fitable_key = fitable;
    Fit::vector<ColumnDescT> selectColumns{Column::idDesc, Column::hostDesc, Column::portDesc, Column::protocolDesc,
                                           Column::callbackFitableIdDesc};
    Fit::vector<ColumnDescT> whereColumns{Column::subscribedGenericIdDesc, Column::subscribedGenericVersionDesc,
                                          Column::subscribedFitableIdDesc};
    (void)QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, selectColumns, whereColumns, inParam),
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query listener by fitable failed, error message: %s params: %s.", errorMsg,
                          g_convertor.ToLogString(inParam, whereColumns).c_str());
        },
        [&](SqlExecResultPtr& result) { return g_convertor.Parse(result, selectColumns); }, resultCollector);
    finalResults.reserve(resultCollector.size());
    for (auto& subscription : resultCollector) {
        finalResults.emplace_back(Fit::move(subscription.listener));
    }
    return finalResults;
}

FitCode TableFitableSubscribe::query_subscription_entry(const Fitable& fitable, const Listener& listener,
                                                        Subscription& result) const
{
    Fit::vector<Subscription> resultCollector;
    Subscription inParam;
    inParam.fitable_key = fitable;
    inParam.listener = listener;
    Fit::vector<ColumnDescT> whereColumns = {Column::idDesc, Column::callbackFitableIdDesc,
                                             Column::subscribedGenericIdDesc, Column::subscribedGenericVersionDesc,
                                             Column::subscribedFitableIdDesc};
    auto returnCode = QueryHandler(
        SqlBuilderT::BuildSelect(TABLE_NAME, g_convertor.GetAllColumns(), whereColumns, inParam),
        [&](const char* errorMsg) {
            FIT_LOG_ERROR("Query all failed, error message: %s params: %s.", errorMsg,
                          g_convertor.ToLogString(inParam, whereColumns).c_str());
        },
        resultCollector);
    if (returnCode == FIT_ERR_SUCCESS && resultCollector.size() == 1) {
        result = resultCollector.back();
        return FIT_ERR_SUCCESS;
    }
    return FIT_ERR_FAIL;
}

FitCode TableFitableSubscribe::QueryHandler(const Fit::Pg::SqlCmd& sqlCmd,
                                            const std::function<void(const char*)>& logFunc,
                                            Fit::vector<Subscription>& resultCollector) const
{
    return QueryHandler(
        sqlCmd, logFunc,
        [](SqlExecResultPtr& result) { return g_convertor.Parse(result, g_convertor.GetAllColumns()); },
        resultCollector);
}

FitCode TableFitableSubscribe::QueryHandler(const Fit::Pg::SqlCmd& sqlCmd,
                                            const std::function<void(const char*)>& logFunc,
                                            const std::function<Fit::vector<Subscription>(SqlExecResultPtr&)>& parser,
                                            Fit::vector<Subscription>& resultCollector) const
{
    auto result = ConnectionPool::Instance().Submit(sqlCmd);
    if (result == nullptr || !result->IsOk()) {
        logFunc(result ? result->GetErrorMessage() : "result is nullptr");
        return FIT_ERR_FAIL;
    }
    resultCollector = parser(result);
    return FIT_ERR_SUCCESS;
}
