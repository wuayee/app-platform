/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides SPIs for registry listener.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/20
 */

#ifndef FIT_REGISTRY_LISTENER_REGISTRY_LISTENER_SPI_HPP
#define FIT_REGISTRY_LISTENER_REGISTRY_LISTENER_SPI_HPP

#include <fit/fit_code.h>
#include <fit/stl/string.hpp>

#include <functional>
#include <memory>
#include <fit/stl/memory.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
using FitableInfo = ::fit::hakuna::kernel::shared::Fitable;
using FitableInstance = ::fit::hakuna::kernel::registry::shared::FitableInstance;
using ApplicationInfo = ::fit::hakuna::kernel::registry::shared::Application;
using ApplicationInstance = ::fit::hakuna::kernel::registry::shared::ApplicationInstance;
using WorkerInfo = ::fit::hakuna::kernel::registry::shared::Worker;

class FitablesChangedCallback;
using FitablesChangedCallbackPtr = std::shared_ptr<FitablesChangedCallback>;

/**
 * 为服务实例的集合提供生命周期的绑定。
 */
class FitableInstanceListGuard {
public:
    FitableInstanceListGuard(Fit::vector<FitableInstance> fitableInstances, FitCode resultCode);
    FitableInstanceListGuard(FitableInstanceListGuard &&input);
    FitableInstanceListGuard& operator=(FitableInstanceListGuard&&);
    ~FitableInstanceListGuard();

    /**
     * 获取服务实例的列表。
     *
     * @return 表示服务实例列表的引用。
     */
    const Fit::vector<FitableInstance>& Get() const;
    FitCode GetResultCode() const;
private:
    Fit::vector<FitableInstance> fitableInstances_;
    FitCode resultCode_;
};

class FitablesChangedCallback {
public:
    FitablesChangedCallback() = default;
    virtual ~FitablesChangedCallback() = default;

    FitablesChangedCallback(const FitablesChangedCallback&) = delete;
    FitablesChangedCallback(FitablesChangedCallback&&) = delete;
    FitablesChangedCallback& operator=(const FitablesChangedCallback&) = delete;
    FitablesChangedCallback& operator=(FitablesChangedCallback&&) = delete;

    virtual FitCode Notify(const Fit::vector<FitableInstance>& instances) = 0;

    static FitablesChangedCallbackPtr Create(std::function<FitCode(const Fit::vector<FitableInstance>&)> action);
};

/**
 * 为注册中心监听程序提供南向接口。
 */
class RegistryListenerSpi {
public:
    RegistryListenerSpi() = default;
    virtual ~RegistryListenerSpi() = default;

    RegistryListenerSpi(const RegistryListenerSpi&) = delete;
    RegistryListenerSpi(RegistryListenerSpi&&) = delete;
    RegistryListenerSpi& operator=(const RegistryListenerSpi&) = delete;
    RegistryListenerSpi& operator=(RegistryListenerSpi&&) = delete;

    virtual const ::Fit::string& GetWorkerId() const = 0;

    virtual FitableInstanceListGuard QueryFitableInstances(const ::Fit::vector<FitableInfo>& fitables) const = 0;

    virtual FitableInstanceListGuard SubscribeFitables(const ::Fit::vector<FitableInfo>& fitables) = 0;

    virtual FitCode UnsubscribeFitables(const ::Fit::vector<FitableInfo>& fitables) = 0;

    virtual void SubscribeFitablesChanged(FitablesChangedCallbackPtr callback) = 0;

    virtual void UnsubscribeFitablesChanged(FitablesChangedCallbackPtr callback) = 0;
};

using RegistryListenerSpiPtr = Fit::shared_ptr<RegistryListenerSpi>;

/**
 * 为注册中心监听程序提供南向接口。
 */
class BaseRegistryListenerSpi : public RegistryListenerSpi {
public:
    BaseRegistryListenerSpi() = default;
    ~BaseRegistryListenerSpi() override = default;

    BaseRegistryListenerSpi(const BaseRegistryListenerSpi&) = delete;
    BaseRegistryListenerSpi(BaseRegistryListenerSpi&&) = delete;
    BaseRegistryListenerSpi& operator=(const BaseRegistryListenerSpi&) = delete;
    BaseRegistryListenerSpi& operator=(BaseRegistryListenerSpi&&) = delete;

    const ::Fit::string& GetWorkerId() const override;
};

class ApplicationInstanceSpi {
public:
    virtual ~ApplicationInstanceSpi() = default;
    virtual Fit::vector<ApplicationInstance> Query(const Fit::vector<ApplicationInfo>& apps) = 0;
    virtual Fit::vector<ApplicationInstance> Subscribe(const Fit::vector<ApplicationInfo>& apps) = 0;
};

using ApplicationInstanceSpiPtr = Fit::shared_ptr<ApplicationInstanceSpi>;
}
}
}

#endif // FIT_REGISTRY_LISTENER_REGISTRY_LISTENER_SPI_HPP
