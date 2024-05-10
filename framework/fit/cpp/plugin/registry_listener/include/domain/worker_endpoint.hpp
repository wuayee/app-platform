/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for worker endpoint.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#ifndef FIT_REGISTRY_LISTENER_WORKER_ENDPOINT_HPP
#define FIT_REGISTRY_LISTENER_WORKER_ENDPOINT_HPP

#include <cstdint>
#include <fit/stl/string.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为工作进程提供网络端点的定义。
 */
class WorkerEndpoint : public virtual RegistryListenerElement, public FitBase {
public:
    explicit WorkerEndpoint(const WorkerEndpointRepoPtr& repo, Fit::string host, uint16_t port, int32_t protocol);
    ~WorkerEndpoint() override = default;
    RegistryListenerPtr GetRegistryListener() const final;

    /**
     * 获取端点所属的工作进程。
     *
     * @return 表示指向工作进程的指针。
     */
    WorkerPtr GetWorker() const;

    /**
     * 获取网络端点所在的主机。
     *
     * @return 表示主机的字符串。
     */
    const Fit::string& GetHost() const;

    /**
     * 获取网络端点所使用的端口号。
     *
     * @return 表示端口号的16位无符号整数。
     */
    uint16_t GetPort() const;

    /**
     * 获取网络端点所支持的传输协议。
     *
     * @return 表示传输协议的32位整数。
     */
    int32_t GetProtocol() const;

    /**
     * 获取一个值，该值指示当前网络端点是否被启用。
     *
     * @return 若为 <code>true</code>，则端点被启用；否则端点未被启用。
     */
    bool IsEnabled() const;

    /**
     * 启用端点。
     */
    void Enable();

    /**
     * 禁用端点。
     */
    void Disable();

    /**
     * 将当前的网络端点与目标网络端点进行比较。
     *
     * @param another 表示指向待与当前网络端点进行比较的另一个网络端点的指针。
     * @return 若当前网络端点大于目标网络端点，则是一个正数；若小于目标网络端点，则是一个负数；否则为 0。
     */
    int32_t Compare(const WorkerEndpointPtr& another) const;

    /**
     * 将当前的网络端点与目标网络端点进行比较。
     *
     * @param host 表示目标网络端点所在主机的字符串。
     * @param port 表示目标网络端点使用的端口号的16位无符号整数。
     * @param protocol 表示目标网络端点支持的传输协议的32位无符号整数。
     * @return 若当前网络端点大于目标网络端点，则是一个整数；若小于目标网络端点，则是一个负数；否则为 0。
     */
    int32_t Compare(const Fit::string& host, uint16_t port, int32_t protocol) const;

    /**
     * 移除当前的网络端点。
     */
    void Remove();
private:
    std::weak_ptr<WorkerEndpointRepo> repo_;
    Fit::string host_;
    uint16_t port_;
    int32_t protocol_;
    bool enabled_ {true};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_WORKER_ENDPOINT_HPP
