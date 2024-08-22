/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-11-23
 * Notes:       :
 */

#include "table_heartbeat.hpp"

#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <fit/internal/fit_string_util.h>
#include <fit/internal/fit_address_utils.h>
#include <fit/internal/registry/repository/util_by_repo.h>

#include "fit_code.h"
#include "sql_wrapper/sql_cmd.hpp"
#include "sql_wrapper/sql_exec_result.hpp"

namespace Fit {
namespace Pg {
namespace {
const char* TABLE_NAME = "registry_heartbeat";
namespace Column {
const char* SCENE_TYPE = "scene_type";
const char* ID = "id";
const char* ALIVE_TIME = "alive_time";
const char* INTERVAL = "interval";
const char* INIT_DELAY = "init_delay";
const char* CALLBACK_FITID = "callback_fitid";
const char* START_TIME = "start_time";
const char* LAST_HEARTBEAT_TIME = "last_heartbeat_time";
const char* EXPIRED_TIME = "expired_time";
const char* STATUS = "status";
const char* HOST = "host";
const char* PORT = "port";
const char* PROTOCOL = "protocol";
const char* FORMATS = "formats";
const char* ENVIRONMENT = "environment";

TableHeartbeat::SqlBuilderT::ColumnDescT idDesc = {
    Column::ID, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) { return info.addressBeatInfo.id.c_str(); },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.id = move(value); }};
TableHeartbeat::SqlBuilderT::ColumnDescT sceneDesc = {
    Column::SCENE_TYPE, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        return info.addressBeatInfo.beat_info.sceneType.c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.beat_info.sceneType = move(value); }};
TableHeartbeat::SqlBuilderT::ColumnDescT aliveTimeDesc = {
    Column::ALIVE_TIME, TYPE_INT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.addressBeatInfo.beat_info.aliveTime));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.beat_info.aliveTime = atoi(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT intervalDesc = {
    Column::INTERVAL, TYPE_INT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.addressBeatInfo.beat_info.interval));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.beat_info.interval = atoi(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT initDelayDesc = {
    Column::INIT_DELAY, TYPE_INT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.addressBeatInfo.beat_info.initDelay));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.beat_info.initDelay = atoi(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT callbackFitIdDesc = {
    Column::CALLBACK_FITID, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        return info.addressBeatInfo.beat_info.callbackFitId.c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.beat_info.callbackFitId = move(value); }};
TableHeartbeat::SqlBuilderT::ColumnDescT startTimeDesc = {
    Column::START_TIME, TYPE_BIGINT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.start_time));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.start_time = atoll(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT lastHeartbeatTimeDesc = {
    Column::LAST_HEARTBEAT_TIME, TYPE_BIGINT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.last_heartbeat_time));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.last_heartbeat_time = atoll(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT expiredTimeDesc = {
    Column::EXPIRED_TIME, TYPE_BIGINT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.expired_time));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.expired_time = atoll(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT statusDesc = {
    Column::STATUS, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        holder.emplace_back(to_string(static_cast<uint32_t>(info.status)));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.status = static_cast<HeartbeatStatus>(atoi(value.c_str())); }};
TableHeartbeat::SqlBuilderT::ColumnDescT hostDesc = {
    Column::HOST, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        if (info.addressBeatInfo.addresses.empty()) {
            return "";
        }
        return info.addressBeatInfo.addresses.front().ip.c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.addresses.front().ip = move(value); }};
TableHeartbeat::SqlBuilderT::ColumnDescT portDesc = {
    Column::PORT, TYPE_INT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        if (info.addressBeatInfo.addresses.empty()) {
            return "";
        }
        holder.emplace_back(to_string(info.addressBeatInfo.addresses.front().port));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.addresses.front().port = atoi(value.c_str()); }};
TableHeartbeat::SqlBuilderT::ColumnDescT protocolDesc = {
    Column::PROTOCOL, TYPE_SMALLINT,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        if (info.addressBeatInfo.addresses.empty()) {
            return "";
        }
        holder.emplace_back(to_string(static_cast<uint32_t>(info.addressBeatInfo.addresses.front().protocol)));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) {
        info.addressBeatInfo.addresses.front().protocol = static_cast<fit_protocol_type>(atoi(value.c_str()));
    }};
TableHeartbeat::SqlBuilderT::ColumnDescT formatsDesc = {
    Column::FORMATS, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        if (info.addressBeatInfo.addresses.empty()) {
            return "";
        }
        holder.emplace_back(join_to_string(info.addressBeatInfo.addresses.front().formats, ","));
        return holder.back().c_str();
    },
    [](string value, AddressStatusInfo& info) {
        info.addressBeatInfo.addresses.front().formats = fit_address_utils::parse_formats(value);
    }};
TableHeartbeat::SqlBuilderT::ColumnDescT environmentDesc = {
    Column::ENVIRONMENT, TYPE_VARCHAR,
    [](const AddressStatusInfo& info, vector<string>& holder) {
        if (info.addressBeatInfo.addresses.empty()) {
            return "";
        }
        return info.addressBeatInfo.addresses.front().environment.c_str();
    },
    [](string value, AddressStatusInfo& info) { info.addressBeatInfo.addresses.front().environment = move(value); }};
}  // namespace Column
}  // namespace

