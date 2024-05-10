/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-04-17 15:03:59
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template<>
FitCode MessageToJson(ContextObj ctx,
                      const ::fit::hakuna::kernel::registry::server::QueryRunningFitablesParam& value,
                      rapidjson::Writer<rapidjson::StringBuffer>& writer)
{
    FitCode ret = FIT_OK;
    writer.StartObject();

    if (value.HasGenericableId()) {
        writer.String("genericableId");
        ret |= MessageToJson(ctx, value.GetGenericableId(), writer);
    }

    if (value.HasGenericableVersion()) {
        writer.String("genericableVersion");
        ret |= MessageToJson(ctx, value.GetGenericableVersion(), writer);
    }

    writer.EndObject();
    return ret;
}

template<>
FitCode MessageToJson(ContextObj ctx,
                      const ::fit::hakuna::kernel::registry::server::RunningFitable& value,
                      rapidjson::Writer<rapidjson::StringBuffer>& writer)
{
    FitCode ret = FIT_OK;
    writer.StartObject();

    if (value.HasMeta()) {
        writer.String("meta");
        ret |= MessageToJson(ctx, value.GetMeta(), writer);
    }

    if (value.HasEnvironments()) {
        writer.String("environments");
        ret |= MessageToJson(ctx, value.GetEnvironments(), writer);
    }

    writer.EndObject();
    return ret;
}


template<>
FitCode JsonToMessage(ContextObj ctx,
                      const rapidjson::Value &jsonValue,
                      ::fit::hakuna::kernel::registry::server::QueryRunningFitablesParam& value)
{
    FitCode ret = FIT_OK;
    value.Reset();
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("genericableId") && !jsonValue["genericableId"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["genericableId"], *value.MutableGenericableId());
    }

    if (jsonValue.HasMember("genericableVersion") && !jsonValue["genericableVersion"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["genericableVersion"], *value.MutableGenericableVersion());
    }
    return ret;
}

template<>
FitCode JsonToMessage(ContextObj ctx,
                      const rapidjson::Value &jsonValue,
                      ::fit::hakuna::kernel::registry::server::RunningFitable& value)
{
    FitCode ret = FIT_OK;
    value.Reset();
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("meta") && !jsonValue["meta"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["meta"], value.meta);
    }

    if (jsonValue.HasMember("environments") && !jsonValue["environments"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["environments"], *value.MutableEnvironments());
    }
    return ret;
}
FIT_REGISTRATIONS
{
    auto meta = std::make_shared<FormatterMeta>();
    meta->SetGenericId("7c52fb4fdfa243af928f23607fbbee02");
    meta->SetFormat(PROTOCOL_TYPE_JSON); // json

    ArgConverterList argsInConverter;
    argsInConverter.push_back(
        ConverterBuilder<const Fit::vector< \
        ::fit::hakuna::kernel::registry::server::QueryRunningFitablesParam> *>::Build());
    meta->SetArgsInConverter(argsInConverter);

    ArgConverterList argsOutConverter;
    argsOutConverter.push_back(
        ConverterBuilder<Fit::vector<::fit::hakuna::kernel::registry::server::RunningFitable> **>::Build());
    meta->SetArgsOutConverter(argsOutConverter);
    meta->SetCreateArgsOut(
        CreateArgOutBuilder<Fit::vector<::fit::hakuna::kernel::registry::server::RunningFitable> **>::Build());

    FormatterPluginCollector::Register({meta});
}
}
}
}
}