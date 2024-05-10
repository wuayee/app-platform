/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2020/11/14 20:23
 * Notes:       :
 */
#include "util/protocol/fit_version.h"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;

class fit_version_test : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(fit_version_test, should_return_correct_value_when_construct_given_value)
{
    // given
    uint8_t expected_mayor_version = 1;
    uint8_t expected_minor_version = 2;
    uint8_t expected_revision_version = 3;

    // when
    fit_version version(expected_mayor_version, expected_minor_version, expected_revision_version);

    // then
    EXPECT_THAT(version.get_major(), ::testing::Eq(expected_mayor_version));
    EXPECT_THAT(version.get_minor(), ::testing::Eq(expected_minor_version));
    EXPECT_THAT(version.get_revision(), ::testing::Eq(expected_revision_version));
}

TEST_F(fit_version_test, should_return_correct_string_value_when_to_string_given_value)
{
    // given
    uint8_t mayor_version = 1;
    uint8_t minor_version = 2;
    uint8_t revision_version = 3;
    Fit::string expected_string = "1.2.3";

    // when
    fit_version version(mayor_version, minor_version, revision_version);
    auto version_string = version.to_string();

    // then
    EXPECT_THAT(version_string, ::testing::Eq(expected_string));
}
