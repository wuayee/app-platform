/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for ODBC column.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_COLUMN_HPP
#define FIT_ODBC_COLUMN_HPP

#include "types.hpp"

#include <fit/stl/string.hpp>

namespace Fit {
namespace Odbc {
class Column {
public:
    virtual ~Column() = default;

    virtual const ::Fit::string& GetName() const noexcept = 0;

    virtual uint16_t GetDataType() const noexcept = 0;

    virtual uint16_t GetSize() const noexcept = 0;

    virtual uint16_t GetDecimalDigits() const noexcept = 0;

    virtual bool IsNullable() const noexcept = 0;
};
}
}

#endif // FIT_ODBC_COLUMN_HPP
