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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::FitableInstance &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("applicationInstances");
    MessageToJson(ctx, ExtractArgToRef(value.applicationInstances), writer);
    writer.String("fitable");
    if (!IsNullArg(value.fitable)) {
        MessageToJson(ctx, ExtractArgToRef(value.fitable), writer);
    } else {
        writer.Null();
    }

    writer.String("aliases");
    MessageToJson(ctx, ExtractArgToRef(value.aliases), writer);

    writer.String("tags");
    MessageToJson(ctx, ExtractArgToRef(value.tags), writer);

    writer.String("extensions");
    MessageToJson(ctx, ExtractArgToRef(value.extensions), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::FitableInstance &value)
{
    value = {};

    if (jsonValue.HasMember("applicationInstances") && !jsonValue["applicationInstances"].IsNull()) {
        JsonToMessage(ctx, jsonValue["applicationInstances"], value.applicationInstances);
    }

    if (jsonValue.HasMember("fitable") && !jsonValue["fitable"].IsNull()) {
        JsonToMessage(ctx, jsonValue["fitable"], value.fitable);
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
    return FIT_OK;
}
}
}
}
}