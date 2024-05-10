/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides repo for worker endpoints.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_WORKER_ENDPOINT_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_WORKER_ENDPOINT_REPO_HPP

#include "../repo/worker_endpoint_repo.hpp"

#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
class MemoryWorkerEndpointRepo : public std::enable_shared_from_this<MemoryWorkerEndpointRepo>,
    public virtual WorkerEndpointRepo {
public:
    explicit MemoryWorkerEndpointRepo(const WorkerPtr& worker);
    ~MemoryWorkerEndpointRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    WorkerPtr GetWorker() const final;
    WorkerEndpointPtr Get(const Fit::string& host, uint16_t port, int32_t protocol, bool createNew) override;
    WorkerEndpointPtr Remove(const Fit::string& host, uint16_t port, int32_t protocol) override;
    uint32_t Count() const override;
    Fit::vector<WorkerEndpointPtr> List() const override;
private:
    std::weak_ptr<Worker> worker_;
    Fit::vector<WorkerEndpointPtr> endpoints_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_WORKER_ENDPOINT_REPO_HPP
