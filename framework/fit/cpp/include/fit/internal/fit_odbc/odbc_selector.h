/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : include
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef ODBC_SELECTOR_H
#define ODBC_SELECTOR_H

#include <utility>

#include "odbc_stmt.h"
#include "sql_helper/join_string_helper.h"

#include "fit/fit_log.h"

namespace odbc {

class select_sql_builder {
public:
    select_sql_builder &bind(const Fit::string &table)
    {
        table_ = table;
        return *this;
    }

    select_sql_builder &add(const Fit::string &col_name)
    {
        columns_.push_back(col_name);
        return *this;
    }

    select_sql_builder &where(const Fit::string &where)
    {
        where_ = where;
        return *this;
    }

    Fit::string build()
    {
        std::ostringstream result;
        result << "select";
        if (columns_.empty()) {
            result << " *";
        }
        result << ' ' << join_string_helper::join(columns_, ",");

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
    using columns_set = Fit::vector<Fit::string>;
    columns_set columns_;
};

template<typename __DATA, typename __DATA_SET = Fit::vector<__DATA>>
class odbc_selector {
public:
    explicit odbc_selector(odbc_stmt_ptr stmt) : stmt_(std::move(stmt)) {}

    ~odbc_selector() = default;

    odbc_selector &bind(const Fit::string &table)
    {
        sql_builder_.bind(table);
        return *this;
    }

    using value_bind_func = std::function<void(const odbc_stmt_ptr &stmt, const Fit::string &name, __DATA &data,
        uint32_t index)>;

    odbc_selector &add(const Fit::string &col_name, value_bind_func value_bind)
    {
        sql_builder_.add(col_name);
        columns_.push_back(col_name);
        value_bind_funcs.push_back(value_bind);
        return *this;
    }

    odbc_selector &where(const Fit::string &where)
    {
        sql_builder_.where(where);
        return *this;
    }

    __DATA_SET execute()
    {
        __DATA row{};
        for (uint32_t i = 0; i < value_bind_funcs.size(); ++i) {
            value_bind_funcs[i](stmt_, columns_[i], row, i + 1);
        }
        stmt_->exec(sql_builder_.build());

        __DATA_SET result;
        if (stmt_->rows() > 0) {
            result.reserve(stmt_->rows());
        }

        ::odbc::reset(row);
        while (stmt_->next()) {
            result.push_back(row);
            ::odbc::reset(row);
        }

        return result;
    }

private:
    odbc_stmt_ptr stmt_;

    select_sql_builder sql_builder_;
    using column_value_bind_func_set = Fit::vector<value_bind_func>;
    column_value_bind_func_set value_bind_funcs;
    using columns_set = Fit::vector<Fit::string>;
    columns_set columns_;
};

}
#endif // ODBC_SELECTOR_H
