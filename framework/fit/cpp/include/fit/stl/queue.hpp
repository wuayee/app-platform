/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/13 9:31
 */
#ifndef FIT_QUEUE_H
#define FIT_QUEUE_H

#include <queue>
#include "deque.hpp"
#include "vector.hpp"

namespace Fit {
template<
    class T,
    class Container = Fit::deque<T>>
using queue = std::queue<T, Container>;

template<
    class T,
    class Container = Fit::vector<T>,
    class Compare = std::less<typename Container::value_type>>
using priority_queue =
std::priority_queue<T, Container, Compare>;
}

#endif // FIT_QUEUE_H
