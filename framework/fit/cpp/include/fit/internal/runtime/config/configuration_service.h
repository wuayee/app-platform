/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/3/20 21:19
 * Notes:       :
 */

#ifndef CONFIGURATION_SERVICE_H
#define CONFIGURATION_SERVICE_H

#include "configuration_entities.h"

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/memory.hpp>

namespace Fit {
namespace Configuration {
class ConfigurationService;
using ConfigurationServicePtr = std::shared_ptr<ConfigurationService>;
class ConfigurationService : public RuntimeElementBase {
public:
    ConfigurationService() : RuntimeElementBase("configurationService") {};
    virtual ~ConfigurationService() override = default;
    virtual bool GenericableHasTag(const Fit::string &genericId, const Fit::string &tag);
    virtual Fit::string GetGenericableDefaultFitableId(const Fit::string &genericId);
    virtual Fit::string GetGenericableRouteId(const Fit::string &genericId);
    virtual Fit::string GetGenericableLoadbalanceId(const Fit::string &genericId);
    virtual TrustConfiguration GetGenericableTrust(const Fit::string &genericId);
    virtual Fit::string GetFitableDegradationId(const Fit::string &genericId, const Fit::string &fitableId);
    virtual Fit::string GetFitableIdByAlias(const Fit::string &genericId, const Fit::string &alias);
    virtual FitableSet GetFitables(const Fit::string &genericId);
    virtual GenericableConfiguration GetGenericableConfig(const Fit::string &genericId);
    virtual GenericConfigPtr GetGenericableConfigPtr(const Fit::string &genericId) const = 0;
    // 只返回FIT_ERR_NOT_FOUND和FIT_ERR_SUCCESS
    virtual int32_t GetGenericableConfig(const Fit::string &genericId, GenericableConfiguration &genericable) = 0;
    static Fit::unique_ptr<ConfigurationService> Create();
    static ConfigurationService* Instance();
    static ConfigurationServicePtr BaseConfigurationService();
};
}
}
#endif // CONFIGURATION_SERVICE_H
