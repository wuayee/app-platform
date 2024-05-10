/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : mock libpq sql execution result wrapper
 * Author       : x00649642
 * Date         : 2023/11/28
 */

#ifndef FIT_PQ_MOCK_SQL_EXEC_RESULT_MOCK_HPP
#define FIT_PQ_MOCK_SQL_EXEC_RESULT_MOCK_HPP

#include <gmock/gmock.h>

#include "registry_server_repository_pg/src/sql_wrapper/sql_exec_result.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class SqlExecResultMock : public SqlExecResult {
public:
    SqlExecResultMock() : SqlExecResult(nullptr, nullptr) {}
    ~SqlExecResultMock() override = default;

    MOCK_METHOD(int, CountAffected, (), (const, override));
    MOCK_METHOD(int, CountRow, (), (const, override));
    MOCK_METHOD(int, CountCol, (), (const, override));

    MOCK_METHOD(bool, IsOk, (), (const, override));
    MOCK_METHOD(const char*, GetErrorMessage, (), (const, override));

    MOCK_METHOD(Fit::vector<Fit::string>, GetResultRow, (int rowIndex), (const, override));
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // FIT_PQ_MOCK_SQL_EXEC_RESULT_MOCK_HPP
