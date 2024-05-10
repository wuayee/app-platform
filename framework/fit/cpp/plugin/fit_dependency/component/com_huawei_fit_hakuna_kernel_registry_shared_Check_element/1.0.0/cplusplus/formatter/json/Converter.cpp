/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : json converter
 * Author       : auto
 * Date         : 2023-10-17 11:32:13
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
                      const ::fit::hakuna::kernel::registry::shared::CheckElement& value,
                      rapidjson::Writer<rapidjson::StringBuffer>& writer)
{
    FitCode ret = FIT_OK;
    writer.StartObject();

    if (value.HasType()) {
        writer.String("type");
        ret |= MessageToJson(ctx, value.GetType(), writer);
    }

    if (value.HasKvs()) {
        writer.String("kvs");
        ret |= MessageToJson(ctx, value.GetKvs(), writer);
    }

    writer.EndObject();
    return ret;
}


template<>
FitCode JsonToMessage(ContextObj ctx,
                      const rapidjson::Value &jsonValue,
                      ::fit::hakuna::kernel::registry::shared::CheckElement& value)
{
    FitCode ret = FIT_OK;
    value.Reset();
    if (!jsonValue.IsObject()) {
        return FIT_ERR_DESERIALIZE_JSON;
    }

    if (jsonValue.HasMember("type") && !jsonValue["type"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["type"], *value.MutableType());
    }

    if (jsonValue.HasMember("kvs") && !jsonValue["kvs"].IsNull()) {
        ret |= JsonToMessage(ctx, jsonValue["kvs"], *value.MutableKvs());
    }
    return ret;
}
}
}
}
}