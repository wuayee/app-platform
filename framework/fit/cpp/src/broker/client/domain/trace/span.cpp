/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-07-26
 */
#include "span.h"
namespace Fit {
namespace Span {
namespace {
    constexpr const char* SPAN_ID_KEY = "FIT_TRACE_SPAN";
}
Fit::string Util::GetOrInitSpanId(ContextObj context)
{
    Fit::string spanId = Fit::Context::Global::GetGlobalContext(context, SPAN_ID_KEY);
    if (spanId.empty()) {
        spanId = "1";
        SaveSpanIdIntoGlobalContext(context, spanId);
    }
    return spanId;
}

Fit::string Util::IncreaseWidth(const Fit::string& spanId)
{
    auto lastDotPos = spanId.find_last_of(".");
    if (lastDotPos == Fit::string::npos) {
        return Fit::to_string(atoi(spanId.c_str()) + 1);
    }
    auto lastStr = spanId.substr(lastDotPos + 1);
    return spanId.substr(0, lastDotPos + 1) + Fit::to_string(atoi(lastStr.c_str()) + 1);
}
Fit::string Util::IncreaseDepth(const Fit::string& spanId)
{
    return spanId + ".1";
}

void Util::SaveSpanIdIntoGlobalContext(ContextObj context, const Fit::string& spanId)
{
    Fit::Context::Global::PutGlobalContext(context, SPAN_ID_KEY, spanId);
}
}
}