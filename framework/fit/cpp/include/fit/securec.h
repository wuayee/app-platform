/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : mock secure function
 * Author       : wangpanbo
 * Date         : 2022/08/24
 */
#ifndef FIT_SECUREC_H
#define FIT_SECUREC_H
#include <stdarg.h>
#include <cstddef>

#ifndef errno_t
typedef int errno_t;
#endif
errno_t memcpy_s(void *dest, size_t destMax, const void *src, size_t count);
errno_t memset_s(void *dest, size_t destMax, int c, size_t count);
int vsnprintf_s(char *strDest, size_t destMax, size_t count, const char *format, va_list argList);
#endif