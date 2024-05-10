/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/7/26 16:50
 * Notes        :
 */
#include <gtest/gtest.h>
#include <gmock/gmock.h>

#include <fit/external/util/base64.h>

TEST(Base64, should_return_correct_value_when_src_bytes_is_abc)
{
    Fit::bytes bytes = Fit::string("abc");
    Fit::string encodeString = "YWJj";

    EXPECT_EQ(Fit::Base64Encode(bytes), encodeString);
    EXPECT_EQ(Fit::Base64Decode(encodeString), bytes);
}

TEST(Base64, should_return_correct_value_when_src_bytes_is_abcd)
{
    Fit::bytes bytes = Fit::string("abcd");
    Fit::string encodeString = "YWJjZA==";

    EXPECT_EQ(Fit::Base64Encode(bytes), encodeString);
    EXPECT_EQ(Fit::Base64Decode(encodeString), bytes);
}

TEST(Base64, should_return_correct_value_when_src_bytes_is_abcde)
{
    Fit::bytes bytes = Fit::string("abcde");
    Fit::string encodeString = "YWJjZGU=";

    EXPECT_EQ(Fit::Base64Encode(bytes), encodeString);
    EXPECT_EQ(Fit::Base64Decode(encodeString), bytes);
}

TEST(Base64, should_return_correct_value_when_src_bytes_is_a_to_9)
{
    Fit::bytes bytes = Fit::string("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    Fit::string encodeString = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0NTY3ODk=";

    EXPECT_EQ(Fit::Base64Encode(bytes), encodeString);
    EXPECT_EQ(Fit::Base64Decode(encodeString), bytes);
}

TEST(Base64, should_return_correct_value_when_src_bytes_has_chinese)
{
    Fit::bytes bytes = Fit::string("\xE4\xB8\xAD\xE6\x96\x87"); // "中文"的utf8编码
    Fit::string encodeString = "5Lit5paH";

    EXPECT_EQ(Fit::Base64Encode(bytes), encodeString);
    EXPECT_EQ(Fit::Base64Decode(encodeString), bytes);
}
