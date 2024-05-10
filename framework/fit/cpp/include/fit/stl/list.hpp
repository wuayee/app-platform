/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/13 9:31
 */
#ifndef FIT_LIST_HPP
#define FIT_LIST_HPP

#include <list>
#include <fit/stl/stl_allocator.hpp>

namespace Fit {
template<
    class T,
    class Allocator = Fit::stl_allocator<T>>
using list = std::list<T, Allocator>;
}

#endif // FIT_LIST_HPP
