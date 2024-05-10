/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2022/06/30
 */
#ifndef FIT_DEQUE_HPP
#define FIT_DEQUE_HPP

#include <deque>
#include <fit/stl/stl_allocator.hpp>

namespace Fit {
template<
    class T,
    class Allocator = Fit::stl_allocator<T>>
using deque = std::deque<T, Allocator>;
}

#endif