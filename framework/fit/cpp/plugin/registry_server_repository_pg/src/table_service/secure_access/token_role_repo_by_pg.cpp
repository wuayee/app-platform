/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#include "token_role_repo_by_pg.h"
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace Fit {
namespace Pg {
using namespace Fit::Repository::Pg;
namespace {
const char* TABLE_NAME = "registry_token_role";
namespace Column {
const char* TOKEN = "token";
const char* TYPE = "type";
const char* ROLE = "role";
const char* TIMEOUT = "timeout";
const char* END_TIME = "end_time";
constexpr const char* CONSTRAINT_INDEX = "registry_token_role_index";

TokenRoleRepoByPg::SqlBuilderT::ColumnDescT tokenDesc = {
    Column::TOKEN, TYPE_VARCHAR,
    [](const AuthTokenRole& info, vector<string>& holder) {
        return info.token.c_str();
    },
    [](string value, AuthTokenRole& info) { info.token = move(value); }};

TokenRoleRepoByPg::SqlBuilderT::ColumnDescT sceneDesc = {
    Column::TYPE, TYPE_VARCHAR,
    [](const AuthTokenRole& info, vector<string>& holder) {
        return info.type.c_str();
    },
    [](string value, AuthTokenRole& info) { info.type = move(value); }};
TokenRoleRepoByPg::SqlBuilderT::ColumnDescT roleDesc = {
    Column::ROLE, TYPE_VARCHAR,
    [](const AuthTokenRole& info, vector<string>& holder) {
        return info.role.c_str();
    },
    [](string value, AuthTokenRole& info) { info.role = value; }};
TokenRoleRepoByPg::SqlBuilderT::ColumnDescT timeoutDesc = {
    Column::TIMEOUT, TYPE_INT,
    [](const AuthTokenRole& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.timeout));
        return holder.back().c_str();
    },
    [](string value, AuthTokenRole& info) { info.timeout = atoi(value.c_str()); }};
TokenRoleRepoByPg::SqlBuilderT::ColumnDescT endTimeDesc = {
    Column::END_TIME, TYPE_BIGINT,
    [](const AuthTokenRole& info, vector<string>& holder) {
        holder.emplace_back(to_string(info.endTime));
        return holder.back().c_str();
    },
    [](string value, AuthTokenRole& info) { info.endTime = atoll(value.c_str()); }};
}  // namespace Column
}  // namespace

TokenRoleRepoByPg::TokenRoleRepoByPg(ConnectionPool* connectionPool) : connectionPool_(connectionPool)
{
}

int32_t TokenRoleRepoByPg::Save(const vector<AuthTokenRole>& authTokenRoles)
{
    int32_t ret = FIT_OK;
    for (const AuthTokenRole& authTokenRole : authTokenRoles) {
        auto sqlCmd = SqlBuilderT::BuildInsertOrUpdate(TABLE_NAME, GetAllColumns(), Column::CONSTRAINT_INDEX,
            GetAllColumns(), authTokenRole);
        auto result = ConnectionPool::Instance().Submit(sqlCmd);

        if (result == nullptr || !result->IsOk()) {
            FIT_LOG_ERROR("Save failed, error message: %s.",
                result ? result->GetErrorMessage() : "result is nullptr");
            ret = FIT_ERR_FAIL;
        }
    }
    return ret;
}

int32_t TokenRoleRepoByPg::Remove(const vector<string>& tokens)
{
    int32_t ret = FIT_OK;
    for (const auto& token : tokens) {
        AuthTokenRole authTokenRole;
        authTokenRole.token = token;
        auto sqlCmd = SqlBuilderT::BuildDelete(TABLE_NAME, GetWhere(), authTokenRole);
        auto result = connectionPool_->Submit(sqlCmd);
        if (!result || !result->IsOk()) {
            FIT_LOG_ERROR("Failed to delete target, (errMsg=%s).",
                result ? result->GetErrorMessage() : "null result");
            ret = FIT_ERR_FAIL;
        }
    }
    return ret;
}

vector<AuthTokenRole> TokenRoleRepoByPg::Query(const vector<string>& tokens)
{
    auto columns = GetAllColumns();
    AuthTokenRole authTokenRole;
    authTokenRole.token = tokens.front();
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, columns, GetWhere(), authTokenRole);
    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to update target, (errMsg=%s, token=%s).",
                      sqlResult ? sqlResult->GetErrorMessage() : "null result", authTokenRole.token.c_str());
        return {};
    }
    return Parse(columns, *sqlResult);
}

vector<TokenRoleRepoByPg::SqlBuilderT::ColumnDescT> TokenRoleRepoByPg::GetWhere()
{
    vector<SqlBuilderT::ColumnDescT> where;
    where.emplace_back(Column::tokenDesc);
    return where;
}
vector<TokenRoleRepoByPg::SqlBuilderT::ColumnDescT> TokenRoleRepoByPg::GetAllColumns()
{
    vector<SqlBuilderT::ColumnDescT> columns;
    columns.emplace_back(Column::tokenDesc);
    columns.emplace_back(Column::sceneDesc);
    columns.emplace_back(Column::roleDesc);
    columns.emplace_back(Column::timeoutDesc);
    columns.emplace_back(Column::endTimeDesc);
    return columns;
}
vector<AuthTokenRole> TokenRoleRepoByPg::Parse(const vector<SqlBuilderT::ColumnDescT>& columns,
                                                AbstractSqlExecResult& recordSet)
{
    vector<AuthTokenRole> result;
    if (columns.size() != static_cast<uint32_t>(recordSet.CountCol())) {
        return {};
    }
    result.reserve(recordSet.CountRow());
    for (int32_t rowCount = 0; rowCount < recordSet.CountRow(); ++rowCount) {
        auto rowRecord = recordSet.GetResultRow(rowCount);
        AuthTokenRole info{};
        for (uint32_t col = 0; col < columns.size(); ++col) {
            columns[col].readValue(move(rowRecord[col]), info);
        }
        result.emplace_back(move(info));
    }
    return result;
}
vector<AuthTokenRole> TokenRoleRepoByPg::QueryAll()
{
    auto columns = GetAllColumns();
    auto sqlCmd = SqlBuilderT::BuildSelect(TABLE_NAME, columns, {}, {});
    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to update target, (errMsg=%s).",
            sqlResult ? sqlResult->GetErrorMessage() : "null result");
        return {};
    }
    return Parse(columns, *sqlResult);
}
}
TokenRoleRepoPtr TokenRoleRepoFactory::CreateDbRepo()
{
    return Fit::make_shared<Fit::Pg::TokenRoleRepoByPg>(&Fit::Repository::Pg::ConnectionPool::Instance());
}
}