/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: config parser for databus core
 */

#include "fstream"

#include "Constants.h"
#include "log/Logger.h"
#include "ConfigParser.h"

#include "nlohmann/json.hpp"
#include "spdlog/sinks/stdout_color_sinks.h"
#include "spdlog/sinks/hourly_file_sink.h"

using namespace DataBus::Common;

void DataBus::Runtime::ConfigParser::Parse()
{
    const std::string logFilePath = "/var/log/databus.log";
    constexpr uint16_t maxLogFiles = 24 * 7;  // 暂时先保存一周的
    const std::initializer_list<spdlog::sink_ptr> sinks = {
        // 按小时分的日志文件
        std::make_shared<spdlog::sinks::hourly_file_sink_mt>(logFilePath, false, maxLogFiles),
        // 标准输入输出
        std::make_shared<spdlog::sinks::stdout_color_sink_mt>()
    };

    // _mt stands for multi-threaded
    DataBus::logger.SetLogHandler(std::make_shared<spdlog::logger>("log", sinks));
}

DataBus::Runtime::Config DataBus::Runtime::ConfigParser::Parse(const std::string& configFilePath)
{
    std::ifstream configFile(configFilePath);
    if (!configFile.good()) {
        logger.Warn("Cannot access the config file {}, using the default values", configFilePath);
        return {};
    }
    nlohmann::json jsonConfig;
    configFile >> jsonConfig;

    // 解析JSON配置。
    const int port = jsonConfig[SERVER_KEY][PORT_KEY];
    const uint64_t mallocSizeLimit = jsonConfig[MEMORY_KEY][SIZE_LIMIT_KEY];
    const int32_t memoryTtlDuration = jsonConfig[MEMORY_KEY][TTL_DURATION_KEY];
    const int32_t memorySweepInterval = jsonConfig[MEMORY_KEY][SWEEP_INTERVAL_KEY];

    return {port, mallocSizeLimit, memoryTtlDuration, memorySweepInterval};
}
