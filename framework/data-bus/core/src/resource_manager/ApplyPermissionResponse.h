/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: Response for HandleApplyPermission
 */

#ifndef DATABUS_APPLY_PERMISSION_RESPONSE_H
#define DATABUS_APPLY_PERMISSION_RESPONSE_H

#include "fbs/common_generated.h"
#include "ApplyPermissionMemoryInfo.h"
#include "UserData.h"

namespace DataBus {
namespace Resource {

// 权限申请回复
struct ApplyPermissionResponse {
    // 禁用默认构造器和运算符重载
    ApplyPermissionResponse() = delete;
    ApplyPermissionResponse(const ApplyPermissionResponse&) = default;
    ApplyPermissionResponse& operator=(const ApplyPermissionResponse&) = delete;

    ApplyPermissionResponse(bool granted, uint32_t seq, const ApplyPermissionMemoryInfo& memoryInfo,
                            Common::ErrorType errorType)
        : granted(granted), seq(seq), applicant(memoryInfo.applicant), sharedMemoryId(memoryInfo.sharedMemoryId),
        memorySize(memoryInfo.memorySize), userData(memoryInfo.userData), errorType(errorType) {}

    bool granted; // 授权结果
    uint32_t seq; // 申请权限的请求序列号
    int32_t applicant; // 申请权限的客户端
    int32_t sharedMemoryId; // 共享内存ID
    uint64_t memorySize; // 共享内存大小
    std::shared_ptr<UserData> userData; // 用户自定义元数据
    Common::ErrorType errorType; // 错误码
};

} // namespace Resource
} // namespace DataBus

#endif // DATABUS_APPLY_PERMISSION_RESPONSE_H
