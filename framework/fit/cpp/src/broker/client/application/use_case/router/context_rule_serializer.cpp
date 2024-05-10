/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/25 11:27
 */

#include "context_rule_serializer.hpp"
#include <sstream>

#include <rapidjson/rapidjson.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include <fit/stl/string.hpp>
namespace Fit {
ContextRuleSerializer::ContextRuleSerializer(ContextObj ctx) : ctx_(ctx) {}

FitCode ContextRuleSerializer::Serialize(Fit::string &serializeResult)
{
    const auto &routeContext = Context::GetAllRouteContext(ctx_);
    return SerializeInner(routeContext, serializeResult);
}

FitCode ContextRuleSerializer::SerializeInner(const Fit::map<Fit::string, Fit::string> &routeContext,
    Fit::string &serializeResult)
{
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);

    writer.StartObject();
    for (const auto &item : routeContext) {
        writer.Key(item.first.c_str());
        writer.String(item.second.c_str());
    }
    writer.EndObject();

    std::ostringstream oss;
    oss << "\"C\" : " << sb.GetString();
    serializeResult = Fit::to_fit_string(oss.str());
    return FIT_OK;
}
} // LCOV_EXCL_LINE