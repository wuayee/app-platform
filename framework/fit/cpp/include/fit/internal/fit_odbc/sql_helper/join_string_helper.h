/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : include
 * Author       : s00558940
 * Create       : 2020/9/23 17:33
 */

#ifndef JOIN_STRING_HELPER_H
#define JOIN_STRING_HELPER_H

#include <string>
#include <sstream>


class join_string_helper {
public:
    template <typename __STL_CONTAINER_TYPE>
    static Fit::string join(const __STL_CONTAINER_TYPE &items, const Fit::string &join_symbol)
    {
        std::ostringstream result;

        auto iter = items.begin();
        if (iter != items.end()) {
            result << *iter;
            ++iter;
        }

        for (; iter != items.end(); ++iter) {
            result << join_symbol << *iter;
        }

        return Fit::to_fit_string(result.str());
    }
};
#endif // JOIN_STRING_HELPER_H
