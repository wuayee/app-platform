/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides facade for registry listener plugin.
 * Author       : liangjishi 00298979
 * Date         : 2021/11/30
 */

#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_get_fitable_addresses/1.0.0/cplusplus/getFitableAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_mark_fitable_address_status/1.0.0/cplusplus/markFitableAddressStatus.hpp>

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/framework/plugin_activator.hpp>

#include <domain/application.hpp>
#include <domain/fitable.hpp>
#include <domain/genericable.hpp>
#include <memory/memory_repo_factory.hpp>
#include <registry_listener.hpp>

#include <support/registry_listener_spi_for_v3.hpp>
#include <support/application_instance_spi_impl.hpp>
#include <fit/internal/runtime/config/configuration_service.h>

#include <fit/fit_log.h>
#include <fit/internal/util/vector_utils.hpp>

#include <sync/active_address_synchronizer.hpp>
#include <sync/address_synchronizer_composite.hpp>
#include <sync/passive_address_synchronizer.hpp>
#include <sync/unavailable_endpoints_synchronizer.hpp>

namespace Fit {
namespace Registry {
namespace Listener {
using namespace Fit::Configuration;
RegistryListenerPtr gRegistryListener;
AddressSynchronizerPtr synchronizer;
constexpr int32_t DEFAULT_EXPIRATION = 5;
constexpr int32_t DEFAULT_PULL_INTERVAL = 120;
constexpr const char *PULL_MODE_KEY = "registry-listener.pull.enabled";
constexpr const char *PULL_INTERVAL_KEY = "registry-listener.pull.interval";
constexpr const char *PUSH_MODE_KEY = "registry-listener.push.enabled";
constexpr const char *ISOLATION_EXPIRATION_KEY = "registry-listener.isolation.expiration";

FitCode Start(::Fit::Framework::PluginContext* context)
{
    int32_t isolationExpiration
        = context->GetConfig()->Get(ISOLATION_EXPIRATION_KEY).AsInt(DEFAULT_EXPIRATION);
    auto pullMode = context->GetConfig()->Get(PULL_MODE_KEY).AsBool(true);
    int32_t pullInterval = context->GetConfig()->Get(PULL_INTERVAL_KEY)
        .AsInt(DEFAULT_PULL_INTERVAL);
    auto pushMode = context->GetConfig()->Get(PUSH_MODE_KEY).AsBool(false);
    FIT_LOG_INFO("Registry listener config, pull.enabled = %d, pull.interval = %d, push.enabled = %d, "
        "isolationExpiration = %d.",
        pullMode, pullInterval, pushMode, isolationExpiration);

    ConfigurationServicePtr configurationService = Fit::shared_ptr<ConfigurationService>(
        ConfigurationService::Instance(), [](const ConfigurationService* configurationService) {});
    ApplicationInstanceSpiPtr applicationInstanceSpi = std::make_shared<ApplicationInstanceSpiImpl>();
    RegistryListenerSpiPtr spi = std::make_shared<RegistryListenerSpiForV3>(
        configurationService, applicationInstanceSpi);
    RepoFactoryPtr repoFactory = std::make_shared<MemoryRepoFactory>();
    gRegistryListener = std::make_shared<RegistryListener>(std::move(spi), std::move(repoFactory), isolationExpiration);

    auto synchronizerComposite = std::make_shared<AddressSynchronizerComposite>();
    synchronizerComposite->Add(std::make_shared<UnavailableEndpointsSynchronizer>(gRegistryListener));
    if (pullMode) {
        synchronizerComposite->Add(std::make_shared<ActiveAddressSynchronizer>(gRegistryListener, pullInterval));
    }
    if (pushMode) {
        synchronizerComposite->Add(std::make_shared<PassiveAddressSynchronizer>(gRegistryListener));
    }
    synchronizer = std::move(synchronizerComposite);
    synchronizer->Start();
    return FIT_OK;
}

FitCode Stop()
{
    FIT_LOG_INFO("Stop registry listener.");
    if (synchronizer != nullptr) {
        synchronizer->Stop();
        synchronizer = nullptr;
    }
    if (gRegistryListener != nullptr) {
        gRegistryListener = nullptr;
    }
    return FIT_OK;
}

/**
 * 标记指定服务实现的地址的状态。
 *
 * @param ctx 表示服务执行的上下文信息。
 * @param fitables 表示待标记地址状态的服务实现的集合。
 * @param worker 表示服务实现地址的Worker信息。
 * @param valid 表示地址的标记。
 * @return 表示执行结果，若为 <code>FIT_OK</code>，则执行成功；否则执行失败。
 */
FitCode MarkFitableAddressStatus(ContextObj ctx,
    const ::fit::hakuna::kernel::shared::Fitable* fitableInfo,
    const ::fit::hakuna::kernel::registry::shared::Worker* workerInfo,
    const bool* valid)
{
    FIT_LOG_INFO("Start to mark fitable address. "
                 "[genericableId=%s, genericableVersion=%s, fitableId=%s, fitableVersion=%s]",
        fitableInfo->genericableId.c_str(), fitableInfo->genericableVersion.c_str(),
        fitableInfo->fitableId.c_str(), fitableInfo->fitableVersion.c_str());
    if (*valid) {
        FIT_LOG_WARN("The genericable only supports to isolate address.");
        return FIT_ERR_FAIL;
    } else {
        gRegistryListener->Isolate(*fitableInfo, *workerInfo);
        return FIT_OK;
    }
}

/**
 * 获取服务实现的地址列表。
 *
 * @param ctx 表示服务执行的上下文信息。
 * @param fitable 表示待获取地址的服务实现。
 * @param result 表示服务实现的地址列表。
 * @return 表示执行结果，若为 <code>FIT_OK</code>，则执行成功；否则执行失败。
 */
FitCode GetFitableAddresses(ContextObj ctx,
    const ::fit::hakuna::kernel::shared::Fitable* fitableInfo,
    ::fit::hakuna::kernel::registry::shared::FitableInstance** result)
{
    *result = gRegistryListener->GetAddresses(ctx, *fitableInfo);
    return FIT_OK;
}
} // namespace Listener
} // namespace Registry
} // namespace Fit

namespace {
FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::Fit::Registry::Listener::MarkFitableAddressStatus)
        .SetGenericId(fit::hakuna::kernel::registry::listener::markFitableAddressStatus::GENERIC_ID)
        .SetFitableId("b3181b5f4d5d467fa8f9a9eac62ad918");
    ::Fit::Framework::Annotation::Fitable(::Fit::Registry::Listener::GetFitableAddresses)
        .SetGenericId(fit::hakuna::kernel::registry::listener::getFitableAddresses::GENERIC_ID)
        .SetFitableId("921534e93cd14b6c9b635e8d7ba9cb34");

    ::Fit::Framework::PluginActivatorRegistrar()
        .SetStart(::Fit::Registry::Listener::Start)
        .SetStop(::Fit::Registry::Listener::Stop);
}
} // LCOV_EXCL_LINE