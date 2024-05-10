/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/30 19:42
 */
#ifndef FIT_STACK_HPP
#define FIT_STACK_HPP

#include <stack>
#include "deque.hpp"
namespace Fit {
template<
    class T,
    class Container = Fit::deque<T>>
using stack = std::stack<T, Container>;
}

#endif // FIT_STACK_HPP
