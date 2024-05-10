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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::Address &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("host");
    MessageToJson(ctx, ExtractArgToRef(value.host), writer);
    writer.String("endpoints");
    MessageToJson(ctx, ExtractArgToRef(value.endpoints), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::Address &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("host") && !jsonValue["host"].IsNull()) {
        JsonToMessage(ctx, jsonValue["host"], value.host);
    }

    if (jsonValue.HasMember("endpoints") && !jsonValue["endpoints"].IsNull()) {
        JsonToMessage(ctx, jsonValue["endpoints"], value.endpoints);
    }

    return FIT_OK;
}
}
}
}
}