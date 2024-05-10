/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: trace id
 * Author: w00561424
 * Date: 2020-07-26
 */
#include "trace_id.h"
#include <sstream>
#include <fit/internal/util/fit_random.h>
#include <fit/internal/fit_time_utils.h>
#include <fit/external/util/string_utils.hpp>
#include "span.h"

namespace Fit {
namespace {
    constexpr const char* TRACE_ID = "FIT_TRACE_ID";
    const unsigned int MAX_TRACE_NUM = 10000;
    const unsigned int TRACE_ID_LAST_NUM = 4;
    const uint32_t BASE_IP_LENGTH = 2;
    Fit::string DecToHex(int value, uint32_t width)
    {
        std::stringstream ipStream;
        Fit::string result;
        ipStream << std::hex << value; // 以十六制形式输出
        ipStream >> result;

        if (width > result.size()) {
            Fit::string zeroString(width - result.size(), '0'); // 补零
            result = zeroString + result;
        }
        return result;
    }
    Fit::string GetIpString(const Fit::string& ip)
    {
        auto ipSet = StringUtils::Split(ip, '.');
        Fit::string result;
        for (const auto& it : ipSet) {
            result += DecToHex(atoi(it.c_str()), BASE_IP_LENGTH);
        }
        return result;
    }
    Fit::string GetSamplingNum()
    {
        Fit::string pre = Fit::to_string(Fit::TimeUtil::GetCurrentLocalTimestampMs());
        Fit::string samplingNum = Fit::to_string((Fit::FitRandom()) % MAX_TRACE_NUM);
        for (unsigned int i = samplingNum.length(); i < TRACE_ID_LAST_NUM; ++i) {
            samplingNum = "0" + samplingNum;
        }
        samplingNum = pre + samplingNum;
        return samplingNum;
    }
}

Fit::string TraceId::CreateId(ContextObj context, const Fit::string& ip)
{
    Fit::string traceId;
    traceId += GetIpString(ip);
    traceId += GetSamplingNum();
    SaveIdIntoGlobalContext(context, traceId);
    return traceId;
}

Fit::string TraceId::GetId(ContextObj context)
{
    return Fit::Context::Global::GetGlobalContext(context, TRACE_ID);
}
void TraceId::SaveIdIntoGlobalContext(ContextObj context, const Fit::string& traceId)
{
    Fit::Context::Global::PutGlobalContext(context, TRACE_ID, traceId);
}
}