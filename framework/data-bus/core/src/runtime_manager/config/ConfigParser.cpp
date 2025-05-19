/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: config parser for databus core
 */

#include <fstream>

#include "Constants.h"
#include "log/Logger.h"
#include "ConfigParser.h"

#include "nlohmann/json.hpp"
#include "spdlog/sinks/stdout_color_sinks.h"
#include "spdlog/sinks/basic_file_sink.h"

using namespace DataBus::Common;

void DataBus::Runtime::ConfigParser::Parse()
{
    const std::string logFilePath = "/log/app/databus.log";
    const std::initializer_list<spdlog::sink_ptr> sinks = {
        // 按小时分的日志文件
        // _mt stands for multithread
        std::make_shared<spdlog::sinks::basic_file_sink_mt>(logFilePath, false),
        // 标准输入输出
        std::make_shared<spdlog::sinks::stdout_color_sink_mt>()
    };

    auto logFileHandler = std::make_shared<spdlog::logger>("log", sinks);
    DataBus::logger.SetLogHandler(logFileHandler);

    // 每次写入刷新一次日志
    logFileHandler->flush_on(spdlog::level::info);
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
