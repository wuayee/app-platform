/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/25
 */
#include "util_by_pg.hpp"
#include <fit/fit_log.h>
namespace Fit {
namespace Pg{
UtilByPg::UtilByPg(ConnectionPool* connectionPool) : connectionPool_(connectionPool)
{
}

FitCode UtilByPg::GetCurrentTimeMs(uint64_t& result)
{
    SqlCmd sqlCmd {};
    sqlCmd.sql = "SELECT FLOOR(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000) AS current_timestamp_ms;";
    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to get current time, (errMsg=%s).",
            sqlResult ? sqlResult->GetErrorMessage() : "null result");
        return FIT_ERR_FAIL;
    }
    if (sqlResult->CountRow() != 1 || sqlResult->CountCol() != 1) {
        FIT_LOG_ERROR("The result is not expect, (row=%d, col=%d).", sqlResult->CountRow(), sqlResult->CountCol());
        return FIT_ERR_FAIL;
    }
    result = atoll(sqlResult->GetResultRow(0)[0].c_str());
    return FIT_OK;
}

FitCode UtilByPg::GetUUid(Fit::string& uuid)
{
    SqlCmd sqlCmd {};
    sqlCmd.sql = "SELECT uuid_generate_v4();";
    auto sqlResult = connectionPool_->Submit(sqlCmd);
    if (!sqlResult || !sqlResult->IsOk()) {
        FIT_LOG_ERROR("Failed to get current time, (errMsg=%s).",
            sqlResult ? sqlResult->GetErrorMessage() : "null result");
        return FIT_ERR_FAIL;
    }
    if (sqlResult->CountRow() != 1 || sqlResult->CountCol() != 1) {
        FIT_LOG_ERROR("The result is not expect, (row=%d, col=%d).", sqlResult->CountRow(), sqlResult->CountCol());
        return FIT_ERR_FAIL;
    }
    uuid = sqlResult->GetResultRow(0)[0].c_str();
    return FIT_OK;
}
}

UtilByRepo& UtilByRepo::Instance()
{
    static Fit::Pg::UtilByPg utilByPg(&Fit::Repository::Pg::ConnectionPool::Instance());
    return utilByPg;
}
}