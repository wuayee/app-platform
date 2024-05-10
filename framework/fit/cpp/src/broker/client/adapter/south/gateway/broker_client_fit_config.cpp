/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: broker client
 * Author: w00561424
 * Date: 2020-05-16
 */

#include <cstdlib>
#include <fit/internal/util/fit_random.h>
#include "configuration_service.h"
#include "fit/fit_code.h"
#include "broker_client_fit_config.h"

namespace {
const char * const TAG_TRUST_IGNORED = "trustIgnored";
const char * const TAG_LOCAL_ONLY = "localOnly";
const char * const TAG_REGISTRY_FITABLE = "registry";
const char * const TAG_TRACE_IGNORE = "traceIgnored";
}
namespace Fit {
BrokerClientFitConfig::BrokerClientFitConfig(Configuration::GenericConfigPtr configPtr)
    : configPtr_(std::move(configPtr)) {}

bool BrokerClientFitConfig::EnableTrust() const
{
    return !configPtr_->HasTag(TAG_TRUST_IGNORED);
}

bool BrokerClientFitConfig::LocalOnly() const
{
    return configPtr_->HasTag(TAG_LOCAL_ONLY);
}

bool BrokerClientFitConfig::TraceIgnore() const
{
    return configPtr_->HasTag(TAG_TRACE_IGNORE);
}

Fit::string BrokerClientFitConfig::GetGenericId() const
{
    return configPtr_->GetGenericId();
}

Fit::string BrokerClientFitConfig::GetRoutine() const
{
    return configPtr_->GetRoute();
}

Fit::string BrokerClientFitConfig::GetDefault() const
{
    return configPtr_->GetDefaultFitableId();
}

Fit::string BrokerClientFitConfig::GetDegradation(const Fit::string &id) const
{
    Configuration::FitableConfiguration fitable {};
    if (configPtr_->GetFitable(id, fitable) != FIT_OK) {
        FIT_LOG_DEBUG("Not get config, generic_id = %s, fitable_id = %s.",
            configPtr_->GetGenericId().c_str(), id.c_str());
        return "";
    }

    return std::move(fitable.degradation);
}

Fit::string BrokerClientFitConfig::GetValidate() const
{
    return configPtr_->GetTrust().validate;
}

Fit::string BrokerClientFitConfig::GetBefore() const
{
    return configPtr_->GetTrust().before;
}

Fit::string BrokerClientFitConfig::GetAfter() const
{
    return configPtr_->GetTrust().after;
}

Fit::string BrokerClientFitConfig::GetError() const
{
    return configPtr_->GetTrust().error;
}

Fit::string BrokerClientFitConfig::GetRuleId() const
{
    return configPtr_->GetRule().id;
}

bool BrokerClientFitConfig::IsRegistryFitable() const
{
    return configPtr_->HasTag(TAG_REGISTRY_FITABLE);
}

Fit::string BrokerClientFitConfig::GetFitableIdByAlias(const Fit::string &alias) const
{
    return configPtr_->GetFitableIdByAlias(alias);
}

Fit::vector<Fit::string> BrokerClientFitConfig::GetParamTagByIdx(int32_t idx) const
{
    const auto &params = configPtr_->GetParams();
    for (const auto &param : params) {
        if (param.index == idx) {
            return param.taggerIds;
        }
    }
    return Fit::vector<Fit::string>();
}

Fit::string BrokerClientFitConfig::GetRandomFitable() const
{
    auto fitables = configPtr_->GetFitables();
    if (fitables.empty()) {
        return "";
    }

    size_t pos = static_cast<size_t>(Fit::FitRandom()) % fitables.size();
    return fitables[pos].fitableId;
}

FitConfigPtr BrokerClientFitConfig::BuildConfig(const Fit::string &genericableId,
    const Configuration::ConfigurationServicePtr &configurationService)
{
    auto cfg = configurationService->GetGenericableConfigPtr(genericableId);
    if (cfg == nullptr) {
        FIT_LOG_DEBUG("Use default genericable configuration. (genericable=%s).", genericableId.c_str());
        return std::make_shared<BrokerClientFitConfig>(
            std::make_shared<Configuration::GenericableConfiguration>());
    }
    return std::make_shared<Fit::BrokerClientFitConfig>(std::move(cfg));
}
}