/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description: UserData Request for HandleApplyPermission
 */

#ifndef DATABUS_USER_DATA_REQUEST_H
#define DATABUS_USER_DATA_REQUEST_H

#include "UserData.h"

namespace DataBus {
namespace Resource {

struct UserDataRequest {
    UserDataRequest(bool isOperatingUserData, const std::shared_ptr<UserData>& userData)
        : isOperatingUserData(isOperatingUserData), userData(userData) {}
    bool isOperatingUserData; // 是否操作用户自定义数据
    std::shared_ptr<UserData> userData; // 用户自定义元数据
};
} // namespace Resource
} // namespace DataBus

#endif // DATABUS_USER_DATA_REQUEST_H
