/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : auto generate by FIT IDL
 * Author       : auto
 * Date         :
 */
#ifndef COM_HUAWEI_FIT_REGISTRY_REGISTRYCOMMON_JSON_CONVERTER_H
#define COM_HUAWEI_FIT_REGISTRY_REGISTRYCOMMON_JSON_CONVERTER_H

#include <fit/external/framework/formatter/json_converter.hpp>
#include "../../registryCommon.hpp"

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::Address &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::Worker &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::Fitable &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::ServiceMeta &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);

template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::registry::ServiceAddress &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer);


template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::Address &value);

template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::Worker &value);

template <> FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::Fitable &value);

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::ServiceMeta &value);

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, ::fit::registry::ServiceAddress &value);
}
}
}
}
#endif