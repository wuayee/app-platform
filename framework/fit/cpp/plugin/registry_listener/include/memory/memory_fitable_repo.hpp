/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable repo based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_FITABLE_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_FITABLE_REPO_HPP

#include "../repo/fitable_repo.hpp"

#include <fit/stl/mutex.hpp>
#include <fit/stl/vector.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务实现的仓库提供基于内存的实现。
 */
class MemoryFitableRepo : public std::enable_shared_from_this<MemoryFitableRepo>, public virtual FitableRepo {
public:
    explicit MemoryFitableRepo(const GenericablePtr& genericable);
    ~MemoryFitableRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    GenericablePtr GetGenericable() const final;
    FitablePtr Get(const Fit::string& id, const Fit::string& version, bool createNew) override;
    FitablePtr Remove(const Fit::string& id, const Fit::string& version) override;
    Fit::vector<FitablePtr> List() const override;
private:
    std::weak_ptr<Genericable> genericable_;
    Fit::vector<FitablePtr> fitables_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_FITABLE_REPO_HPP
