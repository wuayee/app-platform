/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : Provide registry fitable implement for v2.
 * Author       : w00561424
 * Date:        : 2023/09/18
 */
#ifndef REGISTER_FITABLE_V2_H
#define REGISTER_FITABLE_V2_H
#include <register_fitable_base.h>
namespace Fit {
class RegisterFitableV2 : public RegisterFitableBase {
public:
    RegisterFitableV2(Fit::Framework::Formatter::FormatterServicePtr formatterService,
        std::shared_ptr<CommonConfig> commonConfig, Configuration::ConfigurationServicePtr configurationService);
    ~RegisterFitableV2() override {};
    FitCode RegisterFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables,
        int32_t expire = DEFAULT_EXPIRE) override;
    FitCode UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables) override;
    FitCode CheckFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables) override;
};
}
#endif
