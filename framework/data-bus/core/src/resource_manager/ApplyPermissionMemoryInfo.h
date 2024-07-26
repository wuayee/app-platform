/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description: Memory information for ApplyPermissionResponse
 */

#ifndef DATABUS_APPLY_PERMISSION_MEMORY_INFO_H
#define DATABUS_APPLY_PERMISSION_MEMORY_INFO_H

#include <cstdint>

#include "UserData.h"

namespace DataBus {
namespace Resource {

// 权限申请回复内存块信息
struct ApplyPermissionMemoryInfo {
    ApplyPermissionMemoryInfo(int32_t applicant, int32_t sharedMemoryId, uint64_t memorySize,
                              const std::shared_ptr<UserData>& userData)
        : applicant(applicant), sharedMemoryId(sharedMemoryId), memorySize(memorySize), userData(userData) {}
    int32_t applicant; // 申请权限的客户端
    int32_t sharedMemoryId; // 共享内存ID
    uint64_t memorySize; // 共享内存大小
    std::shared_ptr<UserData> userData; // 用户自定义元数据
};

} // namespace Resource
} // namespace DataBus

#endif // DATABUS_APPLY_PERMISSION_MEMORY_INFO_H
