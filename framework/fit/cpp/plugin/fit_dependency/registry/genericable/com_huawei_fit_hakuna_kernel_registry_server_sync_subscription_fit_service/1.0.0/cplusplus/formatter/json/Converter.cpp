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
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::server::SyncSubscriptionService &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("fitable");
    if (!IsNullArg(value.fitable)) {
        MessageToJson(ctx, ExtractArgToRef(value.fitable), writer);
    } else {
        writer.Null();
    }
    writer.String("listenerAddress");
    if (!IsNullArg(value.listenerAddress)) {
        MessageToJson(ctx, ExtractArgToRef(value.listenerAddress), writer);
    } else {
        writer.Null();
    }
    writer.String("operateType");
    MessageToJson(ctx, ExtractArgToRef(value.operateType), writer);
    writer.String("callbackFitId");
    MessageToJson(ctx, ExtractArgToRef(value.callbackFitId), writer);

    writer.EndObject();
    return FIT_OK;
}


template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::server::SyncSubscriptionService &value)
{
    if (jsonValue.HasMember("fitable") && !jsonValue["fitable"].IsNull()) {
        JsonToMessage(ctx, jsonValue["fitable"], value.fitable);
    }

    if (jsonValue.HasMember("listenerAddress") && !jsonValue["listenerAddress"].IsNull()) {
        JsonToMessage(ctx, jsonValue["listenerAddress"], value.listenerAddress);
    }

    if (!jsonValue.HasMember("operateType")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["operateType"], value.operateType);

    if (!jsonValue.HasMember("callbackFitId")) {
        return FIT_ERR_DESERIALIZE;
    }
    JsonToMessage(ctx, jsonValue["callbackFitId"], value.callbackFitId);

    return FIT_OK;
}
FIT_REGISTRATIONS
{
    FormatterMetaRegisterHelper<fit::hakuna::kernel::registry::server::__syncSubscriptionFitService,
        fit::hakuna::kernel::registry::server::syncSubscriptionFitService, PROTOCOL_TYPE_JSON>(
        Annotation::FitableType::MAIN);
}
}
}
}
}