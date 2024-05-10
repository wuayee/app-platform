/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/13
 * Notes:       :
 */

#ifndef FIT_VECTOR_HPP
#define FIT_VECTOR_HPP

#include <vector>
#include <fit/stl/stl_allocator.hpp>
#include "except.hpp"

namespace Fit {
template<
    class T,
    class Allocator = Fit::stl_allocator<T>>
using vector = std::vector<T, Allocator>;
}

#endif // FITVECTOR_HPP
