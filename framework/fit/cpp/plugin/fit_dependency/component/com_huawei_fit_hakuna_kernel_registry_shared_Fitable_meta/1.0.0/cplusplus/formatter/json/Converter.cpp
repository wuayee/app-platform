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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::FitableMeta &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("fitable");
    if (!IsNullArg(value.fitable)) {
        MessageToJson(ctx, ExtractArgToRef(value.fitable), writer);
    } else {
        writer.Null();
    }
    writer.String("formats");
    MessageToJson(ctx, ExtractArgToRef(value.formats), writer);

    writer.String("aliases");
    MessageToJson(ctx, ExtractArgToRef(value.aliases), writer);

    writer.String("tags");
    MessageToJson(ctx, ExtractArgToRef(value.tags), writer);

    writer.String("extensions");
    MessageToJson(ctx, ExtractArgToRef(value.extensions), writer);

    writer.String("environment");
    MessageToJson(ctx, ExtractArgToRef(value.environment), writer);

    writer.String("application");
    if (!IsNullArg(value.application)) {
        MessageToJson(ctx, ExtractArgToRef(value.application), writer);
    } else {
        writer.Null();
    }

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::FitableMeta &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("fitable") && !jsonValue["fitable"].IsNull()) {
        JsonToMessage(ctx, jsonValue["fitable"], value.fitable);
    }

    if (jsonValue.HasMember("formats") && !jsonValue["formats"].IsNull()) {
        JsonToMessage(ctx, jsonValue["formats"], value.formats);
    }

    if (jsonValue.HasMember("aliases") && !jsonValue["aliases"].IsNull()) {
        JsonToMessage(ctx, jsonValue["aliases"], value.aliases);
    }

    if (jsonValue.HasMember("tags") && !jsonValue["tags"].IsNull()) {
        JsonToMessage(ctx, jsonValue["tags"], value.tags);
    }

    if (jsonValue.HasMember("extensions") && !jsonValue["extensions"].IsNull()) {
        JsonToMessage(ctx, jsonValue["extensions"], value.extensions);
    }

    if (jsonValue.HasMember("environment") && !jsonValue["environment"].IsNull()) {
        JsonToMessage(ctx, jsonValue["environment"], value.environment);
    }

    if (jsonValue.HasMember("application") && !jsonValue["application"].IsNull()) {
        JsonToMessage(ctx, jsonValue["application"], value.application);
    }
    return FIT_OK;
}
}
}
}
}