/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#include <fit/internal/util/thread/fit_thread.h>

#include <fit/stl/memory.hpp>
#include <thread>

namespace Fit {
namespace Thread {
class fit_thread::thread_impl {
public:
    explicit thread_impl(std::function<void()> func)
        : thread_(std::move(func)) {}
    ~thread_impl() = default;
    bool joinable()
    {
        return thread_.joinable();
    }

    void join()
    {
        if (thread_.joinable()) {
            thread_.join();
        }
    }

    void detach()
    {
        thread_.detach();
    }

    void set_name(const char* name)
    {
#ifdef __linux__
        if (thread_.joinable()) {
            pthread_setname_np(thread_.native_handle(), name);
        }
#endif
    }

private:
    std::thread thread_;
};

fit_thread::fit_thread(std::function<void()> func)
    : impl(make_unique<thread_impl>(std::move(func))) {}

fit_thread::fit_thread(fit_thread &&) noexcept = default;
fit_thread &fit_thread::operator=(fit_thread &&) noexcept = default;

fit_thread::~fit_thread() = default;

void fit_thread::join()
{
    impl->join();
}

bool fit_thread::joinable()
{
    return impl->joinable();
}

void fit_thread::detach()
{
    impl->detach();
}

void fit_thread::set_name(const char* name)
{
    impl->set_name(name);
}
}
}