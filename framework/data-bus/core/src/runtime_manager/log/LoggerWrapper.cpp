/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: log provider for databus core
 */

#include "log/LoggerWrapper.h"

#include <utility>

using namespace DataBus::Runtime;

LoggerWrapper::~LoggerWrapper()
{
    if (logger_ != nullptr) {
        logger_.reset();
    }
}

LoggerWrapper& LoggerWrapper::GetInstance()
{
    static LoggerWrapper l;
    return l;
}

void LoggerWrapper::SetLogHandler(std::shared_ptr<LoggerImpl> logger)
{
    logger_ = std::move(logger);
}
