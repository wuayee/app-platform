/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : abstract wrapper interface for libpq sql exec result
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#ifndef FIT_PQ_ABSTRACT_SQL_EXEC_RESULT_HPP
#define FIT_PQ_ABSTRACT_SQL_EXEC_RESULT_HPP

#include "fit/stl/vector.hpp"
#include "fit/stl/string.hpp"
#include "fit/stl/memory.hpp"

namespace Fit {
namespace Repository {
namespace Pg {
class AbstractSqlExecResult {
public:
    virtual ~AbstractSqlExecResult() = default;

    virtual int CountAffected() const = 0;
    virtual int CountRow() const = 0;
    virtual int CountCol() const = 0;

    virtual bool IsOk() const = 0;
    virtual const char* GetErrorMessage() const = 0;

    // get the result in rows. Caution⚠: no row index bound check!
    virtual Fit::vector<Fit::string> GetResultRow(int rowIndex) const = 0;
};

using SqlExecResultPtr = unique_ptr<AbstractSqlExecResult>;
}  // namespace Pg
}  // namespace Repository
}  // namespace Fit
#endif  // FIT_PQ_ABSTRACT_SQL_EXEC_RESULT_HPP
