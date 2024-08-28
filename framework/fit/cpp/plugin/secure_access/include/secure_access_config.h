/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef SECURE_ACCESS_CONFIG_H
#define SECURE_ACCESS_CONFIG_H
#include <fit/stl/string.hpp>
#include <fit/fit_code.h>
#include <fit/external/plugin/plugin_config.hpp>
#include <include/domain/auth_key.h>
#include <include/domain/role_permissions.h>
namespace Fit {
class SecureAccessConfig {
public:
    FitCode InitConfig(Fit::Plugin::PluginConfigPtr pluginConfig);
    const Fit::string& AccessKey();
    const Fit::string& CryptoType();
    const Fit::vector<AuthKey>& AuthKeys();
    const Fit::vector<RolePermissions>& RolePermissionsSet();
    static SecureAccessConfig& Instance();
private:
    Fit::string accessKey_ {};
    Fit::string cryptoType_ {};
    Fit::vector<AuthKey> authKeys_ {};
    Fit::vector<RolePermissions> rolePermissionsSet_ {};
};
}
#endif