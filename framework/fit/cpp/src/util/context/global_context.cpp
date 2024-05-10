/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/22 16:23
 */

#include "fit/internal/util/context/global_context.hpp"
#include "fit/fit_log.h"

#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>

namespace Fit {
namespace Context {
void GlobalContext::Ref()
{
    ++refCount;
}

void GlobalContext::UnRef()
{
    --refCount;
    if (refCount == 0) {
        valueMap_.clear();
    }
}

bool GlobalContext::PutGlobalContext(const Fit::string &key, const Fit::string &value)
{
    valueMap_[key] = value;
    return true;
}

bool GlobalContext::RemoveGlobalContext(const Fit::string &key)
{
    auto it = valueMap_.find(key);
    if (it != valueMap_.end()) {
        valueMap_.erase(it);
    }
    return true;
}

Fit::string GlobalContext::GetGlobalContext(const Fit::string &key) const
{
    auto it = valueMap_.find(key);
    if (it != valueMap_.end()) {
        return it->second;
    }
    return "";
}

const GlobalContext::CacheType &GlobalContext::GetAllGlobalContext() const
{
    return valueMap_;
}

bool GlobalContext::RestoreGlobalContext(const GlobalContext::CacheType &context)
{
    valueMap_ = context;
    return true;
}

bool GlobalContext::IsEmpty()
{
    return valueMap_.empty();
}

FitCode GlobalContext::Serialize(Fit::string &result)
{
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    writer.StartObject();
    writer.Key("pair");
    writer.StartArray();
    for (const auto &item : valueMap_) {
        writer.StartObject();
        writer.Key("key");
        writer.String(item.first.c_str());
        writer.Key("value");
        writer.String(item.second.c_str());
        writer.EndObject();
    }
    writer.EndArray();
    writer.EndObject();
    result = sb.GetString();
    return FIT_OK;
}

FitCode GlobalContext::Deserialize(const Fit::string &data)
{
    rapidjson::Document doc;
    doc.Parse(data.c_str());
    if (doc.HasParseError()) {
        FIT_LOG_ERROR("Parse json error: json = %s.", data.c_str());
        return FIT_ERR_DESERIALIZE_JSON;
    }
    if (!doc.HasMember("pair") || !doc["pair"].IsArray()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    auto arr = doc["pair"].GetArray();
    for (size_t i = 0; i < arr.Size(); i++) {
        auto &item = arr[i];
        if (!item.IsObject() || !item.HasMember("key") || !item.HasMember("value")) {
            return FIT_ERR_DESERIALIZE_JSON;
        }
        FIT_LOG_INFO("Add key:%s, value:%s.", item["key"].GetString(), item["value"].GetString());
        valueMap_[item["key"].GetString()] = item["value"].GetString();
    }
    return FIT_OK;
}
}
}