/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for application fitable repo based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_APPLICATION_FITABLE_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_APPLICATION_FITABLE_REPO_HPP

#include "../repo/application_fitable_repo.hpp"

#include <fit/stl/vector.hpp>
#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
class MemoryApplicationFitableRepo : public std::enable_shared_from_this<MemoryApplicationFitableRepo>,
    public virtual ApplicationFitableRepo {
public:
    explicit MemoryApplicationFitableRepo(const ApplicationPtr& application);
    ~MemoryApplicationFitableRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    ApplicationPtr GetApplication() const final;
    ApplicationFitablePtr Get(const FitablePtr& fitable, bool createNew) override;
    ApplicationFitablePtr Remove(const FitablePtr& fitable) override;
    ApplicationFitablePtr RemoveOnlyMyself(const FitablePtr& fitable) override;
    void Attach(ApplicationFitablePtr relation) override;
private:
    std::weak_ptr<Application> application_;
    Fit::vector<ApplicationFitablePtr> relations_ {};
    ::Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_APPLICATION_FITABLE_REPO_HPP
