/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/10
 * Notes:       :
 */

#ifndef TASK_STREAM_HPP
#define TASK_STREAM_HPP

#include <fit/stl/list.hpp>
#include <functional>

namespace Fit {
class TaskStream {
public:
    using Wrapper = std::function<bool()>;

    template<typename F, typename... Args>
    TaskStream &Then(F &&f, typename std::result_of<F(Args...)>::type successResult, Args &&... args)
    {
        auto executor = std::bind(std::forward<F>(f), std::forward<Args>(args)...);
        executors_.push_back([executor, successResult]() -> bool { return executor() == successResult; });

        return *this;
    }

    template<typename F, typename... Args>
    auto Then(F &&f, Args &&... args) -> typename std::enable_if<std::is_void<typename std::result_of<F(
        Args...)>::type>::value, TaskStream &>::type
    {
        auto executor = std::bind(std::forward<F>(f), std::forward<Args>(args)...);
        executors_.push_back([executor]() -> bool {
            executor();
            return true;
        });

        return *this;
    }

    bool Run()
    {
        for (auto &executor : executors_) {
            if (!executor()) {
                return false;
            }
        }

        return true;
    }

private:
    Fit::list<Wrapper> executors_;
};
}
#endif // TASK_STREAM_HPP
