/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2022-01-12 19:35:24
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::broker::client::GenericInvokeOptions &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    FitCode ret = FIT_OK;
    writer.StartObject();

    if (value.HasWorkerId()) {
        writer.String("workerId");
        ret |= MessageToJson(ctx, value.GetWorkerId(), writer);
    }

    if (value.HasAlias()) {
        writer.String("alias");
        ret |= MessageToJson(ctx, value.GetAlias(), writer);
    }

    if (value.HasTimeout()) {
        writer.String("timeout");
        ret |= MessageToJson(ctx, value.GetTimeout(), writer);
    }

    writer.EndObject();
    return ret;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::broker::client::GenericInvokeOptions &value)
{
    FitCode ret = FIT_OK;
    value.Reset();

    if (jsonValue.HasMember("workerId") && !jsonValue["workerId"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["workerId"], *value.MutableWorkerId());
    }

    if (jsonValue.HasMember("alias") && !jsonValue["alias"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["alias"], *value.MutableAlias());
    }

    if (jsonValue.HasMember("timeout") && !jsonValue["timeout"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["timeout"], *value.MutableTimeout());
    }
    return ret;
}
FIT_REGISTRATIONS
{
    FormatterMetaRegisterHelper<fit::hakuna::kernel::broker::client::__genericInvokeV3,
        fit::hakuna::kernel::broker::client::genericInvokeV3, PROTOCOL_TYPE_JSON>(Annotation::FitableType::MAIN);
}
}
}
}
}