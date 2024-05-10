/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : composite config service implement.
 * Author       : w00561424
 * Date         : 2023/08/28
 * Notes:       :
 */

#ifndef CONFIGURATION_SERVICE_COMPOSITE_H
#define CONFIGURATION_SERVICE_COMPOSITE_H
#include <configuration_service.h>
#include <fit/stl/vector.hpp>
#include <fit/stl/mutex.hpp>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Configuration {
class ConfigurationServiceComposite : public ConfigurationService {
public:
    explicit ConfigurationServiceComposite(const Fit::vector<ConfigurationServicePtr>& configServices);
    GenericConfigPtr GetGenericableConfigPtr(const Fit::string &genericId) const override;
    int32_t GetGenericableConfig(const Fit::string &genericId, GenericableConfiguration &genericable) override;
private:
    Fit::vector<ConfigurationServicePtr> configServices_ {};
};
using ConfigurationServiceCompositePtr = Fit::shared_ptr<ConfigurationServiceComposite>;
}
}
#endif