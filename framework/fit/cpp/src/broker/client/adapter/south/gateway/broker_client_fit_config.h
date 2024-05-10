/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */
#ifndef BROKER_CLIENT_FIT_CONFIG_H
#define BROKER_CLIENT_FIT_CONFIG_H

#include "broker/client/application/gateway/fit_config.h"
#include "runtime/config/configuration_service.h"

namespace Fit {
class BrokerClientFitConfig : public IFitConfig {
public:
    explicit BrokerClientFitConfig(Configuration::GenericConfigPtr configPtr);

    ~BrokerClientFitConfig() override = default;

    bool EnableTrust() const override;
    bool LocalOnly() const override;
    bool TraceIgnore() const override;
    Fit::string GetGenericId() const override;
    Fit::string GetRoutine() const override;
    Fit::string GetDefault() const override;
    Fit::string GetDegradation(const Fit::string &id) const override;
    Fit::string GetValidate() const override;
    Fit::string GetBefore() const override;
    Fit::string GetAfter() const override;
    Fit::string GetError() const override;
    Fit::string GetRuleId() const override;
    bool IsRegistryFitable() const override;

    Fit::string GetFitableIdByAlias(const Fit::string &alias) const override;
    Fit::vector<Fit::string> GetParamTagByIdx(int32_t idx) const override;
    Fit::string GetRandomFitable() const override;

    static FitConfigPtr BuildConfig(const Fit::string &genericableId,
        const Configuration::ConfigurationServicePtr &configurationService);
private:
    Configuration::GenericConfigPtr configPtr_ {};
};
}
#endif