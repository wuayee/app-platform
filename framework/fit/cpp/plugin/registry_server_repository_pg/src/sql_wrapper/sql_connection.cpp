/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : query result wrapper for PGconn
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#include "sql_connection.hpp"
#include "sql_exec_result.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
SqlConnection::~SqlConnection()
{
    if (connection_ != nullptr) {
        PQfinish(connection_);
    }
}

bool SqlConnection::IsOk() const
{
    return connection_ != nullptr && PQstatus(connection_) != CONNECTION_BAD;
}

const char* SqlConnection::GetErrorMessage() const
{
    return PQerrorMessage(connection_);
}

void SqlConnection::Reconnect()
{
    PQreset(connection_);
}

SqlExecResultPtr SqlConnection::ExecParam(const char* command, Fit::vector<const char*> params)
{
    return make_unique<SqlExecResult>(PQexecParams(connection_, command, static_cast<int32_t>(params.size()), nullptr,
                                                   params.data(), nullptr, nullptr, 0));
}
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
