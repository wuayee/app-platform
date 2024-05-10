/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : odbc连接器
 * Author       : s00558940
 * Create       : 2021/02/25 22:46
 */

#ifndef ODBC_CONNECTOR_H
#define ODBC_CONNECTOR_H

#include <cstdlib>
#include <cstdint>
#include <string>
#include <memory>

#include "odbc_stmt.h"

namespace odbc {

class connector {
public:
    virtual ~connector() = default;

    virtual bool connect(const Fit::string &server, const Fit::string &user_name, const Fit::string &password) = 0;
    virtual void disconnect() = 0;

    virtual odbc_stmt_ptr create_stmt() = 0;
};

using connector_ptr = std::shared_ptr<connector>;

}

#endif // ODBC_CONNECTOR_H
