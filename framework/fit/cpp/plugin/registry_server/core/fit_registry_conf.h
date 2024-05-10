/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/1/31 15:45
 * Notes:       :
 */

#ifndef FIT_REGISTRY_CONF_H
#define FIT_REGISTRY_CONF_H

#include <cstdint>

#include <fit/fit_code.h>
#include <fit/external/plugin/plugin_config.hpp>

namespace Fit {
namespace Registry {
FitCode InitConfig(Fit::Plugin::PluginConfigPtr pluginConfig);
uint32_t GetProcessWorkerStatusDelayMs();
uint32_t GetThreadWaitInternalMs();
uint32_t GetThreadIdleWaitInternalMs();
void SetMaster(bool master);
bool IsMaster();
uint32_t GetRegistryFitableMemoryToDbConsumerThreadNum();
void SetRegistryFitableNodeSyncThreadNum(uint32_t threadNum);
uint32_t GetRegistryFitableNodeSyncThreadNum();
uint32_t GetRegistrySubscriptionMemoryToDbConsumerThreadNum();
uint32_t GetRegistrySubscriptionNodeSyncThreadNum();
uint32_t GetMaxFitableNodeSyncTaskNum();
uint32_t GetMaxSubscriptionNodeSyncTaskNum();
uint32_t GetFitableDefaultExpiredTimeSeconds();
}
}
#endif // FIT_REGISTRY_CONF_H
