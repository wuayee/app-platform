/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for map context.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/06
 */

#include <fit/internal/util/context/map_context.hpp>

#include <fit/fit_log.h>
#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>

using namespace Fit;
using namespace Fit::Context;

void MapContext::Ref()
{
    referenceCount_++;
}

void MapContext::Unref()
{
    referenceCount_--;
    if (referenceCount_ < 1) {
        values_.clear();
    }
}

void MapContext::Put(const Fit::string& key, Fit::string value)
{
    values_[key] = std::move(value);
}

void MapContext::Remove(const Fit::string& key)
{
    auto iter = values_.find(key);
    if (iter != values_.end()) {
        values_.erase(iter);
    }
}

const Fit::string& MapContext::Get(const Fit::string& key) const noexcept
{
    static Fit::string EMPTY_VALUE;
    auto iter = values_.find(key);
    if (iter == values_.end()) {
        return EMPTY_VALUE;
    } else {
        return iter->second;
    }
}

const MapContext::ContentType& MapContext::GetAll() const noexcept
{
    return values_;
}

void MapContext::Reset(MapContext::ContentType content)
{
    values_ = std::move(content);
}

bool MapContext::IsEmpty() const noexcept
{
    return values_.empty();
}

FitCode MapContext::Serialize(Fit::string& result)
{
    rapidjson::StringBuffer buffer;
    rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);
    writer.StartObject();
    for (auto& pair: values_) {
        writer.Key(pair.first.c_str());
        writer.String(pair.second.c_str());
    }
    writer.EndObject();
    result = buffer.GetString();
    return FIT_OK;
}

FitCode MapContext::Deserialize(const Fit::string& data)
{
    rapidjson::Document doc;
    doc.Parse(data.c_str());
    if (doc.HasParseError()) {
        FIT_LOG_ERROR("Failed to parse context from JSON. [content=%s]", data.c_str());
        return FIT_ERR_DESERIALIZE_JSON;
    } else if (!doc.IsObject()) {
        FIT_LOG_ERROR("The JSON of context does not contains a JSON object. [content=%s]", data.c_str());
        return FIT_ERR_DESERIALIZE_JSON;
    } else {
        ContentType values {};
        for (auto iter = doc.MemberBegin(); iter != doc.MemberEnd(); iter++) {
            auto key = iter->name.GetString();
            if (!iter->value.IsString()) {
                FIT_LOG_ERROR("The value of context item is not a string. [content=%s, key=%s]", data.c_str(), key);
                return FIT_ERR_DESERIALIZE_JSON;
            } else {
                values[key] = iter->value.GetString();
            }
        }
        values_ = std::move(values);
        return FIT_OK;
    }
}
