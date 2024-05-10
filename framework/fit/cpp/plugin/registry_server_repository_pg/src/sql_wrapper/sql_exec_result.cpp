/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : query result wrapper for PGresult
 * Author       : x00649642
 * Create       : 2023-11-21
 * Notes:       :
 */
#include "sql_exec_result.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
SqlExecResult::~SqlExecResult()
{
    if (result_ != nullptr) {
        PQclear(result_);
    }
}

int SqlExecResult::CountAffected() const
{
    const char* affected = PQcmdTuples(result_);
    return (!std::strlen(affected)) ? 0 : std::stoi(affected);
}

int SqlExecResult::CountRow() const
{
    return PQntuples(result_);
}

int SqlExecResult::CountCol() const
{
    return PQnfields(result_);
}

const char* SqlExecResult::GetErrorMessage() const
{
    if (result_ == nullptr) {
        return "result is nullptr";
    }
    return PQresultErrorMessage(result_);
}

bool SqlExecResult::IsOk() const
{
    if (result_ == nullptr) {
        return false;
    }
    const int retCode = PQresultStatus(result_);
    return (retCode == PGRES_COMMAND_OK) || (retCode == PGRES_TUPLES_OK);
}

Fit::vector<Fit::string> SqlExecResult::GetResultRow(int rowIndex) const
{
    Fit::vector<Fit::string> row;
    row.reserve(this->CountCol());
    for (int colIndex = 0; colIndex < this->CountCol(); ++colIndex) {
        row.emplace_back(PQgetvalue(result_, rowIndex, colIndex));
    }
    return row;
}
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
