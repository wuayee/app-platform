/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for ODBC prepared statement.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_PREPARED_STATEMENT_HPP
#define FIT_ODBC_PREPARED_STATEMENT_HPP

#include "types.hpp"

#include <fit/stl/string.hpp>

namespace Fit {
namespace Odbc {
class PreparedStatement {
public:
    virtual ~PreparedStatement() = default;

    virtual void SetUInt64Parameter(uint32_t index, uint64_t value) = 0;

    virtual void SetStringParameter(uint32_t index, const ::Fit::string& value) = 0;

    virtual void SetBooleanParameter(uint32_t index, bool value) = 0;

    virtual void ExecuteUpdate() = 0;

    virtual ResultSetUptr ExecuteQuery() = 0;
};
}
}

#endif // FIT_ODBC_PREPARED_STATEMENT_HPP
