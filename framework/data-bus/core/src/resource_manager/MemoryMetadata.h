/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
 *
 * @author l00862071
 * @since 2024-06-19
 */
struct MemoryMetadata {
    MemoryMetadata(int32_t sharedMemoryId, uint64_t memorySize, const std::shared_ptr<UserData>& userData)
        : sharedMemoryId_(sharedMemoryId), memorySize_(memorySize), userData_(userData) {};

    int32_t sharedMemoryId_; // 共享内存块ID。
    uint64_t memorySize_; // 内存块大小。
    std::shared_ptr<UserData> userData_; // 用户自定义元数据。
};
} // namespace Resource
} // namespace DataBus

#endif // DATABUS_MEMORY_METADATA_H
