/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: DataBus Python SDK的`databus.memory`模块的内存拷贝实现
 * Create Date: 2024-05-20
 */
#include "memory_write.h"

#include <algorithm>

extern "C" {
void WriteToSharedBuffer(uint8_t* src, uintmax_t srcLength, uint8_t* sharedBuffer)
{
    (void) std::copy(src, src + srcLength, sharedBuffer);
}
}