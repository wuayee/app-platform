/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/15 15:49
 */
#ifndef ALLOCATOR_HPP
#define ALLOCATOR_HPP

#include <cstddef>
#include <memory>
#include <functional>

namespace Fit {
namespace Context {
class Allocator {
public:
    virtual ~Allocator() = default;

    virtual void *Malloc(size_t size) = 0;
    virtual void Free(void *obj) = 0;
};

using AllocFunctor = std::function<void* (size_t)>;
using FreeFunctor = std::function<void(void *obj)>;
using AllocatorPtr = std::unique_ptr<Allocator>;
}
}

#endif // ALLOCATOR_HPP
