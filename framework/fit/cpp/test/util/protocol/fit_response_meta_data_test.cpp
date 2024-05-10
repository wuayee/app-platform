/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/5/15
 * Notes:       :
 */

#include <internal/util/protocol/fit_response_meta_data.h>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

class FitResponseMetaDataTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(FitResponseMetaDataTest, should_return_correct_value_when_from_buffer_given_valid_buffer)
{
    // given
    char buffer[] = {0x00, 0x01,
        0x02,
        0x01,
        0x00, 0x00, 0x00, 0x04,
        0x00, 0x00, 0x00, 0x05,
        0x79, 0x66, 0x66, 0x69, 0x69};
    Fit::string meta_bytes(buffer, sizeof(buffer));
    fit_response_meta_data meta_data;
    uint16_t expected_version = 1;
    uint8_t expected_format = 2;
    uint8_t expected_flag = 1;
    uint32_t expected_code = 4;
    Fit::string expected_message = {0x79, 0x66, 0x66, 0x69, 0x69};
    bool expected_parse_result = true;

    // when
    auto ret = meta_data.from_bytes(meta_bytes);

    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result));
    EXPECT_THAT(meta_data.get_version(), ::testing::Eq(expected_version));
    EXPECT_THAT(meta_data.get_payload_format(), ::testing::Eq(expected_format));
    EXPECT_THAT(meta_data.get_flag(), ::testing::Eq(expected_flag));
    EXPECT_THAT(meta_data.get_code(), ::testing::Eq(expected_code));
    EXPECT_THAT(meta_data.get_message(), ::testing::Eq(expected_message));
}

TEST_F(FitResponseMetaDataTest, should_return_false_when_from_buffer_given_not_enough_message)
{
    // given
    char buffer[] = {0x00, 0x01,
        0x02,
        0x01,
        0x00, 0x00, 0x00, 0x04,
        0x00, 0x00, 0x00, 0x05,
        0x79, 0x20, 0x66, 0x69};
    Fit::string meta_bytes(buffer, sizeof(buffer));
    fit_response_meta_data meta_data;
    bool expected_parse_result = false;

    // when
    auto ret = meta_data.from_bytes(meta_bytes);
    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result));
}

TEST_F(FitResponseMetaDataTest, should_return_false_when_from_buffer_given_not_enough_fixed_content)
{
    // given
    char buffer[] = {0x00, 0x01,
        0x02,
        0x01,
        0x00, 0x00, 0x00, 0x04,
        0x00, 0x00, 0x00};
    Fit::string meta_bytes(buffer, sizeof(buffer));
    fit_response_meta_data meta_data;
    bool expected_parse_result = false;

    // when
    auto ret = meta_data.from_bytes(meta_bytes);
    // then
    EXPECT_THAT(ret, ::testing::Eq(expected_parse_result)
    );
}

TEST_F(FitResponseMetaDataTest, should_return_correct_buffer_when_to_buffer_given_value)
{
    // given
    char buffer[] = {0x00, 0x01,
        0x02,
        0x01,
        0x00, 0x00, 0x00, 0x04,
        0x00, 0x00, 0x00, 0x05,
        0x79, 0x66, 0x66, 0x69, 0x69};
    Fit::string expected_meta_bytes(buffer, sizeof(buffer));
    Fit::string msg = {0x79, 0x66, 0x66, 0x69, 0x69};
    fit_response_meta_data meta_data(1, 2, 1, 4, msg);

    // when
    auto result = meta_data.to_bytes();

    // then
    EXPECT_THAT(result, ::testing::Eq(expected_meta_bytes));
}