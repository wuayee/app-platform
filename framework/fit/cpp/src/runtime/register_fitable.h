/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/09/14
 * Notes:       :
 */
#ifndef FIT_REGISTRY_SUITE_H
#define FIT_REGISTRY_SUITE_H
#include <register_fitable_base.h>
namespace Fit {
class RegisterFitable : public RegisterFitableBase {
public:
    RegisterFitable(Fit::Framework::Formatter::FormatterServicePtr formatterService,
        std::shared_ptr<CommonConfig> commonConfig, Configuration::ConfigurationServicePtr configurationService);
    ~RegisterFitable() override {};
    FitCode RegisterFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables,
        int32_t expire = DEFAULT_EXPIRE) override;
    FitCode UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables) override;
    FitCode CheckFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables) override;
};
}
#endif