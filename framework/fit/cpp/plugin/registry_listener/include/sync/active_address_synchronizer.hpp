/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides active synchronizer for fitable addresses.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_ACTIVE_SYNCHRONIZER_HPP
#define FIT_REGISTRY_LISTENER_ACTIVE_SYNCHRONIZER_HPP

#include "../sync/address_synchronizer.hpp"

#include "../registry_listener.hpp"

#include <cstdint>
#include <thread>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务地址提供主动同步程序。
 */
class ActiveAddressSynchronizer : public AddressSynchronizerBase {
public:
    explicit ActiveAddressSynchronizer(RegistryListenerPtr registryListener, uint32_t interval);
    ~ActiveAddressSynchronizer() override = default;
    void Start() override;
    void Stop() override;
private:
    void Synchronize();
    void OnFitablesSubscribed(const vector<FitableInfo>& fitableInfos);
    TaskPtr task_;
    uint32_t interval_;
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_ACTIVE_SYNCHRONIZER_HPP
