/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for fitable unavailable endpoints.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#ifndef FIT_REGISTRY_LISTENER_FITABLE_UNAVAILABLE_ENDPOINT_REPO_HPP
#define FIT_REGISTRY_LISTENER_FITABLE_UNAVAILABLE_ENDPOINT_REPO_HPP

#include <cstdint>
#include <fit/stl/vector.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../domain/fitable_unavailable_endpoint.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务实现的不可用地址提供仓库。
 */
class FitableUnavailableEndpointRepo : public virtual RegistryListenerElement {
public:
    /**
     * 获取仓库所属的服务实现。
     *
     * @return 表示指向服务实现的指针。
     */
    virtual FitablePtr GetFitable() const = 0;

    /**
     * 添加不可用地址。
     *
     * @param host 表示不可用地址所在的主机的字符串。
     * @param port 表示不可用地址的端口号。
     * @param expiration 表示过期信息的32位无符号整数。
     * @return 表示指向不可用地址的指针。
     */
    virtual FitableUnavailableEndpointPtr Add(const Fit::string& host, uint16_t port, uint32_t expiration) = 0;

    /**
     * 移除不可用地址。
     *
     * @param host 表示不可用地址所在的主机的字符串。
     * @param port 表示不可用地址的端口号。
     * @return 若存在该不可用地址，则为指向已移除的不可用地址实例的指针；否则为 nullptr。
     */
    virtual FitableUnavailableEndpointPtr Remove(const Fit::string& host, uint16_t port) = 0;

    /**
     * 检查是否包含指定地址。
     *
     * @param host 表示不可用地址所在的主机的字符串。
     * @param port 表示不可用地址的端口号。
     * @return 若存在该地址，则为 true；否则为 false。
     */
    virtual bool Contains(const Fit::string& host, uint16_t port) const = 0;

    /**
     * 列出所有无效的地址。
     *
     * @return 表示无效地址的指针的集合。
     */
    virtual Fit::vector<FitableUnavailableEndpointPtr> List() const = 0;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_FITABLE_UNAVAILABLE_ENDPOINT_REPO_HPP
