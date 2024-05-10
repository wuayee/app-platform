/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/14 16:46
 * Notes:       :
 */

#include <internal/util/protocol/fit_meta_data.h>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

class fit_meta_data_test : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(fit_meta_data_test, should_return_correct_value_when_construct_given_value)
{
    // given
    uint16_t expected_version = 1;
    uint8_t expected_payload_format = 2;
    fit_version expected_generic_version(1, 2, 3);
    Fit::string expected_generic_id = "test";
    Fit::string expected_fit_id = "ffff";

    // when
    fit_meta_data meta_data(expected_version, expected_payload_format, expected_generic_version, expected_generic_id,
        expected_fit_id);

    // then
    EXPECT_THAT(meta_data.get_version(), ::testing::Eq(expected_version));
    EXPECT_THAT(meta_data.get_payload_format(), ::testing::Eq(expected_payload_format));
    EXPECT_THAT(meta_data.get_generic_version(), ::testing::Eq(expected_generic_version));
    EXPECT_THAT(meta_data.get_generic_id(), ::testing::Eq(expected_generic_id));
    EXPECT_THAT(meta_data.get_fit_id(), ::testing::Eq(expected_fit_id));
}

TEST_F(fit_meta_data_test, should_move_when_call_move_value)
{
    // given
    uint16_t expected_version = 1;
    uint8_t expected_payload_format = 2;
    fit_version expected_generic_version(1, 2, 3);
    Fit::string expected_generic_id = "test";
    Fit::string expected_fit_id = "ffff";
    Fit::string expected_fit_id_after_move = "";
    Fit::string expected_generic_id_after_move = "";

    // when
    fit_meta_data meta_data(expected_version, expected_payload_format, expected_generic_version, expected_generic_id,
        expected_fit_id);
    auto generic_id = meta_data.move_generic_id();
    auto fit_id = meta_data.move_fit_id();

    // then
    EXPECT_THAT(generic_id, ::testing::Eq(expected_generic_id));
    EXPECT_THAT(fit_id, ::testing::Eq(expected_fit_id));
    EXPECT_THAT(meta_data.get_generic_id(), ::testing::Eq(expected_fit_id_after_move));
    EXPECT_THAT(meta_data.get_fit_id(), ::testing::Eq(expected_generic_id_after_move));
}
