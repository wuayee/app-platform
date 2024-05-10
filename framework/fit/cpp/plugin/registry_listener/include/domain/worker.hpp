/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for worker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#ifndef FIT_REGISTRY_LISTENER_WORKER_HPP
#define FIT_REGISTRY_LISTENER_WORKER_HPP

#include <cstdint>
#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../repo/worker_endpoint_repo.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为工作进程提供定义。
 */
class Worker : public std::enable_shared_from_this<Worker>, public virtual RegistryListenerElement, public FitBase {
public:
    explicit Worker(const WorkerRepoPtr& repo, Fit::string id, Fit::string environment, map<string, string> extensions);
    ~Worker() override = default;
    RegistryListenerPtr GetRegistryListener() const final;

    /**
     * 获取工作进程所属的应用程序。
     *
     * @return 表示指向工作进程所属的应用程序的共享指针。
     */
    ApplicationPtr GetApplication() const;

    /**
     * 获取工作进程的唯一标识。
     *
     * @return 表示工作进程唯一标识的字符串。
     */
    const Fit::string& GetId() const;

    /**
     * 获取工作进程所在的环境的标识。
     *
     * @return 表示应用程序的环境标的字符串。
     */
    const Fit::string& GetEnvironment() const;

    /**
     * 将当前工作进程与另一个工作进程进行比较。
     *
     * @param another 表示指向待与当前工作进程比较的另一个工作进程的指针。
     * @return 若当前网络端点大于目标的工作进程，则是一个正数；若小于目标工作进程，则是一个负数；否则为 0。
     */
    int32_t Compare(const WorkerPtr& another) const;

    /**
     * 将当前工作进程与另一个工作进程进行比较。
     *
     * @param another 表示待与当前工作进程比较的另一个工作进程的唯一标识。
     * @return 若当前网络端点大于目标的工作进程，则是一个正数；若小于目标工作进程，则是一个负数；否则为 0。
     */
    int32_t Compare(const Fit::string& id, const Fit::string& environment, const map<string, string>& extensions) const;

    /**
     * 获取所包含的网络端点的仓库。
     *
     * @return 表示指向网络端点仓库的指针。
     */
    WorkerEndpointRepoPtr GetEndpoints();

    const map<string, string>& GetExtensions() const;

    /**
     * 移除当前工作进程。
     */
    void Remove();
private:
    std::weak_ptr<WorkerRepo> repo_;
    Fit::string id_;
    Fit::string environment_;
    WorkerEndpointRepoPtr endpoints_ {};
    Fit::mutex mutex_ {};
    map<string, string> extensions_;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_WORKER_HPP
