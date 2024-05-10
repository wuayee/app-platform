/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/31 15:45
 * Notes:       :
 */

#ifndef FIT_UTIL_H
#define FIT_UTIL_H
#include <fit/stl/vector.hpp>
#include <fit/internal/registry/fit_registry_entities.h>
#include <fit/external/util/context/context_base.h>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>
namespace Fit {
namespace Registry {
vector<RegistryInfo::FlatAddress> GetRegistryAddresses();
void PrintService(const db_service_info_t &dbService, const string& otherText = "");
::fit::hakuna::kernel::registry::shared::FitableInstance Aggregate(
    const ::fit::hakuna::kernel::shared::Fitable& fitable,
    const db_service_set& services, ContextObj ctx);
// 判断addressesIn是不是baseAddresses子集
bool IsSubsetOfBaseAddresses(const Fit::vector<Fit::fit_address>& baseAddresses,
    const Fit::vector<Fit::fit_address>& addressesIn);
void TrySaveApplicationMeta(const ::fit::hakuna::kernel::registry::shared::Application& application);
void TryFillApplicationMeta(::fit::hakuna::kernel::registry::shared::Application& application);
void TryFillApplicationMeta(::fit::hakuna::kernel::registry::shared::FitableInstance& result);
void PreProcessFitableInstance(::fit::hakuna::kernel::registry::shared::FitableInstance& result);
void PreProcessAppInstance(::fit::hakuna::kernel::registry::shared::ApplicationInstance& result);

void CompatibleJavaRegistry(fit_fitable_key_t& key);
}
}
#endif // FIT_UTIL_H
