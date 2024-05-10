/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : Test
 * Author       : songyongtan
 * Date         : 2021/6/10
 */

#include <fit/internal/util/command_line_util.hpp>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

class CommandLineUtilTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(CommandLineUtilTest, should_return_correct_option_value_when_get_option_value_given_mutil_type_option)
{
    const char *optionsStr = "--i 1 --s \"ss\" --d 123.3 --s1 \"1 2\"";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(4));
    EXPECT_THAT(options["i"], ::testing::Eq("1"));
    EXPECT_THAT(options["s"], ::testing::Eq("\"ss\""));
    EXPECT_THAT(options["d"], ::testing::Eq("123.3"));
    EXPECT_THAT(options["s1"], ::testing::Eq("\"1 2\""));
}

TEST_F(CommandLineUtilTest, should_return_correct_option_value_when_get_option_value_given_one_number_option)
{
    const char *optionsStr = "--i 1";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(1));
    EXPECT_THAT(options["i"], ::testing::Eq("1"));
}


TEST_F(CommandLineUtilTest,
    should_return_correct_option_value_when_get_option_value_given_one_number_option_with_seperate_equal)
{
    const char *optionsStr = "--i=1";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(1));
    EXPECT_THAT(options["i"], ::testing::Eq("1"));
}

TEST_F(CommandLineUtilTest, should_return_correct_option_value_when_get_option_value_given_one_string_option)
{
    const char *optionsStr = "--s \"ss\"";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(1));
    EXPECT_THAT(options["s"], ::testing::Eq("\"ss\""));
}

TEST_F(CommandLineUtilTest,
    should_return_correct_option_value_when_get_option_value_given_one_string_option_with_seperate_equal)
{
    const char *optionsStr = "--s=\"ss\"";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(1));
    EXPECT_THAT(options["s"], ::testing::Eq("\"ss\""));
}

TEST_F(CommandLineUtilTest,
    should_return_correct_option_value_when_get_option_value_given_one_string_option_with_whitespace)
{
    const char *optionsStr = "--s1 \"1 2\"";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(1));
    EXPECT_THAT(options["s1"], ::testing::Eq("\"1 2\""));
}

TEST_F(CommandLineUtilTest, should_return_correct_option_value_when_get_option_value_given_one_string_\
option_with_whitespace_with_seperate_equal)
{
    const char *optionsStr = "--s1=\"1 2\"";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(1));
    EXPECT_THAT(options["s1"], ::testing::Eq("\"1 2\""));
}

TEST_F(CommandLineUtilTest,
    should_return_correct_option_value_when_get_option_value_given_option_split_with_mutil_whitespace)
{
    const char *optionsStr = "   --i 1   --s1 \"1 2\"   ";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(2));
    EXPECT_THAT(options["i"], ::testing::Eq("1"));
    EXPECT_THAT(options["s1"], ::testing::Eq("\"1 2\""));
}

TEST_F(CommandLineUtilTest, should_return_correct_option_value_when_get_option_value_given_option_\
split_with_mutil_whitespace_with_seperate_equal)
{
    const char *optionsStr = "   --i  =  1   --s1  =  \"1 2\"   ";
    auto options = Fit::CommandLineUtil::GetOpt(optionsStr);

    ASSERT_THAT(options.size(), ::testing::Eq(2));
    EXPECT_THAT(options["i"], ::testing::Eq("1"));
    EXPECT_THAT(options["s1"], ::testing::Eq("\"1 2\""));
}

TEST_F(CommandLineUtilTest, should_return_correct_option_value_when_get_option_value_given_command_line)
{
    const char *argv[] = {"--i", "1", "--s", "\"ss\"", "--d", "123.3", "--s1", "\"1 2\""};
    auto options = Fit::CommandLineUtil::GetOpt(sizeof(argv)/sizeof(char *), const_cast<char **>(argv));

    ASSERT_THAT(options.size(), ::testing::Eq(4));
    EXPECT_THAT(options["i"], ::testing::Eq("1"));
    EXPECT_THAT(options["s"], ::testing::Eq("\"ss\""));
    EXPECT_THAT(options["d"], ::testing::Eq("123.3"));
    EXPECT_THAT(options["s1"], ::testing::Eq("\"1 2\""));
}
