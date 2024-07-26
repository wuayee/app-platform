/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description: Request for HandleApplyPermission
 */

#ifndef DATABUS_APPLY_PERMISSION_REQUEST_H
#define DATABUS_APPLY_PERMISSION_REQUEST_H

#include "fbs/common_generated.h"
#include "UserData.h"
#include "UserDataRequest.h"

namespace DataBus {
namespace Resource {

// 权限申请请求
struct ApplyPermissionRequest {
    // 禁用默认构造器和运算符重载
    ApplyPermissionRequest() = delete;
    ApplyPermissionRequest(const ApplyPermissionRequest&) = delete;
    ApplyPermissionRequest& operator=(const ApplyPermissionRequest&) = delete;

    ApplyPermissionRequest(int32_t socketFd, uint32_t seq, DataBus::Common::PermissionType permissionType,
                           int32_t sharedMemoryId, const UserDataRequest& userDataRequest)
        : socketFd(socketFd), seq(seq), permissionType(permissionType), sharedMemoryId(sharedMemoryId),
        isOperatingUserData(userDataRequest.isOperatingUserData), userData(userDataRequest.userData) {};
    int32_t socketFd; // 申请权限的客户端
    uint32_t seq; // 申请权限的请求序列号
    DataBus::Common::PermissionType permissionType; // 权限种类
    int32_t sharedMemoryId; // 共享内存ID
    bool isOperatingUserData; // 是否操作用户自定义数据
    std::shared_ptr<UserData> userData; // 用户自定义元数据
};
} // namespace Resource
} // namespace DataBus

#endif // DATABUS_APPLY_PERMISSION_REQUEST_H
