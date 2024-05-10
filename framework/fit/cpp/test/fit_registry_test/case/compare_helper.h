/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 基本比较的封装
 * Author       : songyongtan
 * Create       : 2022-07-11
 */

#pragma once

#include <cstdint>
#include <fit/stl/vector.hpp>
#include <gtest/gtest.h>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Endpoint/1.0.0/cplusplus/Endpoint.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/fit_log.h>


template<typename T>
inline bool Equals(const T& l, const T& r)
{
    return l == r;
}

template<typename T>
inline bool Equals(const Fit::vector<T>& l, const Fit::vector<T>& r)
{
    if (l.size() != r.size()) {
        return false;
    }
    for (size_t i = 0; i < l.size(); ++i) {
        bool found = false;
        for (size_t j = 0; j < r.size(); ++j) {
            if (Equals(l[i], r[j])) {
                found = true;
            }
        }
        if (!found) {
            return false;
        }
    }
    return true;
}

#define EQUAL_HELPER(l, r, field) (Equals((l).field, (r).field))

template<>
inline bool Equals(const fit::hakuna::kernel::shared::Fitable& l, const fit::hakuna::kernel::shared::Fitable& r)
{
    return EQUAL_HELPER(l, r, genericableId) && EQUAL_HELPER(l, r, genericableVersion) &&
           EQUAL_HELPER(l, r, fitableId) && EQUAL_HELPER(l, r, fitableVersion);
}

template<>
inline bool Equals(const fit::hakuna::kernel::registry::shared::Application& l,
    const fit::hakuna::kernel::registry::shared::Application& r)
{
    return EQUAL_HELPER(l, r, name) && EQUAL_HELPER(l, r, nameVersion);
}

template<>
inline bool Equals(
    const fit::hakuna::kernel::registry::shared::Endpoint& l, const fit::hakuna::kernel::registry::shared::Endpoint& r)
{
    return EQUAL_HELPER(l, r, port) && EQUAL_HELPER(l, r, protocol);
}

template<>
inline bool Equals(
    const fit::hakuna::kernel::registry::shared::Address& l, const fit::hakuna::kernel::registry::shared::Address& r)
{
    return EQUAL_HELPER(l, r, host) && EQUAL_HELPER(l, r, endpoints);
}

template<>
inline bool Equals(
    const fit::hakuna::kernel::registry::shared::Worker& l, const fit::hakuna::kernel::registry::shared::Worker& r)
{
    return EQUAL_HELPER(l, r, id) && EQUAL_HELPER(l, r, environment) && EQUAL_HELPER(l, r, addresses);
}

template<>
inline bool Equals(const ::fit::registry::Fitable& l, const ::fit::registry::Fitable& r)
{
    return EQUAL_HELPER(l, r, genericId) && EQUAL_HELPER(l, r, fitId) && EQUAL_HELPER(l, r, genericVersion);
}

template<>
inline bool Equals(const ::fit::registry::Address& l, const ::fit::registry::Address& r)
{
    return EQUAL_HELPER(l, r, host) && EQUAL_HELPER(l, r, port) && EQUAL_HELPER(l, r, protocol) &&
        EQUAL_HELPER(l, r, environment) && EQUAL_HELPER(l, r, id);
}