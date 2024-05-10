/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-07-26
 */
#ifndef TRACE_ID_H
#define TRACE_ID_H
#include <fit/stl/string.hpp>
#include <fit/external/util/context/context_api.hpp>
namespace Fit {
class TraceId {
public:
    static Fit::string CreateId(ContextObj context, const Fit::string& ip);
    static Fit::string GetId(ContextObj context);
    static void SaveIdIntoGlobalContext(ContextObj context, const Fit::string& TraceId);
};
}
#endif