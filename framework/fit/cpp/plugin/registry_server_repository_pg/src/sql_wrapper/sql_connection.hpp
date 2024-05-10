/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : query result wrapper for PGconn
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_SQL_CONNECTION_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_SQL_CONNECTION_HPP

#include "libpq-fe.h"

#include "abstract_sql_connection.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class SqlConnection : public AbstractSqlConnection {
public:
    explicit SqlConnection(const char* connectionInfo) : connection_(PQconnectdb(connectionInfo))
    {
    }
    ~SqlConnection() override;
    // no copy
    SqlConnection(SqlConnection&) = delete;
    SqlConnection& operator=(SqlConnection&) = delete;

    // do move pointer here
    SqlConnection(SqlConnection&& other) : connection_(other.connection_)
    {
        other.connection_ = nullptr;
    }
    SqlConnection& operator=(SqlConnection&& other)
    {
        connection_ = other.connection_;
        other.connection_ = nullptr;
        return *this;
    }

    bool IsOk() const override;
    const char* GetErrorMessage() const override;
    void Reconnect() override;

    SqlExecResultPtr ExecParam(const char* command, Fit::vector<const char*> params) override;

protected:
    // this constructor is just for mock testing
    explicit SqlConnection(std::nullptr_t, std::nullptr_t){};

private:
    PGconn* connection_{nullptr};
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // REGISTRY_SERVER_REPOSITORY_PG_SQL_CONNECTION_HPP
