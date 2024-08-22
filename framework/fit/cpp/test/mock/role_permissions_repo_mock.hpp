/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef ROLE_PERMISSIONS_REPO_MOCK_HPP
#define ROLE_PERMISSIONS_REPO_MOCK_HPP
#include <gmock/gmock.h>
#include <secure_access/include/repo/role_permissions_repo.h>
namespace Fit {
class RolePermissionsRepoMock : public RolePermissionsRepo {
public:
    MOCK_METHOD1(Save, int32_t(const vector<RolePermissions>& rolePermissions));
    MOCK_METHOD1(Remove, int32_t(const vector<string>& roles));
    MOCK_METHOD1(Query, vector<RolePermissions>(const vector<string>& roles));
};
}
#endif