/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: Zhongbin Yu 00286766
 * Date: 2020-04-01 11:02:39
 */

#ifndef FIT_LOG_H
#define FIT_LOG_H

#include <stdint.h>

#ifdef  __cplusplus
extern "C" {
#endif

#define LOG_BUFFER_MAX 1024
#define PID_FIT_FRAMEWORK 860

#define FIT_LOG_LEVEL_CORE 0 // 核心关键必须打印的日志，比如用于指示系统启动正常等状态的日志
#define FIT_LOG_LEVEL_FATAL 1 // 致命错误，系统无法正常运行
#define FIT_LOG_LEVEL_ERROR 2 // 产生了影响的路径被执行，该次的调用可能会失败
#define FIT_LOG_LEVEL_WARN  3 // 非期待的运行路径被执行，但是对系统无影响
#define FIT_LOG_LEVEL_INFO  4 // 默认运行期级别，关键路径信息打印；比如收到fitable调用请求，某个复杂处理开始和完成时打印提示
#define FIT_LOG_LEVEL_DEBUG 5 // 辅助性数据,方便问题排查定位，比如函数参数具体信息

typedef enum {
    stdio = 1,
    file = 2
} FitLogOutputType;

void FitLogSetOutput(FitLogOutputType output);
void FitLogFlush(void);

typedef struct {
    int32_t modId;
    int32_t logLevel;
    const char* fileName;
    int32_t fLine;
    const char* funcName;
    long logId;
    const char* message;
} FitLogInfo;

typedef int32_t (*FitLogPrintFunc)(const FitLogInfo* logInfo);
typedef bool (*FitLogIsEnabledFunc)(const FitLogInfo* logInfo);

typedef struct {
    FitLogPrintFunc printFunc;
    FitLogIsEnabledFunc isEnabledFunc;
} FitLogCallbackInfo;
void FitLogSetCallback(FitLogCallbackInfo cb);

int32_t FitLog(int32_t modId, int logLevel, const char *fileName,
    int fLine, const char *funcName, long logId, const char *format, ...)
    __attribute__((format(printf, 7, 8)));

#define FitLogLocal(moduleId, siLevel, ...) \
        FitLog(moduleId, siLevel, __FILE__, __LINE__, __func__, 0, ##__VA_ARGS__)

#define FIT_LOG_CORE_INNER(MODULE_ID, ...) FitLogLocal(MODULE_ID, FIT_LOG_LEVEL_CORE, ##__VA_ARGS__)
#define FIT_LOG_FATAL_INNER(MODULE_ID, ...) FitLogLocal(MODULE_ID, FIT_LOG_LEVEL_FATAL, ##__VA_ARGS__)
#define FIT_LOG_ERROR_INNER(MODULE_ID, ...) FitLogLocal(MODULE_ID, FIT_LOG_LEVEL_ERROR, ##__VA_ARGS__)
#define FIT_LOG_WARN_INNER(MODULE_ID, ...)  FitLogLocal(MODULE_ID, FIT_LOG_LEVEL_WARN, ##__VA_ARGS__)
#define FIT_LOG_INFO_INNER(MODULE_ID, ...)  FitLogLocal(MODULE_ID, FIT_LOG_LEVEL_INFO, ##__VA_ARGS__)
#define FIT_LOG_DEBUG_INNER(MODULE_ID, ...) FitLogLocal(MODULE_ID, FIT_LOG_LEVEL_DEBUG, ##__VA_ARGS__)

/**********************************************对外接口**************************************************/
#define FIT_LOG_CORE(...)          FIT_LOG_CORE_INNER(PID_FIT_FRAMEWORK, ##__VA_ARGS__)
#define FIT_LOG_FATAL(...)          FIT_LOG_FATAL_INNER(PID_FIT_FRAMEWORK, ##__VA_ARGS__)
#define FIT_LOG_ERROR(...)          FIT_LOG_ERROR_INNER(PID_FIT_FRAMEWORK, ##__VA_ARGS__)
#define FIT_LOG_WARN(...)           FIT_LOG_WARN_INNER(PID_FIT_FRAMEWORK, ##__VA_ARGS__)
#define FIT_LOG_INFO(...)           FIT_LOG_INFO_INNER(PID_FIT_FRAMEWORK, ##__VA_ARGS__)
#define FIT_LOG_DEBUG(...)          FIT_LOG_DEBUG_INNER(PID_FIT_FRAMEWORK, ##__VA_ARGS__)

#ifdef  __cplusplus
}
#endif

#endif
