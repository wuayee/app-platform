/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>

using namespace Fit::Framework::Formatter::Json;
using namespace Fit::Framework::Formatter;
using namespace Fit::Framework;
namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::secure::access::TokenInfo &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();
    writer.String("token");
    MessageToJson(ctx, ExtractArgToRef(value.GetToken()), writer);
    writer.String("timeout");
    MessageToJson(ctx, value.GetTimeout(), writer);
    writer.String("type");
    MessageToJson(ctx, ExtractArgToRef(value.GetType()), writer);
    writer.String("status");
    MessageToJson(ctx, ExtractArgToRef(value.GetStatus()), writer);
    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::secure::access::TokenInfo &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("token") && !jsonValue["token"].IsNull()) {
        JsonToMessage(ctx, jsonValue["token"], value.token);
    }

    if (jsonValue.HasMember("timeout") && !jsonValue["timeout"].IsNull()) {
        JsonToMessage(ctx, jsonValue["timeout"], value.timeout);
    }

    if (jsonValue.HasMember("type") && !jsonValue["type"].IsNull()) {
        JsonToMessage(ctx, jsonValue["type"], value.type);
    }

    if (jsonValue.HasMember("status") && !jsonValue["status"].IsNull()) {
        JsonToMessage(ctx, jsonValue["status"], value.status);
    }
    return FIT_OK;
}
}
}
}
}