/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework;
using namespace Fit::Framework::Annotation;

namespace {
int32_t ReturnInput(void *ctx, int32_t a)
{
    return a;
}

int32_t EmptyArgs(void *ctx)
{
    return 5;
}
}

TEST(FitableRegistrarTest, ShouldReturnSetValueWhenInvokeFunctionProxyGivenValidProxyAndInput)
{
    int32_t expectResult {111};
    auto proxy = FitableFunctionWrapper<int32_t, void *, int32_t>(ReturnInput).GetProxy();

    Arguments args {(void*)nullptr, expectResult};

    EXPECT_THAT(proxy(args), ::testing::Eq(expectResult));
}

TEST(FitableRegistrarTest, ShouldReturnFixedValueWhenInvokeFunctionProxyGivenNormalEmptyArgsFunc)
{
    int32_t expectResult {5};
    auto proxy = FitableRegistrar<int32_t(void *)>(EmptyArgs).SetGenericId("empty").GetFunctionProxy();

    Arguments args {(void*)nullptr};

    EXPECT_THAT(proxy(args), ::testing::Eq(expectResult));
}

TEST(FitableRegistrarTest, ShouldReturnFixedValueWhenInvokeFunctionProxyGivenLambdaEmptyArgsFunc)
{
    int32_t expectResult {5};
    auto proxy = FitableRegistrar<int32_t(void *)>([&expectResult](void *) {
        return expectResult;
    }).SetGenericId("empty").GetFunctionProxy();

    Arguments args {(void*)nullptr};

    EXPECT_THAT(proxy(args), ::testing::Eq(expectResult));
}

TEST(FitableRegistrarTest, ShouldReturnSumResultWhenInvokeFunctionProxyGivenLambdaTwoPointerArgsFunc)
{
    int32_t expectResult {5};
    auto proxy = FitableRegistrar<int32_t(void *, int32_t *, int32_t *)>(
        [](void *ctx, int32_t *a, int32_t *b) {
            return *a + *b;
        }).SetGenericId("test_tow_pointer and return sum").GetFunctionProxy();
    int a {2};
    int b {3};
    Arguments args {(void*)nullptr, &a, &b};

    EXPECT_THAT(proxy(args), ::testing::Eq(expectResult));
}

TEST(FitableRegistrarTest, ShouldReturnSumResultWhenInvokeFunctionProxyGivenLambdaTwoConstPointerArgsFunc)
{
    int32_t expectResult {3};
    auto proxy = FitableRegistrar<int32_t(void *, const std::string *, const int32_t *)>(
        [](void *ctx, const std::string *a, const int32_t *b) {
            return *b;
        }).SetGenericId("test_tow_pointer and return sum").GetFunctionProxy();
    const std::string a {"123"};
    const int b {3};
    Arguments args {(void*)nullptr, &a, &b};

    EXPECT_THAT(proxy(args), ::testing::Eq(expectResult));
}

TEST(FitableRegistrarTest,
    ShouldReturnSumResultInLastArgWhenInvokeFunctionProxyGivenLambdaTwoConstPointerArgsAndAReturnArgsFunc)
{
    int32_t expectReturn {0};
    int32_t expectResult {5};
    auto proxy = FitableRegistrar<int32_t(void *, const int32_t *, const int32_t *, int32_t *)>(
        [](void *ctx, const int32_t *a, const int32_t *b, int32_t *result) {
            *result = *a + *b;
            return 0;
        }).SetGenericId("test_two_pointer and a return arg equal sum").GetFunctionProxy();
    const int a {2};
    const int b {3};
    int result {};
    Arguments args {(void*)nullptr, &a, &b, &result};

    ASSERT_THAT(proxy(args), ::testing::Eq(expectReturn));
    EXPECT_THAT(result, ::testing::Eq(expectResult));
}

TEST(FitableRegistrarTest,
    ShouldReturnSumResultInLastArgWhenInvokeFunctionProxyGivenLambdaTAConstValueAndAConstPointerArgsAndAReturnArgsFunc)
{
    int32_t expectReturn {0};
    int32_t expectResult {5};
    auto proxy = FitableRegistrar<int32_t(void *, const int32_t, const int32_t *, int32_t *)>(
        [](void *ctx, const int32_t a, const int32_t *b, int32_t *result) {
            *result = a + *b;
            return 0;
        }).SetGenericId("test_two_pointer and a return arg equal sum").GetFunctionProxy();
    const int a {2};
    const int b {3};
    int result {};
    Arguments args {(void*)nullptr, a, &b, &result};

    ASSERT_THAT(proxy(args), ::testing::Eq(expectReturn));
    EXPECT_THAT(result, ::testing::Eq(expectResult));
}
