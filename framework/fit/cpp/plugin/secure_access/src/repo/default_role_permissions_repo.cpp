/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#include <include/repo/default_role_permissions_repo.h>
#include <fit/stl/mutex.hpp>
#include <fit/fit_code.h>
namespace Fit {
int32_t DefaultRolePermissionsRepo::Save(const vector<RolePermissions>& rolePermissionsSet)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    for (const auto& rolePermissions : rolePermissionsSet) {
        auto it = rolePermissionsSet_.find(rolePermissions.role);
        if (it == rolePermissionsSet_.end()) {
            rolePermissionsSet_[rolePermissions.role] = rolePermissions;
        } else if (!(rolePermissions == it->second)) {
            it->second = rolePermissions;
        }
    }
    return FIT_OK;
}

int32_t DefaultRolePermissionsRepo::Remove(const vector<string>& roles)
{
    Fit::unique_lock<Fit::mutex> lock(mutex_);
    for (const auto& role : roles) {
        rolePermissionsSet_.erase(role);
    }
    return FIT_OK;
}

vector<RolePermissions> DefaultRolePermissionsRepo::Query(const vector<string>& roles)
{
    vector<RolePermissions> rolePermissionsSet;
    rolePermissionsSet.reserve(roles.size());
    unique_lock<mutex> lock(mutex_);
    for (const auto& role : roles) {
        auto it = rolePermissionsSet_.find(role);
        if (it != rolePermissionsSet_.end()) {
            rolePermissionsSet.emplace_back(it->second);
        }
    }
    return rolePermissionsSet;
}

RolePermissionsRepoPtr RolePermissionsRepoFactory::Create()
{
    return make_shared<DefaultRolePermissionsRepo>();
}
}