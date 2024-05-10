/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory repo factory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <memory/memory_repo_factory.hpp>

#include <memory/memory_application_fitable_repo.hpp>
#include <memory/memory_application_repo.hpp>
#include <memory/memory_fitable_application_repo.hpp>
#include <memory/memory_fitable_repo.hpp>
#include <memory/memory_fitable_unavailable_endpoint_repo.hpp>
#include <memory/memory_genericable_repo.hpp>
#include <memory/memory_worker_endpoint_repo.hpp>
#include <memory/memory_worker_repo.hpp>

using namespace Fit::Registry::Listener;

GenericableRepoPtr MemoryRepoFactory::CreateGenericableRepo(const RegistryListenerPtr& registryListener)
{
    return std::make_shared<MemoryGenericableRepo>(registryListener);
}

FitableRepoPtr MemoryRepoFactory::CreateFitableRepo(const GenericablePtr& genericable)
{
    return std::make_shared<MemoryFitableRepo>(genericable);
}

FitableUnavailableEndpointRepoPtr MemoryRepoFactory::CreateFitableUnavailableEndpointRepo(const FitablePtr& fitable)
{
    return std::make_shared<MemoryFitableUnavailableEndpointRepo>(fitable);
}

ApplicationRepoPtr MemoryRepoFactory::CreateApplicationRepo(const RegistryListenerPtr& registryListener)
{
    return std::make_shared<MemoryApplicationRepo>(registryListener);
}

WorkerRepoPtr MemoryRepoFactory::CreateWorkerRepo(const ApplicationPtr& application)
{
    return std::make_shared<MemoryWorkerRepo>(application);
}

WorkerEndpointRepoPtr MemoryRepoFactory::CreateWorkerEndpointRepo(const WorkerPtr& worker)
{
    return std::make_shared<MemoryWorkerEndpointRepo>(worker);
}

FitableApplicationRepoPtr MemoryRepoFactory::CreateFitableApplicationRepo(const FitablePtr& fitable)
{
    return std::make_shared<MemoryFitableApplicationRepo>(fitable);
}

ApplicationFitableRepoPtr MemoryRepoFactory::CreateApplicationFitableRepo(const ApplicationPtr& application)
{
    return std::make_shared<MemoryApplicationFitableRepo>(application);
}
