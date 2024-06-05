/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: databus service config container
 */

#ifndef DATABUS_CONFIG_H
#define DATABUS_CONFIG_H

#include <iostream>
#include <Constants.h>

namespace DataBus {
namespace Runtime {

struct Config {
public:
    Config() : port(DataBus::Common::DEFAULT_PORT), mallocSizeLimit(DataBus::Common::DEFAULT_MALLOC_SIZE_LIMIT),
        memoryTtlDuration(DataBus::Common::DEFAULT_MEMORY_TTL_DURATION),
        memorySweepInterval(DataBus::Common::DEFAULT_MEMORY_SWEEP_INTERVAL) {}

    Config(int port, uint64_t mallocSizeLimit, int32_t memoryTtlDuration, int32_t memorySweepInterval) : port(port),
        mallocSizeLimit(mallocSizeLimit), memoryTtlDuration(memoryTtlDuration),
        memorySweepInterval(memorySweepInterval) {}

    void SetPort(const int serverPort)
    {
        port = serverPort;
    }

    void SetMallocSizeLimit(const uint64_t sizeLimit)
    {
        mallocSizeLimit = sizeLimit;
    }

    void SetMemoryTtlDuration(const int32_t ttlDuration)
    {
        memoryTtlDuration = ttlDuration;
    }

    void SetMemorySweepInterval(const int32_t sweepInterval)
    {
        memorySweepInterval = sweepInterval;
    }

    int GetPort() const
    {
        return port;
    }

    uint64_t GetMallocSizeLimit() const
    {
        return mallocSizeLimit;
    }

    int32_t GetMemoryTtlDuration() const
    {
        return memoryTtlDuration;
    }

    int32_t GetMemorySweepInterval() const
    {
        return memorySweepInterval;
    }

private:
    int port; // 服务器端口
    uint64_t mallocSizeLimit; // 内存分配上限
    int32_t memoryTtlDuration; // 内存存活时长（毫秒）
    int32_t memorySweepInterval; // 内存清理周期（毫秒）
};
} // namespace Runtime
} // namespace DataBus

#endif // DATABUS_CONFIG_H