/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : abstract wrapper interface for libpq sql PGconn
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#ifndef FIT_PQ_ABSTRACT_SQL_CONNECTION_HPP
#define FIT_PQ_ABSTRACT_SQL_CONNECTION_HPP

#include "fit/stl/vector.hpp"

#include "abstract_sql_exec_result.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class AbstractSqlConnection {
public:
    virtual ~AbstractSqlConnection() = default;

    virtual bool IsOk() const = 0;
    virtual const char* GetErrorMessage() const = 0;
    virtual void Reconnect() = 0;

    virtual SqlExecResultPtr ExecParam(const char* command, Fit::vector<const char*> params) = 0;
};

using SqlConnectionPtr = unique_ptr<AbstractSqlConnection>;
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // FIT_PQ_ABSTRACT_SQL_CONNECTION_HPP
