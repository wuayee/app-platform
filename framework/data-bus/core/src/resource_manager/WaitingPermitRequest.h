/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/

#ifndef DATABUS_WAITING_PERMIT_REQUEST_H
#define DATABUS_WAITING_PERMIT_REQUEST_H

#include "fbs/common_generated.h"

namespace DataBus {
namespace Resource {

// 共享内存块信息结构体
struct WaitingPermitRequest {
    // 禁用默认构造器和运算符重载
    WaitingPermitRequest() = delete;
    WaitingPermitRequest(const WaitingPermitRequest&) = delete;
    WaitingPermitRequest& operator=(const WaitingPermitRequest&) = delete;
    WaitingPermitRequest(int32_t pApplicant, DataBus::Common::PermissionType pType) : applicant_(pApplicant),
                         permissionType_(pType) {}
    int32_t applicant_; // 权限申请客户端
    DataBus::Common::PermissionType permissionType_; // 权限类型
};
}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_WAITING_PERMIT_REQUEST_H
