/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#ifndef FIT_THREAD_H
#define FIT_THREAD_H

#include <functional>
#include <memory>

namespace Fit {
namespace Thread {
class fit_thread {
public:
    explicit fit_thread(std::function<void()> func);

    template<typename Func, typename... Args>
    explicit fit_thread(Func&& func, Args&&... args)
        : fit_thread((std::function<void()>)std::bind(std::forward<Func>(func), std::forward<Args>(args)...)) {}

    bool joinable();

    void join();

    void detach();

    ~fit_thread();

    fit_thread(fit_thread &&) noexcept;
    fit_thread(const fit_thread&) = delete;

    fit_thread& operator=(fit_thread &&) noexcept;
    fit_thread& operator=(const fit_thread&) = delete;
    void set_name(const char* name);

private:
    class thread_impl;
    std::unique_ptr<thread_impl> impl;
};
}  // namespace thread
}  // namespace Fit

#endif