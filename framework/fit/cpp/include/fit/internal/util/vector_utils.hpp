/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides utility methods for vectors.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/27
 */

#ifndef FIT_UTIL_VECTOR_UTILS_HPP
#define FIT_UTIL_VECTOR_UTILS_HPP

#include <fit/stl/vector.hpp>

#include <cstdint>
#include <functional>

namespace Fit {
namespace Util {
/**
 * 为容器提供工具方法。
 */
class VectorUtils {
public:
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    VectorUtils() = delete;

    /**
     * 通过二分查找算法在容器中查找符合条件的元素。
     * <p>通过 compare 方法对容器中的元素进行对比：
     * <ul>
     * <li>若对比结果是一个负数，则待查找的元素在当前元素的左侧</li>
     * <li>若对别结果是一个正数，则待查找的元素在当前元素的右侧</li>
     * <li>否则当前元素即为待查找元素。</li>
     * </ul></p>
     *
     * @tparam E 表示容器中元素的类型。
     * @param items 表示待查找元素的容器的引用。
     * @param compare 表示用以比较容器中元素的方法。
     * @return 若存在符合条件的元素，则为元素所在位置的索引；否则为 -1 减去插入元素时应在位置的索引。
     */
    template<class E>
    static int32_t BinarySearch(const Fit::vector<E>& items, std::function<int32_t(const E&)> compare)
    {
        int32_t left = 0;
        int32_t right = (int32_t)items.size() - 1;
        while (left <= right) {
            int32_t mid = (left + right) / 2;
            int32_t ret = compare(items[mid]);
            if (ret < 0) {
                left = mid + 1;
            } else if (ret > 0) {
                right = mid - 1;
            } else {
                return mid;
            }
        }
        return -1 - left;
    }

    /**
     * 将指定元素插入到容器的指定索引处。
     *
     * @tparam E 表示容器中元素的类型。
     * @param items 表示待插入元素的容器的引用。
     * @param index 表示待将元素插入到容器的索引处的32位整数。
     * @param item 表示待插入到容器中的元素。
     */
    template<class E>
    static void Insert(Fit::vector<E>& items, int32_t index, E item)
    {
        auto iter = items.begin();
        iter += index;
        items.insert(iter, std::move(item));
    }

    /**
     * 将容器中指定索引处的元素从容器中移除。
     *
     * @tparam E 表示容器中元素的类型。
     * @param items 表示待移除元素的容器的引用。
     * @param index 表示待移除的元素在容器中的索引的32位整数。
     * @return 表示移除的元素。
     */
    template<class E>
    static E Remove(Fit::vector<E>& items, int32_t index)
    {
        auto iter = items.begin();
        iter += index;
        auto temp = *iter;
        items.erase(iter);
        return temp;
    }

    template<class T, class R>
    static ::Fit::vector<R> Map(::Fit::vector<T> source, const std::function<R(const T&)>& mapper)
    {
        ::Fit::vector<R> target {};
        target.reserve(source.size());
        for (size_t i = 0; i < source.size(); i++) {
            target.push_back(mapper(source[i]));
        }
        return target;
    }
};
}
}
#endif // FIT_UTIL_VECTOR_UTILS_HPP
