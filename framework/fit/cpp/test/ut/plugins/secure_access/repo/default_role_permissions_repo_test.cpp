/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <secure_access/include/repo/default_role_permissions_repo.h>
#include <fit/fit_code.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class DefaultRolePermissionsRepoTest : public ::testing::Test {
public:
    void SetUp() override
    {
        rolePermissionsRepo_ = make_shared<DefaultRolePermissionsRepo>();
        role_ = "provider";

        fitableIn_.genericableId = "test_gid";
        fitableIn_.genericableVersion = "1.0.0";
        fitableIn_.fitableId = "test_fid";
        fitableIn_.fitableVersion = "1.0.0";
        permission_ = Permission(fitableIn_);

        fitableIn2_.genericableId = "test_gid2";
        fitableIn2_.genericableVersion = "1.0.0";
        fitableIn2_.fitableId = "test_fid2";
        fitableIn2_.fitableVersion = "1.0.0";
        permission2_ = Permission(fitableIn2_);
    }

    void TearDown() override
    {
    }
public:
    Fit::shared_ptr<DefaultRolePermissionsRepo> rolePermissionsRepo_ {};
    string role_ {};
    fit::hakuna::kernel::shared::Fitable fitableIn_;
    fit::hakuna::kernel::shared::Fitable fitableIn2_;
    Permission permission_;
    Permission permission2_;
};

TEST_F(DefaultRolePermissionsRepoTest, should_return_role_permissions_when_save_and_query_given_param)
{
    // given
    RolePermissions rolePermissions(role_, {permission_});
    vector<RolePermissions> rolePermissionsSet {rolePermissions};

    // when
    int32_t saveRet = rolePermissionsRepo_->Save(rolePermissionsSet);
    vector<RolePermissions> actualRolePermissionsSet = rolePermissionsRepo_->Query({role_});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualRolePermissionsSet.front(), rolePermissions);
}

TEST_F(DefaultRolePermissionsRepoTest, should_return_empty_when_save_remove_and_query_given_param)
{
    // given
    RolePermissions rolePermissions(role_, {permission_});
    vector<RolePermissions> rolePermissionsSet {rolePermissions};

    // when
    int32_t saveRet = rolePermissionsRepo_->Save(rolePermissionsSet);
    vector<RolePermissions> actualRolePermissionsSet = rolePermissionsRepo_->Query({role_});
    int32_t removeRet = rolePermissionsRepo_->Remove({role_});
    vector<RolePermissions> actualRolePermissionsSetAfterRemove = rolePermissionsRepo_->Query({role_});
    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualRolePermissionsSet.front(), rolePermissions);
    EXPECT_EQ(removeRet, FIT_OK);
    EXPECT_EQ(actualRolePermissionsSetAfterRemove.empty(), true);
}

TEST_F(DefaultRolePermissionsRepoTest, should_return_role_permissions2_when_save_and_update_and_query_given_param)
{
    // given
    RolePermissions rolePermissions(role_, {permission_, permission2_});
    vector<RolePermissions> rolePermissionsSet {rolePermissions};
    RolePermissions rolePermissions2(role_, {permission2_});
    vector<RolePermissions> rolePermissionsSet2 {rolePermissions2};

    // when
    int32_t saveRet = rolePermissionsRepo_->Save(rolePermissionsSet);
    vector<RolePermissions> actualRolePermissionsSet = rolePermissionsRepo_->Query({role_});
    int32_t saveRet2 = rolePermissionsRepo_->Save(rolePermissionsSet2);
    vector<RolePermissions> actualRolePermissionsSetAfterUpdate = rolePermissionsRepo_->Query({role_});

    // then
    EXPECT_EQ(saveRet, FIT_OK);
    EXPECT_EQ(actualRolePermissionsSet.front(), rolePermissions);
    EXPECT_EQ(saveRet2, FIT_OK);
    EXPECT_EQ(actualRolePermissionsSetAfterUpdate.front(), rolePermissions2);
}