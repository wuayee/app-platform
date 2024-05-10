/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides factory for repos.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_REPO_FACTORY_HPP
#define FIT_REGISTRY_LISTENER_REPO_FACTORY_HPP

#include "registry_listener_types.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为领域对象提供仓库。
 */
class RepoFactory {
public:
    RepoFactory() = default;
    virtual ~RepoFactory() = default;

    RepoFactory(const RepoFactory&) = delete;
    RepoFactory(RepoFactory&&) = delete;
    RepoFactory& operator=(const RepoFactory&) = delete;
    RepoFactory& operator=(RepoFactory&&) = delete;

    /**
     * 使用所属的注册中心监听程序创建泛化服务仓库的新实例。
     *
     * @param registryListener 表示指向所属注册中心监听程序的指针。
     * @return 表示指向新创建的泛化服务仓库的指针。
     */
    virtual GenericableRepoPtr CreateGenericableRepo(const RegistryListenerPtr& registryListener) = 0;

    /**
     * 使用所属的泛化服务创建服务实现仓库的新实例。
     *
     * @param genericable 表示指向所属泛化服务的指针。
     * @return 表示指向新创建的服务实现仓库的指针。
     */
    virtual FitableRepoPtr CreateFitableRepo(const GenericablePtr& genericable) = 0;

    /**
     * 使用所属的服务实现创建不可用地址仓库的新实例。
     *
     * @param fitable 表示指向所属服务实现的指针。
     * @return 表示指向新创建的不可用地址仓库的指针。
     */
    virtual FitableUnavailableEndpointRepoPtr CreateFitableUnavailableEndpointRepo(const FitablePtr& fitable) = 0;

    /**
     * 使用所属的注册中心监听程序创建应用程序仓库的新实例。
     *
     * @param registryListener 表示指向所属注册中心监听程序的指针。
     * @return 表示指向新创建的应用程序仓库的指针。
     */
    virtual ApplicationRepoPtr CreateApplicationRepo(const RegistryListenerPtr& registryListener) = 0;

    /**
     * 使用所属的应用程序创建工作进程仓库的新实例。
     *
     * @param application 表示指向所属应用程序的指针。
     * @return 表示指向新创建的工作进程仓库的指针。
     */
    virtual WorkerRepoPtr CreateWorkerRepo(const ApplicationPtr& application) = 0;

    /**
     * 使用所属的工作进程创建工作进程网络端点仓库的新实例。
     *
     * @param worker 表示指向所属的工作进程的指针。
     * @return 表示指向新创建的工作进程网络端点的仓库的指针。
     */
    virtual WorkerEndpointRepoPtr CreateWorkerEndpointRepo(const WorkerPtr& worker) = 0;

    /**
     * 为指定的服务实现创建与应用程序的关联关系的仓库。
     *
     * @param fitable 表示指向待创建关联关系仓库的服务实现的指针。
     * @return 表示指向新创建的服务实现与应用程序关联关系的仓库的指针。
     */
    virtual FitableApplicationRepoPtr CreateFitableApplicationRepo(const FitablePtr& fitable) = 0;

    /**
     * 为指定的应用程序创建与服务实现的关联关系的仓库。
     *
     * @param application 表示指向待创建关联关系仓库的应用程序的指针。
     * @return 表示指向新创建的应用程序与服务实现关联关系的仓库的指针。
     */
    virtual ApplicationFitableRepoPtr CreateApplicationFitableRepo(const ApplicationPtr& application) = 0;
};

/**
 * 定义仓库工厂的共享指针。
 */
using RepoFactoryPtr = std::shared_ptr<RepoFactory>;
}
}
}

#endif // FIT_REGISTRY_LISTENER_REPO_FACTORY_HPP
