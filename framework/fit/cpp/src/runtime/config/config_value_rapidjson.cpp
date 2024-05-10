/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/16
 * Notes:       :
 */
#include <fit/internal/runtime/config/config_value_rapidjson.hpp>
#include <fit/stl/memory.hpp>
#include <fit/external/util/string_utils.hpp>
#include <fit/fit_log.h>

namespace Fit {
namespace Config {
ValueRapidJson ValueRapidJson::nullValue_;

ValueRapidJson::ValueRapidJson(rapidjson::Value *jsonValue, rapidjson::Document *document)
    : document_(document), value_(jsonValue)
{
    if (value_->IsArray()) {
        for (uint32_t i = 0; i < value_->Size(); ++i) {
            arrays_.insert(
                std::make_pair(i, make_unique<ValueRapidJson>(&value_->operator[](i), document_)));
        }
    } else if (value_->IsObject()) {
        for (auto &item : value_->GetObject()) {
            objects_.insert(
                std::make_pair(item.name.GetString(),
                    make_unique<ValueRapidJson>(&item.value, document_)));
        }
    }
}

ValueRapidJson::~ValueRapidJson() {}

Value &ValueRapidJson::operator[](int32_t index)
{
    if (!IsArray()) {
        throw std::runtime_error("need array type with index");
    }

    if (static_cast<size_t>(index) >= arrays_.size()) {
        return nullValue_;
    }

    return *arrays_[index];
}

Value &ValueRapidJson::operator[](const char *key)
{
    if (!IsObject()) {
        throw std::runtime_error(std::string("need object type with key"));
    }

    auto keyPaths = StringUtils::Split(key, '.');
    const auto &beginPath = keyPaths.front();
    ValueRapidJson *configItem = this;
    for (auto &item : keyPaths) {
        configItem = &configItem->FindMember(item.c_str());
        if (configItem->IsNull()) {
            break;
        }
    }

    return *configItem;
}

ValueType ValueRapidJson::GetType() const
{
    if (value_ == nullptr) {
        return VALUE_TYPE_NULL;
    }

    if (value_->IsNull()) {
        return VALUE_TYPE_NULL;
    } else if (value_->IsInt()) {
        return VALUE_TYPE_INT;
    } else if (value_->IsDouble() || value_->IsFloat()) {
        return VALUE_TYPE_DOUBLE;
    } else if (value_->IsString()) {
        return VALUE_TYPE_STRING;
    } else if (value_->IsBool()) {
        return VALUE_TYPE_BOOL;
    } else if (value_->IsObject()) {
        return VALUE_TYPE_OBJECT;
    } else if (value_->IsArray()) {
        return VALUE_TYPE_ARRAY;
    }
    return VALUE_TYPE_NULL;
}

int32_t ValueRapidJson::Size() const
{
    if (!IsArray()) {
        throw std::runtime_error("need array type");
    }

    return value_->Size();
}

bool ValueRapidJson::AsBool() const
{
    if (!IsBool()) {
        throw std::runtime_error("need bool type");
    }

    return value_->GetBool();
}

int32_t ValueRapidJson::AsInt() const
{
    if (!IsInt()) {
        throw std::runtime_error("need int type");
    }

    return value_->GetInt();
}

double ValueRapidJson::AsDouble() const
{
    if (!IsDouble()) {
        throw std::runtime_error("need double type");
    }

    return value_->GetDouble();
}

string ValueRapidJson::AsString() const
{
    if (!IsString()) {
        throw std::runtime_error("need string type");
    }

    return value_->GetString();
}

bool ValueRapidJson::AsBool(bool defaultValue) const
{
    if (!IsBool()) {
        return defaultValue;
    }

    return value_->GetBool();
}

int32_t ValueRapidJson::AsInt(int32_t defaultValue) const
{
    if (!IsInt()) {
        return defaultValue;
    }

    return value_->GetInt();
}

double ValueRapidJson::AsDouble(double defaultValue) const
{
    if (!IsDouble()) {
        return defaultValue;
    }

    return value_->GetDouble();
}

string ValueRapidJson::AsString(const char *defaultValue) const
{
    if (!IsString()) {
        return defaultValue;
    }

    return value_->GetString();
}

string ValueRapidJson::AsString(const string& defaultValue) const
{
    return AsString(defaultValue.c_str());
}

bool ValueRapidJson::IsNull() const
{
    return !value_ || value_->IsNull();
}

bool ValueRapidJson::IsBool() const
{
    return value_ && value_->IsBool();
}

bool ValueRapidJson::IsInt() const
{
    return value_ && value_->IsNumber();
}

bool ValueRapidJson::IsDouble() const
{
    return value_ && (value_->IsDouble() || value_->IsFloat());
}

bool ValueRapidJson::IsString() const
{
    return value_ && value_->IsString();
}

bool ValueRapidJson::IsObject() const
{
    return value_ && value_->IsObject();
}

bool ValueRapidJson::IsArray() const
{
    return value_ && value_->IsArray();
}

ValueRapidJson &ValueRapidJson::AddMember(const char *name)
{
    if (!IsObject()) {
        throw std::runtime_error(std::string("need object type for add member"));
    }

    auto &newItem = value_->AddMember(::rapidjson::Value().SetString(name, document_->GetAllocator()),
        ::rapidjson::Value(), document_->GetAllocator()).FindMember(name)->value;

    return *objects_.insert(
        std::make_pair(name, make_unique<ValueRapidJson>(&newItem, document_))).first->second;
}

ValueRapidJson &ValueRapidJson::FindMember(const char *name)
{
    if (!IsObject()) {
        throw std::runtime_error(std::string("need object type for find member"));
    }

    auto iter = objects_.find(name);
    if (iter == objects_.end()) {
        return nullValue_;
    }

    return *iter->second;
}

ValueRapidJson &ValueRapidJson::SetValue(bool val)
{
    if (!value_ || !document_) {
        throw std::runtime_error("no node is assigned");
    }
    if (!IsBool()) {
        Clear();
    }

    value_->SetBool(val);

    return *this;
}

ValueRapidJson &ValueRapidJson::SetValue(int32_t val)
{
    if (!value_ || !document_) {
        throw std::runtime_error("no node is assigned");
    }
    if (!IsInt()) {
        Clear();
    }

    value_->SetInt(val);

    return *this;
}

ValueRapidJson &ValueRapidJson::SetValue(double val)
{
    if (!value_ || !document_) {
        throw std::runtime_error("no node is assigned");
    }
    if (!IsDouble()) {
        Clear();
    }

    value_->SetDouble(val);

    return *this;
}

ValueRapidJson &ValueRapidJson::SetValue(const char *val)
{
    if (!value_ || !document_) {
        throw std::runtime_error("no node is assigned");
    }
    if (!IsString()) {
        Clear();
    }

    value_->SetString(val, document_->GetAllocator());

    return *this;
}

ValueRapidJson &ValueRapidJson::SetObject()
{
    if (!value_ || !document_) {
        throw std::runtime_error("no node is assigned");
    }

    if (!IsObject()) {
        Clear();
    }

    value_->SetObject();

    return *this;
}

ValueRapidJson &ValueRapidJson::SetNull()
{
    if (!value_ || !document_) {
        return *this;
    }

    if (!IsNull()) {
        Clear();
    }

    value_->SetNull();

    return *this;
}

ValueRapidJson &ValueRapidJson::SetArray()
{
    if (!value_ || !document_) {
        throw std::runtime_error("no node is assigned");
    }

    if (!IsArray()) {
        Clear();
    }

    value_->SetArray();

    return *this;
}

ValueRapidJson &ValueRapidJson::Reserve(int32_t reserveSize)
{
    if (!IsArray()) {
        throw std::runtime_error(std::string("need array type for reserve"));
    }
    value_->Reserve(reserveSize, document_->GetAllocator());

    return *this;
}

ValueRapidJson &ValueRapidJson::PushBack()
{
    if (!IsArray()) {
        throw std::runtime_error(std::string("need array type for push back"));
    }

    value_->PushBack(::rapidjson::Value(), document_->GetAllocator());
    auto &newItem = (*value_)[value_->Size() - 1];

    return *arrays_.insert(
        std::make_pair(value_->Size() - 1,
            make_unique<ValueRapidJson>(&newItem, document_))).first->second;
}

void ValueRapidJson::Clear()
{
    objects_.clear();
    arrays_.clear();
}

Fit::vector<Fit::string> ValueRapidJson::GetKeys() const
{
    if (!IsObject()) {
        throw std::runtime_error(std::string("need Object type for get keys"));
    }

    Fit::vector<Fit::string> res;
    for (auto& m : value_->GetObject()) {
        res.emplace_back(m.name.GetString());
    }
    return res;
}
}
} // LCOV_EXCL_LINE