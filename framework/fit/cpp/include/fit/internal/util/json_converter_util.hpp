/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : wangpanbo
 * Date         : 2023/07/31
 * Notes:       :
 */
#ifndef JSON_CONVERTER_UTIL_HPP
#define JSON_CONVERTER_UTIL_HPP

#include <fit_code.h>
#include <fit/fit_log.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>
#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
namespace Fit {
namespace JsonConverterUtil {
inline FitCode MessageToJson(const Fit::vector<Fit::string>& strings, Fit::string& jsonValue)
{
    rapidjson::StringBuffer buffer;
    rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);
    writer.StartArray();
    for (const auto &str : strings) {
        writer.String(str.c_str());
    }
    writer.EndArray();
    jsonValue = buffer.GetString();
    return FIT_OK;
}

inline FitCode JsonToMessage(const Fit::string& data, Fit::vector<Fit::string>& message)
{
    rapidjson::Document doc;
    doc.Parse(data.c_str());
    if (doc.HasParseError() || !doc.IsArray()) {
        FIT_LOG_ERROR("Failed to parse context from JSON. [content=%s]", data.c_str());
        return FIT_ERR_FAIL;
    }

    for (rapidjson::SizeType i = 0; i < doc.Size(); ++i) {
        if (!doc[i].IsString()) {
            FIT_LOG_ERROR("Type is not string.");
            return FIT_ERR_FAIL;
        }
        message.emplace_back(doc[i].GetString());
    }
    return FIT_OK;
}

inline FitCode MessageToJson(const Fit::map<Fit::string, Fit::string>& kvs, Fit::string& jsonValue)
{
    rapidjson::StringBuffer buffer;
    rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);
    writer.StartObject();
    for (auto& pair: kvs) {
        writer.Key(pair.first.c_str());
        writer.String(pair.second.c_str());
    }
    writer.EndObject();
    jsonValue = buffer.GetString();
    return FIT_OK;
}

inline FitCode JsonToMessage(const Fit::string& data, Fit::map<Fit::string, Fit::string>& message)
{
    rapidjson::Document doc;
    doc.Parse(data.c_str());
    if (doc.HasParseError() || !doc.IsObject()) {
        FIT_LOG_ERROR("Failed to parse context from JSON. [content=%s]", data.c_str());
        return FIT_ERR_FAIL;
    }
    for (auto iter = doc.MemberBegin(); iter != doc.MemberEnd(); iter++) {
        auto key = iter->name.GetString();
        if (iter->value.IsString()) {
            message[key] = iter->value.GetString();
        } else {
            FIT_LOG_ERROR("The value of context item is not a string. [content=%s, key=%s]", data.c_str(), key);
            return FIT_ERR_FAIL;
        }
    }
    return FIT_OK;
}
}
}
#endif
