/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/5/27
 * Notes:       :
 */

#ifndef FIT_RUNTIME_H
#define FIT_RUNTIME_H

#include <stdint.h>
#include <fit/fit_code.h>

#ifdef  __cplusplus
extern "C" {
#endif
/**
 * 传入配置文件方式启动引擎
 * @param runtimeConfigFile
 * @return FIT_OK成功，其它失败
 */
int32_t FitRuntimeStart(const char* runtimeConfigFile);
/**
 * 传入配置文件方式启动引擎, 同时附加启动参数，如果配置和附加参数包含相同配置时，附加启动参数会覆盖配置文件中的配置
 * @param runtimeConfigFile 配置文件
 * @param commandLine
 *        --name1 value1 --name2 value2 --name3 value3 --a.b.c cValue
 *        such as:
 *          bool: --XXX true/false
 *          int: --XXX 12345
 *          double: --XXX 12345.2
 *          string: --XXX "str"
 * @return
 */
int32_t FitRuntimeStartWithOption(const char* runtimeConfigFile, const char *option);
/**
 * 使用启动程序的命令行参数启动
 * @param argc
 * @param argv
 *        --name1 value1 --name2 value2 --name3 value3 --a.b.c cValue
 *        such as:
 *          bool: --XXX true/false
 *          int: --XXX 12345
 *          double: --XXX 12345.2
 *          string: --XXX "str"
 *        配置文件(必须)：--config_file XXX
 * @return FIT_OK成功，其它失败
 */
int32_t FitRuntimeStartWithCommandLine(int32_t argc, char *argv[]);
int32_t FitRuntimeStop(void);

#ifdef  __cplusplus
}
#endif
#endif // FITRUNTIME_H
