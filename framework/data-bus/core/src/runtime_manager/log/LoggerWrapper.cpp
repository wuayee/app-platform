/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: log provider for databus core
 */

#include "log/LoggerWrapper.h"

#include <utility>

#include "spdlog/sinks/stdout_color_sinks.h"

using namespace DataBus::Runtime;

LoggerWrapper::LoggerWrapper() : logger_(spdlog::stdout_color_mt("default")) {}

LoggerWrapper::~LoggerWrapper()
{
    if (logger_ != nullptr) {
        logger_.reset();
    }
}

LoggerWrapper& LoggerWrapper::Instance()
{
    static LoggerWrapper l;
    return l;
}

void LoggerWrapper::SetLogHandler(std::shared_ptr<LoggerImpl> logger)
{
    logger_ = std::move(logger);
}
