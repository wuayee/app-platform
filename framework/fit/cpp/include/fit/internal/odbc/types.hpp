/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definitions for ODBC types.
 * Author       : liangjishi 00298979
 * Date         : 2022/03/15
 */

#ifndef FIT_ODBC_TYPES_HPP
#define FIT_ODBC_TYPES_HPP

#include <fit/stl/memory.hpp>

namespace Fit {
namespace Odbc {
class DataSource;
using DataSourceUptr = ::Fit::unique_ptr<DataSource>;
class Connection;
using ConnectionUptr = ::Fit::unique_ptr<Connection>;
class PreparedStatement;
using PreparedStatementUptr = ::Fit::unique_ptr<PreparedStatement>;
class ResultSet;
using ResultSetUptr = ::Fit::unique_ptr<ResultSet>;
class Transaction;
using TransactionUptr = ::Fit::unique_ptr<Transaction>;
}
}

#endif // FIT_ODBC_TYPES_HPP
