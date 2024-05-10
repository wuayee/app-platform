/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/08/26
 * Notes:       :
 */
#include <configuration_service.h>
namespace Fit {
namespace Configuration {
bool ConfigurationService::GenericableHasTag(const Fit::string &genericId, const Fit::string &tag)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s.", genericId.c_str());
        return false;
    }

    return genericable.HasTag(tag);
}
Fit::string ConfigurationService::GetGenericableDefaultFitableId(const Fit::string &genericId)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s.", genericId.c_str());
        return "";
    }

    return genericable.GetDefaultFitableId();
}
Fit::string ConfigurationService::GetGenericableRouteId(const Fit::string &genericId)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s.", genericId.c_str());
        return "";
    }

    return genericable.GetRoute();
}
Fit::string ConfigurationService::GetGenericableLoadbalanceId(const Fit::string &genericId)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s.", genericId.c_str());
        return "";
    }

    return genericable.GetLoadbalance();
}
TrustConfiguration ConfigurationService::GetGenericableTrust(const Fit::string &genericId)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s.", genericId.c_str());
        return {};
    }

    return genericable.GetTrust();
}
Fit::string ConfigurationService::GetFitableDegradationId(const Fit::string &genericId,
    const Fit::string &fitableId)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s, fitable_id = %s.", genericId.c_str(), fitableId.c_str());
        return "";
    }

    FitableConfiguration fitable {};
    if (genericable.GetFitable(fitableId, fitable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s, fitable_id = %s.", genericId.c_str(), fitableId.c_str());
        return "";
    }

    return std::move(fitable.degradation);
}
Fit::string ConfigurationService::GetFitableIdByAlias(const Fit::string &genericId, const Fit::string &alias)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s, alias = %s.", genericId.c_str(), alias.c_str());
        return "";
    }

    return genericable.GetFitableIdByAlias(alias);
}
FitableSet ConfigurationService::GetFitables(const Fit::string &genericId)
{
    GenericableConfiguration genericable {};
    if (GetGenericableConfig(genericId, genericable) != FIT_ERR_SUCCESS) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s, alias = %s.", genericId.c_str(), genericId.c_str());
        return {};
    }

    return genericable.GetFitables();
}

GenericableConfiguration ConfigurationService::GetGenericableConfig(const Fit::string &genericId)
{
    GenericableConfiguration result {};
    GetGenericableConfig(genericId, result);
    return result;
}
}
}