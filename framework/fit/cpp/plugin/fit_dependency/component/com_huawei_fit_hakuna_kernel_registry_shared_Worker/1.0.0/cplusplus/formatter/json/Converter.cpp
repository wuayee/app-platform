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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::Worker &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("addresses");
    MessageToJson(ctx, ExtractArgToRef(value.addresses), writer);
    writer.String("id");
    MessageToJson(ctx, ExtractArgToRef(value.id), writer);
    writer.String("expire");
    MessageToJson(ctx, ExtractArgToRef(value.expire), writer);
    writer.String("environment");
    MessageToJson(ctx, ExtractArgToRef(value.environment), writer);
    writer.String("extensions");
    MessageToJson(ctx, ExtractArgToRef(value.extensions), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::Worker &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("addresses") && !jsonValue["addresses"].IsNull()) {
        JsonToMessage(ctx, jsonValue["addresses"], value.addresses);
    }

    if (jsonValue.HasMember("id") && !jsonValue["id"].IsNull()) {
        JsonToMessage(ctx, jsonValue["id"], value.id);
    }

    if (jsonValue.HasMember("expire") && !jsonValue["expire"].IsNull()) {
        JsonToMessage(ctx, jsonValue["expire"], value.expire);
    }

    if (jsonValue.HasMember("environment") && !jsonValue["environment"].IsNull()) {
        JsonToMessage(ctx, jsonValue["environment"], value.environment);
    }

    if (jsonValue.HasMember("extensions") && !jsonValue["extensions"].IsNull()) {
        JsonToMessage(ctx, jsonValue["extensions"], value.extensions);
    }

    return FIT_OK;
}
}
}
}
}