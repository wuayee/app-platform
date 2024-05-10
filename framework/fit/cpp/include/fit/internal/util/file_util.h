/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide file util
 * Author       : w00561424
 * Date:        : 2023/09/27
 */
#ifndef FILE_UTIL_H
#define FILE_UTIL_H
#include <iostream>
#include <fit_log.h>
#include <fit/stl/string.hpp>
namespace Fit {
constexpr int32_t BUFFER_LEN = 1024;
static Fit::string GetOutputFromExec(FILE* pipe)
{
    if (pipe == nullptr) {
        FIT_LOG_ERROR("Failed to execute command.");
        return "";
    }
    char buffer[BUFFER_LEN] = {0};
    Fit::string result;
    while (fgets(buffer, BUFFER_LEN, pipe) != nullptr) {
        result += buffer;
    }
    if (result.back() == '\n') {
        result.pop_back();
    }
    pclose(pipe);
    return result;
}
}
#endif