/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit define
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#ifndef FIT_DEFINE_H
#define FIT_DEFINE_H

#define FIT_LOCAL_API __attribute__((visibility("hidden")))
#define FIT_PUBLIC_API __attribute__((visibility("default")))

#define FIT_DISABLE_MOVE_AND_COPY_CONSTRUCTOR(CLASS)                                                                   \
    CLASS(CLASS&&) = delete;                                                                                           \
    CLASS(const CLASS&) = delete;                                                                                      \
    CLASS& operator=(CLASS&&) = delete;                                                                                \
    CLASS& operator=(const CLASS&) = delete

#endif