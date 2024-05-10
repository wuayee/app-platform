/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/
#include <fit/securec.h>
#include <vector>
#include <string>
#include <cstdio>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;

class UTSecurecTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
public:
};

// 1. destMax等于0，返回-1
TEST_F(UTSecurecTest, should_return_error_when_memcpy_s_given_destMax_0)
{
    // given
    char dest[2] = {0};
    char expectedDest[2] = {0};
    char src[2] = {'a', 'b'};
    // when
    int ret = memcpy_s(dest, 0, src, 2);
    // then
    EXPECT_EQ(-1, ret);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}

// 2. count大于destMax，返回-1
TEST_F(UTSecurecTest, should_return_error_when_memcpy_s_given_count_greater_destMax)
{
    // given
    char dest[2] = {0};
    char expectedDest[2] = {0};
    char src[2] = {'a', 'b'};
    // when
    int ret = memcpy_s(dest, 2, src, 3);
    // then
    EXPECT_EQ(-1, ret);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}

// 3. dest为空 ，返回-1
TEST_F(UTSecurecTest, should_return_error_when_memcpy_s_given_dest_null)
{
    // given
    char dest[2] = {0};
    char expectedDest[2] = {0};
    char src[2] = {'a', 'b'};
    // when
    int ret = memcpy_s(nullptr, 2, src, 2);
    // then
    EXPECT_EQ(-1, ret);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}

// 4. src为空 ，返回-1
TEST_F(UTSecurecTest, should_return_error_when_memcpy_s_given_src_null)
{
    // given
    char dest[2] = {0};
    char expectedDest[2] = {0};
    char src[2] = {'a', 'b'};
    // when
    int ret = memcpy_s(dest, 2, nullptr, 2);
    // then
    EXPECT_EQ(-1, ret);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}

// 5. normal
TEST_F(UTSecurecTest, should_return_ok_when_memcpy_s_given_src)
{
    // given
    char dest[2] = {0};
    char expectedDest[2] = {'a', 'b'};
    char src[2] = {'a', 'b'};
    // when
    int ret = memcpy_s(dest, 2, src, 2);

    // then
    EXPECT_EQ(0, ret);
    EXPECT_EQ(dest[0], expectedDest[0]);
    EXPECT_EQ(dest[1], expectedDest[1]);
}

// 1. count大于destMax，返回-1
TEST_F(UTSecurecTest, should_return_error_when_memset_s_given_count_greater_dest_max)
{
    // given
    char dest[1];
    // when
    int ret = memset_s(dest, 1, 0x0, 2);
    // then
    EXPECT_EQ(-1, ret);
}

// 2. dest为空 且 destMax不等于0，-1
TEST_F(UTSecurecTest, should_return_error_when_memset_s_given_dest_nullptr_and_destMax_not_0)
{
    // given
    // when
    int ret = memset_s(nullptr, 1, 0x0, 1);
    // then
    EXPECT_EQ(-1, ret);
}

// 3. dest为空 且 destMax等于0
TEST_F(UTSecurecTest, should_return_ok_when_memset_s_given_dest_nullptr_and_destMax_0)
{
    // given
    // when
    int ret = memset_s(nullptr, 0, 0x0, 0);
    // then
    EXPECT_EQ(0, ret);
}

// 4. normal
TEST_F(UTSecurecTest, should_return_error_when_memset_s_given_dest_not_nullptr_and_destMax_6)
{
    // given
    char dest[6] = {'6', '6', '6', '6', '6', '6'};
    char expectedDest[6] = {0};
    // when
    int ret = memset_s(dest, 6, 0x0, 6);
    // then
    EXPECT_EQ(0, ret);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}

int FormatTest(char* dest, size_t destMax, size_t count, const char* format, ...)
{
    va_list args;
    va_start(args, format);
    return vsnprintf_s(dest, destMax, count, format, args);
}

// vsnprintf_s 测试
// 1. strDest为空, 返回 -1
TEST_F(UTSecurecTest, should_return_error_when_vsnprintf_s_given_dest_nullptr)
{
    // given
    // when
    int ret = FormatTest(nullptr, 1, 1, "Test %d,%d", 6, 8);
    // then
    EXPECT_EQ(-1, ret);
}

// 2. format为nullptr, 返回 -1
TEST_F(UTSecurecTest, should_return_error_when_vsnprintf_s_given_format_is_nullptr)
{
    // given
    char dest[12];
    // when
    int ret = FormatTest(&dest[0], 12, 11, nullptr);
    // then
    EXPECT_EQ(-1, ret);
}

// 3. destMax等于0, 返回 -1
TEST_F(UTSecurecTest, should_return_error_when_vsnprintf_s_given_destMax_0)
{
    // given
    char dest[1];
    // when
    int ret = FormatTest(&dest[0], 0, 1, "Test %d,%d", 6, 8);
    // then
    EXPECT_EQ(-1, ret);
}

// 4. count >= destMax, 返回 -1
TEST_F(UTSecurecTest, should_return_error_when_vsnprintf_s_given_count_greater_destMax)
{
    // given
    char dest[1];
    // when
    int ret = FormatTest(&dest[0], 1, 2, "Test %d,%d", 6, 8);
    // then
    EXPECT_EQ(-1, ret);
}

// 5. count < length, strDest[count] = '\0', 返回-1
TEST_F(UTSecurecTest, should_return_error_when_vsnprintf_s_given_count_less_formats_len)
{
    // given
    char dest[3];
    char expectedDest[3] = "Te";
    expectedDest[2] = '\0';
    // when
    int ret = FormatTest(&dest[0], 3, 2, "Test %d,%d", 6, 8);

    // then
    EXPECT_EQ(-1, ret);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}

// 6. normal
TEST_F(UTSecurecTest, should_return_true_when_vsnprintf_s_given_dest_greater_source)
{
    // given
    char dest[12];
    char expectedDest[12] = "Test 6,8";
    // when
    int ret = FormatTest(dest, 12, 11, "Test %d,%d", 6, 8);

    // then
    EXPECT_EQ(ret > 0, true);
    EXPECT_EQ(strcmp(dest, expectedDest), 0);
}