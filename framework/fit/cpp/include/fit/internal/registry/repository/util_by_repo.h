/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/25
 */
#ifndef UTIL_BY_REPO_HPP
#define UTIL_BY_REPO_HPP
#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
namespace Fit {
class UtilByRepo {
public:
    virtual FitCode GetCurrentTimeMs(uint64_t& result) = 0;
    static UtilByRepo& Instance();
};
}
#endif