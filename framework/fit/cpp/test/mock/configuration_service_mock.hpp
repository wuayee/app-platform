/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/28
 * Notes:       :
 */

#ifndef CONFIGURATION_SERVICE_MOCK_HPP
#define CONFIGURATION_SERVICE_MOCK_HPP

#include <runtime/config/configuration_service.h>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

struct ConfigurationServiceMock : public Fit::Configuration::ConfigurationService {
    MOCK_METHOD2(GenericableHasTag, bool(const Fit::string &generic_id, const Fit::string &tag));
    MOCK_METHOD1(GetGenericableDefaultFitableId, Fit::string(const Fit::string &generic_id));
    MOCK_METHOD1(GetGenericableRouteId, Fit::string(const Fit::string &generic_id));
    MOCK_METHOD1(GetGenericableLoadbalanceId, Fit::string(const Fit::string &generic_id));
    MOCK_METHOD1(GetGenericableTrust, Fit::Configuration::TrustConfiguration(const Fit::string &generic_id));
    MOCK_METHOD2(GetFitableDegradationId, Fit::string(const Fit::string &generic_id, const Fit::string &fitable_id));
    MOCK_METHOD2(GetFitableIdByAlias, Fit::string(const Fit::string &generic_id, const Fit::string &fitable_id));
    MOCK_METHOD1(GetFitables, Fit::Configuration::FitableSet(const Fit::string &generic_id));
    MOCK_METHOD1(GetGenericableConfig, Fit::Configuration::GenericableConfiguration(const Fit::string &generic_id));
    MOCK_CONST_METHOD1(GetGenericableConfigPtr,
        Fit::Configuration::GenericConfigPtr(const Fit::string &generic_id));
    MOCK_METHOD2(GetGenericableConfig, int32_t(const Fit::string &generic_id,
        Fit::Configuration::GenericableConfiguration &genericable));
};
#endif // CONFIGURATION_SERVICE_MOCK_HPP
