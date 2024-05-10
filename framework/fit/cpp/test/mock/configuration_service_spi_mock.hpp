/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : mock config service spi
 * Author       : w00561424
 * Date         : 2023/09/01
 * Notes:       :
 */

#ifndef configuration_service_spi_mock_h
#define configuration_service_spi_mock_h
#include <runtime/config/configuration_service_spi.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
class ConfigurationServiceSpiMock : public Fit::Configuration::ConfigurationServiceSpi {
public:
    MOCK_METHOD3(GetRunningFitables, int32_t(const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment,
        Fit::vector<Fit::Configuration::GenericConfigPtr>& genericableConfigs));
};
#endif