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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::Application &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("name");
    MessageToJson(ctx, ExtractArgToRef(value.name), writer);
    writer.String("nameVersion");
    MessageToJson(ctx, ExtractArgToRef(value.nameVersion), writer);
    writer.String("extensions");
    MessageToJson(ctx, ExtractArgToRef(value.extensions), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::Application &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_PARAM;
    }
    value = {};

    if (jsonValue.HasMember("name") && !jsonValue["name"].IsNull()) {
        JsonToMessage(ctx, jsonValue["name"], value.name);
    }

    if (jsonValue.HasMember("nameVersion") && !jsonValue["nameVersion"].IsNull()) {
        JsonToMessage(ctx, jsonValue["nameVersion"], value.nameVersion);
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