/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide uuid
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef FIT_UUID_H
#define FIT_UUID_H
#include <unistd.h>
#include <thread>
#include <sstream>
#include <iomanip>
#include <type_traits>
#include <sys/types.h>
#include <fit/stl/string.hpp>
#include <fit/internal/util/fit_random.h>
#include <fit/internal/fit_time_utils.h>
namespace Fit {
static Fit::string GenerateUuid() {
    // 获取主机名作为机器ID
    char hostName[256];
    gethostname(hostName, sizeof(hostName));

    // 获取当前时间戳
    uint64_t millis = Fit::TimeUtil::GetCurrentLocalTimestampMs();

    // 生成随机数
    int r = Fit::FitRandom<unsigned long long>();

    // 获取进程ID和线程ID
    pid_t pid = getpid();
    std::thread::id threadId = std::this_thread::get_id();
    std::stringstream tidStream;
    tidStream << threadId;
    std::string tidStr = tidStream.str();

    // 构造UUID
    std::stringstream ss;
    ss << std::hex << std::setfill('0');

    // 主机名部分（取前8个字符的ASCII值）
    for (int i = 0; i < 8 && hostName[i] != '\0'; ++i) {
        ss << std::setw(2) << static_cast<int>(hostName[i]);
    }

    // 时间戳部分
    ss << std::setw(12) << millis;
    // 进程ID部分
    ss << std::setw(8) << pid;
    // 线程ID部分
    ss << std::setw(16) << std::stoull(tidStr);
    // 随机数部分
    ss << std::setw(16) << r;

    return Fit::string(ss.str());
}
}
#endif // FIT_UUID_H