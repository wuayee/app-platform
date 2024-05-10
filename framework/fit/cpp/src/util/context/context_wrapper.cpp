/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 15:06
 */

#include <fit/internal/util/context/context_wrapper.hpp>

namespace Fit {
namespace Context {
ContextWrapper::ContextWrapper(AllocatorPtr allocator)
    : context(std::move(allocator)) {}

bool ContextWrapper::IsValidContext() const noexcept
{
    return magic == CONTEXT_MAGIC;
}

ContextWrapper *ContextWrapperCast(ContextObj ctx)
{
    if (ctx == nullptr) {
        return nullptr;
    }

    auto wrapper = static_cast<ContextWrapper *>(ctx);
    if (!wrapper->IsValidContext()) {
        return nullptr;
    }

    return wrapper;
}
}
}