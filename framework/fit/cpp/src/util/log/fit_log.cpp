/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : 日志控制，增加cli可控制日志打印级别，实时控制日志打印
 * Author       : songyongtan
 * Create       : 2020/11/03 10:31
 * Notes:       :
 */

#include <fit/fit_log.h>

#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <cstdarg>

#include "fit_log_inner.h"
#include "securec.h"

namespace {
FitLogCallbackInfo &GetLogCallback()
{
    static FitLogCallbackInfo instance {};
    return instance;
}

inline bool IsEnabledLog(FitLogInfo& logInfo)
{
    if (GetLogCallback().isEnabledFunc) {
        return GetLogCallback().isEnabledFunc(&logInfo);
    }
    return FitLogGetEnableLogLevel() >= logInfo.logLevel;
}
}  // namespace

void FitLogSetCallback(FitLogCallbackInfo cb)
{
    GetLogCallback() = cb;
}

int32_t FitLog(int32_t modId,
    int logLevel,
    const char *fileName,
    int fLine,
    const char *funcName,
    long logId,
    const char *format,
    ...)
{
    FitLogInfo logInfo {modId, logLevel, fileName, fLine, funcName, logId, nullptr};
    if (!IsEnabledLog(logInfo)) {
        return FIT_ERR_SKIP;
    }

    char buf[LOG_BUFFER_MAX] = {0};
    va_list args;
    va_start(args, format);
    auto msgContentLen = vsnprintf_s(buf, sizeof(buf), sizeof(buf) - 1, format, args);
    va_end(args);

    if (msgContentLen <= 0) {
        return FIT_ERR_FAIL;
    }

    logInfo.message = buf;
    if (GetLogCallback().printFunc != nullptr) {
        return GetLogCallback().printFunc(&logInfo);
    }

    return FitLogInner(&logInfo);
}
