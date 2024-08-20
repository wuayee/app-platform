/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef ROLE_PERMISSION_REPO_H
#define ROLE_PERMISSION_REPO_H
#include <include/domain/role_permissions.h>
#include <fit/stl/memory.hpp>
namespace Fit {
class RolePermissionsRepo {
public:
    virtual ~RolePermissionsRepo() = default;
    virtual int32_t Save(const vector<RolePermissions>& rolePermissionsSet) = 0;
    virtual int32_t Remove(const vector<string>& roles) = 0;
    virtual vector<RolePermissions> Query(const vector<string>& roles) = 0;
};

using RolePermissionsRepoPtr = shared_ptr<RolePermissionsRepo>;

class RolePermissionsRepoFactory {
public:
    static RolePermissionsRepoPtr Create();
};
}
#endif
