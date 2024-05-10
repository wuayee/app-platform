/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : registry application repo factory
 * Author       : songyongtan
 * Create       : 2023-09-22
 * Notes:       :
 */

#include <fit/internal/registry/repository/fit_registry_application_repo.h>

namespace Fit {
unique_ptr<RegistryApplicationRepo> RegistryApplicationRepoFactory::CreateBackendRepo()
{
    return nullptr;
}
}