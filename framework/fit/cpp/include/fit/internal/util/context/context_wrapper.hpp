/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 15:02
 */
#ifndef CONTEXTWRAPPER_HPP
#define CONTEXTWRAPPER_HPP

#include <cstdint>
#include <fit/external/util/context/context_base.h>
#include "allocator.hpp"
#include "context.hpp"

namespace Fit {
namespace Context {
constexpr uint16_t CONTEXT_MAGIC = 0xC0DE;
struct ContextWrapper {
public:
    explicit ContextWrapper(AllocatorPtr allocator);
    bool IsValidContext() const noexcept;

    uint16_t magic {CONTEXT_MAGIC};
    Context context {nullptr};
};

ContextWrapper *ContextWrapperCast(ContextObj ctx);
}
}

#endif // CONTEXTWRAPPER_HPP
