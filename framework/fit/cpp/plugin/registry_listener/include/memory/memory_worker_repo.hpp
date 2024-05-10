/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for worker repo based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_WORKER_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_WORKER_REPO_HPP

#include "../repo/worker_repo.hpp"

#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
class MemoryWorkerRepo : public std::enable_shared_from_this<MemoryWorkerRepo>, public virtual WorkerRepo {
public:
    explicit MemoryWorkerRepo(const ApplicationPtr& application);
    ~MemoryWorkerRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    ApplicationPtr GetApplication() const final;
    WorkerPtr Get(const Fit::string& id, const Fit::string& environment, const map<string, string>& extensions,
        bool createNew) override;
    WorkerPtr Remove(
        const Fit::string& id, const Fit::string& environment, const map<string, string>& extensions) override;
    uint32_t Count() const override;
    Fit::vector<WorkerPtr> List() const override;
private:
    std::weak_ptr<Application> application_;
    Fit::vector<WorkerPtr> workers_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_WORKER_REPO_HPP
