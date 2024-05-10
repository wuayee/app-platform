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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::shared::Fitable &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("genericableId");
    MessageToJson(ctx, ExtractArgToRef(value.genericableId), writer);
    writer.String("genericableVersion");
    MessageToJson(ctx, ExtractArgToRef(value.genericableVersion), writer);
    writer.String("fitableId");
    MessageToJson(ctx, ExtractArgToRef(value.fitableId), writer);
    writer.String("fitableVersion");
    MessageToJson(ctx, ExtractArgToRef(value.fitableVersion), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::hakuna::kernel::shared::Fitable &value)
{
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    value = {};

    if (jsonValue.HasMember("genericableId") && !jsonValue["genericableId"].IsNull()) {
        JsonToMessage(ctx, jsonValue["genericableId"], value.genericableId);
    }

    if (jsonValue.HasMember("genericableVersion") && !jsonValue["genericableVersion"].IsNull()) {
        JsonToMessage(ctx, jsonValue["genericableVersion"], value.genericableVersion);
    }

    if (jsonValue.HasMember("fitableId") && !jsonValue["fitableId"].IsNull()) {
        JsonToMessage(ctx, jsonValue["fitableId"], value.fitableId);
    }

    if (jsonValue.HasMember("fitableVersion") && !jsonValue["fitableVersion"].IsNull()) {
        JsonToMessage(ctx, jsonValue["fitableVersion"], value.fitableVersion);
    }

    return FIT_OK;
}
}
}
}
}