/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : json converter
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
template<>
FitCode MessageToJson(ContextObj ctx, const ::fit::hakuna::kernel::registry::server::WorkerDetail& value,
    rapidjson::Writer<rapidjson::StringBuffer>& writer)
{
    FitCode ret = FIT_OK;
    writer.StartObject();

    writer.String("worker");
    ret |= MessageToJson(ctx, value.worker, writer);

    writer.String("app");
    ret |= MessageToJson(ctx, value.app, writer);

    writer.String("fitables");
    ret |= MessageToJson(ctx, value.fitables, writer);

    writer.EndObject();
    return ret;
}

template<>
FitCode JsonToMessage(
    ContextObj ctx, const rapidjson::Value& jsonValue, ::fit::hakuna::kernel::registry::server::WorkerDetail& value)
{
    FitCode ret = FIT_OK;
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    {
        auto iter = jsonValue.FindMember("worker");
        if (iter != jsonValue.MemberEnd() && !iter->value.IsNull()) {
            ret |= JsonToMessage(ctx, iter->value, value.worker);
        }
    }
    {
        auto iter = jsonValue.FindMember("app");
        if (iter != jsonValue.MemberEnd() && !iter->value.IsNull()) {
            ret |= JsonToMessage(ctx, iter->value, value.app);
        }
    }
    {
        auto iter = jsonValue.FindMember("fitables");
        if (iter != jsonValue.MemberEnd() && !iter->value.IsNull()) {
            ret |= JsonToMessage(ctx, iter->value, value.fitables);
        }
    }

    return ret;
}
FIT_REGISTRATIONS
{
    FormatterMetaRegisterHelper<fit::hakuna::kernel::registry::server::__QueryWorkerDetail,
        fit::hakuna::kernel::registry::server::QueryWorkerDetail, PROTOCOL_TYPE_JSON>(Annotation::FitableType::MAIN);
}
}
}
}
}