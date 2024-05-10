/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Description  : Provides registry v3 interface implementation for registry listener SPI.
 * Author       : w00561424
 * Date         : 2023/09/11
 */
#include <support/registry_listener_spi_for_v3.hpp>
#include <fit/fit_code.h>
#include <algorithm>
namespace Fit {
namespace Registry {
namespace Listener {
using namespace Fit::Configuration;
RegistryListenerSpiForV3::RegistryListenerSpiForV3(Configuration::ConfigurationServicePtr configurationService,
    ApplicationInstanceSpiPtr applicationInstanceSpi) : configurationService_(std::move(configurationService)),
    applicationInstanceSpi_(std::move(applicationInstanceSpi))
{
}
FitableInstanceListGuard RegistryListenerSpiForV3::QueryFitableInstances(
    const ::Fit::vector<FitableInfo>& fitables) const
{
    auto ret = FIT_OK;
    Fit::vector<FitableInstance> fitableInstances {};
    for (const auto& fitable : fitables) {
        // 1. 根据fitable查询应用信息
        auto genericableConfig  = configurationService_->GetGenericableConfigPtr(fitable.genericableId);
        if (genericableConfig == nullptr) {
            FIT_LOG_WARN("Genericable is null, gid : %s.", fitable.genericableId.c_str());
            continue;
        }
        FitableConfiguration fitableConfig;
        if (genericableConfig->GetFitable(fitable.fitableId, fitableConfig) != FIT_ERR_SUCCESS) {
            FIT_LOG_WARN("Fitable meta not found, fitableId: %s.", fitable.fitableId.c_str());
            continue;
        }
        size_t count = fitableConfig.applications.size();
        if (fitableConfig.applicationsFormats.size() != count) {
            FIT_LOG_ERROR("Application size is not equal formats size, application:formats(%lu:%lu).",
                count, fitableConfig.applicationsFormats.size());
            continue;
        }
        // 2. 根据应用查询实例信息 & 组装fitableInstance
        FitableInstance fitableInstance {};
        fitableInstance.fitable = new ::fit::hakuna::kernel::shared::Fitable();
        fitableInstance.fitable->genericableId = genericableConfig->GetGenericId();
        fitableInstance.fitable->genericableVersion = "1.0.0";
        fitableInstance.fitable->fitableId = fitableConfig.fitableId;
    
        fitableInstance.aliases = fitableConfig.aliases;
        fitableInstance.tags = genericableConfig->GetTags();
        fitableInstance.extensions = fitableConfig.extensions;
        for (size_t i = 0; i < count; ++i) {
            Fit::vector<ApplicationInstance> applicationInstances
                = applicationInstanceSpi_->Query({fitableConfig.applications[i]});
            for (auto& applicationInstance : applicationInstances) {
                applicationInstance.formats = fitableConfig.applicationsFormats[i];
            }
            fitableInstance.applicationInstances.insert(fitableInstance.applicationInstances.end(),
                applicationInstances.begin(), applicationInstances.end());
        }
        fitableInstances.emplace_back(std::move(fitableInstance));
    }

    return FitableInstanceListGuard {fitableInstances, ret};
}
FitableInstanceListGuard RegistryListenerSpiForV3::SubscribeFitables(const ::Fit::vector<FitableInfo>& fitables)
{
    return QueryFitableInstances(fitables);
}
FitCode RegistryListenerSpiForV3::UnsubscribeFitables(const ::Fit::vector<FitableInfo>& fitables)
{
    return FIT_ERR_SUCCESS;
}
void RegistryListenerSpiForV3::SubscribeFitablesChanged(FitablesChangedCallbackPtr callback)
{
}
void RegistryListenerSpiForV3::UnsubscribeFitablesChanged(FitablesChangedCallbackPtr callback)
{
}
}
}
}