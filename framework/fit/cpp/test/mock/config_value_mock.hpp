/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/22
 * Notes:       :
 */

#ifndef CONFIG_VALUE_MOCK_HPP
#define CONFIG_VALUE_MOCK_HPP

#include <gmock/gmock.h>
#include <fit/external/runtime/config/config_value.hpp>

class ConfigValueMock : public Fit::Config::Value {
public:
    MOCK_METHOD1(BracketOp, Fit::Config::Value &(int32_t index));
    Fit::Config::Value &operator[](int32_t index) override { return BracketOp(index); }
    MOCK_METHOD1(BracketOp, Fit::Config::Value &(const char *key));
    Fit::Config::Value &operator[](const char *key) override { return BracketOp(key); }

    MOCK_CONST_METHOD0(GetType, Fit::Config::ValueType());
    MOCK_CONST_METHOD0(GetKeys, Fit::vector<Fit::string>());
    MOCK_CONST_METHOD0(Size, int32_t());
    MOCK_CONST_METHOD0(AsBool, bool());
    MOCK_CONST_METHOD0(AsInt, int32_t());
    MOCK_CONST_METHOD0(AsDouble, double());
    MOCK_CONST_METHOD0(AsString, Fit::string());

    MOCK_CONST_METHOD1(AsBool, bool(bool defaultValue));
    MOCK_CONST_METHOD1(AsInt, int32_t(int32_t defaultValue));
    MOCK_CONST_METHOD1(AsDouble, double(double defaultValue));
    MOCK_CONST_METHOD1(AsString, Fit::string(const char * defaultValue));
    MOCK_CONST_METHOD1(AsString, Fit::string(const Fit::string& defaultValue));

    MOCK_CONST_METHOD0(IsNull, bool());
    MOCK_CONST_METHOD0(IsBool, bool());
    MOCK_CONST_METHOD0(IsInt, bool());
    MOCK_CONST_METHOD0(IsDouble, bool());
    MOCK_CONST_METHOD0(IsString, bool());
    MOCK_CONST_METHOD0(IsObject, bool());
    MOCK_CONST_METHOD0(IsArray, bool());
};
#endif // CONFIG_VALUE_MOCK_HPP
