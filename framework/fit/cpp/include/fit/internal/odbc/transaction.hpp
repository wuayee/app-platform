/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for ODBC transaction.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_TRANSACTION_HPP
#define FIT_ODBC_TRANSACTION_HPP

namespace Fit {
namespace Odbc {
/**
 * 为数据库提供事务。
 */
class Transaction {
public:
    /**
     * 释放事务占用的所有资源。
     */
    virtual ~Transaction() = default;

    /**
     * 提交事务。
     */
    virtual void Commit() = 0;

    /**
     * 回滚事务。
     */
    virtual void Rollback() = 0;
};
}
}

#endif // FIT_ODBC_TRANSACTION_HPP
