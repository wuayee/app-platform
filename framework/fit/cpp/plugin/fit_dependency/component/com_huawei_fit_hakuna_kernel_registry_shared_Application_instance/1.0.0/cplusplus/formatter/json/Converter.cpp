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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::shared::ApplicationInstance &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("workers");
    MessageToJson(ctx, ExtractArgToRef(value.workers), writer);
    writer.String("application");
    if (!IsNullArg(value.application)) {
        MessageToJson(ctx, ExtractArgToRef(value.application), writer);
    } else {
        writer.Null();
    }
    writer.String("formats");
    MessageToJson(ctx, ExtractArgToRef(value.formats), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("workers") && !jsonValue["workers"].IsNull()) {
        JsonToMessage(ctx, jsonValue["workers"], value.workers);
    }

    if (jsonValue.HasMember("application") && !jsonValue["application"].IsNull()) {
        JsonToMessage(ctx, jsonValue["application"], value.application);
    }

    if (jsonValue.HasMember("formats") && !jsonValue["formats"].IsNull()) {
        JsonToMessage(ctx, jsonValue["formats"], value.formats);
    }

    return FIT_OK;
}
}
}
}
}