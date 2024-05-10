/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for repo factory based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_REPO_FACTORY_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_REPO_FACTORY_HPP

#include "../repo_factory.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为仓库工厂提供基于内存的实现。
 */
class MemoryRepoFactory : public virtual RepoFactory {
public:
    GenericableRepoPtr CreateGenericableRepo(const RegistryListenerPtr& registryListener) override;
    FitableRepoPtr CreateFitableRepo(const GenericablePtr& genericable) override;
    FitableUnavailableEndpointRepoPtr CreateFitableUnavailableEndpointRepo(const FitablePtr& fitable) override;
    ApplicationRepoPtr CreateApplicationRepo(const RegistryListenerPtr& registryListener) override;
    WorkerRepoPtr CreateWorkerRepo(const ApplicationPtr& application) override;
    WorkerEndpointRepoPtr CreateWorkerEndpointRepo(const WorkerPtr& worker) override;
    FitableApplicationRepoPtr CreateFitableApplicationRepo(const FitablePtr& fitable) override;
    ApplicationFitableRepoPtr CreateApplicationFitableRepo(const ApplicationPtr& application) override;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_REPO_FACTORY_HPP
