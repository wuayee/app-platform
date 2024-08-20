/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <secure_access/include/secure_access_config.h>
#include <fit/external/plugin/plugin_config.hpp>
#include <fit/stl/memory.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
using namespace Fit::Plugin;
class SecureAccessConfigTest : public ::testing::Test {
public:
    void SetUp() override
    {
        pluginConfig_ = Fit::Plugin::CreatePluginConfig("libFitPlugin_SecureAccess_test.json");
        secureAccessConfig_ = Fit::make_shared<SecureAccessConfig>();

        authKey_.ak = "test_ak1";
        authKey_.sk = "test_sk1";
        authKey_.role = "provider";

        fit::hakuna::kernel::shared::Fitable fitableIn;
        fitableIn.genericableId = "provider_gid";
        fitableIn.genericableVersion = "1.0.0";
        fitableIn.fitableId = "provider_fid";
        fitableIn.fitableVersion = "1.0.0";
        permission_ = Permission(fitableIn);
        rolePermissions_ = RolePermissions(authKey_.role, {permission_});
    }

    void TearDown() override
    {
    }
public:
    PluginConfigPtr pluginConfig_ {};
    Fit::shared_ptr<SecureAccessConfig> secureAccessConfig_ {};
    AuthKey authKey_ {};
    Permission permission_ {};
    RolePermissions rolePermissions_ {};
};

TEST_F(SecureAccessConfigTest, should_return_ok_when_init_given_param)
{
    // given
    // when
    FitCode ret = secureAccessConfig_->InitConfig(pluginConfig_);
    Fit::string accessKey = secureAccessConfig_->AccessKey();
    Fit::string cryptoType = secureAccessConfig_->CryptoType();
    Fit::vector<AuthKey> authKeys = secureAccessConfig_->AuthKeys();
    Fit::vector<RolePermissions> rolePermissionsSet = secureAccessConfig_->RolePermissionsSet();
    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(accessKey, authKey_.ak);
    EXPECT_EQ(cryptoType, "scc");
    EXPECT_EQ(authKeys.front(), authKey_);
    EXPECT_EQ(rolePermissionsSet.front(), rolePermissions_);
}