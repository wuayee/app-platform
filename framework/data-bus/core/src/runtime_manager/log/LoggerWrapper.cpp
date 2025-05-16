/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
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
