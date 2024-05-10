/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides synchronizer for fitable addresses.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_ADDRESS_SYNCHRONIZER_HPP
#define FIT_REGISTRY_LISTENER_ADDRESS_SYNCHRONIZER_HPP

#include "../registry_listener.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务地址提供同步程序。
 */
class AddressSynchronizer {
public:
    /**
     * 初始化服务地址同步程序的新实例。
     */
    AddressSynchronizer() = default;

    /**
     * 释放服务地址同步程序占用的所有资源。
     */
    virtual ~AddressSynchronizer() = default;

    AddressSynchronizer(const AddressSynchronizer&) = delete;
    AddressSynchronizer(AddressSynchronizer&&) = delete;
    AddressSynchronizer& operator=(const AddressSynchronizer&) = delete;
    AddressSynchronizer& operator=(AddressSynchronizer&&) = delete;

    /**
     * 启动同步程序。
     */
    virtual void Start() = 0;

    /**
     * 停止同步程序。
     */
    virtual void Stop() = 0;
};

using AddressSynchronizerPtr = std::shared_ptr<AddressSynchronizer>;

/**
 * 为服务地址同步程序提供基类。
 */
class AddressSynchronizerBase : public virtual AddressSynchronizer {
public:
    explicit AddressSynchronizerBase(RegistryListenerPtr registryListener);
    ~AddressSynchronizerBase() override = default;
protected:
    RegistryListenerPtr GetRegistryListener() const;
    void AcceptChanges(const Fit::vector<::fit::hakuna::kernel::registry::shared::FitableInstance>& addresses);
private:
    void AcceptFitableChanges(const ::fit::hakuna::kernel::registry::shared::FitableInstance& fitableAddress);
    void AcceptApplicationInstanceChange(FitablePtr& fitable,
        vector<ApplicationPtr>& usedApplications,
        const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& applicationInstance);
    static void AcceptApplicationChanges(FitablePtr& fitable, ApplicationPtr& application,
        const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& applicationInstance,
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Worker>& workers,
        Fit::vector<ApplicationPtr>& usedApplications);
    static void AcceptWorkerChanges(const WorkerRepoPtr& repo,
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Worker>& workers);
    static void AcceptEndpoints(const WorkerPtr& worker, const ::fit::hakuna::kernel::registry::shared::Worker& info);
    RegistryListenerPtr registryListener_;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_ADDRESS_SYNCHRONIZER_HPP
