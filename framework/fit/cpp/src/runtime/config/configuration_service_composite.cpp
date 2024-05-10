/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : composite config service implement.
 * Author       : w00561424
 * Date         : 2023/08/28
 * Notes:       :
 */
#include <configuration_service_composite.h>
#include <algorithm>
namespace Fit {
namespace Configuration {
ConfigurationServiceComposite::ConfigurationServiceComposite(const Fit::vector<ConfigurationServicePtr>& configServices)
    : configServices_(configServices)
{
}

GenericConfigPtr ConfigurationServiceComposite::GetGenericableConfigPtr(const Fit::string &genericId) const
{
    GenericConfigPtr genericConfigGetterPtr {nullptr};
    for (const auto& configService : configServices_) {
        if (configService == nullptr) {
            continue;
        }
        genericConfigGetterPtr = configService->GetGenericableConfigPtr(genericId);
        if (genericConfigGetterPtr != nullptr) {
            return genericConfigGetterPtr;
        }
    }
    return genericConfigGetterPtr;
}

int32_t ConfigurationServiceComposite::GetGenericableConfig(
    const Fit::string &genericId, GenericableConfiguration &genericable)
{
    int32_t ret = FIT_ERR_NOT_FOUND;
    for (const auto& configService : configServices_) {
        if (configService == nullptr) {
            continue;
        }
        ret = configService->GetGenericableConfig(genericId, genericable);
        if (ret == FIT_ERR_SUCCESS) {
            return ret;
        }
    }
    return ret;
}
}
}