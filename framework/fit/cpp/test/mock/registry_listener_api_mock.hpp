/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2022-07-29
 * Notes:       :
 */

#ifndef REGISTRY_LISTENER_API_MOCK_HPP
#define REGISTRY_LISTENER_API_MOCK_HPP

#include <gmock/gmock.h>

#include "broker/client/adapter/south/gateway/registry_listener_api.h"

class RegistryListenerApiMock : public Fit::RegistryListenerApi {
public:
    MOCK_METHOD2(GetFitableAddresses, FitCode(const Fitable& fitable, Fit::vector<ServiceAddress>& result));
    MOCK_METHOD1(GetRegistryFitableAddresses, FitCode(Fit::vector<ServiceAddress>& result));
};

#endif // REGISTRY_LISTENER_API_MOCK_HPP
