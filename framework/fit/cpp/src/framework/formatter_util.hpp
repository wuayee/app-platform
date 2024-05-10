/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/29
 * Notes:       :
 */

#ifndef FORMATTER_UTIL_HPP
#define FORMATTER_UTIL_HPP

#include <cstdint>
namespace Fit {
namespace Framework {
namespace Formatter {
constexpr uint32_t CHAR_BITS = sizeof(char) * 8;
constexpr uint32_t NULL_FLAGS_BASE = 0x80;

inline void FitRequestInitNullArgumentFlags(std::string &nullFlags, uint32_t argc)
{
    for (uint32_t i = 0; i < argc; i += CHAR_BITS) {
        nullFlags.push_back(0);
    }
}

inline bool FitRequestIsValidArguments(const std::string &nullFlags, uint32_t expectedCount)
{
    return static_cast<uint32_t>(nullFlags.size()) == expectedCount &&
        nullFlags.size() * CHAR_BITS >= static_cast<size_t>(nullFlags.size());
}
/**
 *
 * @param nullFlags
 * @param index : start from 0
 * @param isNull
 */
inline void FitRequestSetNullArgumentFlags(std::string &nullFlags, uint32_t index, bool isNull)
{
    // 空的时候为0，不需要再设置了，只有1的情况需要设置
    if (!isNull) {
        return;
    }
    uint32_t nullFlagIndex = index / CHAR_BITS;
    uint32_t nullFlagOffset = index % CHAR_BITS;
    nullFlags.at(nullFlagIndex)
        = static_cast<uint32_t>(nullFlags.at(nullFlagIndex)) | (NULL_FLAGS_BASE >> nullFlagOffset);
}
/**
 *
 * @param nullFlags
 * @param index : start from 0
 * @return null - true, or else false
 */
inline bool FitRequestIsNullArgumentFlags(const std::string &nullFlags, uint32_t index)
{
    // start from 0
    uint32_t nullFlagIndex = (index + 1) / CHAR_BITS;
    uint32_t nullFlagOffset = index % CHAR_BITS;
    auto isNullFlag = NULL_FLAGS_BASE >> nullFlagOffset;
    return static_cast<uint32_t>(nullFlags.at(nullFlagIndex)) & isNullFlag;
}
}
}
}
#endif // FORMATTER_UTIL_HPP
