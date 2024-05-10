/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: Zhongbin Yu 00286766
 * Date: 2020-04-01 11:02:39
 */

#ifndef FIT_SCOPE_H
#define FIT_SCOPE_H

#include <functional>

namespace Fit {
class scope_guard {
public:
    explicit scope_guard(std::function<void()> on_exit)
        : on_exit_(std::move(on_exit)), dismissed_(false) {}

    ~scope_guard()
    {
        if (!dismissed_) {
            on_exit_();
        }
    }

    void dismiss()
    {
        dismissed_ = true;
    }

private:
    std::function<void()> on_exit_;
    bool dismissed_;
};

#define FIT_FILELINE_NAME_CAT(name, line) name##line

#define FIT_FILELINE_NAME(name, line) FIT_FILELINE_NAME_CAT(name, line)

#define FIT_ON_SCOPE_EXIT(callback) \
    Fit::scope_guard FIT_FILELINE_NAME(__on_exit, __LINE__)(callback)
}

#endif

