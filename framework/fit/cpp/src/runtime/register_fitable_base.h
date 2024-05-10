/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : Provide register fitable interface.
 * Author       : w00561424
 * Date:        : 2023/09/18
 */
#ifndef REGISTER_FITABLE_BASE_H
#define REGISTER_FITABLE_BASE_H
#include <fit/fit_code.h>
#include <fit/stl/vector.hpp>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/internal/runtime/config/configuration_service.h>
#include <fit/external/util/context/context_base.h>
#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Address/1.0.0/cplusplus/Address.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp>
#include <common_config.h>

namespace Fit {
class RegisterFitableBase {
public:
    virtual ~RegisterFitableBase() = default;
    virtual FitCode RegisterFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables,
        int32_t expire) = 0;
    virtual FitCode UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables) = 0;
    virtual FitCode CheckFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables) = 0;
protected:
    Fit::vector<::fit::hakuna::kernel::registry::shared::Address> GetLocalAddresses(
        const Fit::vector<fit::registry::Address>& serverAddresses);
    static Fit::string ComputeAppVersion(const ::fit::hakuna::kernel::registry::shared::Application& application,
        const ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta>& fitables);
    ::Fit::vector<::fit::hakuna::kernel::shared::Fitable> Convert(
        const Framework::Annotation::FitableDetailPtrList& fitables);
    void Build(const Fit::Framework::Annotation::FitableDetailPtrList &fitables, ContextObj ctx,
        ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta>& fitableMetas);
    static Fit::string ComputeWorkerVersion(const ::fit::hakuna::kernel::registry::shared::Worker& worker,
        const fit::hakuna::kernel::registry::shared::Application& application);
protected:
    Fit::Framework::Formatter::FormatterServicePtr formatterService_ {nullptr};
    std::shared_ptr<CommonConfig> commonConfig_ {nullptr};
    Configuration::ConfigurationServicePtr configurationService_ {nullptr};
    ::fit::hakuna::kernel::registry::shared::Application application_ {};
};
}
#endif
