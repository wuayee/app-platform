/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: config parser for databus core
 */
#include "ConfigParser.h"

#include "spdlog/sinks/stdout_color_sinks.h"
#include "spdlog/sinks/hourly_file_sink.h"

#include "log/Logger.h"

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

void DataBus::Runtime::ConfigParser::Parse(const std::string& configFilePath)
{
    // unimplemented! do real parsing on configFilePath.
    (void) configFilePath;
    Parse();
}
