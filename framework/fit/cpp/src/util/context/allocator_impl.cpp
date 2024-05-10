/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 15:57
 */

#include "allocator_impl.hpp"
#include <cstring>
#include <fit/memory/base_allocator.hpp>

namespace Fit {
namespace Context {
void *AllocatorDefault::Malloc(size_t size)
{
    return BaseAllocator::Malloc(size, __FUNCTION__);
}

void AllocatorDefault::Free(void *obj)
{
    BaseAllocator::Free(obj, __FUNCTION__);
}

AllocatorPtr AllocatorDefault::CreateDefaultAllocator()
{
    return AllocatorPtr(new AllocatorDefault);
}

AllocatorImpl::AllocatorImpl(AllocFunctor alloc, FreeFunctor free)
    : alloc_(std::move(alloc)), free_(std::move(free)) {}

void *AllocatorImpl::Malloc(size_t size)
{
    return alloc_(size);
}

void AllocatorImpl::Free(void *obj)
{
    free_(obj);
}

AllocatorPtr AllocatorImpl::CreateAllocatorImpl(
    AllocFunctor alloc, FreeFunctor free)
{
    return AllocatorPtr(new AllocatorImpl(std::move(alloc), std::move(free)));
}
}
}