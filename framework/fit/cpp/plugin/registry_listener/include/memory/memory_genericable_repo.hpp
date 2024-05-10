/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for genericable repo based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_GENERICABLE_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_GENERICABLE_REPO_HPP

#include "../repo/genericable_repo.hpp"

#include <fit/stl/mutex.hpp>
#include <fit/stl/vector.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为泛化服务的仓库提供基于内存的实现。
 */
class MemoryGenericableRepo : public std::enable_shared_from_this<MemoryGenericableRepo>,
    public virtual GenericableRepo {
public:
    explicit MemoryGenericableRepo(const RegistryListenerPtr& registryListener);
    ~MemoryGenericableRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    GenericablePtr Get(const Fit::string& id, const Fit::string& version, bool createNew) override;
    GenericablePtr Remove(const Fit::string& id, const Fit::string& version) override;
    Fit::vector<GenericablePtr> List() const override;
private:
    std::weak_ptr<RegistryListener> registryListener_;
    Fit::vector<GenericablePtr> genericables_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_GENERICABLE_REPO_HPP
