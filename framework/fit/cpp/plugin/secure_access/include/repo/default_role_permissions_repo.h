/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef DEFAULT_ROLE_PERMISSIONS_ROLE_H
#define DEFAULT_ROLE_PERMISSIONS_ROLE_H
#include <include/repo/role_permissions_repo.h>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/mutex.hpp>
namespace Fit {
class DefaultRolePermissionsRepo : public RolePermissionsRepo {
public:
    int32_t Save(const vector<RolePermissions>& rolePermissionsSet) override;
    int32_t Remove(const vector<string>& roles) override;
    vector<RolePermissions> Query(const vector<string>& roles) override;
private:
    Fit::mutex mutex_;
    Fit::unordered_map<Fit::string, RolePermissions> rolePermissionsSet_ {};
};
}
#endif