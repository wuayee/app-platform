/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides unavailable endpoints for fitable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#ifndef FIT_REGISTRY_LISTENER_UNAVAILABLE_ENDPOINT_HPP
#define FIT_REGISTRY_LISTENER_UNAVAILABLE_ENDPOINT_HPP

#include <cstdint>
#include <fit/stl/string.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"


namespace Fit {
namespace Registry {
namespace Listener {
class FitableUnavailableEndpoint : public std::enable_shared_from_this<FitableUnavailableEndpoint>,
    public virtual RegistryListenerElement, public FitBase {
public:
    explicit FitableUnavailableEndpoint(const FitableUnavailableEndpointRepoPtr& repo,
        Fit::string host, uint16_t port, uint32_t expiration);
    ~FitableUnavailableEndpoint() override = default;
    RegistryListenerPtr GetRegistryListener() const final;

    /**
     * 获取所属的服务实现。
     *
     * @return 表示指向所属泛化服务的指针。
     */
    FitablePtr GetFitable() const;

    /**
     * 获取不可用地址所在的主机。
     *
     * @return 表示主机的字符串。
     */
    const Fit::string& GetHost() const;

    /**
     * 获取不可用地址的端口。
     *
     * @return 表示端口的16位无符号整数。
     */
    uint16_t GetPort() const;

    /**
     * 获取过期信息。
     *
     * @return 表示过期信息的32位无符号整数，当大于0时表示尚未过期。
     */
    uint32_t GetExpiration() const;

    /**
     * 设置过期信息。
     *
     * @param expiration 表示过期信息的32位无符号整数。为0则已过期。
     */
    void SetExpiration(uint32_t expiration);

    /**
     * 尝试使无效地址过期。
     * <p>会削减过期信息，当过期信息清零时成功使无效地址过期。</p>
     *
     * @return 若地址已过期，则为 true；否则为 false。
     */
    bool TryExpire();

    /**
     * 移除当前无效地址。
     */
    void Remove();

    /**
     * 将当前无效地址与目标无效地址进行比较。
     *
     * @param another 表示待与当前无效地址比较的另一个无效地址。
     * @return 若当前无效地址大于目标无效地址，则是一个正数；若小于目标地址，则是一个负数；否则为 0。
     */
    int32_t Compare(const FitableUnavailableEndpointPtr& another) const;

    /**
     * 将当前无效地址与目标无效地址进行比较。
     *
     * @param host 表示目标无效地址的主机的字符串。
     * @param port 表示目标无效地址的端口号的16位无符号整数。
     * @return 若当前无效地址大于目标无效地址，则是一个正数；若小于目标地址，则是一个负数；否则为 0。
     */
    int32_t Compare(const Fit::string& host, uint16_t port) const;
private:
    std::weak_ptr<FitableUnavailableEndpointRepo> repo_;
    Fit::string host_;
    uint16_t port_;
    uint32_t expiration_;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_UNAVAILABLE_ENDPOINT_HPP
