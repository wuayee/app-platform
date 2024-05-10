/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/12 20:28
 */

#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <fit/external/util/context/context_api.hpp>

using namespace Fit::Context;

class FitContextTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override {}
};


struct Message1 {
    Message1()
    {
        std::cout << "Call message1 construct" << std::endl;
    }

    ~Message1()
    {
        std::cout << "Call message1 deconstruct" << std::endl;
    }
    int a{1};
};

struct Message2 {
    Message2()
    {
        std::cout << "Call Message2 construct" << std::endl;
    }

    ~Message2()
    {
        std::cout << "Call Message2 deconstruct" << std::endl;
    }

    int a{2};
};

struct Message3 {
    Message3()
    {
        std::cout << "Call Message3 construct" << std::endl;
    }

    ~Message3()
    {
        std::cout << "Call Message3 deconstruct" << std::endl;
    }

    Message2 *m2{nullptr};
    int a{3};
};

TEST_F(FitContextTest, should_return_nullptr_when_ContextMalloc_when_given_bad_AllocFunctor)
{
    auto ctx = NewContext([](size_t size) {return nullptr;}, [](void *obj) {});

    auto mem = ContextMallocNoManage(ctx, 100);

    EXPECT_EQ(mem, nullptr);
    ContextDestroy(ctx);
}

TEST_F(FitContextTest, should_return_InFree_when_ContextMalloc_when_given_FreeFunctor)
{
    auto ctx = NewContext([](size_t size) {return nullptr;},
        [](void *obj) {std::cout << "InFree" << std::endl;});
    testing::internal::CaptureStdout();

    auto mem = ContextMallocNoManage(ctx, 100);
    EXPECT_EQ(mem, nullptr);

    ContextFreeNoManage(ctx, mem);
    auto stdout = testing::internal::GetCapturedStdout();
    EXPECT_EQ(stdout, "InFree\n");
    ContextDestroy(ctx);
}

TEST_F(FitContextTest, should_return_nullptr_when_NewObj_given_ContextMalloc_return_nullptr)
{
    auto ctx = NewContext([](size_t size) {return nullptr;},
        [](void *obj) {std::cout << "InFree" << std::endl;});

    auto m1 = NewObj<Message1>(ctx);

    EXPECT_EQ(nullptr, m1);
    ContextDestroy(ctx);
}

TEST_F(FitContextTest, should_return_right_info_when_NewObj_given_one_messsage_create)
{
    auto ctx = NewContext();
    auto expect = "Call message1 construct\nCall message1 deconstruct\n";
    testing::internal::CaptureStdout();

    auto m1 = NewObj<Message1>(ctx);
    EXPECT_EQ(m1->a, 1);

    ContextFreeAll(ctx);
    ContextDestroy(ctx);

    auto stdout = testing::internal::GetCapturedStdout();

    EXPECT_EQ(stdout, expect);
}

TEST_F(FitContextTest, should_return_right_info_when_NewObj_given_two_messsage_create)
{
    auto ctx = NewContext();
    auto expect =
        "Call message1 construct\nCall Message2 construct\nCall Message2 deconstruct\nCall message1 deconstruct\n";
    testing::internal::CaptureStdout();

    auto m1 = NewObj<Message1>(ctx);
    EXPECT_EQ(m1->a, 1);
    auto m2 = NewObj<Message2>(ctx);
    EXPECT_EQ(m2->a, 2);

    ContextFreeAll(ctx);
    ContextDestroy(ctx);

    auto stdout = testing::internal::GetCapturedStdout();

    EXPECT_EQ(stdout, expect);
}

TEST_F(FitContextTest, should_return_right_info_when_NewObj_given_two_messsage_and_include_create)
{
    auto ctx = NewContext();
    auto expect = "Call message1 construct\nCall Message3 construct\nCall Message2 construct\n"
                  "Call Message2 deconstruct\nCall Message3 deconstruct\nCall message1 deconstruct\n";
    testing::internal::CaptureStdout();

    auto m1 = NewObj<Message1>(ctx);
    EXPECT_EQ(m1->a, 1);
    auto m3 = NewObj<Message3>(ctx);
    EXPECT_EQ(m3->a, 3);
    m3->m2 = NewObj<Message2>(ctx);
    EXPECT_EQ(m3->m2->a, 2);

    ContextFreeAll(ctx);
    ContextDestroy(ctx);

    auto stdout = testing::internal::GetCapturedStdout();

    EXPECT_EQ(stdout, expect);
}