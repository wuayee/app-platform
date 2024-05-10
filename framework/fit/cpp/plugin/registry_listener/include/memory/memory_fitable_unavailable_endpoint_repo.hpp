/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable unavailable endpoint repo based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_FITABLE_UNAVAILABLE_ENDPOINT_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_FITABLE_UNAVAILABLE_ENDPOINT_REPO_HPP

#include "../repo/fitable_unavailable_endpoint_repo.hpp"

#include <fit/stl/mutex.hpp>
#include <fit/stl/vector.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务实现不可用地址仓库提供基于内存的实现。
 */
class MemoryFitableUnavailableEndpointRepo :
    public std::enable_shared_from_this<MemoryFitableUnavailableEndpointRepo>,
    public virtual FitableUnavailableEndpointRepo {
public:
    explicit MemoryFitableUnavailableEndpointRepo(const FitablePtr& fitable);
    ~MemoryFitableUnavailableEndpointRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    FitablePtr GetFitable() const final;
    FitableUnavailableEndpointPtr Add(const Fit::string& host, uint16_t port, uint32_t expiration) override;
    FitableUnavailableEndpointPtr Remove(const Fit::string& host, uint16_t port) override;
    bool Contains(const Fit::string& host, uint16_t port) const override;
    Fit::vector<FitableUnavailableEndpointPtr> List() const override;
private:
    std::weak_ptr<Fitable> fitable_;
    Fit::vector<FitableUnavailableEndpointPtr> endpoints_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_FITABLE_UNAVAILABLE_ENDPOINT_REPO_HPP
