/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-11-24
 * Notes:       :
 */

#include "fit/stl/memory.hpp"
#include "table_address.hpp"
#include "table_application.hpp"
#include "table_fitable.hpp"
#include "table_fitable_subscribe.hpp"
#include "table_worker.hpp"
#include "fit/internal/registry/repository/fit_subscription_repository.h"
#include "fit/internal/registry/repository/fit_worker_table_operation.h"

using namespace Fit;
using namespace Fit::Repository::Pg;

FitWorkerTableOperationPtr FitWorkerTableOperationFactory::Create()
{
    return make_shared<TableWorker>();
}

FitAddressTableOperationPtr FitAddressTableOperationFactory::Create()
{
    return make_shared<TableAddress>();
}

FitFitableTableOperationPtr FitFitableTableOperationFactory::Create()
{
    return make_shared<TableFitable>();
}

fit_subscription_repository_ptr fit_subscription_repository_factory::Create()
{
    return make_unique<TableFitableSubscribe>();
}

unique_ptr<RegistryApplicationRepo> RegistryApplicationRepoFactory::CreateBackendRepo()
{
    return make_unique<TableApplication>();
}
