/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : scene subscribe table implement
 * Author       : songyongtan
 * Create       : 2023-11-24
 * Notes:       :
 */

#include "table_scene_subscribe.hpp"

#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <fit/internal/fit_string_util.h>
#include <fit/internal/fit_address_utils.h>

#include "heartbeat/heartbeat_entity.hpp"
#include "sql_wrapper/sql_exec_result.hpp"

namespace Fit {
namespace Pg {
namespace {
const char* TABLE_NAME = "registry_scene_subscribe";
namespace Column {
const char* SCENE_TYPE = "scene_type";
const char* ID = "subscriber_id";
const char* CALLBACK_FITID = "subscriber_callback_fitid";
constexpr const char* CONSTRAINT_INDEX = "registry_scene_subscribe_index";

TableSceneSubscribe::SqlBuilderT::ColumnDescT sceneDesc = {
    Column::SCENE_TYPE, TYPE_VARCHAR,
    [](const SubscribeBeatInfo& info, vector<string>& holder) { return info.sceneType.c_str(); },
    [](string value, SubscribeBeatInfo& info) { info.sceneType = move(value); }};
TableSceneSubscribe::SqlBuilderT::ColumnDescT idDesc = {
    Column::ID, TYPE_VARCHAR, [](const SubscribeBeatInfo& info, vector<string>& holder) { return info.id.c_str(); },
    [](string value, SubscribeBeatInfo& info) { info.id = move(value); }};
TableSceneSubscribe::SqlBuilderT::ColumnDescT callbackFitIdDesc = {
    Column::CALLBACK_FITID, TYPE_VARCHAR,
    [](const SubscribeBeatInfo& info, vector<string>& holder) { return info.callbackFitId.c_str(); },
    [](string value, SubscribeBeatInfo& info) { info.callbackFitId = move(value); }};
}  // namespace Column
}  // namespace

TableSceneSubscribe::TableSceneSubscribe(ConnectionPool* connectionPool) : connectionPool_(connectionPool)
{
}
FitCode TableSceneSubscribe::add(const fit_scene_subscriber& info)
{
    auto sqlCmd = SqlBuilderT::BuildInsertOrUpdate(TABLE_NAME, GetAllColumns(), Column::CONSTRAINT_INDEX, {},
                                                   info.get_subscribe_info());
    auto result = connectionPool_->Submit(sqlCmd);
    if (!result || !result->IsOk()) {
        FIT_LOG_ERROR("Failed to add scene subscriber, (errMsg=%s, id=%s, scene=%s).",
                      result ? result->GetErrorMessage() : "null result", info.get_subscribe_info().id.c_str(),
                      info.get_subscribe_info().sceneType.c_str());
        return FIT_ERR_FAIL;
    }
    return FIT_OK;
}
FitCode TableSceneSubscribe::remove(const fit_scene_subscriber& info)
{
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, {Column::idDesc, Column::sceneDesc}, info.get_subscribe_info());

    auto result = connectionPool_->Submit(sqlCmd);
    if (!result || !result->IsOk()) {
        FIT_LOG_ERROR("Failed to delete scene subscriber, (errMsg=%s, id=%s, scene=%s).",
                      result ? result->GetErrorMessage() : "null result", info.get_subscribe_info().id.c_str(),
                      info.get_subscribe_info().sceneType.c_str());
        return FIT_ERR_FAIL;
    }
    if (result->CountAffected() == 0) {
        return FIT_ERR_NOT_FOUND;
    }
    return FIT_OK;
}
FitCode TableSceneSubscribe::remove(const string& id)
{
    SubscribeBeatInfo info{};
    info.id = id;
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, {Column::idDesc}, info);

    auto result = connectionPool_->Submit(sqlCmd);
    if (!result || !result->IsOk()) {
        FIT_LOG_ERROR("Failed to delete scene subscriber, (errMsg=%s, id=%s).",
                      result ? result->GetErrorMessage() : "null result", id.c_str());
        return FIT_ERR_FAIL;
    }
    if (result->CountAffected() == 0) {
        return FIT_ERR_NOT_FOUND;
    }
    return FIT_OK;
}
fit_scene_subscriber_set TableSceneSubscribe::query(const SceneType& sceneType)
{
    auto columns = GetAllColumns();
    SubscribeBeatInfo info{};
    info.sceneType = sceneType;
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, columns, {Column::sceneDesc}, info);

    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to query scene subscriber, (errMsg=%s, sceneType=%s).",
                      sqlResult ? sqlResult->GetErrorMessage() : "null result", sceneType.c_str());
        return {};
    }
    auto subscribersInfo = Parse(columns, *sqlResult);
    fit_scene_subscriber_set result;
    result.reserve(subscribersInfo.size());
    for (auto& item : subscribersInfo) {
        result.emplace_back(fit_scene_subscriber(move(item)));
    }
    return result;
}
vector<TableSceneSubscribe::SqlBuilderT::ColumnDescT> TableSceneSubscribe::GetAllColumns()
{
    vector<SqlBuilderT::ColumnDescT> columns;
    columns.emplace_back(Column::sceneDesc);
    columns.emplace_back(Column::idDesc);
    columns.emplace_back(Column::callbackFitIdDesc);
    return columns;
}
vector<SubscribeBeatInfo> TableSceneSubscribe::Parse(const vector<SqlBuilderT::ColumnDescT>& columns,
                                                     AbstractSqlExecResult& recordSet)
{
    if (columns.size() != static_cast<uint32_t>(recordSet.CountCol())) {
        return {};
    }
    vector<SubscribeBeatInfo> result;
    result.reserve(recordSet.CountRow());
    for (int32_t rowCount = 0; rowCount < recordSet.CountRow(); ++rowCount) {
        auto rowRecord = recordSet.GetResultRow(rowCount);
        SubscribeBeatInfo info{};
        for (uint32_t col = 0; col < columns.size(); ++col) {
            columns[col].readValue(move(rowRecord[col]), info);
        }
        result.emplace_back(move(info));
    }
    return result;
}
}  // namespace Pg
}  // namespace Fit
