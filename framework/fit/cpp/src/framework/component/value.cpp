/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/7
 * Notes:       :
 */

#include <fit/value.hpp>
#include <fit/stl/memory.hpp>
#include <climits>

namespace Fit {
class Value::Impl {
public:
    union {
        bool b;
        double d;
        Fit::string *s;
        ObjectValue *object;
        ArrayValue *array;
    } data_ {};
    ValueType type_ {ValueType::NULL_};
};

Value::Value() : impl_(make_unique<Impl>()) {}

Value::Value(std::unique_ptr<Impl> impl) : impl_(std::move(impl)) {}

Value::Value(const Value &other) : impl_(make_unique<Impl>())
{
    *this = other;
}

Value::Value(Value &&other) noexcept
{
    impl_ = std::move(other.impl_);
}

Value &Value::operator=(const Fit::Value &other)
{
    if (this == &other) {
        return *this;
    }
    if (!other.impl_) {
        impl_.reset();
        return *this;
    }

    switch (other.Type()) {
        case ValueType::NULL_:
            SetNull();
            break;
        case ValueType::BOOL:
            SetBool(other.AsBool());
            break;
        case ValueType::NUMBER:
            SetDouble(other.AsDouble());
            break;
        case ValueType::STRING:
            SetString(other.AsString());
            break;
        case ValueType::OBJECT:
            SetObject();
            *impl_->data_.object = *other.impl_->data_.object;
            break;
        case ValueType::ARRAY:
            SetArray();
            *impl_->data_.array = *other.impl_->data_.array;
            break;
        default:
            break;
    }

    return *this;
}

Value &Value::operator=(Fit::Value &&other) noexcept
{
    if (this == &other) {
        return *this;
    }
    impl_ = std::move(other.impl_);

    return *this;
}

Value::Value(bool value) : impl_(make_unique<Impl>())
{
    SetBool(value);
}

Value::Value(std::nullptr_t) : impl_(make_unique<Impl>())
{
    SetNull();
}

Value::Value(int32_t value) : impl_(make_unique<Impl>())
{
    SetDouble(value);
}

Value::Value(double value) : impl_(make_unique<Impl>())
{
    SetDouble(value);
}

Value::Value(const char *value) : impl_(make_unique<Impl>())
{
    SetString(value);
}

Value::Value(std::initializer_list<Value> init) : impl_(make_unique<Impl>())
{
    if (init.size() == 1) {
        *this = *init.begin();
        return;
    }
    bool isObject = true;
    for (const auto &element : init) {
        if (!element.IsArray() || element.AsArray().Size() != 2 || !element.AsArray()[0].IsString()) {
            isObject = false;
            break;
        }
    }
    if (isObject) {
        SetObject();
        for (const auto &element : init) {
            AsObject().Add(element.AsArray()[0].AsString()) = element.AsArray()[1];
        }
    } else {
        SetArray();
        for (const auto &element : init) {
            AsArray().PushBack() = element;
        }
    }
}

Value::~Value()
{
    SetNull();
}

ValueType Value::Type() const
{
    if (!impl_) {
        return ValueType::NULL_;
    }
    return impl_->type_;
}

bool Value::IsNull() const
{
    if (!impl_) {
        return true;
    }
    return impl_->type_ == ValueType::NULL_;
}

bool Value::IsBool() const
{
    if (!impl_) {
        return false;
    }
    return impl_->type_ == ValueType::BOOL;
}

bool Value::IsNumber() const
{
    if (!impl_) {
        return false;
    }
    return impl_->type_ == ValueType::NUMBER;
}

bool Value::IsString() const
{
    if (!impl_) {
        return false;
    }
    return impl_->type_ == ValueType::STRING;
}

bool Value::IsObject() const
{
    if (!impl_) {
        return false;
    }
    return impl_->type_ == ValueType::OBJECT;
}

bool Value::IsArray() const
{
    if (!impl_) {
        return false;
    }
    return impl_->type_ == ValueType::ARRAY;
}

Value &Value::SetNull()
{
    if (!impl_) {
        return *this;
    }
    switch (Type()) {
        case ValueType::STRING:
            delete impl_->data_.s;
            impl_->data_.s = nullptr;
            break;
        case ValueType::OBJECT:
            delete impl_->data_.object;
            impl_->data_.object = nullptr;
            break;
        case ValueType::ARRAY:
            delete impl_->data_.array;
            impl_->data_.array = nullptr;
            break;
        default:
            break;
    }
    impl_->type_ = ValueType::NULL_;

    return *this;
}

Value &Value::SetBool(bool value)
{
    if (!IsBool()) {
        SetNull();
    }

    impl_->type_ = ValueType::BOOL;
    impl_->data_.b = value;

    return *this;
}

Value &Value::SetInt32(int32_t value)
{
    return SetDouble(value);
}

Value &Value::SetUInt32(uint32_t value)
{
    return SetDouble(value);
}

Value &Value::SetDouble(double value)
{
    if (!IsNumber()) {
        SetNull();
    }

    impl_->type_ = ValueType::NUMBER;
    impl_->data_.d = value;

    return *this;
}

Value &Value::SetString(const char *value)
{
    if (!IsString()) {
        SetNull();
    }

    impl_->type_ = ValueType::STRING;
    if (impl_->data_.s == nullptr) {
        impl_->data_.s = new Fit::string();
        *impl_->data_.s = value;
    } else {
        *impl_->data_.s = value;
    }

    return *this;
}

ObjectValue &Value::SetObject()
{
    if (!IsObject()) {
        SetNull();
    }

    impl_->type_ = ValueType::OBJECT;
    if (impl_->data_.object == nullptr) {
        impl_->data_.object = new ObjectValue();
    } else {
        impl_->data_.object->Clear();
    }

    return *impl_->data_.object;
}

ArrayValue &Value::SetArray()
{
    if (!IsArray()) {
        SetNull();
    }

    impl_->type_ = ValueType::ARRAY;
    if (impl_->data_.array == nullptr) {
        impl_->data_.array = new ArrayValue();
    } else {
        impl_->data_.array->Clear();
    }

    return *impl_->data_.array;
}

bool Value::AsBool() const
{
    if (!IsBool()) {
        throw std::logic_error("need a bool value");
    }

    return impl_->data_.b;
}

int32_t Value::AsInt32() const
{
    double result = AsDouble();
    if (result < INT_MIN || result > INT_MAX) {
        throw std::logic_error("not in INT_MIN~INT_MAX");
    }

    return int32_t(result);
}

uint32_t Value::AsUInt32() const
{
    double result = AsDouble();
    if (result < 0 || result > UINT_MAX) {
        throw std::logic_error("not in 0~UINT_MAX");
    }

    return uint32_t(result);
}

double Value::AsDouble() const
{
    if (!IsNumber()) {
        throw std::logic_error("need a number value");
    }

    return impl_->data_.d;
}

const char *Value::AsString() const
{
    if (!IsString()) {
        throw std::logic_error("need a string value");
    }

    return impl_->data_.s->c_str();
}

ObjectValue &Value::AsObject()
{
    if (!IsObject()) {
        throw std::logic_error("need a object value");
    }

    return *impl_->data_.object;
}

const ObjectValue &Value::AsObject() const
{
    if (!IsObject()) {
        throw std::logic_error("need a object value");
    }

    return *impl_->data_.object;
}

ArrayValue &Value::AsArray()
{
    if (!IsArray()) {
        throw std::logic_error("need a array value");
    }

    return *impl_->data_.array;
}

const ArrayValue &Value::AsArray() const
{
    if (!IsArray()) {
        throw std::logic_error("need a array value");
    }

    return *impl_->data_.array;
}

bool Value::AsBool(bool defaultValue) const
{
    if (!IsBool()) {
        return defaultValue;
    }

    return impl_->data_.b;
}

int32_t Value::AsInt32(int32_t defaultValue) const
{
    if (!IsNumber()) {
        return defaultValue;
    }
    double result = impl_->data_.d;
    if (result < INT_MIN || result > INT_MAX) {
        return defaultValue;
    }

    return int32_t(result);
}

uint32_t Value::AsUInt32(uint32_t defaultValue) const
{
    if (!IsNumber()) {
        return defaultValue;
    }
    double result = impl_->data_.d;
    if (result < 0 || result > UINT_MAX) {
        return defaultValue;
    }

    return uint32_t(result);
}

double Value::AsDouble(double defaultValue) const
{
    if (!IsNumber()) {
        return defaultValue;
    }

    return impl_->data_.d;
}

const char *Value::AsString(const char *defaultValue) const
{
    if (!IsString()) {
        return defaultValue;
    }

    return impl_->data_.s->c_str();
}

const ObjectValue &Value::AsObject(const ObjectValue &defaultValue) const
{
    if (!IsObject()) {
        return defaultValue;
    }

    return *impl_->data_.object;
}

const ArrayValue &Value::AsArray(const ArrayValue &defaultValue) const
{
    if (!IsArray()) {
        return defaultValue;
    }

    return *impl_->data_.array;
}
}