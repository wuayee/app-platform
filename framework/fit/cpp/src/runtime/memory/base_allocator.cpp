/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 内存分配
 * Author       : w00561424
 * Date         : 2022/7/15
 * Notes:       :
 */
#include <fit/memory/base_allocator.hpp>

void* BaseAllocator::Malloc(size_t count, const char* funcName)
{
    return malloc(count);
}
void BaseAllocator::Free(void* ptr, const char* funcName)
{
    free(ptr);
}