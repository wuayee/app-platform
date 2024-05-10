/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::Endpoint &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("port");
    MessageToJson(ctx, ExtractArgToRef(value.port), writer);
    writer.String("protocol");
    MessageToJson(ctx, ExtractArgToRef(value.protocol), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::Endpoint &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("port") && !jsonValue["port"].IsNull()) {
        JsonToMessage(ctx, jsonValue["port"], value.port);
    }

    if (jsonValue.HasMember("protocol") && !jsonValue["protocol"].IsNull()) {
        JsonToMessage(ctx, jsonValue["protocol"], value.protocol);
    }

    return FIT_OK;
}
}
}
}
}