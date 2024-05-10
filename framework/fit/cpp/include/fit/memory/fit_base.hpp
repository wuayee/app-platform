/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : wangpanbo
 * Date         : 2022/6/30
 * Notes:       :
 */

#ifndef FIT_BASE_HPP
#define FIT_BASE_HPP
#include <new>
#include <fit/stl/stl_allocator.hpp>
class FitBase {
public:
    void* operator new(size_t count) // 默认抛异常
    {
        return BaseAllocator::Malloc(count, __FUNCTION__);
    }
    void operator delete(void *object)
    {
        BaseAllocator::Free(object, __FUNCTION__);
    }
    void *operator new[](size_t count)
    {
        return BaseAllocator::Malloc(count, __FUNCTION__);
    }
    void operator delete[](void *object)
    {
        BaseAllocator::Free(object, __FUNCTION__);
    }
    void *operator new(size_t count, const std::nothrow_t& tag) // 需要传入nothrow_t
    {
        return BaseAllocator::Malloc(count, __FUNCTION__);
    }
    void operator delete(void* object, const std::nothrow_t& tag) noexcept
    {
        BaseAllocator::Free(object, __FUNCTION__);
    }
    void *operator new[](size_t count, const std::nothrow_t& tag)
    {
        return BaseAllocator::Malloc(count, __FUNCTION__);
    }
    void operator delete[](void* object, const std::nothrow_t& tag) noexcept
    {
        BaseAllocator::Free(object, __FUNCTION__);
    }

    inline void* operator new(std::size_t, void* object) noexcept
    {
        return object;
    }
    inline void* operator new[](std::size_t, void* object) noexcept
    {
        return object;
    }

    inline void operator delete(void*, void*) noexcept
    {
    }
    inline void operator delete[](void*, void*) noexcept
    {
    }
};
#endif // FIT_BASE_HPP