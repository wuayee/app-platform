/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/29 19:53
 */
#ifndef FIT_SET_HPP
#define FIT_SET_HPP

#include <set>
#include <fit/stl/stl_allocator.hpp>
namespace Fit {
template<
    class Key,
    class Compare = std::less<Key>,
    class Allocator = Fit::stl_allocator<Key>>
using set = std::set<Key, Compare, Allocator>;

template <typename Key, typename Compare = std::less<Key>,
    typename Alloc = Fit::stl_allocator<Key> >
using multiset = std::multiset<Key, Compare, Alloc>;
}

#endif // FIT_SET_HPP
