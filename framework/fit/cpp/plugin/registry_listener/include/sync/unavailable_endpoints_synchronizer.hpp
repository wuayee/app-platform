/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides synchronizer for unavailable endpoints.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/22
 */

#ifndef FIT_REGISTRY_LISTENER_UNAVAILABLE_ENDPOINTS_SYNCHRONIZER_HPP
#define FIT_REGISTRY_LISTENER_UNAVAILABLE_ENDPOINTS_SYNCHRONIZER_HPP

#include "../sync/address_synchronizer.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
class UnavailableEndpointsSynchronizer : public AddressSynchronizerBase {
public:
    explicit UnavailableEndpointsSynchronizer(RegistryListenerPtr registryListener);
    ~UnavailableEndpointsSynchronizer() override = default;
    void Start() override;
    void Stop() override;
private:
    void Synchronize();
    Fit::vector<FitableUnavailableEndpointPtr> ListUnavailableEndpoints() const;
    static Fit::vector<FitablePtr> CollectFitables(const Fit::vector<FitableUnavailableEndpointPtr>& endpoints);
    static Fit::vector<FitableInfo> ToFitableInfos(const Fit::vector<FitablePtr>& fitables);
    static void RemoveUnexpired(Fit::vector<FitableUnavailableEndpointPtr>& unavailableEndpoints);
    TaskPtr task_ {nullptr};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_UNAVAILABLE_ENDPOINTS_SYNCHRONIZER_HPP
