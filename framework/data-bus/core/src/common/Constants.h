/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: DataBus common constants
 */

#ifndef DATABUS_CONSTANTS_H
#define DATABUS_CONSTANTS_H

#include <cstdint>

namespace DataBus {
namespace Common {

// DataBus 默认配置。
constexpr int DEFAULT_PORT = 5284;
constexpr uint64_t MB = 1024 * 1024;
constexpr uint64_t GB = 1024 * MB;
constexpr uint64_t DEFAULT_MALLOC_SIZE_LIMIT = 40 * GB;
constexpr int32_t MINUTE = 60 * 1000;
constexpr int32_t DEFAULT_MEMORY_TTL_DURATION = 30 * MINUTE;
constexpr int32_t DEFAULT_MEMORY_SWEEP_INTERVAL = MINUTE;


// 配置文件中配置项的键。
const std::string SERVER_KEY = "server";
const std::string MEMORY_KEY = "memory";
const std::string PORT_KEY = "port";
const std::string SIZE_LIMIT_KEY = "sizeLimit";
const std::string TTL_DURATION_KEY = "ttlDuration";
const std::string SWEEP_INTERVAL_KEY = "sweepInterval";
}  // namespace Common
} // namespace DataBus

#endif // DATABUS_CONSTANTS_H
