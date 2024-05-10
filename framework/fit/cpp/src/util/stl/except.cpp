/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/3/18
 * Notes:       :
 */

#include <fit/stl/except.hpp>
#include <stdexcept>
#include <iostream>

namespace Fit {
void ThrowInvalidArgument(const char* msg)
{
#ifdef __cpp_exceptions
    throw std::invalid_argument(msg);
#else
    std::cerr << "Exception. [msg=" << msg << "]" << std::endl;
#endif
}
void ThrowOutOfRange(const char* msg)
{
#ifdef __cpp_exceptions
    throw std::out_of_range(msg);
#else
    std::cerr << "Exception. [msg=" << msg << "]" << std::endl;
#endif
}
} // LCOV_EXCL_LINE