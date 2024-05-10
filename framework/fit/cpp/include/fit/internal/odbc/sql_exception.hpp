/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for SQL exception.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_SQL_EXCEPTION_HPP
#define FIT_ODBC_SQL_EXCEPTION_HPP

#include <fit/internal/exception.hpp>

namespace Fit {
namespace Odbc {
/**
 * 当访问数据库失败时引发的异常。
 */
class SqlException : public Exception {
public:
    /**
     * 使用异常信息初始化异常的新实例。
     *
     * @param message 表示异常信息的字符串。
     */
    explicit SqlException(::Fit::string message);
    ~SqlException() override = default;
};
}
}
#endif // FIT_ODBC_SQL_EXCEPTION_HPP
