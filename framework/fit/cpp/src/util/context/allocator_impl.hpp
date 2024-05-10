/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 15:52
 */
#ifndef ALLOCATOR_IMPL_HPP
#define ALLOCATOR_IMPL_HPP

#include <cstddef>
#include <fit/internal/util/context/allocator.hpp>

namespace Fit {
namespace Context {
class AllocatorDefault : public Allocator {
public:
    void *Malloc(size_t size) override;
    void Free(void *obj) override;

    static AllocatorPtr CreateDefaultAllocator();
};

class AllocatorImpl : public Allocator {
public:
    AllocatorImpl(AllocFunctor alloc, FreeFunctor free);
    ~AllocatorImpl() override = default;

    void *Malloc(size_t size) override;
    void Free(void *obj) override;

    static AllocatorPtr CreateAllocatorImpl(
        AllocFunctor alloc, FreeFunctor free);
private:
    AllocFunctor alloc_;
    FreeFunctor free_;
};
}
}

#endif // ALLOCATOR_IMPL_HPP
