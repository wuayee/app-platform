/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-12-25
 * Notes:       :
 */

#include "json_formatter_entry.hpp"
#include <fit/external/framework/formatter/json_converter.hpp>
#include <fit/fit_log.h>

namespace Fit {
int32_t JsonFormatterEntry::GetFormateType() const
{
    return PROTOCOL_TYPE_JSON;
}

FitCode JsonFormatterEntry::SerializeRequest(ContextObj ctx, const ArgConverterList& converters,
    const BaseSerialization& target, const Arguments& args, string& result)
{
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    writer.StartArray();
    for (uint32_t i = 0; i < args.size(); ++i) {
        Fit::string argBuffer;
        auto ret = converters[i].Serialize(ctx, args[i], argBuffer);
        if (ret != FIT_OK && ret != FIT_NULL_PARAM) {
            FIT_LOG_ERROR("Error serialize, generic id = %s, format size = %lu, arg index = %d.",
                target.genericId.c_str(), target.formats.size(), i);
            return ret;
        }
        if (ret == FIT_NULL_PARAM) {
            writer.Null();
        } else {
            rapidjson::Document doc;
            doc.Parse(argBuffer.c_str());
            if (doc.HasParseError()) {
                FIT_LOG_ERROR("Failed to serialize with json. [index=%d, json=%s]", i, argBuffer.c_str());
                return FIT_ERR_SERIALIZE_JSON;
            }
            doc.Accept(writer);
        }
    }
    writer.EndArray();
    result = sb.GetString();

    return FIT_OK;
}
FitCode JsonFormatterEntry::DeserializeRequest(ContextObj ctx, const ArgConverterList& converters,
    const BaseSerialization& target, const string& buffer, Arguments& result)
{
    rapidjson::Document doc;
    doc.Parse(buffer.c_str());
    if (doc.HasParseError() || !doc.IsArray()) {
        FIT_LOG_ERROR("Parse json error or not array. (json=%s).", buffer.c_str());
        return FIT_ERR_DESERIALIZE_JSON;
    }

    auto arr = doc.GetArray();
    if (arr.Size() != converters.size()) {
        FIT_LOG_ERROR("Not matched formatter. (genericId=%s, reqArgSize=%u, converterSize=%lu).",
            target.genericId.c_str(), arr.Size(), converters.size());
        return FIT_ERR_NOT_MATCH;
    }
    for (size_t i = 0; i < arr.Size(); ++i) {
        rapidjson::StringBuffer sb;
        rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
        arr[i].Accept(writer);
        Fit::any arg;
        auto ret = converters[i].Deserialize(ctx, sb.GetString(), arg);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to deserialize. (genericId=%s, index=%lu, json=%s.", target.genericId.c_str(), i,
                buffer.c_str());
            return ret;
        }
        result.emplace_back(move(arg));
    }

    return FIT_OK;
}
}
