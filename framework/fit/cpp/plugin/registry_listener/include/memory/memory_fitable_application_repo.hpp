/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  :
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_FITABLE_APPLICATION_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_FITABLE_APPLICATION_REPO_HPP

#include "../repo/fitable_application_repo.hpp"

#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
class MemoryFitableApplicationRepo : public std::enable_shared_from_this<MemoryFitableApplicationRepo>,
    public virtual FitableApplicationRepo {
public:
    explicit MemoryFitableApplicationRepo(const FitablePtr& fitable);
    ~MemoryFitableApplicationRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    FitablePtr GetFitable() const final;
    ApplicationFitablePtr Get(const ApplicationPtr& application, bool createNew) override;
    ApplicationFitablePtr Remove(const ApplicationPtr& application) override;
    ApplicationFitablePtr RemoveOnlyMyself(const ApplicationPtr& application) override;
    Fit::vector<ApplicationFitablePtr> List() const override;
    void Attach(ApplicationFitablePtr relation) override;
private:
    std::weak_ptr<Fitable> fitable_;
    Fit::vector<ApplicationFitablePtr> relations_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_FITABLE_APPLICATION_REPO_HPP
