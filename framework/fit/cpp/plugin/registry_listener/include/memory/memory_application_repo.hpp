/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for application repo based on memory.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_MEMORY_APPLICATION_REPO_HPP
#define FIT_REGISTRY_LISTENER_MEMORY_APPLICATION_REPO_HPP

#include "../repo/application_repo.hpp"

#include <fit/stl/vector.hpp>
#include <fit/stl/mutex.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为应用程序长裤提供基于内存的实现。
 */
class MemoryApplicationRepo : public std::enable_shared_from_this<MemoryApplicationRepo>,
    public virtual ApplicationRepo {
public:
    explicit MemoryApplicationRepo(const RegistryListenerPtr& registryListener);
    ~MemoryApplicationRepo() override = default;
    RegistryListenerPtr GetRegistryListener() const final;
    ApplicationPtr Get(const Fit::string& name, const Fit::string& version, const map<string, string>& extensions,
        bool createNew) override;
    ApplicationPtr Remove(const Fit::string& name, const Fit::string& version) override;
    uint32_t Count() const override;
private:
    std::weak_ptr<RegistryListener> registryListener_;
    Fit::vector<ApplicationPtr> applications_ {};
    mutable Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_MEMORY_APPLICATION_REPO_HPP
