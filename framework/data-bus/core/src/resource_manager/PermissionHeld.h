/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
