/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fitable meta service mock
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#ifndef FIT_FITABLE_META_SERVICE_MOCK_HPP
#define FIT_FITABLE_META_SERVICE_MOCK_HPP
#include <registry_server/v3/fit_fitable_meta/include/fit_fitable_meta_service.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
namespace Fit {
namespace Registry {
class FitFitableMetaServiceMock : public FitFitableMetaService {
public:
    MOCK_METHOD1(Save, int32_t(const Fit::vector<Fit::RegistryInfo::FitableMeta>&));
    MOCK_METHOD2(Query, Fit::vector<Fit::RegistryInfo::FitableMeta>(
        const Fit::vector<Fit::string>&, const Fit::string&));
    MOCK_METHOD2(Query, Fit::vector<Fit::RegistryInfo::FitableMeta>(
        const Fit::RegistryInfo::Fitable&, const Fit::string&));
    MOCK_METHOD2(Remove, int32_t(const Fit::vector<Fit::RegistryInfo::Application>&, const Fit::string&));
    MOCK_METHOD1(IsApplicationExist, bool(const Fit::RegistryInfo::Application&));
};
}
}
#endif
