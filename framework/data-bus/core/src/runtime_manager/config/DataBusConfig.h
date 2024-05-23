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

    Config(int port, uint64_t memorySizeLimit) : port_(port), memorySizeLimit_(memorySizeLimit) {}

    int GetPort() const
    {
        return port_;
    }

    uint64_t GetMemorySizeLimit() const
    {
        return memorySizeLimit_;
    }

private:
    int port_; // 服务器端口
    uint64_t memorySizeLimit_; // 内存分配上限
};
} // namespace Runtime
} // namespace DataBus

#endif // DATABUS_CONFIG_H
