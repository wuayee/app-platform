/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for workers.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_WORKER_REPO_HPP
#define FIT_REGISTRY_LISTENER_WORKER_REPO_HPP

#include <fit/stl/vector.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/worker.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为工作进程提供仓库。
 */
class WorkerRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取工作进程仓库所属的应用程序。
     *
     * @return 表示指向所属应用程序的指针。
     */
    virtual ApplicationPtr GetApplication() const = 0;

    /**
     * 获取指定名称和版本的工作进程。
     *
     * @param name 表示工作进程的唯一标识的字符串。
     * @param environment 表示工作进程所在环境的标识的字符串。
     * @param createNew 若为 true，则当不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在对应的工作进程，则为指向该实例的指针；否则为 nullptr。
     */
    virtual WorkerPtr Get(const Fit::string& id, const Fit::string& environment, const map<string, string>& extensions,
        bool createNew) = 0;

    /**
     * 移除指定工作进程。
     *
     * @param id 表示待移除的工作进程的唯一标识的字符串。
     * @param environment 表示工作进程所在环境的标识的字符串。
     * @return 若存在该工作进程，则为指向已移除的工作进程的指针；否则为 nullptr。
     */
    virtual WorkerPtr Remove(
        const Fit::string& id, const Fit::string& environment, const map<string, string>& extensions) = 0;

    /**
     * 查询工作进程的数量。
     *
     * @return 表示工作进程数量的32位无符号整数。
     */
    virtual uint32_t Count() const = 0;

    /**
     * 列出仓库中包含的所有工作进程。
     *
     * @return 表示指向工作进程的指针的集合。
     */
    virtual Fit::vector<WorkerPtr> List() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_WORKER_REPO_HPP
