/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide uuid
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef FIT_UUID_H
#define FIT_UUID_H
#include <sstream>
#include <iomanip>
#include <random>
#include <fit/stl/string.hpp>
#include <fit/internal/util/fit_random.h>
namespace Fit {
constexpr const uint32_t RANDOM_BYTES_SIZE = 16;
constexpr const uint32_t VERSION_POS = 6;
constexpr const uint32_t VARIANT_POS = 8;
constexpr const uint32_t FIRST_PARAGRAPH_POS = 4;
constexpr const uint32_t SECOND_PARAGRAPH_POS = 6;
constexpr const uint32_t THIRD_PARAGRAPH_POS = 8;
constexpr const uint32_t FOURTH_PARAGRAPH_POS = 10;
constexpr const uint32_t MIN_VALUE = 0;
constexpr const uint32_t MAX_VALUE = 255;
static Fit::string GenerateUuid()
{
    unsigned int seed = FitRandom<unsigned int>();
    std::mt19937 generator(seed);
    std::uniform_int_distribution<> dist(MIN_VALUE, MAX_VALUE);

    uint8_t randomBytes[RANDOM_BYTES_SIZE];
    for (uint32_t i = 0; i < RANDOM_BYTES_SIZE; ++i) {
        randomBytes[i] = static_cast<uint8_t>(dist(generator));
    }

    randomBytes[VERSION_POS] &= 0x0f;  // Clear version bits
    randomBytes[VERSION_POS] |= 0x40;  // Set to version 4
    randomBytes[VARIANT_POS] &= 0x3f;  // Clear variant bits
    randomBytes[VARIANT_POS] |= 0x80;  // Set to IETF variant

    std::ostringstream uuidSs;
    for (uint32_t i = 0; i < RANDOM_BYTES_SIZE; ++i) {
        if (i == FIRST_PARAGRAPH_POS || i == SECOND_PARAGRAPH_POS || i == THIRD_PARAGRAPH_POS ||
            i == FOURTH_PARAGRAPH_POS) {
            uuidSs << '-';
        }
        uuidSs << std::hex << std::setw(2) << std::setfill('0') << static_cast<int>(randomBytes[i]);
    }
    return uuidSs.str();
}
}
#endif // FIT_UUID_H