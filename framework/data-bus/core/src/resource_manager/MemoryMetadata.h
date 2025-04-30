/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: Shared memory metadata.
 */

#ifndef DATABUS_MEMORY_METADATA_H
#define DATABUS_MEMORY_METADATA_H

#include <cstdint>

#include "UserData.h"

namespace DataBus {
namespace Resource {

/**
 * 共享内存元数据。
 */
struct MemoryMetadata {
    MemoryMetadata(int32_t sharedMemoryId, uint64_t memorySize, const std::shared_ptr<UserData>& userData)
        : sharedMemoryId(sharedMemoryId), memorySize(memorySize), userData(userData) {};

    int32_t sharedMemoryId; // 共享内存块ID。
    uint64_t memorySize; // 内存块大小。
    std::shared_ptr<UserData> userData; // 用户自定义元数据。
};
} // namespace Resource
} // namespace DataBus

#endif // DATABUS_MEMORY_METADATA_H
