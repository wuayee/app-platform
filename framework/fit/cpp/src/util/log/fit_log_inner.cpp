/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: Zhongbin Yu 00286766
 * Date: 2020-04-01 11:02:39
 */

#include "fit_log_inner.h"
#include <fit/fit_code.h>
#include <iostream>
#include <libgen.h>

#include <cerrno>
#include <cstdio>
#include <memory>
#include <string>
#include <thread>
#include <iomanip>
#include <sstream>
#include <cstring>
#include <mutex>
#include <list>
#include <chrono>
#include <ctime>
#include <dirent.h>
#include <map>
#include <sys/stat.h>
#include <fit/external/framework/plugin_activator.hpp>
#include <fit/external/util/context/context_base.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/external/framework/formatter/json_converter.hpp>
#include "fit_log.h"

static FitLogOutputType g_fitLogOutput =
    static_cast<FitLogOutputType>(FitLogOutputType::stdio | FitLogOutputType::file);

namespace {
constexpr uint32_t MAX_LOG_SIZE = 10 * 1024 * 1024; // 10M
constexpr uint32_t MAX_LOG_NUM = 20;
constexpr uint32_t MAX_PATH = 1024;
constexpr uint32_t MICRO_SECONDS_PER_SECOND = 1000000;
constexpr uint32_t MICRO_SECONDS_PER_MILLION_SECOND = 1000;
constexpr uint32_t MILLION_SECONDS_PER_SECOND = 1000;

std::string GetCurrentTimeStringWithMicroSeconds();
int32_t& DefaultEnableLogLevel()
{
    static int32_t instance = FIT_LOG_LEVEL_DEBUG;
    return instance;
}
const char* GetLogLevelName(int level);

const char* GetGeneralColorCode(int level)
{
    static const char* colorCodeSet[] = {"", "", "\033[91;1;4m", "\033[93;1;4m", "\033[32m", "\033[32m"};
    return colorCodeSet[level];
}
std::list<std::string> GetExistLogFiles(const char *dir, const char* targetName);

class Logger {
public:
    virtual ~Logger() = default;
    virtual void Print(const char *time, size_t threadHashCode, const FitLogInfo& logInfo) = 0;
    virtual void Flush() = 0;
};
class FileLogger : public Logger {
public:
    FileLogger(const char* dir, const char* fileName, uint32_t maxBakNum, uint32_t maxFileSize, bool flushNow)
        : dir_(dir), fileName_(fileName), maxBakNum_(maxBakNum), maxFileSize_(maxFileSize), flushNow_(flushNow)
    {
        existFiles_ = GetExistLogFiles(dir, fileName);
    }
    ~FileLogger() override
    {
        std::lock_guard<std::mutex> guard(logMt_);
        if (currentFileFd_) { // LCOV_EXCL_LINE
            fclose(currentFileFd_);
        }
    }
    void Print(const char* time, size_t threadHashCode, const FitLogInfo& logInfo) override
    {
        std::lock_guard<std::mutex> guard(logMt_);
        if (!UpdateLogFile()) { // LCOV_EXCL_LINE
            return;
        }
        const char* formatStr = "[%s][%016zx][%5s][%5d][%3ld][%s:%d][%s]: %s\n";
        auto appendSize = fprintf(currentFileFd_, formatStr, time, threadHashCode, GetLogLevelName(logInfo.logLevel),
            logInfo.modId, logInfo.logId, logInfo.fileName, logInfo.fLine, logInfo.funcName, logInfo.message);
        if (flushNow_) { // LCOV_EXCL_LINE
            fflush(currentFileFd_);
        }
        TryRollFile(appendSize); // LCOV_EXCL_LINE
    }
    void Flush() override
    {
        std::lock_guard<std::mutex> guard(logMt_);
        fflush(currentFileFd_);
    }
    void SetOutputPosition(const char* dir, const char* fileName)
    {
        std::lock_guard<std::mutex> guard(logMt_);
        if (dir_ != dir || fileName_ != fileName) { // LCOV_EXCL_LINE
            dir_ = dir;
            fileName_ = fileName;
            if (currentFileFd_ != nullptr) { // LCOV_EXCL_LINE
                fclose(currentFileFd_);
                currentFileFd_ = nullptr;
            }
            existFiles_ = GetExistLogFiles(dir, fileName);
        }
    }
    void SetMaxBakNum(uint32_t val)
    {
        maxBakNum_ = val;
    }
    void SetMaxFileSize(uint32_t val)
    {
        maxFileSize_ = val;
    }
    void SetFlushNow(bool val)
    {
        flushNow_ = val;
    }

protected:
    bool UpdateLogFile()
    {
        if (currentFileFd_ == nullptr) {
            currentFilePath_ = dir_ + "/" + fileName_ + ".log";
            currentFileFd_ = fopen(currentFilePath_.c_str(), "a+");
            if (currentFileFd_ == nullptr) { // LCOV_EXCL_LINE
                std::cout << "Can not open file." << currentFilePath_;
                return false;
            }
            struct stat statbuf {};
            stat(currentFilePath_.c_str(), &statbuf);
            currentFileSize_ = static_cast<uint32_t>(statbuf.st_size);
        }
        return true;
    }
    void TryRollFile(int32_t appendSize)
    {
        if (appendSize > 0) { // LCOV_EXCL_LINE
            currentFileSize_ += static_cast<uint32_t>(appendSize);
            if (currentFileSize_ >= maxFileSize_) { // LCOV_EXCL_LINE
                fclose(currentFileFd_);
                std::ostringstream newFileName;
                newFileName << dir_ << "/" <<fileName_ << "_" << std::to_string(time(nullptr)) << ".log";
                rename(currentFilePath_.c_str(), newFileName.str().c_str());
                existFiles_.push_back(newFileName.str());
                while (existFiles_.size() > maxBakNum_) { // LCOV_EXCL_LINE
                    remove(existFiles_.front().c_str());
                    existFiles_.pop_front();
                }

                currentFileSize_ = 0;
                currentFileFd_ = nullptr;
            }
        }
    }

private:
    std::string dir_ {};
    std::string fileName_ {};
    uint32_t maxBakNum_ {1};
    uint32_t maxFileSize_ {};
    std::mutex logMt_ {};
    FILE* currentFileFd_ {};
    std::string currentFilePath_ {};
    uint32_t currentFileSize_ {};
    bool flushNow_ {false};
    std::list<std::string> existFiles_ {};
};

void FormatStdoutOutput(const char *time, size_t threadHashCode, const FitLogInfo& logInfo);

FileLogger& GetFileLogger()
{
    static FileLogger* instance = new FileLogger(".",
        program_invocation_short_name, MAX_LOG_NUM, MAX_LOG_SIZE, true);
    return *instance;
}
void LogToFile(const char *time, size_t threadHashCode, const FitLogInfo& logInfo)
{
    GetFileLogger().Print(time, threadHashCode, logInfo);
}
}

