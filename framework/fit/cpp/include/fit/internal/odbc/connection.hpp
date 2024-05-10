/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for ODBC connection.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_CONNECTION_HPP
#define FIT_ODBC_CONNECTION_HPP

#include "types.hpp"

#include <fit/stl/string.hpp>

namespace Fit {
namespace Odbc {
/**
 * 为数据库提供连接。
 */
class Connection {
public:
    /**
     * 释放数据库连接占用的所有资源。
     */
    virtual ~Connection() = default;

    /**
     * 开启事务。
     *
     * @return 表示新开启的事务的实例。
     */
    virtual TransactionUptr BeginTransaction() = 0;

    /**
     * 准备一个SQL语句。
     * <p>在使用完成后，需要通过delete操作释放语句所占用的资源。</p>
     *
     * @param sql 表示SQL脚本的字符串。
     * @return 表示指向用以执行SQL的语句实例的指针。
     */
    virtual PreparedStatementUptr PrepareStatement(::Fit::string sql) = 0;
};
}
}

#endif // FIT_ODBC_CONNECTION_HPP
