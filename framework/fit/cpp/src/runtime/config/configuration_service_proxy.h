/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide config service invoke proxy.
 * Author       : w00561424
 * Date         : 2023/08/28
 * Notes:       :
 */
#ifndef CONFIGURATION_SERVICE_PROXY_H
#define CONFIGURATION_SERVICE_PROXY_H
#include <configuration_service.h>
#include <configuration_service_composite.h>
namespace Fit {
namespace Configuration {
class ConfigurationServiceProxy : public ConfigurationService {
public:
    ConfigurationServiceProxy() = default;
    ConfigurationServiceProxy(ConfigurationServicePtr baseConfigService,
        ConfigurationServiceCompositePtr configurationServiceComposite);
    bool Start() override;
    bool Stop() override;
    GenericConfigPtr GetGenericableConfigPtr(const Fit::string &genericId) const override;
    int32_t GetGenericableConfig(const Fit::string &genericId, GenericableConfiguration &genericable) override;
    ConfigurationServicePtr BaseConfigurationService();
private:
    bool HasConfigClient();
    ConfigurationServicePtr baseConfigService_ {nullptr};
    ConfigurationServiceCompositePtr configurationServiceCompositeDefault_ {nullptr};
};
}
}
#endif