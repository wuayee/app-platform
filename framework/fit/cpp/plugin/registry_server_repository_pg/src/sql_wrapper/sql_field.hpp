/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : sql fields
 * Author       : x00649642
 * Create       : 2023-11-28
 * Notes:       :
 */
#ifndef REGISTRY_SERVER_REPOSITORY_PG_SQL_WRAPPER_FIELD_HPP
#define REGISTRY_SERVER_REPOSITORY_PG_SQL_WRAPPER_FIELD_HPP

#include <fit/stl/string.hpp>

namespace Fit {
namespace Pg {
constexpr const char* TYPE_VARCHAR = "varchar";
constexpr const char* TYPE_INT = "int";
constexpr const char* TYPE_BIGINT = "bigint";
constexpr const char* TYPE_SMALLINT = "smallint";

template <typename T> struct ColumnDesc {
    string name;
    string type;
    std::function<const char*(const T& info, vector<string>& holder)> getSqlParam;
    std::function<void(string value, T& info)> readValue;
};
}  // namespace Pg
}  // namespace Fit

#endif  // REGISTRY_SERVER_REPOSITORY_PG_SQL_WRAPPER_FIELD_HPP
