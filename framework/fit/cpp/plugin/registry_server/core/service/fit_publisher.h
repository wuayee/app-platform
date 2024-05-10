/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/10/13 15:01
 * Notes:       :
 */

#ifndef FIT_PUBLISHER_H
#define FIT_PUBLISHER_H

#include <functional>
#include <fit/stl/vector.hpp>

namespace Fit {
namespace Registry {
template<typename __DATA_TYPE>
class fit_data_publisher {
public:
    using notify_function_t = std::function<void(const __DATA_TYPE &data)>;
    void subscribe(notify_function_t notify_func)
    {
        subscribers_.push_back(notify_func);
    }

    void notify(const __DATA_TYPE &data)
    {
        for (auto &item : subscribers_) {
            item(data);
        }
    }

    void notify(const Fit::vector<__DATA_TYPE> &data_set)
    {
        for (auto &item : subscribers_) {
            item(data_set);
        }
    }

private:
    Fit::vector<notify_function_t> subscribers_;
};
}
}

#endif // FIT_PUBLISHER_H
