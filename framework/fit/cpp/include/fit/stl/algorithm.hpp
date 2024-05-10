/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : algorithm
 * Author       : songyongtan
 * Create       : 2023-09-21
 * Notes:       :
 */

#ifndef FIT_STL_ALGORITHM_HPP
#define FIT_STL_ALGORITHM_HPP

namespace Fit {
template<typename Map, typename K, typename V>
auto find_or(const Map& c, K key, V or_value) -> typename Map::mapped_type
{
    auto iter = c.find(key);
    if (iter == c.end()) {
        return or_value;
    }
    return iter->second;
}
}

#endif