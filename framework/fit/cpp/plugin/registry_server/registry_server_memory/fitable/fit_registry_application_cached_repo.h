/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application cached repo
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#ifndef FIT_REGISTRY_APPLICATION_CACHED_REPO_H
#define FIT_REGISTRY_APPLICATION_CACHED_REPO_H

#include <fit/internal/registry/repository/fit_registry_application_repo.h>
#include <fit/stl/memory.hpp>

namespace Fit {
class RegistryApplicationCachedRepo : public RegistryApplicationRepo {
public:
    explicit RegistryApplicationCachedRepo(vector<unique_ptr<RegistryApplicationRepo>> repos);
    const char* GetName() const override;
    FitCode Save(ApplicationMeta application) override;
    FitCode Delete(const Application& id) override;
    vector<ApplicationMeta> Query(const string& appName) const override;
    FitCode Query(const Application& id, ApplicationMeta& result) const override;
    vector<ApplicationMeta> QueryAll() const override;

private:
    unique_ptr<RegistryApplicationRepo> cacheRepo_;
    unique_ptr<RegistryApplicationRepo> backendRepo_;
    vector<unique_ptr<RegistryApplicationRepo>> repos_;
};

}

#endif