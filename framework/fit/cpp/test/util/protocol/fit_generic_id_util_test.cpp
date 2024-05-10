/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/14 11:32
 * Notes:       :
 */

#include "util/protocol/fit_generic_id_util.h"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

class fit_generic_id_util_test : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(fit_generic_id_util_test,
    should_return_correct_generic_id_hex_when_str_to_hex_given_valid_generic_id_lower_str)
{
    // given
    uint8_t result[fit_generic_id_util::GENERIC_ID_LENGTH] {};
    uint8_t expect_result[fit_generic_id_util::GENERIC_ID_LENGTH] {0x4c, 0x78, 0x01, 0x43, 0xf5, 0x8b, 0x44, 0x0f, 0x98,
        0x9d, 0x1e, 0x00, 0x64, 0xab, 0x46, 0x75};
    Fit::string generic_id_str = "4c780143f58b440f989d1e0064ab4675";
    bool expect_return = true;

    // when
    auto ret = fit_generic_id_util::str_to_hex(generic_id_str, result, sizeof(result));

    // then
    EXPECT_THAT(ret, ::testing::Eq(expect_return));
    EXPECT_THAT(0, ::testing::Eq(memcmp(expect_result, result, fit_generic_id_util::GENERIC_ID_LENGTH)));
}

TEST_F(fit_generic_id_util_test,
    should_return_correct_generic_id_hex_when_str_to_hex_given_valid_generic_id_upper_str)
{
    // given
    uint8_t result[fit_generic_id_util::GENERIC_ID_LENGTH] {};
    uint8_t expect_result[fit_generic_id_util::GENERIC_ID_LENGTH] {0x4c, 0x78, 0x01, 0x43, 0xf5, 0x8b, 0x44, 0x0f, 0x98,
        0x9d, 0x1e, 0x00, 0x64, 0xab, 0x46, 0x75};
    Fit::string generic_id_str = "4C780143F58b440F989d1e0064ab4675";
    bool expect_return = true;

    // when
    auto ret = fit_generic_id_util::str_to_hex(generic_id_str, result, sizeof(result));

    // then
    EXPECT_THAT(ret, ::testing::Eq(expect_return));
    EXPECT_THAT(0, ::testing::Eq(memcmp(expect_result, result, fit_generic_id_util::GENERIC_ID_LENGTH)));
}


TEST_F(fit_generic_id_util_test, should_return_false_when_str_to_hex_given_str_is_not_hex_str)
{
    // given
    uint8_t hex_result[fit_generic_id_util::GENERIC_ID_LENGTH] {};
    Fit::string generic_id_str = "9289a2a4322d47d38f33fc32c47testa";
    bool expect_return = false;

    // when
    auto ret = fit_generic_id_util::str_to_hex(generic_id_str, hex_result, sizeof(hex_result));

    // then
    EXPECT_THAT(ret, ::testing::Eq(expect_return));
}

TEST_F(fit_generic_id_util_test, should_return_false_when_str_to_hex_given_longger_generic_id_str)
{
    // given
    uint8_t result[fit_generic_id_util::GENERIC_ID_LENGTH] {};
    Fit::string generic_id_str = "4c780143f58b440f989d1e0064ab467512345";
    bool expect_return = false;

    // when
    auto ret = fit_generic_id_util::str_to_hex(generic_id_str, result, sizeof(result));

    // then
    EXPECT_THAT(ret, ::testing::Eq(expect_return));
}

TEST_F(fit_generic_id_util_test, should_return_false_when_str_to_hex_given_shorter_generic_id_str)
{
    // given
    uint8_t result[fit_generic_id_util::GENERIC_ID_LENGTH] {};
    Fit::string generic_id_str = "4c780143f58b440f989d1e0064ab46";
    bool expect_return = false;

    // when
    auto ret = fit_generic_id_util::str_to_hex(generic_id_str, result, sizeof(result));

    // then
    EXPECT_THAT(ret, ::testing::Eq(expect_return));
}

TEST_F(fit_generic_id_util_test, should_return_false_when_str_to_hex_given_generic_id_str_is_not_hex)
{
    // given
    uint8_t result[fit_generic_id_util::GENERIC_ID_LENGTH] {};
    Fit::string generic_id_str = "4C780143F58b440F989d1e0064ab467?";
    bool expect_return = false;

    // when
    auto ret = fit_generic_id_util::str_to_hex(generic_id_str, result, sizeof(result));

    // then
    EXPECT_THAT(ret, ::testing::Eq(expect_return));
}

TEST_F(fit_generic_id_util_test, should_return_correct_str_when_hex_to_upper_str_given_generic_id_hex)
{
    // given
    uint8_t hex_generic_id[fit_generic_id_util::GENERIC_ID_LENGTH] {0x4c, 0x78, 0x01, 0x43, 0xf5, 0x8b, 0x44, 0x0f,
        0x98, 0x9d, 0x1e, 0x00, 0x64, 0xab, 0x46, 0x75};
    Fit::string expect_result = "4C780143F58B440F989D1E0064AB4675";

    // when
    auto result = fit_generic_id_util::hex_to_str(hex_generic_id, sizeof(hex_generic_id), true);

    // then
    EXPECT_THAT(result, ::testing::Eq(expect_result));
}

TEST_F(fit_generic_id_util_test, should_return_correct_str_when_hex_to_lower_str_given_generic_id_hex)
{
    // given
    uint8_t hex_generic_id[fit_generic_id_util::GENERIC_ID_LENGTH] {0x4c, 0x78, 0x01, 0x43, 0xf5, 0x8b, 0x44, 0x0f,
        0x98, 0x9d, 0x1e, 0x00, 0x64, 0xab, 0x46, 0x75};
    Fit::string expect_result = "4c780143f58b440f989d1e0064ab4675";

    // when
    auto result = fit_generic_id_util::hex_to_str(hex_generic_id, sizeof(hex_generic_id), false);

    // then
    EXPECT_THAT(result, ::testing::Eq(expect_result));
}