void FitLogSetOutput(FitLogOutputType output)
{
    g_fitLogOutput = output;
}
int FitLogSetEnableLogLevel(int logLevel)
{
    auto old = DefaultEnableLogLevel();
    if (old != logLevel) {
        DefaultEnableLogLevel() = logLevel;
        FIT_LOG_INFO("Log level changed, %d -> %d.", old, logLevel);
    }

    return old;
}

int FitLogGetEnableLogLevel()
{
    return DefaultEnableLogLevel();
}
int32_t FitLogInner(const FitLogInfo* logInfo)
{
    auto timeStr = GetCurrentTimeStringWithMicroSeconds();
    auto threadHashCode = std::hash<std::thread::id>()(std::this_thread::get_id());
    auto baseFilename = basename(const_cast<char*>(logInfo->fileName));

    FitLogInfo processedLogInfo = *logInfo;
    processedLogInfo.fileName = baseFilename;
    if (g_fitLogOutput & FitLogOutputType::stdio) {
        FormatStdoutOutput(timeStr.c_str(), threadHashCode, processedLogInfo);
    }
    if (g_fitLogOutput & FitLogOutputType::file) {
        LogToFile(timeStr.c_str(), threadHashCode, processedLogInfo);
    }

    return FIT_ERR_SUCCESS;
}
void FitLogFlush()
{
    GetFileLogger().Flush();
}

