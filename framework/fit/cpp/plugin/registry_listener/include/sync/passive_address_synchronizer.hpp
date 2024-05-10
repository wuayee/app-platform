/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides passive synchronizer for fitable addresses.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_PASSIVE_ADDRESS_SYNCHRONIZER_HPP
#define FIT_REGISTRY_LISTENER_PASSIVE_ADDRESS_SYNCHRONIZER_HPP

#include "../sync/address_synchronizer.hpp"

#include "../registry_listener.hpp"

#include <functional>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务地址提供被动同步程序。
 */
class PassiveAddressSynchronizer : public AddressSynchronizerBase {
public:
    /**
     * 使用待同步地址的注册中心监听程序初始化被动地址同步程序。
     *
     * @param registryListener 表示指向注册中心监听程序的指针。
     */
    explicit PassiveAddressSynchronizer(RegistryListenerPtr registryListener);
    ~PassiveAddressSynchronizer() override = default;
    void Start() override;
    void Stop() override;
private:
    void Synchronize(const Fit::vector<FitableInstance>& fitableInstances);
    const FitablesChangedCallbackPtr& GetCallback();
    void OnFitablesSubscribed(const Fit::vector<FitableInfo>& fitableInfos);
    void OnFitablesUnsubscribed(const Fit::vector<FitableInfo>& fitableInfos);
    bool started_ {false};
    FitablesChangedCallbackPtr callback_ {};
    Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_PASSIVE_ADDRESS_SYNCHRONIZER_HPP
