/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : include
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef ODBC_UPDATER_H
#define ODBC_UPDATER_H

#include <fit/internal/fit_time_utils.h>
#include "odbc_stmt.h"
#include "sql_helper/join_string_helper.h"
#include "odbc_code_define.h"
#include "fit/fit_log.h"

namespace odbc {

class update_sql_builder {
public:
    update_sql_builder &bind(const Fit::string &table)
    {
        table_ = table;
        return *this;
    }

    update_sql_builder &update(const Fit::string &column_name, const Fit::string &value)
    {
        update_columns_.push_back(make_pair(column_name, value));
        return *this;
    }

    Fit::string build_update_values()
    {
        std::ostringstream result;

        auto iter = update_columns_.begin();
        if (iter != update_columns_.end()) {
            result << iter->first << "=" << iter->second;
            ++iter;
        }

        for (; iter != update_columns_.end(); ++iter) {
            result << ',' << iter->first << "=" << iter->second;
        }

        return Fit::to_fit_string(result.str());
    }

    update_sql_builder &where(const Fit::string &where)
    {
        where_ = where;
        return *this;
    }

    Fit::string build()
    {
        std::ostringstream result;
        result << "UPDATE " << table_
               << " SET "
               << build_update_values();
        if (!where_.empty()) {
            result << " WHERE "
                   << where_;
        }

        FIT_LOG_DEBUG("sql = [%s].", result.str().c_str());

        return Fit::to_fit_string(result.str());
    }

private:
    using update_columns_set = Fit::vector<Fit::pair<Fit::string, Fit::string>>;
    Fit::string table_;
    Fit::string where_;
    update_columns_set update_columns_;
};

class odbc_updater {
public:
    explicit odbc_updater(odbc_stmt_ptr stmt) : stmt_(std::move(stmt)) {}

    ~odbc_updater() = default;

    odbc_updater &bind(const Fit::string &table)
    {
        sql_builder_.bind(table);
        return *this;
    }

    template<typename __VALUE_TYPE>
    odbc_updater &update(const Fit::string &col_name, const __VALUE_TYPE &value)
    {
        sql_builder_.update(col_name, Fit::to_string(value));
        return *this;
    }

    odbc_updater &update(const Fit::string &col_name, const Fit::string &value)
    {
        sql_builder_.update(col_name, '\'' + value + '\'');
        return *this;
    }

    odbc_updater &update_timestamp(const Fit::string &col_name, const time_t &value)
    {
        Fit::string render_value = Fit::TimeUtil::to_string<Fit::TimeUtil::normal_utc_time>(value);
        sql_builder_.update(col_name, '\'' + render_value + '\'');
        return *this;
    }

    odbc_updater &where(const Fit::string &where)
    {
        sql_builder_.where(where);
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
    update_sql_builder sql_builder_;
};

}
#endif // ODBC_UPDATER_H
