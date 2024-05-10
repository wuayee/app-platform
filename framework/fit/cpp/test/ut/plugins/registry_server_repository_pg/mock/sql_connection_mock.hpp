/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : mock libpq sql connection wrapper
 * Author       : x00649642
 * Date         : 2023/11/29
 */

#ifndef FIT_PQ_MOCK_SQL_CONNECTION_MOCK_HPP
#define FIT_PQ_MOCK_SQL_CONNECTION_MOCK_HPP

#include <gmock/gmock.h>

#include "registry_server_repository_pg/src/sql_wrapper/sql_connection.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class SqlConnectionMock : public SqlConnection {
public:
    SqlConnectionMock() : SqlConnection(nullptr, nullptr) {}
    ~SqlConnectionMock() override = default;

    MOCK_METHOD(bool, IsOk, (), (const, override));
    MOCK_METHOD(const char*, GetErrorMessage, (), (const, override));
    MOCK_METHOD(void, Reconnect, (), (override));

    MOCK_METHOD(SqlExecResultPtr, ExecParam, (const char* command, Fit::vector<const char*> params), (override));
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // FIT_PQ_MOCK_SQL_CONNECTION_MOCK_HPP
