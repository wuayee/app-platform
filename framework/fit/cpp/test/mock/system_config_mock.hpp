/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/22
 * Notes:       :
 */

#ifndef SYSTEM_CONFIG_MOCK_HPP
#define SYSTEM_CONFIG_MOCK_HPP

#include <gmock/gmock.h>
#include <fit/internal/runtime/config/system_config.hpp>
#include <fit/internal/runtime/runtime_element.hpp>

class SystemConfigMock : public Fit::Config::SystemConfig, public Fit::RuntimeElementBase {
public:
    SystemConfigMock() : RuntimeElementBase("systemConfigMock") {}
    MOCK_CONST_METHOD1(GetValue, Fit::Config::Value &(const char *key));
    MOCK_CONST_METHOD0(GetWorkerId, const Fit::string&());
    MOCK_CONST_METHOD0(GetEnvName, const Fit::string&());
    MOCK_CONST_METHOD0(GetAppName, const Fit::string&());
    MOCK_CONST_METHOD0(GetAppVersion, Fit::string());
};
#endif // SYSTEM_CONFIG_MOCK_HPP
