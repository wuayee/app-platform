/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description: Response for HandleApplyPermission
 */

#ifndef DATABUS_APPLY_PERMISSION_RESPONSE_H
#define DATABUS_APPLY_PERMISSION_RESPONSE_H

#include "fbs/common_generated.h"

namespace DataBus {
namespace Resource {

// 权限申请回复
struct ApplyPermissionResponse {
    // 禁用默认构造器和运算符重载
    ApplyPermissionResponse() = delete;
    ApplyPermissionResponse(const ApplyPermissionResponse&) = default;
    ApplyPermissionResponse& operator=(const ApplyPermissionResponse&) = delete;

    ApplyPermissionResponse(bool granted, int32_t applicant, int32_t sharedMemoryId, uint64_t memorySize,
                            Common::ErrorType errorType) : granted_(granted), applicant_(applicant),
                            sharedMemoryId_(sharedMemoryId), memorySize_(memorySize), errorType_(errorType) {}

    bool granted_; // 授权结果
    int32_t applicant_; // 申请权限的客户端
    int32_t sharedMemoryId_; // 共享内存ID
    uint64_t memorySize_; // 共享内存大小
    Common::ErrorType errorType_; // 错误码
};

} // namespace Resource
} // namespace DataBus

#endif // DATABUS_APPLY_PERMISSION_RESPONSE_H
