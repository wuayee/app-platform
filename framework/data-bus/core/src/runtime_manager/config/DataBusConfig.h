/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: databus service config container
 */

#ifndef DATABUS_CONFIG_H
#define DATABUS_CONFIG_H

#include <iostream>

namespace DataBus {
namespace Runtime {

struct Config {
public:
    // 禁用默认构造器和赋值语义
    Config() = delete;
    Config(const Config&) = delete;
    Config& operator=(const Config&) = delete;

    Config(int port, uint64_t memorySizeLimit, int32_t memoryTtlDuration, int32_t memorySweepInterval) : port_(port),
        memorySizeLimit_(memorySizeLimit), memoryTtlDuration_(memoryTtlDuration),
        memorySweepInterval_(memorySweepInterval) {}

    int GetPort() const
    {
        return port_;
    }

    uint64_t GetMemorySizeLimit() const
    {
        return memorySizeLimit_;
    }

    int32_t GetMemoryTtlDuration() const
    {
        return memoryTtlDuration_;
    }

    int32_t GetMemorySweepInterval() const
    {
        return memorySweepInterval_;
    }

private:
    int port_; // 服务器端口
    uint64_t memorySizeLimit_; // 内存分配上限
    int32_t memoryTtlDuration_; // 内存存活时长（毫秒）
    int32_t memorySweepInterval_; // 内存清理周期（毫秒）
};
} // namespace Runtime
} // namespace DataBus

#endif // DATABUS_CONFIG_H
