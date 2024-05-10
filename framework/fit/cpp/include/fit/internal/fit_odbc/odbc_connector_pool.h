/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : odbc连接池封装
 * Author       : s00558940
 * Create       : 2021/02/25 22:46
 */

#ifndef ODBC_CONNECTOR_POOL_H
#define ODBC_CONNECTOR_POOL_H

#include <cstdlib>
#include <cstdint>
#include <string>
#include <memory>
#include <functional>

#include "odbc_stmt.h"
#include "odbc_connector.h"

namespace odbc {

class odbc_connector_pool {
public:
    using get_connector_func = std::function<void(const connector_ptr &connector)>;
    virtual ~odbc_connector_pool() = default;

    virtual bool init(uint32_t size) = 0;
    virtual int32_t get_connector(get_connector_func func) = 0;
};

using connector_pool_ptr = std::shared_ptr<odbc_connector_pool>;

}

#endif // ODBC_CONNECTOR_H
