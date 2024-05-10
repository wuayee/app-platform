/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/24 15:06
 * Notes:       :
 */

#ifndef FIT_RANGE_UTILS_H
#define FIT_RANGE_UTILS_H

#include <string>

namespace Fit {
template<typename T>
class range_skip {
public:
    using iter_type = typename std::remove_const<
        typename std::remove_reference<decltype(std::begin(std::declval<T>()))>::type>::type;
    range_skip(T &data, uint32_t skip_num)
        : data_(data), skip_num_(skip_num) {}

    range_skip(range_skip<T> &other, uint32_t skip_num)
        : data_(other.data_), skip_num_(skip_num + other.skip_num_) {}

    range_skip(const range_skip<T> &other, uint32_t skip_num)
        : data_(other.data_), skip_num_(skip_num + other.skip_num_) {}

    ~range_skip() = default;

    iter_type begin() const
    {
        auto begin_iter = std::begin(data_);
        for (uint32_t i = 0; i < skip_num_ && begin_iter != std::end(data_); ++i) {
            begin_iter++;
        }

        return begin_iter;
    }
    iter_type end() const
    {
        return std::end(data_);
    }

    size_t size() const
    {
        if (data_.size() <= skip_num_) {
            return 0;
        }

        return data_.size() - skip_num_;
    }

private:
    T &data_;
    uint32_t skip_num_ {};
};
}
#endif // FIT_RANGE_UTILS_H
