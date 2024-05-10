/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/18 11:39
 */

#ifndef FIT_DEFINE_C_H
#define FIT_DEFINE_C_H

#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    uint32_t size;
    char *data;
}Fit_String;
typedef Fit_String _Fit_String;
inline void Fit_StringInit(Fit_String& v)
{
    v.size = 0;
    v.data = NULL;
}
#define FIT_STRING_INIT(str) (str).size = 0; (str).data = NULL
#define FIT_STRING_DATA(str) (str).data
#define FIT_STRING_SIZE(str) (str).size

typedef struct {
    uint32_t size;
    char *data;
}Fit_Bytes;
typedef Fit_Bytes _Fit_Bytes;

#define FIT_BYTES_INIT(bytes) (bytes).size = 0; (bytes).data = NULL
#define FIT_BYTES_DATA(bytes) str.data
#define FIT_BYTES_SIZE(bytes) str.size

#define FIT_ARRAY_TYPE(type) _Fit_Array_##type
#define FIT_ARRAY_DECLARE(type) \
typedef struct { \
    uint32_t size; \
    type* data; \
}FIT_ARRAY_TYPE(type)
#define FIT_ARRAY_INIT(arr) (arr).size = 0; (arr).data = NULL
#define FIT_ARRAY_SIZE(arr) (arr).size
#define FIT_ARRAY_SIZE_P(arr) (arr)->size
#define FIT_ARRAY_ITEM(arr, index) (arr).data[index]
#define FIT_ARRAY_ITEM_P(arr, index) (arr)->data[index]

FIT_ARRAY_DECLARE(bool);
FIT_ARRAY_DECLARE(int32_t);
FIT_ARRAY_DECLARE(uint32_t);
FIT_ARRAY_DECLARE(int64_t);
FIT_ARRAY_DECLARE(uint64_t);
FIT_ARRAY_DECLARE(float);
FIT_ARRAY_DECLARE(double);
FIT_ARRAY_DECLARE(Fit_String);
FIT_ARRAY_DECLARE(Fit_Bytes);
FIT_ARRAY_DECLARE(_Fit_String);
FIT_ARRAY_DECLARE(_Fit_Bytes);
#ifdef __cplusplus
}
#endif

#endif // FITDEFINEC_H
