/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for registry listener.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/08
 */

#ifndef FIT_REGISTRY_LISTENER_REGISTRY_LISTENER_HPP
#define FIT_REGISTRY_LISTENER_REGISTRY_LISTENER_HPP

#include "repo_factory.hpp"

#include "support/registry_listener_spi.hpp"
#include "repo/application_repo.hpp"
#include "repo/genericable_repo.hpp"
#include "util/task_scheduler.hpp"

#include <fit/external/util/context/context_api.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>

#include <functional>

#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 表示注册中心监听程序。
 */
class RegistryListener : public std::enable_shared_from_this<RegistryListener> {
public:
    /**
     * 初始化注册中心监听程序的新实例。
     */
    explicit RegistryListener(RegistryListenerSpiPtr spi, RepoFactoryPtr repoFactory, uint32_t isolationExpiration);

    /**
     * 释放注册中心监听程序占用的所有资源。
     */
    virtual ~RegistryListener();

    RegistryListener(const RegistryListener&) = delete;
    RegistryListener(RegistryListener&&) = delete;
    RegistryListener& operator=(const RegistryListener&) = delete;
    RegistryListener& operator=(RegistryListener&&) = delete;

    /**
     * 为所使用的仓库提供工厂。
     *
     * @return 表示用以创建仓库的工厂。
     */
    RepoFactoryPtr GetRepoFactory() const;

    /**
     * 获取注册中心监听程序所使用的南向接口。
     *
     * @return 表示指向南向接口的指针。
     */
    const RegistryListenerSpiPtr& GetSpi() const;

    /**
     * 获取包含所有泛化服务的仓库。
     *
     * @return 表示指向泛化服务仓库的指针。
     */
    GenericableRepoPtr GetGenericables();

    /**
     * 列出所有已服务实现。
     *
     * @return 表示指向服务实现的指针的列表。
     */
    Fit::vector<FitableInfo> ListFitables();

    /**
     * 获取服务实现。
     *
     * @param info 表示服务实现的信息的引用。
     * @param createNew 若为 true，则当服务实现不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在该服务实现，则为指向服务实现的指针；否则为 nullptr。
     */
    FitablePtr GetFitable(const FitableInfo& info, bool createNew);

    /**
     * 获取服务实现。
     *
     * @param genericableId 表示泛化服务的唯一标识的字符串。
     * @param genericableVersion 表示泛化服务的版本的字符串。
     * @param fitableId 表示服务实现的唯一标识的字符串。
     * @param fitableVersion 表示服务实现的版本的字符串。
     * @param createNew 若为 true，则当服务实现不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在该服务实现，则为指向服务实现的指针；否则为 nullptr。
     */
    FitablePtr GetFitable(const Fit::string& genericableId, const Fit::string& genericableVersion,
        const Fit::string& fitableId, const Fit::string& fitableVersion, bool createNew);

    /**
     * 获取应用程序。
     *
     * @param info 表示应用程序的信息的引用。
     * @param createNew 若为 true，则当应用程序不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在该应用程序，则为指向应用程序的指针；否则为 nullptr。
     */
    ApplicationPtr GetApplication(const ApplicationInfo& info, bool createNew);

    /**
     * 获取应用程序。
     *
     * @param name 表示应用程序的名称的字符串。
     * @param version 表示应用程序的版本的字符串。
     * @param createNew 若为 true，则当应用程序不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在该应用程序，则为指向应用程序的指针；否则为 nullptr。
     */
    ApplicationPtr GetApplication(
        const Fit::string& name, const Fit::string& version, const map<string, string>& extensions, bool createNew);

    /**
     * 订阅指定服务的变更。
     *
     * @param fitableInfos 表示服务实现的信息的容器的应用。
     */
    void SubscribeFitables(const vector<FitableInfo>& fitableInfos);

    /**
     * 取消订阅服务的变更。
     *
     * @param fitableInfos 表示服务实现的信息的容器的应用。
     */
    void UnsubscribeFitables(const vector<FitableInfo>& fitableInfos);

    /**
     * 观察服务被订阅的状态变化。
     *
     * @param callback 表示当服务被订阅时触发的回调方法。
     */
    void ObserveFitablesSubscribed(std::function<void(const vector<FitableInfo>&)> callback);

    /**
     * 观察服务被取消订阅的状态变化。
     *
     * @param callback 表示当服务被取消订阅时触发的回调方法。
     */
    void ObserveFitablesUnsubscribed(std::function<void(const vector<FitableInfo>&)> callback);

    /**
     * 获取指定服务的地址。
     *
     * @param ctx 表示
     * @param fitableInfo 表示服务的信息的引用。
     * @return 表示指向服务地址的指针。
     */
    FitableInstance* GetAddresses(ContextObj& ctx, const FitableInfo& fitableInfo);

    /**
     * 隔离指定地址。
     *
     * @param fitableInfo 表示待隔离的地址生效的服务实现的引用。
     * @param workerInfo 表示待隔离的地址的引用。
     */
    void Isolate(const FitableInfo& fitableInfo, const WorkerInfo& workerInfo);

    /**
     * 自动调度指定任务。
     *
     * @param task 表示任务的执行方法。
     * @param interval 表示任务的执行间隔的秒数的32位无符号整数。
     */
    void ScheduleTask(TaskPtr task, uint32_t interval);

    /**
     * 取消任务的自动调度。
     *
     * @param task 表示待取消的自动调度的任务。
     */
    void UnscheduleTask(const TaskPtr& task);
private:
    FitableInstance* GetAddresses(ContextObj& ctx, const FitableInfo& fitableInfo, bool subscribeNew);
    RegistryListenerSpiPtr spi_;
    RepoFactoryPtr repoFactory_;
    uint32_t isolationExpiration_;
    GenericableRepoPtr genericables_ {nullptr};
    ApplicationRepoPtr applications_ {nullptr};
    std::shared_ptr<TaskScheduler> taskScheduler_ {nullptr};
    Fit::mutex mutex_ {};
    Fit::vector<std::function<void(const vector<FitableInfo>&)>> fitablesSubscribedCallbacks_ {};
    Fit::vector<std::function<void(const vector<FitableInfo>&)>> fitablesUnsubscribedCallbacks_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_REGISTRY_LISTENER_HPP
