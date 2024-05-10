/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : odbc 删除方法
 * Author       : s00558940
 * Create       : 2020/10/6 9:48
 */

#ifndef ODBC_DELETER_H
#define ODBC_DELETER_H

#include "odbc_stmt.h"
#include "sql_helper/join_string_helper.h"
#include "fit/fit_log.h"

namespace odbc {

class delete_sql_builder {
public:
    delete_sql_builder &bind(const Fit::string &table)
    {
        table_ = table;
        return *this;
    }

    delete_sql_builder &where(const Fit::string &where)
    {
        where_ = where;
        return *this;
    }

    Fit::string build()
    {
        std::ostringstream result;
        result << "delete";

        result << " from " << table_;
        if (!where_.empty()) {
            result << " where " << where_;
        }

        FIT_LOG_DEBUG("sql = [%s].", result.str().c_str());

        return Fit::to_fit_string(result.str());
    }

private:
    Fit::string table_;
    Fit::string where_;
};

class odbc_deleter {
public:
    explicit odbc_deleter(odbc_stmt_ptr stmt) : stmt_(std::move(stmt)) {}

    ~odbc_deleter() = default;

    odbc_deleter &bind(const Fit::string &table)
    {
        sql_builder_.bind(table);
        return *this;
    }

    odbc_deleter &where(const Fit::string &where)
    {
        sql_builder_.where(where);
        return *this;
    }

    int32_t execute()
    {
        if (!stmt_->exec(sql_builder_.build())) {
            return code::ERROR;
        }
        return stmt_->rows();
    }

private:
    odbc_stmt_ptr stmt_;
    delete_sql_builder sql_builder_;
};

}
#endif // ODBC_DELETER_H
