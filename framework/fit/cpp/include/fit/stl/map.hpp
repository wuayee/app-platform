/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : wangpanbo
 * Date         : 2021/4/14
 * Notes:       :
 */

#ifndef FIT_MAP_HPP
#define FIT_MAP_HPP

#include <map>
#include <fit/stl/stl_allocator.hpp>
#include "except.hpp"
#include "pair.hpp"

namespace Fit {
template<
    class Key,
    class T,
    class Compare = std::less<Key>,
    class Allocator = Fit::stl_allocator<Fit::pair<const Key, T>>>
using map = std::map<Key, T, Compare, Allocator>;

template<
    class Key,
    class T,
    class Compare = std::less<Key>,
    class Allocator = Fit::stl_allocator<Fit::pair<const Key, T>>>
using multimap = std::multimap<Key, T, Compare, Allocator>;
}
#endif // FITMAP_HPP
