/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2022-07-18
 */

#ifndef STL_ALLOCATOR_HPP
#define STL_ALLOCATOR_HPP

#include <cstddef>
#include <utility>
#include <type_traits>

#include <iostream>
#include <memory>
#include <cstring>
#include <fit/memory/base_allocator.hpp>

namespace Fit {
#ifndef USE_CUSTOM_MEMORY_STRUCTURE
template<typename T>
using stl_allocator = std::allocator<T>;
#else
template<typename T>
class stl_allocator {
public:
    using size_type = size_t;
    using difference_type = ptrdiff_t;
    using pointer = T *;
    using const_pointer = const T *;
    using reference = T &;
    using const_reference = const T &;
    using value_type = T;

    template<typename T1>
    struct rebind { using other = stl_allocator<T1>; };

    // 2103. propagate_on_container_move_assignment
    using propagate_on_container_move_assignment = std::true_type;

    stl_allocator() noexcept {}

    stl_allocator(const stl_allocator &) noexcept {}

    template<typename T1>
    stl_allocator(const stl_allocator<T1> &) noexcept {}

    ~stl_allocator() noexcept {}

    pointer address(reference val) const noexcept
    {
        return std::__addressof(val);
    }

    const_pointer address(const_reference val) const noexcept
    {
        return std::__addressof(val);
    }

    // count允许为0
    pointer allocate(size_type count, const void * = nullptr)
    {
        return (pointer)BaseAllocator::Malloc((count * sizeof(T)), __FUNCTION__);
    }

    // ptr不能是空指针
    void deallocate(pointer ptr, size_type)
    {
        BaseAllocator::Free(ptr, __FUNCTION__);
    }

    size_type max_size() const noexcept
    {
        return size_t(-1) / sizeof(T);
    }

    template<typename P, typename... Args>
    void construct(P *ptr, Args &&... args)
    {
        ::new((void *)ptr) P(std::forward<Args>(args)...);
    }

    template<typename P>
    void destroy(P *ptr)
    {
        ptr->~P();
    }

    void *operator new(uint64_t size)
    {
        return BaseAllocator::Malloc(size, __FUNCTION__);
    }

    void operator delete(void *object)
    {
        BaseAllocator::Free(object, __FUNCTION__);
    }
};

template<typename T>
inline bool operator==(const stl_allocator<T> &,
    const stl_allocator<T> &)
{
    return true;
}

template<typename T>
inline bool operator!=(const stl_allocator<T> &,
    const stl_allocator<T> &)
{
    return false;
}
#endif
}
#endif
