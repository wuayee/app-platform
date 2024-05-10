/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */
#ifndef COM_HUAWEI_FIT_HEARTBEAT_HEARTBEATCOMMON_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_HEARTBEAT_HEARTBEATCOMMON_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/formatter/json/Converter.hpp>
#include "../../heartbeatCommon.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::heartbeat::BeatInfo &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::heartbeat::SubscribeBeatInfo &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::heartbeat::HeartbeatEvent &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);


template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::heartbeat::BeatInfo &value);

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::heartbeat::SubscribeBeatInfo &value);

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::heartbeat::HeartbeatEvent &value);
}
}
}
}
#endif