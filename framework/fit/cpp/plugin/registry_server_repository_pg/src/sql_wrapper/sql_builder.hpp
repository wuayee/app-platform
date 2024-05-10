/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq helper
 * Author       : songyongtan
 * Create       : 2023-11-23
 * Notes:       :
 */

#ifndef FIT_PQ_HELPER_HPP
#define FIT_PQ_HELPER_HPP

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/external/util/string_utils.hpp>
#include <sstream>

#include "sql_field.hpp"
#include "sql_cmd.hpp"

namespace Fit {
namespace Pg {
template <typename T> class SqlBuilder {
public:
    using ColumnDescT = ColumnDesc<T>;
    static SqlCmd BuildInsert(const string& tableName, const vector<ColumnDescT>& columns, const T& info)
    {
        SqlCmd result;
        result.params.reserve(columns.size());
        result.holder.reserve(columns.size());
        std::ostringstream sql;
        int32_t paramIndex = 0;
        sql << "insert into " << tableName;
        sql << StringUtils::Wrapper(
            "(", ")",
            StringUtils::Join(",", columns, [](std::stringstream& ss, const ColumnDescT& v) { ss << v.name; }));
        sql << StringUtils::Wrapper(
            " values (", ")",
            StringUtils::Join(",", columns, [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                result.params.emplace_back(v.getSqlParam(info, result.holder));
                ss << "$" << ++paramIndex << "::" << v.type;
            }));
        result.sql = to_fit_string(sql.str());
        return result;
    }
    static SqlCmd BuildUpdate(const string& tableName, const vector<ColumnDescT>& columns,
                              const vector<ColumnDescT>& where, const T& info)
    {
        SqlCmd result;
        result.params.reserve(columns.size() + where.size());
        result.holder.reserve(columns.size() + where.size());
        std::ostringstream sql;
        int32_t paramIndex = 0;
        sql << "update " << tableName << " set ";
        sql << StringUtils::Join(",", columns,
                                 [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                                     result.params.emplace_back(v.getSqlParam(info, result.holder));
                                     ss << v.name << "=$" << ++paramIndex << "::" << v.type;
                                 });
        sql << " where ";
        sql << StringUtils::Join(" and ", where,
                                 [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                                     result.params.emplace_back(v.getSqlParam(info, result.holder));
                                     ss << v.name << "=$" << ++paramIndex << "::" << v.type;
                                 });
        result.sql = to_fit_string(sql.str());
        return result;
    }
    static SqlCmd BuildInsertOrUpdate(const string& tableName, const vector<ColumnDescT>& insertColumns,
                                      const string& conflictConstraint, const vector<ColumnDescT>& updateColumns,
                                      const T& info)
    {
        SqlCmd result;
        result.params.reserve(insertColumns.size() + updateColumns.size());
        result.holder.reserve(insertColumns.size() + updateColumns.size());
        std::ostringstream sql;
        int32_t paramIndex = 0;
        sql << "insert into " << tableName;
        sql << StringUtils::Wrapper(
            "(", ")",
            StringUtils::Join(",", insertColumns, [](std::stringstream& ss, const ColumnDescT& v) { ss << v.name; }));
        sql << StringUtils::Wrapper(
            " values (", ")",
            StringUtils::Join(",", insertColumns,
                              [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                                  result.params.emplace_back(v.getSqlParam(info, result.holder));
                                  ss << "$" << ++paramIndex << "::" << v.type;
                              }));
        if (conflictConstraint.empty()) {
            result.sql = to_fit_string(sql.str());
            return result;
        }
        sql << " on conflict on constraint " << conflictConstraint << " do ";
        if (updateColumns.empty()) {
            sql << "nothing";
            result.sql = to_fit_string(sql.str());
            return result;
        }
        sql << "update set ";
        sql << StringUtils::Join(",", updateColumns,
                                 [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                                     result.params.emplace_back(v.getSqlParam(info, result.holder));
                                     ss << v.name << "=$" << ++paramIndex << "::" << v.type;
                                 });

        result.sql = to_fit_string(sql.str());
        return result;
    }
    static SqlCmd BuildDelete(const string& tableName, const vector<ColumnDescT>& where, const T& info)
    {
        SqlCmd result;
        result.params.reserve(where.size());
        result.holder.reserve(where.size());
        std::ostringstream sql;
        sql << "delete from " << tableName;
        sql << " where ";
        int32_t paramIndex = 0;
        sql << StringUtils::Join(" and ", where,
                                 [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                                     result.params.emplace_back(v.getSqlParam(info, result.holder));
                                     ss << v.name << "=$" << ++paramIndex << "::" << v.type;
                                 });
        result.sql = to_fit_string(sql.str());
        return result;
    }
    static SqlCmd BuildSelect(const string& tableName, const vector<ColumnDescT>& columns,
                              const vector<ColumnDescT>& where, const T& info)
    {
        SqlCmd result;
        result.params.reserve(columns.size() + where.size());
        result.holder.reserve(columns.size() + where.size());
        std::ostringstream sql;
        sql << "select ";
        sql << StringUtils::Join(", ", columns, [](std::stringstream& ss, const ColumnDescT& v) { ss << v.name; });
        sql << " from " << tableName;
        if (where.empty()) {
            result.sql = to_fit_string(sql.str());
            return result;
        }
        sql << " where ";
        int32_t paramIndex = 0;
        sql << StringUtils::Join(" and ", where,
                                 [&info, &result, &paramIndex](std::stringstream& ss, const ColumnDescT& v) {
                                     result.params.emplace_back(v.getSqlParam(info, result.holder));
                                     ss << v.name << "=$" << ++paramIndex << "::" << v.type;
                                 });
        result.sql = to_fit_string(sql.str());
        return result;
    }
};
}  // namespace Pg
}  // namespace Fit
#endif
