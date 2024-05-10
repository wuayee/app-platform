/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 *
 * Description  : Test FitSystemPropertyUtils::Address
 * Author       : 王攀博 00561424
 * Date         : 2024/03/01
 */

#include <fit/fit_log.h>
#include "fit/internal/fit_system_property_utils.h"
fit::registry::Address FitSystemPropertyUtils::Address()
{
    fit::registry::Address address;
    address.host = "127.0.0.1";
    address.port = 8080;
    address.protocol = 3;
    address.id = "127.0.0.1:8080";
    address.environment = "debug";
    address.formats = {0};
    return address;
}

Fit::vector<fit::registry::Address> FitSystemPropertyUtils::Addresses()
{
    fit::registry::Address address;
    address.environment = "test_env";
    address.formats = {0};
    address.host = "127.0.0.1";
    address.port = 8080;
    address.protocol = 0;
    address.id = "127.0.0.1:8888";
    FIT_LOG_CORE("Test stub");

    fit::registry::Address address2;
    address2.environment = "test_env";
    address2.formats = {0};
    address2.host = "127.0.0.1";
    address2.port = 9999;
    address2.protocol = 0;
    address2.id = "127.0.0.1:9999";
    FIT_LOG_CORE("Test stub");
    return {address, address2};
}