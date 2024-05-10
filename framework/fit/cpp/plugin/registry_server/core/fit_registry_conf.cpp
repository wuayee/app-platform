/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/6/15 21:50
 * Notes        :
 */

#include "fit_registry_conf.h"

#include <mutex>

#include <fit/fit_log.h>

namespace Fit {
namespace Registry {
namespace {
const uint32_t PROCESS_WORKER_STATUS_DELAY_MS = 1000;
const uint32_t THREAD_WAIT_INTERNAL_MS = 1;
const uint32_t THREAD_IDLE_WAIT_INTERNAL_MS = 500;
constexpr const char* REGISTRY_ROLE_MASTER = "master";
const uint32_t REGISTRY_FITABLE_MEMORY_TO_DB_CONSUMER_DEFAULT_THREAD_NUM = 1;
const uint32_t REGISTRY_FITABLE_NODE_SYNC_THREAD_NUM = 2;
const uint32_t REGISTRY_SUBSCRIPTION_MEMORY_TO_DB_CONSUMER_DEFAULT_THREAD_NUM = 1;
const uint32_t REGISTRY_SUBSCRIPTION_NODE_SYNC_THREAD_NUM = 2;
const uint32_t REGISTRY_MAX_FITABLE_NODE_SYNC_TASK_NUM = 2000;
const uint32_t REGISTRY_MAX_SUBSCRIPTION_NODE_SYNC_TASK_NUM = 2000;
const uint32_t FITABLE_DEFAULT_EXPIRED_TIME_SECONDS = 90;
}

struct RegistryConfig {
    uint32_t processWorkerStatusDelayMs {PROCESS_WORKER_STATUS_DELAY_MS};
    uint32_t threadWaitInternalMs {THREAD_WAIT_INTERNAL_MS};
    uint32_t threadIdleWaitInternalMs {THREAD_IDLE_WAIT_INTERNAL_MS};
    bool isMaster {false};
    uint32_t registryFitableMemoryToDbConsumerThreadNum {REGISTRY_FITABLE_MEMORY_TO_DB_CONSUMER_DEFAULT_THREAD_NUM};
    uint32_t registryFitableNodeSyncThreadNum {REGISTRY_FITABLE_NODE_SYNC_THREAD_NUM};
    uint32_t registrySubscriptionMemoryToDbConsumerThreadNum {
        REGISTRY_SUBSCRIPTION_MEMORY_TO_DB_CONSUMER_DEFAULT_THREAD_NUM
    };
    uint32_t registrySubscriptionNodeSyncThreadNum {REGISTRY_SUBSCRIPTION_NODE_SYNC_THREAD_NUM};
    uint32_t registryMaxFitableNodeSyncTaskNum {REGISTRY_MAX_FITABLE_NODE_SYNC_TASK_NUM};
    uint32_t registryMaxSubscriptionNodeSyncTaskNum {REGISTRY_MAX_SUBSCRIPTION_NODE_SYNC_TASK_NUM};
    uint32_t fitableDefaultExpiredTimeSeconds {FITABLE_DEFAULT_EXPIRED_TIME_SECONDS};
};

static RegistryConfig &GetRegistryConfig()
{
    static RegistryConfig config;
    return config;
}

void PrintRegistryConfig()
{
    FIT_LOG_INFO("Process-worker-status-delay-ms = %d, thread-wait-internal-ms = %d, "
        "thread-idle-wait-internal-ms = %d, role(1:master) = %d,"
        "fitable-memory-to-db-consumer-thread-num = %d,"
        "fitable-node-sync-thread-num = %d,"
        "subscription-memory-to-db-consumer-thread-num = %d,"
        "subscription-node-sync-thread-num = %d,"
        "max-fitable-node-sync-task-num = %d,"
        "max-subscription-node-sync-task-num = %d,"
        "fitable-default-expired-time-seconds = %d",
        GetRegistryConfig().processWorkerStatusDelayMs, GetRegistryConfig().threadWaitInternalMs,
        GetRegistryConfig().threadIdleWaitInternalMs, int32_t(GetRegistryConfig().isMaster),
        GetRegistryConfig().registryFitableMemoryToDbConsumerThreadNum,
        GetRegistryConfig().registryFitableNodeSyncThreadNum,
        GetRegistryConfig().registrySubscriptionMemoryToDbConsumerThreadNum,
        GetRegistryConfig().registrySubscriptionNodeSyncThreadNum,
        GetRegistryConfig().registryMaxFitableNodeSyncTaskNum,
        GetRegistryConfig().registryMaxSubscriptionNodeSyncTaskNum,
        GetRegistryConfig().fitableDefaultExpiredTimeSeconds);
}
FitCode InitConfig(Fit::Plugin::PluginConfigPtr pluginConfig)
{
    GetRegistryConfig().processWorkerStatusDelayMs
        = static_cast<uint32_t>(pluginConfig->Get("registry-server.process-worker-status-delay-ms").
        AsInt(PROCESS_WORKER_STATUS_DELAY_MS));
    GetRegistryConfig().threadWaitInternalMs
        = static_cast<uint32_t>(pluginConfig->Get("registry-server.thread-wait-internal-ms").
        AsInt(THREAD_WAIT_INTERNAL_MS));
    GetRegistryConfig().threadIdleWaitInternalMs
        = static_cast<uint32_t>(pluginConfig->Get("registry-server.thread-idle-wait-internal-ms").
        AsInt(THREAD_IDLE_WAIT_INTERNAL_MS));
    Fit::string valueRegistryRole = pluginConfig->Get("registry-server.role").AsString("");
    if (!valueRegistryRole.empty()) {
        GetRegistryConfig().isMaster = (valueRegistryRole == REGISTRY_ROLE_MASTER);
    }
    GetRegistryConfig().registryFitableMemoryToDbConsumerThreadNum =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.fitable-memory-to-db-consumer-thread-num").
        AsInt(REGISTRY_FITABLE_MEMORY_TO_DB_CONSUMER_DEFAULT_THREAD_NUM));
    GetRegistryConfig().registryFitableNodeSyncThreadNum =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.fitable-node-sync-thread-num").
        AsInt(REGISTRY_FITABLE_NODE_SYNC_THREAD_NUM));

