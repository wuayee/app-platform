/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description:
 * Author: l00558918
 * Date: 2021-02-27
 */

#ifndef FIT_UNORDERED_MAP_H
#define FIT_UNORDERED_MAP_H

#include <unordered_map>
#include <utility>
#include <functional>
#include "stl_allocator.hpp"
#include "pair.hpp"

namespace Fit {
template<
    class Key,
    class T,
    class Hash = std::hash<Key>,
    class KeyEqual = std::equal_to<Key>,
    class Allocator = Fit::stl_allocator<Fit::pair<const Key, T>>>
using unordered_map = std::unordered_map<Key, T, Hash, KeyEqual, Allocator>;

template<class Key, class T, class Hash = std::hash<Key>, class KeyEqual = std::equal_to<Key>,
    class Allocator = Fit::stl_allocator<Fit::pair<const Key, T>>>
using unordered_multimap = std::unordered_multimap<Key, T, Hash, KeyEqual, Allocator>;
}
#endif
