/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 时间处理
 * Author       : s00558940
 * Create       : 2020/7/28 16:36
 * Notes:       :
 */
#ifndef FIT_CPP_FIT_TIME_UTILS_H
#define FIT_CPP_FIT_TIME_UTILS_H

#include <chrono>
#include <ostream>
#include <sys/time.h>
#include <fit/stl/string.hpp>
#include "time.h"


namespace Fit {
namespace TimeUtil {
const uint64_t BASE_TIME_MS = 1000;

// eg. 2000-01-01 01:00:01
class normal_local_time {
};

// eg. 2000-01-01 01:00:01
class normal_utc_time {
};

template<typename T>
Fit::string to_string(const time_t t);

template<>
inline Fit::string to_string<normal_local_time>(const time_t t)
{
    char buffer[32] = {};

    struct tm convert_tm {};
    localtime_r(&t, &convert_tm);
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", &convert_tm);

    return buffer;
}

template<>
inline Fit::string to_string<normal_utc_time>(const time_t t)
{
    char buffer[32] = {};

    struct tm convert_tm {};
    gmtime_r(&t, &convert_tm);
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", &convert_tm);

    return buffer;
}

static uint64_t GetCurrentLocalTimestampMs()
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::system_clock::now().time_since_epoch()).count();
}

static Fit::string TimestampToStringMs(uint64_t timeStamp)
{
    auto msTime = std::chrono::milliseconds(timeStamp);
    uint64_t millionSecond = timeStamp % BASE_TIME_MS;

    auto curTimePoint = std::chrono::time_point<std::chrono::system_clock, std::chrono::milliseconds>(msTime);
    auto curTime = std::chrono::system_clock::to_time_t(curTimePoint);
    auto timeStampString = to_string<normal_local_time>(curTime) + "," + Fit::to_string(millionSecond);
    return timeStampString;
}
}
}
#endif // FIT_CPP_FIT_TIME_UTILS_H
