/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: config parser for databus core
 */
#include "ConfigParser.h"

#include "spdlog/sinks/stdout_color_sinks.h"

#include "log/Logger.h"

void DataBus::Runtime::ConfigParser::Parse()
{
    // may later include "spdlog/sinks/basic_file_sink.h"
    // and use `spdlog::basic_logger_mt("databus", "databus-log.txt")`
    // _mt stands for multi-threaded
    DataBus::logger.SetLogHandler(spdlog::stdout_color_mt("console"));
}

void DataBus::Runtime::ConfigParser::Parse(const std::string& configFilePath)
{
    // unimplemented! do real parsing on configFilePath.
    (void) configFilePath;
    Parse();
}