namespace {
std::string GetCurrentTimeStringWithMicroSeconds()
{
    auto timePointNow = std::chrono::system_clock::now();
    auto durationSinceEpoch = timePointNow.time_since_epoch();
    time_t microsecondsSinceEpoch = std::chrono::duration_cast<std::chrono::microseconds>(durationSinceEpoch).count();
    time_t secondsSinceEpoch = microsecondsSinceEpoch / MICRO_SECONDS_PER_SECOND;
    tm nowTm {};
    localtime_r(&secondsSinceEpoch, &nowTm);
    std::ostringstream timeStream;
    char tempBuffer[64] = {0};
    strftime(tempBuffer, sizeof(tempBuffer), "%F %T", &nowTm);
    constexpr int32_t timeWidth = 3;
    timeStream <<
        tempBuffer <<
        "." <<
        std::setw(timeWidth) <<
        std::setfill('0') <<
        ((microsecondsSinceEpoch / MICRO_SECONDS_PER_MILLION_SECOND) % MILLION_SECONDS_PER_SECOND) <<
        "." <<
        std::setw(timeWidth) << std::setfill('0') <<
        microsecondsSinceEpoch % MICRO_SECONDS_PER_MILLION_SECOND;

    return timeStream.str();
}
int32_t GetLogLevelByName(const char *name, int32_t defaultLevel)
{
    const std::map<std::string, int32_t> levelMapping = {
        {"core",  FIT_LOG_LEVEL_CORE},
        {"fatal", FIT_LOG_LEVEL_FATAL},
        {"error", FIT_LOG_LEVEL_ERROR},
        {"warn",  FIT_LOG_LEVEL_WARN},
        {"info",  FIT_LOG_LEVEL_INFO},
        {"debug", FIT_LOG_LEVEL_DEBUG},
    };

    auto mappingIter = levelMapping.find(name);
    if (mappingIter != levelMapping.end()) {
        return mappingIter->second;
    }

    return defaultLevel;
}
const char* GetLogLevelName(int level)
{
    static const char* name[] = {"CORE", "FATAL", "ERROR", "WARN", "INFO", "DEBUG"};
    if (level < 0 || static_cast<unsigned long>(level) >= sizeof(name) / sizeof(const char *)) {
        return "";
    }
    return name[level];
}

std::list<std::string> GetExistLogFiles(const char *dir, const char* targetName)
{
    std::string logFileHeader = std::string(targetName) + "_";
    std::list<std::string> result;
    struct dirent *dp {};
    DIR *dfd;

    if ((dfd = opendir(dir)) == nullptr) {
        return result;
    }
    while ((dp = readdir(dfd)) != nullptr) {
        if (dp->d_type != DT_REG) {
            continue;
        }
        std::string fileName {dp->d_name};
        if (fileName.find(logFileHeader) != 0) {
            continue;
        }
        if (fileName.rfind(".log") != fileName.size() - strlen(".log")) {
            continue;
        }
        result.push_back(std::string(dir) + "/" + fileName);
    }
    closedir(dfd);

    return result;
}
void FormatStdoutOutput(const char *time, size_t threadHashCode, const FitLogInfo& logInfo)
{
    auto generalColorCode = GetGeneralColorCode(logInfo.logLevel);
    auto bufColorCode = generalColorCode;

    (void)fprintf(
        stdout,
        "%s[%s][%016zx][%5s][%5d][%3ld] [%s:%d] [%s]: %s%s\033[0m\n",
        generalColorCode,
        time,
        threadHashCode,
        GetLogLevelName(logInfo.logLevel),
        logInfo.modId,
        logInfo.logId,
        logInfo.fileName,
        logInfo.fLine,
        logInfo.funcName,
        bufColorCode,
        logInfo.message);
}

FitCode Start(::Fit::Framework::PluginContext *context)
{
    FitLogSetEnableLogLevel(GetLogLevelByName(context->GetConfig()->Get("log.level").AsString("info").c_str(),
        FIT_LOG_LEVEL_INFO));
    FitLogSetOutput(static_cast<FitLogOutputType>(
        (context->GetConfig()->Get("log.stdout").AsBool(false) ? FitLogOutputType::stdio : 0) |
            (context->GetConfig()->Get("log.file").AsBool(true) ? FitLogOutputType::file : 0)));
    FIT_LOG_INFO("Inner logger is started. [level=%s]", GetLogLevelName(FitLogGetEnableLogLevel()));
    GetFileLogger().SetOutputPosition(context->GetConfig()->Get("log.dir").AsString(".").c_str(),
        context->GetConfig()->Get("log.filename").AsString(program_invocation_short_name).c_str());
    GetFileLogger().SetMaxBakNum(context->GetConfig()->Get("log.max-file-num").AsInt(MAX_LOG_NUM));
    GetFileLogger().SetMaxFileSize(context->GetConfig()->Get("log.max-file-size").AsInt(MAX_LOG_SIZE));
    GetFileLogger().SetFlushNow(context->GetConfig()->Get("log.flush-now").AsBool(true));

    return FIT_OK;
}

FitCode SetLogLevelImpl(ContextObj ctx, const Fit::string* level)
{
    if (level == nullptr) {
        return FIT_ERR_PARAM;
    }
    FitLogSetEnableLogLevel(GetLogLevelByName(level->c_str(), FIT_LOG_LEVEL_INFO));
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::PluginActivatorRegistrar()
        .SetStart(Start);
    Fit::Framework::Annotation::Fitable(SetLogLevelImpl)
        .SetGenericId("0c4433e27ab6461db072d45975a70938")
        .SetFitableId("setLogLevel");
    Fit::Framework::Formatter::FormatterPluginCollector::Register(
        {Fit::Framework::Formatter::FormatterMetaBuilder<Fit::Framework::Formatter::PROTOCOL_TYPE_JSON,
            Fit::Framework::ArgumentsIn<const Fit::string*>,
            Fit::Framework::ArgumentsOut<void>>::Build("0c4433e27ab6461db072d45975a70938",
            Fit::Framework::Annotation::FitableType::MAIN)});
}
} // LCOV_EXCL_LINE