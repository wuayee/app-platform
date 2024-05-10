/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for ODBC result set.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_RESULT_SET_HPP
#define FIT_ODBC_RESULT_SET_HPP

#include <fit/stl/string.hpp>

#include <fit/internal/odbc/column.hpp>

namespace Fit {
namespace Odbc {
class ResultSet {
public:
    virtual ~ResultSet() = default;

    virtual uint32_t CountColumns() const = 0;

    virtual const Column* GetColumn(uint32_t number) const = 0;

    virtual const Column* GetColumn(const ::Fit::string& name) const = 0;

    virtual bool Next() const = 0;

    virtual bool IsNull(uint32_t number) const = 0;

    virtual bool IsNull(const ::Fit::string& name) const = 0;

    virtual uint64_t GetUInt64(uint32_t number) const = 0;

    virtual uint64_t GetUInt64(const ::Fit::string& name) const = 0;

    virtual ::Fit::string GetString(uint32_t number) const = 0;

    virtual ::Fit::string GetString(const ::Fit::string& name) const = 0;
};
}
}

#endif // FIT_ODBC_RESULT_SET_HPP
