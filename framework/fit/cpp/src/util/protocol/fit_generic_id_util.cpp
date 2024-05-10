/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : for u.u.i.d
 * Author       : songyongtan
 * Create       : 2020/11/13 20:40
 * Notes:       :
 */
#include "fit_generic_id_util.h"
#include <fit/fit_log.h>

#include <algorithm>
#include <iomanip>
#include <sstream>

namespace fit_generic_id_util {
static constexpr uint8_t CHAR_NUM_BASE = '0';
static constexpr uint8_t CHAR_UPPER_BASE = 'A';
static constexpr uint8_t CHAR_LOWER_BASE = 'a';
uint8_t str_to_hex(char first, char second)
{
    uint8_t result = 0;
    uint8_t tmp = first;
    if (std::isdigit(first)) {
        result = (tmp - CHAR_NUM_BASE) << 4; // 4 表示高四位
    } else if (isupper(first)) {
        result = (tmp - CHAR_UPPER_BASE + 10) << 4; // 10,4 16进制数额外加10
    } else if (islower(first)) {
        result = (tmp - CHAR_LOWER_BASE + 10) << 4; // 10,4 16进制数额外加10
    }

    tmp = second;
    if (std::isdigit(second)) {
        result |= (tmp - CHAR_NUM_BASE);
    } else if (isupper(second)) {
        result |= (tmp - CHAR_UPPER_BASE + 10); // 10 16进制数额外加10
    } else if (islower(second)) {
        result |= (tmp - CHAR_LOWER_BASE + 10); // 10 16进制数额外加10
    }

    return result;
}

bool str_to_hex(const Fit::string &str, uint8_t *result, uint32_t result_len)
{
    constexpr uint32_t generic_id_str_length = 32;
    if (str.size() != generic_id_str_length || result_len < GENERIC_ID_LENGTH) {
        FIT_LOG_ERROR("Invalid generic id str length, data = %s, result len = %u.", str.c_str(), result_len);
        return false;
    }

    for (auto &item : str) {
        if (!isxdigit(item)) {
            return false;
        }
    }
    for (uint32_t i = 0; i < GENERIC_ID_LENGTH; ++i) {
        result[i] = str_to_hex(str[2 * i], str[2 * i + 1]); // 2
    }

    return true;
}

Fit::string hex_to_str(const uint8_t hex[GENERIC_ID_LENGTH], uint32_t hex_len, bool upper)
{
    std::ostringstream format;
    if (upper) {
        format.setf(std::ios::uppercase);
    }
    format.fill('0');
    for (uint32_t i = 0; i < hex_len; ++i) {
        format << std::setw(2) << std::hex << static_cast<uint32_t>(hex[i]); // 2 is index
    }

    return Fit::to_fit_string(format.str());
}
}