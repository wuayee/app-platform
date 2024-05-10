/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : wangpanbo
 * Date         : 2022/6/29
 * Notes:       :
 */

#ifndef BASE_ALLOCATOR_HPP
#define BASE_ALLOCATOR_HPP

#include <cstdlib>
class BaseAllocator {
public:
    static void* Malloc(size_t count, const char* funcName);
    static void Free(void* ptr, const char* funcName);
};
#endif // BASE_ALLOCATOR_HPP