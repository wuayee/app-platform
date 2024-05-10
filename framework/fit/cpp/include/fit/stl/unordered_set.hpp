/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2021-09-09
 */

#ifndef FIT_UNORDERED_SET_H
#define FIT_UNORDERED_SET_H

#include <unordered_set>
#include <utility>
#include <functional>
#include <fit/stl/stl_allocator.hpp>
namespace Fit {
template<
    class Key,
    class Hash = std::hash<Key>,
    class KeyEqual = std::equal_to<Key>,
    class Allocator = Fit::stl_allocator<Key>>
using unordered_set = std::unordered_set<Key, Hash, KeyEqual, Allocator>;
}
#endif
