/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/29 14:29
 */

#include <fit/external/util/context/context_base.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

class FitContextBaseTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override {}
};

TEST_F(FitContextBaseTest, should_return_not_nullptr_when_NewContextDefault)
{
    auto ctx = NewContextDefault();
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_nullptr_when_NewContextWithMemFunc_given_bad_allocator_func)
{
    auto ctx = NewContextWithMemFunc(nullptr, nullptr);
    EXPECT_THAT(nullptr, ::testing::Eq(ctx));
    ContextDestroy(ctx);
}

static int g_allocCnt = 0;
static int g_freeCnt = 0;
static void *TestMalloc(size_t size)
{
    g_allocCnt++;
    return malloc(size);
}

static void TestFree(void *data)
{
    g_freeCnt++;
    free(data);
}

TEST_F(FitContextBaseTest, should_return_not_nullptr_when_NewContextWithMemFunc_given_right_allocator_func)
{
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_cnt_when_ContextMallocNoManage_given_right_allocator_func)
{
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    auto data = ContextMallocNoManage(ctx, 10);
    ContextFreeNoManage(ctx, data);

    EXPECT_THAT(g_allocCnt, ::testing::Eq(1));
    EXPECT_THAT(g_freeCnt, ::testing::Eq(1));

    g_allocCnt = 0;
    g_freeCnt = 0;
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_empty_alias_when_ContextGetAlias_given_not_set_alias)
{
    const char *alias{""};
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    auto result = ContextGetAlias(ctx);

    auto cmpResult = std::string(result);
    EXPECT_THAT(std::string(alias), ::testing::Eq(cmpResult));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_empty_alias_when_ContextGetAlias_given_set_alias_with_null_ctx)
{
    const char *alias{"test_alias"};

    ContextSetAlias(nullptr, alias);
    auto result = ContextGetAlias(nullptr);

    auto cmpResult = std::string(result);
    EXPECT_THAT(std::string(""), ::testing::Eq(cmpResult));
}

TEST_F(FitContextBaseTest, should_return_right_alias_when_ContextGetAlias_given_set_alias)
{
    const char *alias{"test_alias"};
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    ContextSetAlias(ctx, alias);
    auto result = ContextGetAlias(ctx);

    auto cmpResult = std::string(result);
    EXPECT_THAT(std::string(alias), ::testing::Eq(cmpResult));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_setted_retry_when_get_retry_given_set_retry)
{
    uint32_t expectedRetry {1};
    auto ctx = NewContextDefault();
    ContextSetRetry(ctx, expectedRetry);

    auto result = ContextGetRetry(ctx);

    EXPECT_THAT(result, ::testing::Eq(expectedRetry));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_default_retry_when_get_retry_given_no_set_retry)
{
    uint32_t expectedRetry {0};
    auto ctx = NewContextDefault();

    auto result = ContextGetRetry(ctx);

    EXPECT_THAT(result, ::testing::Eq(expectedRetry));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_default_retry_when_get_retry_given_null_context)
{
    uint32_t expectedRetry {0};

    ContextSetRetry(nullptr, expectedRetry);

    auto result = ContextGetRetry(nullptr);

    EXPECT_THAT(result, ::testing::Eq(expectedRetry));
}

TEST_F(FitContextBaseTest, should_return_default_timeout_when_get_timeout_given_no_set_timeout)
{
    uint32_t expectedTimeout {5000};
    auto ctx = NewContextDefault();

    auto result = ContextGetTimeout(ctx);

    EXPECT_THAT(result, ::testing::Eq(expectedTimeout));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_setted_timeout_when_get_timeout_given_set_timeout)
{
    uint32_t expectedTimeout {1000};
    auto ctx = NewContextDefault();
    ContextSetTimeout(ctx, expectedTimeout);

    auto result = ContextGetTimeout(ctx);

    EXPECT_THAT(result, ::testing::Eq(expectedTimeout));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_default_timeout_when_get_timeout_given_null_context)
{
    uint32_t expectedTimeout {5000};

    ContextSetTimeout(nullptr, expectedTimeout);

    auto result = ContextGetTimeout(nullptr);

    EXPECT_THAT(result, ::testing::Eq(expectedTimeout));
}

TEST_F(FitContextBaseTest, should_return_default_policy_when_get_policy_given_no_set_policy)
{
    FitablePolicy expectedPolicy {FitablePolicy::POLICY_DEFAULT};
    auto ctx = NewContextDefault();

    auto result = ContextGetPolicy(ctx);

    EXPECT_THAT(result, ::testing::Eq(expectedPolicy));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_setted_policy_when_get_policy_given_set_policy)
{
    FitablePolicy expectedPolicy {FitablePolicy::POLICY_RULE};
    auto ctx = NewContextDefault();
    ContextSetPolicy(ctx, expectedPolicy);

    auto result = ContextGetPolicy(ctx);

    EXPECT_THAT(result, ::testing::Eq(expectedPolicy));
    ContextDestroy(ctx);
}

TEST_F(FitContextBaseTest, should_return_default_policy_when_get_policy_given_null_context)
{
    FitablePolicy expectedPolicy {FitablePolicy::POLICY_DEFAULT};

    ContextSetPolicy(nullptr, expectedPolicy);

    auto result = ContextGetPolicy(nullptr);

    EXPECT_THAT(result, ::testing::Eq(expectedPolicy));
}