TableHeartbeat::TableHeartbeat(ConnectionPool* connectionPool) : connectionPool_(connectionPool)
{
}
int32_t TableHeartbeat::add_beat(const AddressStatusInfo& info)
{
    auto sqlCmd = SqlBuilderT::BuildInsert(TABLE_NAME, GetAllColumns(), info);
    auto result = connectionPool_->Submit(sqlCmd);
    if (!result || !result->IsOk()) {
        FIT_LOG_ERROR("Failed to insert target, (errMsg=%s, id=%s).",
                      result ? result->GetErrorMessage() : "null result", info.addressBeatInfo.id.c_str());
        return FIT_ERR_FAIL;
    }
    return FIT_OK;
}
int32_t TableHeartbeat::modify_beat(const AddressStatusInfo& info)
{
    vector<SqlBuilderT::ColumnDescT> columns{Column::aliveTimeDesc,
                                             Column::intervalDesc,
                                             Column::initDelayDesc,
                                             Column::callbackFitIdDesc,
                                             Column::lastHeartbeatTimeDesc,
                                             Column::startTimeDesc,
                                             Column::expiredTimeDesc,
                                             Column::statusDesc,
                                             Column::hostDesc,
                                             Column::portDesc,
                                             Column::protocolDesc,
                                             Column::formatsDesc,
                                             Column::environmentDesc};
    auto sqlCmd = SqlBuilderT::BuildUpdate(TABLE_NAME, columns, GetWhere(), info);
    auto result = connectionPool_->Submit(sqlCmd);
    if (!result || !result->IsOk()) {
        FIT_LOG_ERROR("Failed to update target, (errMsg=%s, id=%s).",
                      result ? result->GetErrorMessage() : "null result", info.addressBeatInfo.id.c_str());
        return FIT_ERR_FAIL;
    }
    if (result->CountAffected() == 0) {
        return FIT_ERR_NOT_FOUND;
    }
    return FIT_OK;
}
FitCode TableHeartbeat::remove_beat(const AddressBeatInfo& info)
{
    AddressStatusInfo statusInfo{};
    statusInfo.addressBeatInfo = info;
    auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, GetWhere(), statusInfo);
    auto result = connectionPool_->Submit(sqlCmd);
    if (!result || !result->IsOk()) {
        FIT_LOG_ERROR("Failed to delete target, (errMsg=%s, id=%s).",
                      result ? result->GetErrorMessage() : "null result", info.id.c_str());
        return FIT_ERR_FAIL;
    }
    if (result->CountAffected() == 0) {
        return FIT_ERR_NOT_FOUND;
    }
    return FIT_OK;
}
FitCode TableHeartbeat::query_beat(const AddressBeatInfo& info, AddressStatusInfo& result)
{
    auto columns = GetAllColumns();
    AddressStatusInfo statusInfo{};
    statusInfo.addressBeatInfo = info;
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, columns, GetWhere(), statusInfo);
    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to update target, (errMsg=%s, id=%s).",
                      sqlResult ? sqlResult->GetErrorMessage() : "null result", info.id.c_str());
        return FIT_ERR_FAIL;
    }
    auto datas = Parse(columns, *sqlResult);
    if (datas.empty()) {
        return FIT_ERR_NOT_FOUND;
    }
    result = move(datas.front());
    return FIT_OK;
};

AddressStatusSet TableHeartbeat::query_all_beat()
{
    auto columns = GetAllColumns();
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, columns, {}, {});
    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to query all, (errMsg=%s).", sqlResult ? sqlResult->GetErrorMessage() : "null result");
        return {};
    }
    return Parse(columns, *sqlResult);
}

FitCode TableHeartbeat::get_current_time_ms(uint64_t& result)
{
    return UtilByRepo::Instance().GetCurrentTimeMs(result);
}

vector<TableHeartbeat::SqlBuilderT::ColumnDescT> TableHeartbeat::GetWhere()
{
    vector<SqlBuilderT::ColumnDescT> where;
    where.emplace_back(Column::idDesc);
    where.emplace_back(Column::sceneDesc);
    return where;
}
vector<TableHeartbeat::SqlBuilderT::ColumnDescT> TableHeartbeat::GetAllColumns()
{
    vector<SqlBuilderT::ColumnDescT> columns;
    columns.emplace_back(Column::sceneDesc);
    columns.emplace_back(Column::idDesc);
    columns.emplace_back(Column::aliveTimeDesc);
    columns.emplace_back(Column::intervalDesc);
    columns.emplace_back(Column::initDelayDesc);
    columns.emplace_back(Column::callbackFitIdDesc);
    columns.emplace_back(Column::startTimeDesc);
    columns.emplace_back(Column::lastHeartbeatTimeDesc);
    columns.emplace_back(Column::expiredTimeDesc);
    columns.emplace_back(Column::statusDesc);
    columns.emplace_back(Column::hostDesc);
    columns.emplace_back(Column::portDesc);
    columns.emplace_back(Column::protocolDesc);
    columns.emplace_back(Column::formatsDesc);
    columns.emplace_back(Column::environmentDesc);
    return columns;
}
vector<AddressStatusInfo> TableHeartbeat::Parse(const vector<SqlBuilderT::ColumnDescT>& columns,
                                                AbstractSqlExecResult& recordSet)
{
    if (columns.size() != static_cast<uint32_t>(recordSet.CountCol())) {
        return {};
    }
    vector<AddressStatusInfo> result;
    result.reserve(recordSet.CountRow());
    for (int32_t rowCount = 0; rowCount < recordSet.CountRow(); ++rowCount) {
        auto rowRecord = recordSet.GetResultRow(rowCount);
        AddressStatusInfo info{};
        info.addressBeatInfo.addresses.resize(1, {});
        for (uint32_t col = 0; col < columns.size(); ++col) {
            columns[col].readValue(move(rowRecord[col]), info);
        }
        result.emplace_back(move(info));
    }
    return result;
}
}  // namespace Pg
}  // namespace Fit
