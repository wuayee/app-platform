/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for application.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#ifndef FIT_REGISTRY_LISTENER_APPLICATION_HPP
#define FIT_REGISTRY_LISTENER_APPLICATION_HPP

#include <cstdint>
#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../repo/application_fitable_repo.hpp"
#include "../repo/worker_repo.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为应用程序提供定义。
 */
class Application : public std::enable_shared_from_this<Application>,
    public virtual RegistryListenerElement, public FitBase {
public:
    explicit Application(
        const ApplicationRepoPtr& repo, Fit::string name, Fit::string version, map<string, string> extensions);
    ~Application() override = default;
    RegistryListenerPtr GetRegistryListener() const final;

    /**
     * 获取应用程序的名称。
     *
     * @return 表示应用程序的名称的字符串。
     */
    const Fit::string& GetName() const;

    /**
     * 获取应用程序的版本信息。
     *
     * @return 表示应用程序版本信息的字符串。
     */
    const Fit::string& GetVersion() const;

    /**
     * @brief 获取应用扩展信息
     *
     * @return 应用扩展信息
     */
    const map<string, string> GetExtensions() const;

    /**
     * 比较当前应用程序与另一个应用程序实例。
     *
     * @param another 表示指向待与当前应用程序比较的另一个应用程序的指针。
     * @return 若当前应用程序大于目标应用程序，则是一个正数；若小于目标应用程序，在是一个负数；否则为 0。
     */
    int32_t Compare(const ApplicationPtr& another) const;

    /**
     * 比较当前应用程序与另一个应用程序。
     *
     * @param name 表示待与当前应用程序比较的应用程序的名称的字符串。
     * @param version 表示待与当前应用程序比较的应用程序的版本的字符串。
     * @return 若当前应用程序大于目标应用程序，则是一个正数；若小于目标应用程序，在是一个负数；否则为 0。
     */
    int32_t Compare(const Fit::string& name, const Fit::string& version) const;

    /**
     * 获取所包含的工作进程的仓库。
     *
     * @return 表示指向工作进程仓库的指针。
     */
    WorkerRepoPtr GetWorkers();

    /**
     * 获取应用所包含的与服务实现的关联关系的仓库。
     *
     * @return 表示指向与所包含的服务实现的关联关系的仓库。
     */
    ApplicationFitableRepoPtr GetFitables();

    /**
     * 移除当前的应用程序。
     */
    void Remove();
private:
    std::weak_ptr<ApplicationRepo> repo_;
    Fit::string name_;
    Fit::string version_;
    map<string, string> extensions_;
    WorkerRepoPtr workers_ {};
    ApplicationFitableRepoPtr fitables_ {};
    Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_APPLICATION_HPP
