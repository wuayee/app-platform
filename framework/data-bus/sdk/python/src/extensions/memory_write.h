/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: DataBus Python SDK的`databus.memory`模块的内存拷贝实现
 * Create Date: 2024-05-20
 */
#ifndef MEMORY_WRITE_H
#define MEMORY_WRITE_H

#ifdef __cplusplus
#include <cstdint>
#else
#include <stdint.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

void WriteToSharedBuffer(const uint8_t* src, uintmax_t srcLength, uint8_t* sharedBuffer);

#ifdef __cplusplus
}
#endif

#endif  // MEMORY_WRITE_H
