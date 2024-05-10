/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/14 19:03
 * Notes:       :
 */

#include <internal/util/protocol/fit_meta_package_builder.h>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

class fit_meta_package_builder_test : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(fit_meta_package_builder_test, should_return_correct_raw_bytes_when_build_given_meta_data)
{
    // given
    uint16_t expected_version = 1;
    uint8_t expected_format = 2;
    fit_version expected_generic_version(1, 0, 0);
    Fit::string expected_generic_id = "a9a9bff19f5b4d0fbad3c52fc369aa40";
    Fit::string expected_fit_id = "my fitable";

    fit_meta_data meta_data(expected_version, expected_format, expected_generic_version, expected_generic_id,
        expected_fit_id);

    Fit::string expected_meta_raw_bytes {0x00, 0x01, 0x02, 0x01, 0x00, 0x00, static_cast<char>(0xa9),
        static_cast<char>(0xa9), static_cast<char>(0xbf), static_cast<char>(0xf1), static_cast<char>(0x9f),
        0x5b, 0x4d, 0x0f, static_cast<char>(0xba), static_cast<char>(0xd3),
        static_cast<char>(0xc5), 0x2f, static_cast<char>(0xc3), 0x69, static_cast<char>(0xaa), 0x40, 0x00, 0x0a, 0x6d,
        0x79, 0x20, 0x66, 0x69, 0x74, 0x61, 0x62, 0x6c, 0x65};

    // when
    auto result = fit_meta_package_builder::build(meta_data);
    // then
    EXPECT_THAT(result, ::testing::Eq(expected_meta_raw_bytes));
}