/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide application instance service methon mocker.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */

#ifndef FIT_APPLICATION_INSTANCE_SERVICE_MOCK_HPP
#define FIT_APPLICATION_INSTANCE_SERVICE_MOCK_HPP
#include <registry_server/v3/fit_application_instance/include/fit_application_instance_service.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
namespace Fit {
namespace Registry {
class FitApplicationInstanceServiceMock : public FitApplicationInstanceService {
public:
    MOCK_METHOD1(Save, int32_t(const Fit::vector<Fit::RegistryInfo::ApplicationInstance>&));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::ApplicationInstance>(
        const Fit::vector<Fit::RegistryInfo::Application>&));
    MOCK_METHOD2(Query, Fit::vector<Fit::RegistryInfo::ApplicationInstance>(
        const Fit::vector<Fit::RegistryInfo::Application>&, const Fit::string&));
    MOCK_METHOD1(Remove, int32_t(const Fit::string&));
    MOCK_METHOD2(Remove, int32_t(const Fit::vector<Fit::RegistryInfo::Application>&, const Fit::string&));
    MOCK_METHOD2(Check, int32_t(const Fit::string&, const Fit::string&));
};
}
}
#endif
