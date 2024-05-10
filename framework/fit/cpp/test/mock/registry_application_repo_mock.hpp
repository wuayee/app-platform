/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application repo mock
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#ifndef FIT_REGISTRY_APPLICATION_REPO_MOCK_HPP
#define FIT_REGISTRY_APPLICATION_REPO_MOCK_HPP

#include <gmock/gmock.h>
#include <fit/internal/registry/repository/fit_registry_application_repo.h>

namespace Fit {
class RegistryApplicationRepoMock : public RegistryApplicationRepo {
public:
    MOCK_METHOD1(Save, FitCode(ApplicationMeta application));
    MOCK_METHOD1(Delete, FitCode(const Application& id));
    MOCK_CONST_METHOD0(GetName, const char*());
    MOCK_CONST_METHOD1(Query, vector<ApplicationMeta>(const string& appName));
    MOCK_CONST_METHOD2(Query, FitCode(const Application& id, ApplicationMeta& result));
    MOCK_CONST_METHOD0(QueryAll, vector<ApplicationMeta>());
};
}

#endif // SYSTEM_CONFIG_MOCK_HPP
