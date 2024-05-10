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
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::Address &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("host");
    MessageToJson(ctx, ExtractArgToRef(value.host), writer);
    writer.String("port");
    MessageToJson(ctx, ExtractArgToRef(value.port), writer);
    writer.String("id");
    MessageToJson(ctx, ExtractArgToRef(value.id), writer);
    writer.String("protocol");
    MessageToJson(ctx, ExtractArgToRef(value.protocol), writer);
    writer.String("formats");
    MessageToJson(ctx, ExtractArgToRef(value.formats), writer);
    writer.String("environment");
    MessageToJson(ctx, ExtractArgToRef(value.environment), writer);

    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::Worker &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("address");
    if (!IsNullArg(value.address)) {
        MessageToJson(ctx, ExtractArgToRef(value.address), writer);
    } else {
        writer.Null();
    }
    writer.String("token");
    MessageToJson(ctx, ExtractArgToRef(value.token), writer);

    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::Fitable &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("genericId");
    MessageToJson(ctx, ExtractArgToRef(value.genericId), writer);
    writer.String("genericVersion");
    MessageToJson(ctx, ExtractArgToRef(value.genericVersion), writer);
    writer.String("fitId");
    MessageToJson(ctx, ExtractArgToRef(value.fitId), writer);
    writer.String("fitVersion");
    MessageToJson(ctx, ExtractArgToRef(value.fitVersion), writer);

    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::ServiceMeta &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("fitable");
    if (!IsNullArg(value.fitable)) {
        MessageToJson(ctx, ExtractArgToRef(value.fitable), writer);
    } else {
        writer.Null();
    }
    writer.String("serviceName");
    MessageToJson(ctx, ExtractArgToRef(value.serviceName), writer);
    writer.String("pluginName");
    MessageToJson(ctx, ExtractArgToRef(value.pluginName), writer);

    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::ServiceAddress &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("serviceMeta");
    if (!IsNullArg(value.serviceMeta)) {
        MessageToJson(ctx, ExtractArgToRef(value.serviceMeta), writer);
    } else {
        writer.Null();
    }
    writer.String("addressList");
    MessageToJson(ctx, ExtractArgToRef(value.addressList), writer);

    writer.EndObject();
    return FIT_OK;
}


template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::Address &value)
{
    FitCode ret = FIT_OK;
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("host") && !jsonValue["host"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["host"], value.host);
    }

    if (jsonValue.HasMember("port") && !jsonValue["port"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["port"], value.port);
    }
    
    if (jsonValue.HasMember("id") && !jsonValue["id"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["id"], value.id);
    }
    
    if (jsonValue.HasMember("protocol") && !jsonValue["protocol"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["protocol"], value.protocol);
    }
    
    if (jsonValue.HasMember("formats") && !jsonValue["formats"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["formats"], value.formats);
    }
    
    if (jsonValue.HasMember("environment") && !jsonValue["environment"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["environment"], value.environment);
    }

    return ret;
}

template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::Worker &value)
{
    if (jsonValue.HasMember("address") && !jsonValue["address"].IsNull()) {
        JsonToMessage(ctx, jsonValue["address"], value.address);
    }

    if (!jsonValue.HasMember("token")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["token"], value.token);

    return FIT_OK;
}

template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::Fitable &value)
{
    if (!jsonValue.HasMember("genericId")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["genericId"], value.genericId);

    if (!jsonValue.HasMember("genericVersion")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["genericVersion"], value.genericVersion);

    if (!jsonValue.HasMember("fitId")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["fitId"], value.fitId);

    if (!jsonValue.HasMember("fitVersion")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["fitVersion"], value.fitVersion);

    return FIT_OK;
}

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::ServiceMeta &value)
{
    if (jsonValue.HasMember("fitable") && !jsonValue["fitable"].IsNull()) {
        JsonToMessage(ctx, jsonValue["fitable"], value.fitable);
    }

    if (!jsonValue.HasMember("serviceName")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["serviceName"], value.serviceName);

    if (!jsonValue.HasMember("pluginName")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["pluginName"], value.pluginName);

    return FIT_OK;
}

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::ServiceAddress &value)
{
    if (jsonValue.HasMember("serviceMeta") && !jsonValue["serviceMeta"].IsNull()) {
        JsonToMessage(ctx, jsonValue["serviceMeta"], value.serviceMeta);
    }

    if (!jsonValue.HasMember("addressList")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["addressList"], value.addressList);

    return FIT_OK;
}
}
}
}
}