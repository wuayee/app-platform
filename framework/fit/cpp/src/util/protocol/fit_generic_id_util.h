/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : for u.u.i.d
 * Author       : songyongtan
 * Create       : 2020/11/13 20:19
 * Notes:       :
 */

#ifndef FIT_GENERIC_ID_UTIL_H
#define FIT_GENERIC_ID_UTIL_H

#include <cstdint>
#include <fit/stl/string.hpp>

namespace fit_generic_id_util {
constexpr uint32_t GENERIC_ID_LENGTH = 16;
bool str_to_hex(const Fit::string &str, uint8_t result[GENERIC_ID_LENGTH], uint32_t result_len);
Fit::string hex_to_str(const uint8_t hex[GENERIC_ID_LENGTH], uint32_t hex_len, bool upper);
}

#endif // FIT_GENERIC_ID_UTIL_H
