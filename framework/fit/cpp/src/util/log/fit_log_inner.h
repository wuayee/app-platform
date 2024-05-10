/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/4 16:41
 * Notes:       :
 */

#ifndef FIT_LOG_INNER_H
#define FIT_LOG_INNER_H

#include <cstdint>
#include <fit/fit_log.h>

#ifdef  __cplusplus
extern "C" {
#endif

int FitLogGetEnableLogLevel(void);
int FitLogSetEnableLogLevel(int logLevel);

int32_t FitLogInner(const FitLogInfo* logInfo);

#ifdef  __cplusplus
}
#endif

#endif // FIT_LOG_H
