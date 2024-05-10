/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2021-09-14 10:45:38
 */

#include "Converter.hpp"
#include <fit/external/util/registration.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template <>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::server::SyncSeviceAddress &value,
    rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    FitCode ret = FIT_OK;
    writer.StartObject();

    if (value.HasFitableInstance()) {
        writer.String("fitableInstance");
        ret |= MessageToJson(ctx, value.GetFitableInstance(), writer);
    }

    if (value.HasOperateType()) {
        writer.String("operateType");
        ret |= MessageToJson(ctx, value.GetOperateType(), writer);
    }

    writer.EndObject();
    return ret;
}

template <>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue,
    ::fit::hakuna::kernel::registry::server::SyncSeviceAddress &value)
{
    FitCode ret = FIT_OK;
    value.Reset();

    if (jsonValue.HasMember("fitableInstance") && !jsonValue["fitableInstance"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["fitableInstance"], value.fitableInstance);
    }

    if (jsonValue.HasMember("operateType") && !jsonValue["operateType"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["operateType"], *value.MutableOperateType());
    }
    return ret;
}
FIT_REGISTRATIONS
{
    FormatterMetaRegisterHelper<fit::hakuna::kernel::registry::server::__synchronizeFitService,
        fit::hakuna::kernel::registry::server::synchronizeFitService, PROTOCOL_TYPE_JSON>(
        Annotation::FitableType::MAIN);
}
}
}
}
}