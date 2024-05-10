/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2022-07-29
 * Notes:       :
 */

#ifndef BROKER_GENERICABLE_CONFIG_MOCK_HPP
#define BROKER_GENERICABLE_CONFIG_MOCK_HPP

#include <gmock/gmock.h>

#include "broker/client/application/gateway/fit_config.h"

namespace Fit {
class BrokerGenericableConfigMock : public IFitConfig {
public:
    MOCK_CONST_METHOD0(EnableTrust, bool());
    MOCK_CONST_METHOD0(LocalOnly, bool());
    MOCK_CONST_METHOD0(TraceIgnore, bool());
    MOCK_CONST_METHOD0(GetGenericId, Fit::string());
    MOCK_CONST_METHOD0(GetGenericVersion, const Fit::string&());
    MOCK_CONST_METHOD0(GetRoutine, Fit::string());
    MOCK_CONST_METHOD0(GetDefault, Fit::string());
    MOCK_CONST_METHOD1(GetDegradation, Fit::string(const Fit::string& id));
    MOCK_CONST_METHOD0(GetValidate, Fit::string());
    MOCK_CONST_METHOD0(GetBefore, Fit::string());
    MOCK_CONST_METHOD0(GetAfter, Fit::string());
    MOCK_CONST_METHOD0(GetError, Fit::string());
    MOCK_CONST_METHOD0(GetRuleId, Fit::string());
    MOCK_CONST_METHOD0(IsRegistryFitable, bool());
    MOCK_CONST_METHOD1(GetFitableIdByAlias, Fit::string(const Fit::string& alias));
    MOCK_CONST_METHOD1(GetParamTagByIdx, Fit::vector<Fit::string>(int32_t idx));
    MOCK_CONST_METHOD0(GetRandomFitable, Fit::string());
};
}  // namespace Fit

#endif  // BROKER_GENERICABLE_CONFIG_MOCK_HPP
