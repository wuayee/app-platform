/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application memory repo
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#ifndef FIT_REGISTRY_APPLICATION_MEMORY_REPO_H
#define FIT_REGISTRY_APPLICATION_MEMORY_REPO_H

#include <fit/internal/registry/repository/fit_registry_application_repo.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/set.hpp>

namespace Fit {
class RegistryApplicationMemoryRepo : public RegistryApplicationRepo {
public:
    const char* GetName() const override;
    FitCode Save(ApplicationMeta application) override;
    FitCode Delete(const Application& id) override;
    vector<ApplicationMeta> Query(const string& appName) const override;
    FitCode Query(const Application& id, ApplicationMeta& result) const override;
    vector<ApplicationMeta> QueryAll() const override;

private:
    mutable mutex mt_;
    // key is Application::GetStrId()
    map<string, std::unique_ptr<ApplicationMeta>> appIndexOfId_;
    // key is name
    map<string, set<ApplicationMeta*>> appIndexOfName_;
};

}

#endif