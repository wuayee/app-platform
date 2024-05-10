/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/14 17:32
 * Notes:       :
 */

#include <internal/util/protocol/fit_meta_package_parser.h>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

class fit_meta_package_parser_test : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(fit_meta_package_parser_test, should_return_correct_value_when_parse_given_raw_bytes)
{
    // given
    Fit::string meta_bytes {0x00, 0x01, 0x02, 0x01, 0x00, 0x00, static_cast<char>(0xa9), static_cast<char>(0xa9),
        static_cast<char>(0xbf), static_cast<char>(0xf1), static_cast<char>(0x9f), 0x5b, 0x4d, 0x0f,
        static_cast<char>(0xba), static_cast<char>(0xd3),
        static_cast<char>(0xc5), 0x2f, static_cast<char>(0xc3), 0x69, static_cast<char>(0xaa), 0x40, 0x00, 0x0a, 0x6d,
        0x79, 0x20, 0x66, 0x69, 0x74, 0x61, 0x62, 0x6c, 0x65};
    fit_meta_data meta_data;
    uint16_t expected_version = 1;
    uint8_t expected_format = 2;
    fit_version expected_generic_version(1, 0, 0);
    Fit::string expected_generic_id = "a9a9bff19f5b4d0fbad3c52fc369aa40";
    Fit::string expected_fit_id = "my fitable";
    bool expected_parse_result = true;

    // when
    fit_meta_package_parser parser(meta_bytes);
    auto ret = parser.parse_to(meta_data);
    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result));
    EXPECT_THAT(meta_data.get_version(), ::testing::Eq(expected_version));
    EXPECT_THAT(meta_data.get_payload_format(), ::testing::Eq(expected_format));
    EXPECT_THAT(meta_data.get_generic_version(), ::testing::Eq(expected_generic_version));
    EXPECT_THAT(meta_data.get_generic_id(), ::testing::Eq(expected_generic_id));
    EXPECT_THAT(meta_data.get_fit_id(), ::testing::Eq(expected_fit_id));
}

TEST_F(fit_meta_package_parser_test, should_return_false_when_parse_given_raw_bytes_is_not_enough_fit_id)
{
    // given
    Fit::string meta_bytes {0x00, 0x01, 0x02, 0x01, 0x00, 0x00, static_cast<char>(0xa9), static_cast<char>(0xa9),
        static_cast<char>(0xbf), static_cast<char>(0xf1), static_cast<char>(0x9f), 0x5b, 0x4d, 0x0f,
        static_cast<char>(0xba), static_cast<char>(0xd3),
        static_cast<char>(0xc5), 0x2f, static_cast<char>(0xc3), 0x69, static_cast<char>(0xaa), 0x40, 0x00, 0x0a, 0x6d,
        0x79, 0x20, 0x66, 0x69, 0x74, 0x61, 0x62, 0x6c};
    fit_meta_data meta_data;
    bool expected_parse_result = false;

    // when
    fit_meta_package_parser parser(std::move(meta_bytes));
    auto ret = parser.parse_to(meta_data);
    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result));
}

TEST_F(fit_meta_package_parser_test, should_return_false_when_parse_given_raw_bytes_is_not_enough_fixed_content)
{
    // given
    Fit::string meta_bytes {0x00, 0x01, 0x02, 0x01, 0x00, 0x00, static_cast<char>(0xa9), static_cast<char>(0xa9),
        static_cast<char>(0xbf), static_cast<char>(0xf1), static_cast<char>(0x9f), 0x5b, 0x4d, 0x0f,
        static_cast<char>(0xba), static_cast<char>(0xd3),
        static_cast<char>(0xc5), 0x2f, static_cast<char>(0xc3), 0x69, static_cast<char>(0xaa), 0x40};
    fit_meta_data meta_data;
    bool expected_parse_result = false;

    // when
    fit_meta_package_parser parser(meta_bytes);
    auto ret = parser.parse_to(meta_data);
    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result));
}

TEST_F(fit_meta_package_parser_test, should_return_false_when_parse_given_raw_bytes_is_not_include_fit_id)
{
    // given
    Fit::string meta_bytes {0x00, 0x01, 0x02, 0x01, 0x00, 0x00, static_cast<char>(0xa9), static_cast<char>(0xa9),
        static_cast<char>(0xbf), static_cast<char>(0xf1), static_cast<char>(0x9f), 0x5b, 0x4d, 0x0f,
        static_cast<char>(0xba), static_cast<char>(0xd3),
        static_cast<char>(0xc5), 0x2f, static_cast<char>(0xc3), 0x69, static_cast<char>(0xaa), 0x40};
    fit_meta_data meta_data;
    bool expected_parse_result = false;

    // when
    fit_meta_package_parser parser(meta_bytes);
    auto ret = parser.parse_to(meta_data);
    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result));
}