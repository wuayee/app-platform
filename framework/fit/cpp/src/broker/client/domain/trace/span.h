/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-07-26
 */
#ifndef SPAN_H
#define SPAN_H
#include <fit/stl/string.hpp>
#include <fit/external/util/context/context_api.hpp>
namespace Fit {
namespace Span {
class Util {
public:
static Fit::string GetOrInitSpanId(ContextObj context);
static Fit::string IncreaseWidth(const Fit::string& spanId);
static Fit::string IncreaseDepth(const Fit::string& spanId);
static void SaveSpanIdIntoGlobalContext(ContextObj context, const Fit::string& spanId);
};
}
}
#endif