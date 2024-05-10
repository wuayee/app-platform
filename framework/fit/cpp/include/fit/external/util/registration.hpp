/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/10
 * Notes:       :
 */

#ifndef REGISTRATION_HPP
#define REGISTRATION_HPP

#define FIT_NAME_CAT_(name, line) name##line
#define FIT_NAME_CAT(name, line) FIT_NAME_CAT_(name, line)
#define FIT_REGISTRATIONS \
static void FIT_NAME_CAT(_fitable_registrations_init_func_, __LINE__)(); \
namespace { \
struct FIT_NAME_CAT(for_init_, __LINE__) { \
        FIT_NAME_CAT(for_init_, __LINE__)() { FIT_NAME_CAT(_fitable_registrations_init_func_, __LINE__)(); } \
    } FIT_NAME_CAT(instance_, __LINE__); \
} \
static void FIT_NAME_CAT(_fitable_registrations_init_func_, __LINE__)()

#endif // REGISTRATION_HPP
