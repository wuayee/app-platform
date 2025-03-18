/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: PermissionType associated with a shared memory block held by a client.
 */

#ifndef DATABUS_PERMISSION_HELD_H
#define DATABUS_PERMISSION_HELD_H

#include "fbs/common_generated.h"

namespace DataBus {
namespace Resource {

/**
 * 客户端当前持有的权限。
 *
 * @author l00862071
 * @since 2024-05-28
 */
struct PermissionHeld {
    PermissionHeld(int32_t sharedMemoryId, Common::PermissionType permissionType) : sharedMemoryId(sharedMemoryId),
                                                                                    permissionType(permissionType) {};

    int32_t sharedMemoryId;
    Common::PermissionType permissionType;
};
} // namespace Resource
} // namespace DataBus

#endif // DATABUS_PERMISSION_HELD_H
