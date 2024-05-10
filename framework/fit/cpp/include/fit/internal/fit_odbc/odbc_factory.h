/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : odbc 工厂
 * Author       : s00558940
 * Create       : 2020/10/6 9:48
 */

#ifndef ODBC_FACTORY_H
#define ODBC_FACTORY_H

#include "odbc_connector.h"
#include "odbc_connector_pool.h"

namespace odbc {
class odbc_factory {
public:
    static connector_ptr create();
    static connector_pool_ptr create_pool(const Fit::string &server, const Fit::string &user_name,
        const Fit::string &password);
};

}

#endif // ODBC_FACTORY_H
