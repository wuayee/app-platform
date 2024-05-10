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
FitCode MessageToJson(ContextObj ctx, const ::fit::heartbeat::BeatInfo &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("sceneType");
    MessageToJson(ctx, ExtractArgToRef(value.sceneType), writer);
    writer.String("interval");
    MessageToJson(ctx, ExtractArgToRef(value.interval), writer);
    writer.String("aliveTime");
    MessageToJson(ctx, ExtractArgToRef(value.aliveTime), writer);
    writer.String("initDelay");
    MessageToJson(ctx, ExtractArgToRef(value.initDelay), writer);
    writer.String("callbackFitId");
    MessageToJson(ctx, ExtractArgToRef(value.callbackFitId), writer);

    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::heartbeat::SubscribeBeatInfo &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("sceneType");
    MessageToJson(ctx, ExtractArgToRef(value.sceneType), writer);
    writer.String("callbackFitId");
    MessageToJson(ctx, ExtractArgToRef(value.callbackFitId), writer);
    writer.String("mode");
    MessageToJson(ctx, ExtractArgToRef(value.mode), writer);

    writer.EndObject();
    return FIT_OK;
}

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::heartbeat::HeartbeatEvent &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("sceneType");
    MessageToJson(ctx, ExtractArgToRef(value.sceneType), writer);
    writer.String("eventType");
    MessageToJson(ctx, ExtractArgToRef(value.eventType), writer);
    writer.String("address");
    if (!IsNullArg(value.address)) {
        MessageToJson(ctx, ExtractArgToRef(value.address), writer);
    } else {
        writer.Null();
    }

    writer.EndObject();
    return FIT_OK;
}


template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::heartbeat::BeatInfo &value)
{
    FitCode ret = FIT_OK;
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("sceneType") && !jsonValue["sceneType"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["sceneType"], value.sceneType);
    }

    if (jsonValue.HasMember("interval") && !jsonValue["interval"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["interval"], value.interval);
    }
    
    if (jsonValue.HasMember("aliveTime") && !jsonValue["aliveTime"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["aliveTime"], value.aliveTime);
    }
    
    if (jsonValue.HasMember("initDelay") && !jsonValue["initDelay"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["initDelay"], value.initDelay);
    }

    if (jsonValue.HasMember("callbackFitId") && !jsonValue["callbackFitId"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["callbackFitId"], value.callbackFitId);
    }

    return ret;
}

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::heartbeat::SubscribeBeatInfo &value)
{
    FitCode ret = FIT_OK;
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("sceneType") && !jsonValue["sceneType"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["sceneType"], value.sceneType);
    }

    if (jsonValue.HasMember("callbackFitId") && !jsonValue["callbackFitId"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["callbackFitId"], value.callbackFitId);
    }

    if (jsonValue.HasMember("mode") && !jsonValue["mode"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["mode"], value.mode);
    }

    return ret;
}

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::heartbeat::HeartbeatEvent &value)
{
    FitCode ret = FIT_OK;
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("sceneType") && !jsonValue["sceneType"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["sceneType"], value.sceneType);
    }

    if (jsonValue.HasMember("eventType") && !jsonValue["eventType"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["eventType"], value.eventType);
    }

    if (jsonValue.HasMember("address") && !jsonValue["address"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["address"], value.address);
    }

    return FIT_OK;
}
}
}
}
}