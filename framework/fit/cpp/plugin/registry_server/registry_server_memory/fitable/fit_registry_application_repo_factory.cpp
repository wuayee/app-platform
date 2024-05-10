/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application repo factory
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#include <fit/internal/registry/repository/fit_registry_application_repo.h>
#include "fit_registry_application_memory_repo.h"
#include "fit_registry_application_cached_repo.h"

namespace Fit {
unique_ptr<RegistryApplicationRepo> RegistryApplicationRepoFactory::CreateRepo()
{
    return CreateCachedRepo(CreateMemoryRepo(), CreateBackendRepo());
}
unique_ptr<RegistryApplicationRepo> RegistryApplicationRepoFactory::CreateMemoryRepo()
{
    return make_unique<RegistryApplicationMemoryRepo>();
}
unique_ptr<RegistryApplicationRepo> RegistryApplicationRepoFactory::CreateCachedRepo(
    unique_ptr<RegistryApplicationRepo> cache, unique_ptr<RegistryApplicationRepo> backend)
{
    vector<unique_ptr<RegistryApplicationRepo>> v;
    constexpr uint32_t repoSize = 2;
    v.reserve(repoSize);
    if (cache) {
        v.emplace_back(move(cache));
    }
    if (backend) {
        v.emplace_back(move(backend));
    }
    return make_unique<RegistryApplicationCachedRepo>(move(v));
}
}