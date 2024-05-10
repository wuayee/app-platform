/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : application
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#ifndef FIT_REGISTRY_APPLICATION_REPO_H
#define FIT_REGISTRY_APPLICATION_REPO_H

#include <fit/stl/memory.hpp>
#include <fit/internal/registry/fit_registry_entities.h>

namespace Fit {
class RegistryApplicationRepo {
public:
    using Application = RegistryInfo::Application;
    using ApplicationMeta = RegistryInfo::ApplicationMeta;
    virtual ~RegistryApplicationRepo() = default;
    virtual const char* GetName() const = 0;
    virtual FitCode Save(ApplicationMeta application) = 0;
    virtual FitCode Delete(const Application& id) = 0;
    virtual vector<ApplicationMeta> Query(const string& appName) const = 0;
    virtual FitCode Query(const Application& id, ApplicationMeta& result) const = 0;
    virtual vector<ApplicationMeta> QueryAll() const = 0;
};

using RegistryApplicationRepoPtr = std::shared_ptr<RegistryApplicationRepo>;
class RegistryApplicationRepoFactory {
public:
    static unique_ptr<RegistryApplicationRepo> CreateRepo();
    static unique_ptr<RegistryApplicationRepo> CreateMemoryRepo();
    static unique_ptr<RegistryApplicationRepo> CreateBackendRepo();
    static unique_ptr<RegistryApplicationRepo> CreateCachedRepo(
        unique_ptr<RegistryApplicationRepo> cache, unique_ptr<RegistryApplicationRepo> backend);
};
}

#endif