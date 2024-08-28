/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef ROLE_PERMISSION_H
#define ROLE_PERMISSION_H
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <algorithm>
#include "permission.h"
namespace Fit {
class RolePermissions {
public:
    RolePermissions() = default;
    RolePermissions(const string& roleIn, const vector<Permission>& permissionsIn) : role(roleIn),
        permissions(permissionsIn)
    {
        SortPermissions(permissions);
    }
    RolePermissions(const RolePermissions& rolePermissions) : role(rolePermissions.role),
        permissions(rolePermissions.permissions)
    {
        SortPermissions(permissions);
    }
    RolePermissions& operator=(const RolePermissions& rolePermissions)
    {
        if (this == &rolePermissions) {
            return *this;
        }
        this->role = rolePermissions.role;
        this->permissions = rolePermissions.permissions;
        return *this;
    }

    bool operator==(const RolePermissions& rolePermissions) const
    {
        if ((this->role != rolePermissions.role) || (this->permissions.size() != rolePermissions.permissions.size())) {
            return false;
        }

        auto curIt = this->permissions.begin();
        auto inIt = rolePermissions.permissions.begin();
        for (; curIt != this->permissions.end(); ++curIt, ++inIt) {
            if (!(*curIt == *inIt)) {
                return false;
            }
        }
        return true;
    }

    bool ContainPermission(const Permission& permission)
    {
        for (const auto& permissionTemp : permissions) {
            if ((permissionTemp.fitable.fitableId == permission.fitable.fitableId) &&
                (permissionTemp.fitable.genericableId == permission.fitable.genericableId) &&
                (permissionTemp.fitable.genericableVersion == permission.fitable.genericableVersion)) {
                return true;
            }
        }
        return false;
    }
private:
    void SortPermissions(vector<Permission>& permissions)
    {
        std::sort(permissions.begin(), permissions.end(), [](const Permission& l, const Permission& r) {
            return l.fitable.genericableId < r.fitable.genericableId || l.fitable.fitableId < r.fitable.fitableId ||
                l.fitable.genericableVersion < r.fitable.genericableVersion ||
                l.fitable.fitableVersion < r.fitable.fitableVersion;
        });
    }
public:
    string role;
    vector<Permission> permissions;
};
}
#endif
