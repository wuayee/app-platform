/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : 批量插入或更新
 * Author       : s00558940
 * Create       : 2020/10/6 9:48
 */

#ifndef ODBC_INSERT_OR_UPDAT_BATCH_H
#define ODBC_INSERT_OR_UPDAT_BATCH_H

#include <fit/internal/fit_time_utils.h>
#include "odbc_stmt.h"
#include "sql_helper/join_string_helper.h"
#include "odbc_code_define.h"
#include "fit/fit_log.h"

namespace odbc {

class insert_or_update_batch_sql_builder {
public:
    insert_or_update_batch_sql_builder &bind(const Fit::string &table)
    {
        table_ = table;
        return *this;
    }

    insert_or_update_batch_sql_builder &row()
    {
        result_ << build_row();
        return *this;
    }

    insert_or_update_batch_sql_builder &insert(const Fit::string &column_name, const Fit::string &value)
    {
        insert_columns_.push_back(make_pair(column_name, value));
        return *this;
    }

    insert_or_update_batch_sql_builder &update(const Fit::string &column_name, const Fit::string &value)
    {
        update_columns_.push_back(make_pair(column_name, value));
        return *this;
    }

    void get_insert_columns_and_values(Fit::vector<Fit::string> &columns, Fit::vector<Fit::string> &values)
    {
        for (auto &item : insert_columns_) {
            columns.push_back(item.first);
            values.push_back(item.second);
        }
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

    Fit::string build()
    {
        result_ << build_row();

        return Fit::to_fit_string(result_.str());
    }

protected:
    Fit::string build_row()
    {
        if (insert_columns_.empty()) {
            return "";
        }

        std::ostringstream result;
        result << "INSERT INTO " << table_ << " (";

        Fit::vector<Fit::string> columns;
        Fit::vector<Fit::string> values;
        get_insert_columns_and_values(columns, values);

        result << join_string_helper::join(columns, ",") << ')'
               << "VALUES(" << join_string_helper::join(values, ",") << ')'
               << " ON DUPLICATE KEY UPDATE "
               << build_update_values() << ";";

        FIT_LOG_DEBUG("sql = [%s].", result.str().c_str());
        update_columns_.clear();
        insert_columns_.clear();

        return Fit::to_fit_string(result.str());
    }

private:
    using insert_columns_set = Fit::vector<Fit::pair<Fit::string, Fit::string>>;
    using update_columns_set = Fit::vector<Fit::pair<Fit::string, Fit::string>>;
    Fit::string table_;
    insert_columns_set insert_columns_;
    update_columns_set update_columns_;

    std::ostringstream result_;
};

class odbc_inserter_or_updater_batch {
public:
    explicit odbc_inserter_or_updater_batch(odbc_stmt_ptr stmt) : stmt_(std::move(stmt)) {}

    ~odbc_inserter_or_updater_batch() = default;

    odbc_inserter_or_updater_batch &bind(const Fit::string &table)
    {
        sql_builder_.bind(table);
        return *this;
    }

    odbc_inserter_or_updater_batch &row()
    {
        sql_builder_.row();
        return *this;
    }

    template<typename __VALUE_TYPE>
    odbc_inserter_or_updater_batch &insert(const Fit::string &col_name, const __VALUE_TYPE &value)
    {
        sql_builder_.insert(col_name, Fit::to_string(value));
        return *this;
    }

    odbc_inserter_or_updater_batch &insert(const Fit::string &col_name, const Fit::string &value)
    {
        sql_builder_.insert(col_name, '\'' + value + '\'');
        return *this;
    }

    odbc_inserter_or_updater_batch &insert_timestamp(const Fit::string &col_name, const time_t &value)
    {
        Fit::string render_value = Fit::TimeUtil::to_string<Fit::TimeUtil::normal_utc_time>(value);
        sql_builder_.insert(col_name, '\'' + render_value + '\'');
        return *this;
    }

    template<typename __VALUE_TYPE>
    odbc_inserter_or_updater_batch &update(const Fit::string &col_name, const __VALUE_TYPE &value)
    {
        sql_builder_.update(col_name, Fit::to_string(value));
        return *this;
    }

    odbc_inserter_or_updater_batch &update(const Fit::string &col_name, const Fit::string &value)
    {
        sql_builder_.update(col_name, '\'' + value + '\'');
        return *this;
    }

    odbc_inserter_or_updater_batch &update_timestamp(const Fit::string &col_name, const time_t &value)
    {
        Fit::string render_value = Fit::TimeUtil::to_string<Fit::TimeUtil::normal_utc_time>(value);
        sql_builder_.update(col_name, '\'' + render_value + '\'');
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
    insert_or_update_batch_sql_builder sql_builder_;
};

}
#endif // ODBC_INSERTER_OR_UPDATER_H
