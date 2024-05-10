/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : include
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef ODBC_STMT_H
#define ODBC_STMT_H

#include <cstdint>
#include <string>
#include <utility>
#include <vector>
#include <sstream>
#include <memory>
#include <functional>
#include <ctime>
#include <fit/stl/string.hpp>

namespace odbc {
template<typename __DATA>
void reset(__DATA &data);

class odbc_stmt {
public:
    using string_converter = std::function<void(Fit::string)>;
    virtual ~odbc_stmt() = default;
    virtual bool bind(uint32_t col_index, Fit::string &value) = 0;
    virtual bool bind(uint32_t col_index, uint32_t &value) = 0;
    virtual bool bind(uint32_t col_index, int32_t &value) = 0;
    // db type timestamp
    virtual bool bind_timestamp(uint32_t col_index, time_t &value) = 0;
    // db type bigint
    virtual bool bind(uint32_t col_index, uint64_t &value) = 0;
    virtual bool bind(uint32_t col_index, int64_t &value) = 0;
    // db type : bit(1)
    virtual bool bind(uint32_t col_index, bool &value) = 0;
    virtual bool bind(uint32_t col_index, float &value) = 0;
    virtual bool bind(uint32_t col_index, double &value) = 0;
    virtual bool bind(uint32_t col_index, string_converter converter) = 0;

    virtual bool exec(const Fit::string &sql) = 0;
    virtual bool next() = 0;
    virtual int32_t cols() = 0;
    virtual int32_t rows() = 0;
};

using odbc_stmt_ptr = std::shared_ptr<odbc_stmt>;

}

#endif // ODBC_STMT_H
