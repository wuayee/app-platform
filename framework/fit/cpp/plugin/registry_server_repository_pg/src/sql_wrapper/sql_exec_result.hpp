/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : query result wrapper for PGresult
 * Author       : x00649642
 * Create       : 2023-11-21
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_SQL_EXEC_RESULT_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_SQL_EXEC_RESULT_HPP

#include "libpq-fe.h"

#include "abstract_sql_exec_result.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class SqlExecResult : public AbstractSqlExecResult {
public:
    explicit SqlExecResult(PGresult* result) : result_(result)
    {
    }

    // no copy!! so that `PGclear` in destructor won't be called twice
    SqlExecResult(SqlExecResult&) = delete;
    SqlExecResult& operator=(SqlExecResult&) = delete;

    // do move pointer here
    SqlExecResult(SqlExecResult&& other) : result_(other.result_)
    {
        other.result_ = nullptr;
    }

    SqlExecResult& operator=(SqlExecResult&& other)
    {
        result_ = other.result_;
        other.result_ = nullptr;
        return *this;
    }

    ~SqlExecResult() override;

    int CountAffected() const override;
    int CountRow() const override;
    int CountCol() const override;

    bool IsOk() const override;
    const char* GetErrorMessage() const override;

    // get the result in rows. Caution⚠: no row index bound check!
    Fit::vector<Fit::string> GetResultRow(int rowIndex) const override;

protected:
    // this constructor is just for mock testing
    explicit SqlExecResult(std::nullptr_t, std::nullptr_t){};

private:
    PGresult* result_{nullptr};
};
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit

#endif  // REGISTRY_SERVER_REPOSITORY_PG_SQL_EXEC_RESULT_HPP
