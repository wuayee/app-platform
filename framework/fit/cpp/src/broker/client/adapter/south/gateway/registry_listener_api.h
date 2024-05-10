/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : for registry listener api
 * Author       : songyongtan
 * Create       : 2022-07-21
 * Notes:       :
 */

#ifndef REGISTRY_LISTENER_API_H
#define REGISTRY_LISTENER_API_H

#include <fit/internal/framework/entity.hpp>
#include <fit/fit_code.h>
#include <fit/stl/memory.hpp>

namespace Fit {
class RegistryListenerApi {
public:
    using Fitable = Framework::Fitable;
    using Address= Framework::Address;
    using ServiceAddress= Framework::ServiceAddress;
    virtual ~RegistryListenerApi() = default;
    virtual FitCode GetFitableAddresses(const Fitable& fitable, vector<ServiceAddress>& result) = 0;
    virtual FitCode GetRegistryFitableAddresses(vector<ServiceAddress>& result) = 0;
    static unique_ptr<RegistryListenerApi> CreateDefault();
};
}

#endif