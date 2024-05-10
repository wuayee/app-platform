/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implement for writable value
 * Author       : songyongtan
 * Create       : 2023-08-16
 * Notes:       :
 */

#include "config_writable_value.hpp"

#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/except.hpp>
#include <algorithm>

#include "config_encryptedable_value.hpp"

namespace Fit {
namespace Config {
WritableValue WritableValue::Null;
WritableValue& WritableValue::SetNull()
{
    return *this;
}
WritableValue& WritableValue::SetValue(bool val)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::SetValue(int32_t val)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::SetValue(double val)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::SetValue(const char* val)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::SetValue(const string& val)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::SetObject()
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::AddMember(const char* name)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::FindMember(const char* name)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::SetArray()
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::Reserve(int32_t reserveSize)
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
WritableValue& WritableValue::PushBack()
{
    FIT_THROW_EXCEPTION1(std::runtime_error, "not support");
}
class WritableValueProxy : public WritableValue {
public:
    WritableValueProxy();
    /**
     * 只有Array类型才可使用
     * @param index
     * @return 返回对应位置的值, 不存在时返回value IsNull为true
     */
    Value& operator[](int32_t index) override;
    /**
     * 只有Object类型才可使用
     * @param key
     * @return 返回对应的值, 不存在时返回value IsNull为true
     */
    Value& operator[](const char* key) override;
    ValueType GetType() const override;
    vector<string> GetKeys() const override;
    /**
     * 返回Array的大小
     * @return
     */
    int32_t Size() const override;
    // 类型不匹配时抛出std::runtime_error异常
    bool AsBool() const override;
    int32_t AsInt() const override;
    double AsDouble() const override;
    string AsString() const override;
    // 失败时返回默认值
    bool AsBool(bool defaultValue) const override;
    int32_t AsInt(int32_t defaultValue) const override;
    double AsDouble(double defaultValue) const override;
    string AsString(const char* defaultValue) const override;
    string AsString(const string& defaultValue) const override;
    bool IsNull() const override;
    bool IsBool() const override;
    bool IsInt() const override;
    bool IsDouble() const override;
    bool IsString() const override;
    bool IsObject() const override;
    bool IsArray() const override;
    /**
     * 清空对象，后续IsNull为true
     * @return 自身
     */
    WritableValue& SetNull() override;
    /**
     * 更改为bool类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    WritableValue& SetValue(bool val) override;
    /**
     * 更改为int类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    WritableValue& SetValue(int32_t val) override;
    /**
     * 更改为double类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    WritableValue& SetValue(double val) override;
    /**
     * 更改为字符串类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    WritableValue& SetValue(const char* val) override;
    WritableValue& SetValue(const string& val) override;
    /**
     * 更改为object类型节点, 如果之前是其它类型则数据被清空
     * @return 自身
     */
    WritableValue& SetObject() override;
    /**
     * 添加name节点
     * @param name
     * @return 返回新增节点
     */
    WritableValue& AddMember(const char* name) override;
    /**
     * 查找name节点
     * @param name
     * @return 返回查找到的节点，未查到是返回节点IsNull方法为true
     */
    WritableValue& FindMember(const char* name) override;
    /**
     * 更改为数组类型节点, 如果之前是其它类型则数据被清空
     * @param reserveSize
     * @return 自身
     */
    WritableValue& SetArray() override;
    WritableValue& Reserve(int32_t reserveSize) override;
    /**
     * 数组中添加新节点
     * @return 返回新增的节点
     */
    WritableValue& PushBack() override;

private:
    unique_ptr<WritableValue> data_;
};
unique_ptr<WritableValue> WritableValue::New()
{
    return make_unique<WritableValueProxy>();
}

Value& ObjectValue::operator[](const char* key)
{
    auto iter = data_.find(key);
    if (iter == data_.end()) {
        return Null;
    }
    return *iter->second;
}
ValueType ObjectValue::GetType() const
{
    return VALUE_TYPE_OBJECT;
}
vector<string> ObjectValue::GetKeys() const
{
    vector<string> result;
    result.reserve(data_.size());
    for (const auto& e : data_) {
        result.emplace_back(e.first);
    }
    return result;
}
bool ObjectValue::IsNull() const
{
    return false;
}
bool ObjectValue::IsObject() const
{
    return true;
}
WritableValue& ObjectValue::AddMember(const char* name)
{
    auto& v = data_[name];
    v = make_unique<WritableValueProxy>();
    return *v;
}
WritableValue& ObjectValue::FindMember(const char* name)
{
    auto iter = data_.find(name);
    if (iter == data_.end()) {
        return WritableValue::Null;
    }
    return *iter->second;
}

Value& ArrayValue::operator[](int32_t index)
{
    if (index >= (int32_t)data_.size() || index < 0) {
        return Null;
    }
    return *data_[index];
}
ValueType ArrayValue::GetType() const
{
    return VALUE_TYPE_ARRAY;
}
int32_t ArrayValue::Size() const
{
    return data_.size();
}
bool ArrayValue::IsNull() const
{
    return false;
}
bool ArrayValue::IsArray() const
{
    return true;
}
WritableValue& ArrayValue::Reserve(int32_t reserveSize)
{
    data_.reserve(reserveSize);
    return *this;
}
WritableValue& ArrayValue::PushBack()
{
    data_.emplace_back(make_unique<WritableValueProxy>());
    return *data_.back();
}

StringValue::StringValue(string value) : data_(move(value)) {}
ValueType StringValue::GetType() const
{
    return VALUE_TYPE_STRING;
}
bool StringValue::IsNull() const
{
    return false;
}
bool StringValue::IsString() const
{
    return true;
}
string StringValue::AsString() const
{
    return data_;
}
string StringValue::AsString(const char*) const
{
    return AsString();
}
string StringValue::AsString(const string&) const
{
    return AsString();
}
WritableValue& StringValue::SetValue(const char* val)
{
    data_ = val;
    return *this;
}
WritableValue& StringValue::SetValue(const string& val)
{
    data_ = val;
    return *this;
}

IntegerValue::IntegerValue(int32_t value) : data_(value) {}
ValueType IntegerValue::GetType() const
{
    return VALUE_TYPE_INT;
}
bool IntegerValue::IsNull() const
{
    return false;
}
int32_t IntegerValue::AsInt() const
{
    return data_;
}
int32_t IntegerValue::AsInt(int32_t) const
{
    return data_;
}
bool IntegerValue::IsInt() const
{
    return true;
}
WritableValue& IntegerValue::SetValue(int32_t val)
{
    data_ = val;
    return *this;
}

BooleanValue::BooleanValue(bool value) : data_(value) {}
ValueType BooleanValue::GetType() const
{
    return VALUE_TYPE_BOOL;
}
bool BooleanValue::IsNull() const
{
    return false;
}
bool BooleanValue::AsBool() const
{
    return data_;
}
bool BooleanValue::AsBool(bool) const
{
    return data_;
}
bool BooleanValue::IsBool() const
{
    return true;
}
WritableValue& BooleanValue::SetValue(bool val)
{
    data_ = val;
    return *this;
}

DoubleValue::DoubleValue(double value) : data_(value) {}
ValueType DoubleValue::GetType() const
{
    return VALUE_TYPE_DOUBLE;
}
bool DoubleValue::IsNull() const
{
    return false;
}
double DoubleValue::AsDouble() const
{
    return data_;
}
double DoubleValue::AsDouble(double) const
{
    return data_;
}
bool DoubleValue::IsDouble() const
{
    return true;
}
WritableValue& DoubleValue::SetValue(double val)
{
    data_ = val;
    return *this;
}

WritableValueProxy::WritableValueProxy() : data_(make_unique<WritableValue>()) {}

Value& WritableValueProxy::operator[](int32_t index)
{
    return (*data_)[index];
}
/**
 * 只有Object类型才可使用
 * @param key
 * @return 返回对应的值, 不存在时返回value WritableValueProxy::IsNull为true
 */
Value& WritableValueProxy::operator[](const char* key)
{
    return (*data_)[key];
}
ValueType WritableValueProxy::GetType() const
{
    return data_->GetType();
}
vector<string> WritableValueProxy::GetKeys() const
{
    return data_->GetKeys();
}
int32_t WritableValueProxy::Size() const
{
    return data_->Size();
}
bool WritableValueProxy::AsBool() const
{
    return data_->AsBool();
}
int32_t WritableValueProxy::AsInt() const
{
    return data_->AsInt();
}
double WritableValueProxy::AsDouble() const
{
    return data_->AsDouble();
}
string WritableValueProxy::AsString() const
{
    return data_->AsString();
}
bool WritableValueProxy::AsBool(bool defaultValue) const
{
    return data_->AsBool(defaultValue);
}
int32_t WritableValueProxy::AsInt(int32_t defaultValue) const
{
    return data_->AsInt(defaultValue);
}
double WritableValueProxy::AsDouble(double defaultValue) const
{
    return data_->AsDouble(defaultValue);
}
string WritableValueProxy::AsString(const char* defaultValue) const
{
    return data_->AsString(defaultValue);
}
string WritableValueProxy::AsString(const string& defaultValue) const
{
    return data_->AsString(defaultValue);
}
bool WritableValueProxy::IsNull() const
{
    return data_->IsNull();
}
bool WritableValueProxy::IsBool() const
{
    return data_->IsBool();
}
bool WritableValueProxy::IsInt() const
{
    return data_->IsInt();
}
bool WritableValueProxy::IsDouble() const
{
    return data_->IsDouble();
}
bool WritableValueProxy::IsString() const
{
    return data_->IsString();
}
bool WritableValueProxy::IsObject() const
{
    return data_->IsObject();
}
bool WritableValueProxy::IsArray() const
{
    return data_->IsArray();
}
WritableValue& WritableValueProxy::SetNull()
{
    if (!data_->IsNull()) {
        data_ = make_unique<WritableValue>();
    }
    return *this;
}
WritableValue& WritableValueProxy::SetValue(bool val)
{
    if (!data_->IsBool()) {
        data_ = make_unique<BooleanValue>(val);
        return *this;
    }
    data_->SetValue(val);
    return *this;
}
WritableValue& WritableValueProxy::SetValue(int32_t val)
{
    if (!data_->IsInt()) {
        data_ = make_unique<IntegerValue>(val);
        return *this;
    }
    data_->SetValue(val);
    return *this;
}
WritableValue& WritableValueProxy::SetValue(double val)
{
    if (!data_->IsDouble()) {
        data_ = make_unique<DoubleValue>(val);
        return *this;
    }
    data_->SetValue(val);
    return *this;
}
WritableValue& WritableValueProxy::SetValue(const char* val)
{
    if (!data_->IsString()) {
        data_ = make_unique<EncryptedableValue>(val);
        return *this;
    }
    data_->SetValue(val);
    return *this;
}
WritableValue& WritableValueProxy::SetValue(const string& val)
{
    if (!data_->IsString()) {
        data_ = make_unique<EncryptedableValue>(val);
        return *this;
    }
    data_->SetValue(val);
    return *this;
}
WritableValue& WritableValueProxy::SetObject()
{
    if (!data_->IsObject()) {
        data_ = make_unique<ObjectValue>();
    }
    return *this;
}
WritableValue& WritableValueProxy::AddMember(const char* name)
{
    return data_->AddMember(name);
}
WritableValue& WritableValueProxy::FindMember(const char* name)
{
    return data_->FindMember(name);
}
WritableValue& WritableValueProxy::SetArray()
{
    if (!data_->IsObject()) {
        data_ = make_unique<ArrayValue>();
    }
    return *this;
}
WritableValue& WritableValueProxy::Reserve(int32_t reserveSize)
{
    return data_->Reserve(reserveSize);
}
WritableValue& WritableValueProxy::PushBack()
{
    return data_->PushBack();
}
}
} // LCOV_EXCL_LINE