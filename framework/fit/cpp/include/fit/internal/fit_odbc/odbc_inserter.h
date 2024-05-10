/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : 批量插入或更新
 * Author       : s00558940
 * Create       : 2020/10/6 9:48
 */

#ifndef ODBC_INSERTER_H
#define ODBC_INSERTER_H

#include <fit/internal/fit_time_utils.h>
#include "odbc_stmt.h"
#include "sql_helper/join_string_helper.h"
#include "odbc_code_define.h"
#include "fit/fit_log.h"

namespace odbc {

class insert_sql_builder {
public:
    insert_sql_builder &bind(const Fit::string &table)
    {
        table_ = table;
        return *this;
    }

    insert_sql_builder &enable_ignore(bool enable)
    {
        enable_ignore_ = enable;
        return *this;
    }

    insert_sql_builder &insert(const Fit::string &column_name, const Fit::string &value)
    {
        insert_columns_.push_back(make_pair(column_name, value));
        return *this;
    }

    void get_insert_columns_and_values(Fit::vector<Fit::string> &columns, Fit::vector<Fit::string> &values)
    {
        for (auto &item : insert_columns_) {
            columns.push_back(item.first);
            values.push_back(item.second);
        }
    }

    Fit::string build()
    {
        std::ostringstream result;
        if (enable_ignore_) {
            result << "INSERT IGNORE INTO " << table_ << " (";
        } else {
            result << "INSERT INTO " << table_ << " (";
        }

        Fit::vector<Fit::string> columns;
        Fit::vector<Fit::string> values;
        get_insert_columns_and_values(columns, values);

        result << join_string_helper::join(columns, ",") << ')'
               << "VALUES(" << join_string_helper::join(values, ",") << ')';

        FIT_LOG_DEBUG("sql = [%s].", result.str().c_str());

        return Fit::to_fit_string(result.str());
    }

private:
    using insert_columns_set = Fit::vector<Fit::pair<Fit::string, Fit::string>>;
    Fit::string table_;
    insert_columns_set insert_columns_;
    bool enable_ignore_ {false};
};

class odbc_inserter {
public:
    explicit odbc_inserter(odbc_stmt_ptr stmt) : stmt_(std::move(stmt)) {}

    ~odbc_inserter() = default;

    odbc_inserter &bind(const Fit::string &table)
    {
        sql_builder_.bind(table);
        return *this;
    }

    odbc_inserter &enable_ignore(bool enable)
    {
        sql_builder_.enable_ignore(enable);
        return *this;
    }

    template<typename __VALUE_TYPE>
    odbc_inserter &insert(const Fit::string &col_name, const __VALUE_TYPE &value)
    {
        sql_builder_.insert(col_name, Fit::to_string(value));
        return *this;
    }

    odbc_inserter &insert(const Fit::string &col_name, const Fit::string &value)
    {
        sql_builder_.insert(col_name, '\'' + value + '\'');
        return *this;
    }

    odbc_inserter &insert_timestamp(const Fit::string &col_name, const time_t &value)
    {
        Fit::string render_value = Fit::TimeUtil::to_string<Fit::TimeUtil::normal_utc_time>(value);
        sql_builder_.insert(col_name, '\'' + render_value + '\'');
        return *this;
    }

    /*!
     * 返回执行sql影响的行数，失败返回 code::ERROR
     * @return
     */
    int32_t execute()
    {
        if (!stmt_->exec(sql_builder_.build())) {
            return code::ERROR;
        }
        return stmt_->rows();
    }

private:
    odbc_stmt_ptr stmt_;
    insert_sql_builder sql_builder_;
};

}
#endif // ODBC_INSERTER_H
