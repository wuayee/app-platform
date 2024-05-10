/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-12-23
 * Notes:       :
 */

#include "protobuf_formatter_entry.hpp"
#include <fit/external/framework/formatter/protobuf_converter.hpp>
#include <fit/fit_log.h>
#include "formatter_util.hpp"

namespace Fit {
int32_t ProtobufFormatterEntry::GetFormateType() const
{
    return PROTOCOL_TYPE_PROTOBUF;
}

FitCode ProtobufFormatterEntry::SerializeRequest(ContextObj ctx, const ArgConverterList& converters,
    const BaseSerialization& target, const Arguments& args, string& result)
{
    Protobuf::FitRequest request;
    request.mutable_arguments()->Reserve(args.size());
    FitRequestInitNullArgumentFlags(*request.mutable_argumentnulls(), args.size());
    for (uint32_t i = 0; i < args.size(); ++i) {
        string argBuffer;
        auto ret = converters[i].Serialize(ctx, args[i], argBuffer);
        if (ret != FIT_OK && ret != FIT_NULL_PARAM) {
            FIT_LOG_ERROR("Failed to serialize, generic id = %s, formats size = %lu, arg index = %d.",
                target.genericId.c_str(), target.formats.size(), i);
            return ret;
        }
        FitRequestSetNullArgumentFlags(*request.mutable_argumentnulls(), i, ret == FIT_NULL_PARAM);
        request.mutable_arguments()->Add(std::string(argBuffer.data(), argBuffer.size()));
    }
    result = to_fit_string(request.SerializeAsString());
    return FIT_OK;
}

FitCode ProtobufFormatterEntry::DeserializeRequest(ContextObj ctx, const ArgConverterList& converters,
    const BaseSerialization& target, const string& buffer, Arguments& result)
{
    Protobuf::FitRequest request;
    if (!request.ParseFromArray(buffer.data(), buffer.size())) {
        FIT_LOG_ERROR(
            "Error deserialize, generic id = %s, format size = %lu.", target.genericId.c_str(), target.formats.size());
        return FIT_ERR_DESERIALIZE_PB;
    }

    if (request.arguments().size() != static_cast<int32_t>(converters.size())) {
        FIT_LOG_ERROR("Not matched formatter. (genericId=%s, reqArgSize=%d, converterSize=%lu).",
            target.genericId.c_str(), request.arguments().size(), converters.size());
        return FIT_ERR_NOT_MATCH;
    }

    for (int32_t i = 0; i < request.arguments().size(); ++i) {
        Argument arg;
        auto ret =
            converters[i].Deserialize(ctx, string(request.arguments(i).data(), request.arguments(i).size()), arg);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Failed to deserialize. (genericId=%s, index=%d).", target.genericId.c_str(), i);
            return ret;
        }
        result.emplace_back(move(arg));
    }
    return FIT_OK;
}
}
