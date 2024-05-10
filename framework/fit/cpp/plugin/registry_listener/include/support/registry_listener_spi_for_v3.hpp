/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Description  : Provides registry v3 interface implementation for registry listener SPI.
 * Author       : w00561424
 * Date         : 2023/09/11
 */
#ifndef REGISTRY_LISTENER_SPI_FOR_V3_HPP
#define REGISTRY_LISTENER_SPI_FOR_V3_HPP
#include <support/registry_listener_spi.hpp>
#include <fit/internal/runtime/config/configuration_service.h>
namespace Fit {
namespace Registry {
namespace Listener {
class RegistryListenerSpiForV3 : public BaseRegistryListenerSpi {
public:
    RegistryListenerSpiForV3(Configuration::ConfigurationServicePtr configurationService,
        ApplicationInstanceSpiPtr applicationInstanceSpi);
    ~RegistryListenerSpiForV3() override = default;
    FitableInstanceListGuard QueryFitableInstances(const ::Fit::vector<FitableInfo>& fitables) const override;
    FitableInstanceListGuard SubscribeFitables(const ::Fit::vector<FitableInfo>& fitables) override;
    FitCode UnsubscribeFitables(const ::Fit::vector<FitableInfo>& fitables) override;
    void SubscribeFitablesChanged(FitablesChangedCallbackPtr callback) override;
    void UnsubscribeFitablesChanged(FitablesChangedCallbackPtr callback) override;
private:
    Configuration::ConfigurationServicePtr configurationService_ {};
    ApplicationInstanceSpiPtr applicationInstanceSpi_ {};
};
}
}
}
#endif