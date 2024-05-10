/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/16
 * Notes:       :
 */

#include <fit/external/runtime/config/config_value.hpp>
#include <stdexcept>

namespace Fit {
namespace Config {
Value::Value()
{
}

Value::~Value()
{
}

Value &Value::operator[](int32_t index)
{
    throw std::runtime_error("need array type");
}

Value &Value::operator[](const char *key)
{
    throw std::runtime_error("need object type");
}

ValueType Value::GetType() const
{
    return VALUE_TYPE_NULL;
}

Fit::vector<Fit::string> Value::GetKeys() const
{
    throw std::runtime_error("need object type");
}

int32_t Value::Size() const
{
    throw std::runtime_error("need array type");
}

bool Value::AsBool() const
{
    throw std::runtime_error("need bool type");
}

int32_t Value::AsInt() const
{
    throw std::runtime_error("need int type");
}

double Value::AsDouble() const
{
    throw std::runtime_error("need double type");
}

string Value::AsString() const
{
    throw std::runtime_error("need string type");
}

bool Value::AsBool(bool defaultValue) const
{
    return defaultValue;
}

int32_t Value::AsInt(int32_t defaultValue) const
{
    return defaultValue;
}

double Value::AsDouble(double defaultValue) const
{
    return defaultValue;
}

string Value::AsString(const char *defaultValue) const
{
    return defaultValue;
}

string Value::AsString(const string& defaultValue) const
{
    return defaultValue;
}

bool Value::IsNull() const
{
    return true;
}

bool Value::IsBool() const
{
    return false;
}

bool Value::IsInt() const
{
    return false;
}

bool Value::IsDouble() const
{
    return false;
}

bool Value::IsString() const
{
    return false;
}

bool Value::IsObject() const
{
    return false;
}

bool Value::IsArray() const
{
    return false;
}
}
}
