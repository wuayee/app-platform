/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for worker endpoints.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_WORKER_ENDPOINT_REPO_HPP
#define FIT_REGISTRY_LISTENER_WORKER_ENDPOINT_REPO_HPP

#include <cstdint>
#include <fit/stl/vector.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/worker_endpoint.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为工作进程网络端点提供仓库。
 */
class WorkerEndpointRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取工作进程网络端点仓库所属的工作进程。
     *
     * @return 表示指向所属工作进程的指针。
     */
    virtual WorkerPtr GetWorker() const = 0;

    /**
     * 获取指定名称和版本的工作进程网络端点。
     *
     * @param host 表示工作进程网络端点的所在主机的字符串。
     * @param port 表示工作进程网络端点所使用的端口号的16位无符号整数。
     * @param protocol 表示工作进程网络端点所支持的传输协议的32位整数。
     * @param createNew 若为 true，则当不存在时创建新实例；否则直接返回 nullptr。
     * @return 若存在对应的工作进程网络端点，则为指向该实例的指针；否则为 nullptr。
     */
    virtual WorkerEndpointPtr Get(const Fit::string& host, uint16_t port, int32_t protocol, bool createNew) = 0;

    /**
     * 移除指定工作进程网络端点。
     *
     * @param host 表示工作进程网络端点的所在主机的字符串。
     * @param port 表示工作进程网络端点所使用的端口号的16位无符号整数。
     * @param protocol 表示工作进程网络端点所支持的传输协议的32位整数。
     * @return 若存在该工作进程网络端点，则为指向已移除的工作进程网络端点的指针；否则为 nullptr。
     */
    virtual WorkerEndpointPtr Remove(const Fit::string& host, uint16_t port, int32_t protocol) = 0;

    /**
     * 查询工作进程网络端点的数量。
     *
     * @return 表示工作进程网络端点数量的32位无符号整数。
     */
    virtual uint32_t Count() const = 0;

    /**
     * 列出仓库中包含的所有网络端点。
     *
     * @return 表示指向网络端点的指针的集合。
     */
    virtual Fit::vector<WorkerEndpointPtr> List() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_WORKER_ENDPOINT_REPO_HPP
