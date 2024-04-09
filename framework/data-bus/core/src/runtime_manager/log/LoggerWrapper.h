/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: log provider for databus core
 */
#ifndef DATABUS_LOGGER_WRAPPER_H
#define DATABUS_LOGGER_WRAPPER_H

#include <functional>

#include "spdlog/spdlog.h"

namespace DataBus {
namespace Runtime {
class LoggerWrapper final {
public:
    using LoggerImpl = spdlog::logger;

    LoggerWrapper(LoggerWrapper&) = delete;
    LoggerWrapper(LoggerWrapper&&) = delete;

    LoggerWrapper& operator=(LoggerWrapper&) = delete;
    LoggerWrapper& operator=(LoggerWrapper&&) = delete;

    static LoggerWrapper& Instance();

    void SetLogHandler(std::shared_ptr<LoggerImpl> logger);

    template<typename... Args>
    inline void Trace(spdlog::format_string_t<Args...> logString, Args&&... args)
    {
        logger_->trace(std::forward<decltype(logString)>(logString), std::forward<Args>(args)...);
    }

    template<typename... Args>
    inline void Debug(spdlog::format_string_t<Args...> logString, Args&&... args)
    {
        logger_->debug(std::forward<decltype(logString)>(logString), std::forward<Args>(args)...);
    }

    template<typename... Args>
    inline void Info(spdlog::format_string_t<Args...> logString, Args&&... args)
    {
        logger_->info(std::forward<decltype(logString)>(logString), std::forward<Args>(args)...);
    }

    template<typename... Args>
    inline void Warn(spdlog::format_string_t<Args...> logString, Args&&... args)
    {
        logger_->warn(std::forward<decltype(logString)>(logString), std::forward<Args>(args)...);
    }

    template<typename... Args>
    inline void Error(spdlog::format_string_t<Args...> logString, Args&&... args)
    {
        logger_->error(std::forward<decltype(logString)>(logString), std::forward<Args>(args)...);
    }

    template<typename... Args>
    inline void Critical(spdlog::format_string_t<Args...> logString, Args&&... args)
    {
        logger_->critical(std::forward<decltype(logString)>(logString), std::forward<Args>(args)...);
    }

private:
    LoggerWrapper();
    ~LoggerWrapper();

    std::shared_ptr<LoggerImpl> logger_{nullptr};
};
}  // namespace Runtime
}  // namespace DataBus

#endif  // DATABUS_LOGGER_WRAPPER_H
