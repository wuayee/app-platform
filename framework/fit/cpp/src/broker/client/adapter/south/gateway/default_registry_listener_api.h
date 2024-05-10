/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : default implenment for registry listener api
 * Author       : songyongtan
 * Create       : 2022-07-21
 * Notes:       :
 */

#ifndef DEFAULT_REGISTRY_LISTENER_API_H
#define DEFAULT_REGISTRY_LISTENER_API_H

#include "registry_listener_api.h"

#include <cstdint>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>

namespace Fit {
class DefaultRegistryListenerApi : public RegistryListenerApi {
public:
    FitCode GetFitableAddresses(const Fitable& fitable, vector<ServiceAddress>& result) override;
    FitCode GetRegistryFitableAddresses(vector<ServiceAddress>& result) override;

    static uint32_t CalculateApplicationInstanceAddressSize(
        const vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>& applicationInstances);
    static void GetAddressesFromApplicationInstance(const Fitable& fitable,
        const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>& applicationInstances,
        vector<ServiceAddress>& result);
};
}

#endif