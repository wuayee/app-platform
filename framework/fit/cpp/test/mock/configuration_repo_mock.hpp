/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/25
 * Notes:       :
 */

#ifndef CONFIGURATION_REPO_MOCK_HPP
#define CONFIGURATION_REPO_MOCK_HPP
#include <runtime/config/configuration_repo.h>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace Fit::Configuration;
using namespace ::testing;

class ConfigurationRepoMock : public ConfigurationRepo {
public:
    MOCK_METHOD2(Get, int32_t(const Fit::string &, GenericableConfiguration &));
    MOCK_METHOD1(Getter, GenericConfigPtr(const Fit::string &));
    MOCK_METHOD1(Set, int32_t(GenericConfigPtr));
};
#endif // CONFIGURATION_REPO_MOCK_HPP