    GetRegistryConfig().registrySubscriptionMemoryToDbConsumerThreadNum =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.subscription-memory-to-db-consumer-thread-num").
        AsInt(REGISTRY_SUBSCRIPTION_MEMORY_TO_DB_CONSUMER_DEFAULT_THREAD_NUM));
    GetRegistryConfig().registrySubscriptionNodeSyncThreadNum =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.subscription-node-sync-thread-num").
        AsInt(REGISTRY_SUBSCRIPTION_NODE_SYNC_THREAD_NUM));
    GetRegistryConfig().registryMaxFitableNodeSyncTaskNum =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.max-fitable-node-sync-task-num").
        AsInt(REGISTRY_MAX_FITABLE_NODE_SYNC_TASK_NUM));
    GetRegistryConfig().registryMaxSubscriptionNodeSyncTaskNum =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.max-subscription-node-sync-task-num").
        AsInt(REGISTRY_MAX_SUBSCRIPTION_NODE_SYNC_TASK_NUM));

    GetRegistryConfig().fitableDefaultExpiredTimeSeconds =
        static_cast<uint32_t>(pluginConfig->Get("registry-server.fitable-default-expired-time-seconds").
        AsInt(FITABLE_DEFAULT_EXPIRED_TIME_SECONDS));
    PrintRegistryConfig();
    return FIT_OK;
}

uint32_t GetProcessWorkerStatusDelayMs()
{
    return GetRegistryConfig().processWorkerStatusDelayMs;
}
uint32_t GetThreadWaitInternalMs()
{
    return GetRegistryConfig().threadWaitInternalMs;
}
uint32_t GetThreadIdleWaitInternalMs()
{
    return GetRegistryConfig().threadIdleWaitInternalMs;
}
void SetMaster(bool master)
{
    GetRegistryConfig().isMaster = master;
}
bool IsMaster()
{
    return GetRegistryConfig().isMaster;
}
uint32_t GetRegistryFitableMemoryToDbConsumerThreadNum()
{
    return GetRegistryConfig().registryFitableMemoryToDbConsumerThreadNum;
}
void SetRegistryFitableNodeSyncThreadNum(uint32_t threadNum)
{
    GetRegistryConfig().registryFitableNodeSyncThreadNum = threadNum;
}
uint32_t GetRegistryFitableNodeSyncThreadNum()
{
    return GetRegistryConfig().registryFitableNodeSyncThreadNum;
}
uint32_t GetRegistrySubscriptionMemoryToDbConsumerThreadNum()
{
    return GetRegistryConfig().registrySubscriptionMemoryToDbConsumerThreadNum;
}
uint32_t GetRegistrySubscriptionNodeSyncThreadNum()
{
    return GetRegistryConfig().registrySubscriptionMemoryToDbConsumerThreadNum;
}
uint32_t GetMaxFitableNodeSyncTaskNum()
{
    return GetRegistryConfig().registryMaxFitableNodeSyncTaskNum;
}
uint32_t GetMaxSubscriptionNodeSyncTaskNum()
{
    return GetRegistryConfig().registryMaxSubscriptionNodeSyncTaskNum;
}
uint32_t GetFitableDefaultExpiredTimeSeconds()
{
    return GetRegistryConfig().fitableDefaultExpiredTimeSeconds;
}
}
}
