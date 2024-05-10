/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/29 14:29
 */

#include <fit/external/util/context/context_api.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <thread>

namespace {
int g_cppMallocCnt = 0;
int g_cppFreeCnt = 0;
std::function<void*(size_t)> mallocFunctor = [](size_t size) ->void* {
    g_cppMallocCnt++;
    return malloc(size);
};

std::function<void(void *obj)> freeFunctor = [](void *data) {
    g_cppFreeCnt++;
    free(data);
};
}

class FitContextApiTest : public ::testing::Test {
public:
    void SetUp() override
    {
        ctx_ = Fit::Context::NewContext();
        Fit::Context::Global::RestoreGlobalContext(ctx_, {});
        EXPECT_THAT(nullptr, ::testing::Ne(ctx_));
    }

    void TearDown() override
    {
        ContextDestroy(ctx_);
    }

    ContextObj ctx_;
};

TEST_F(FitContextApiTest, should_return_not_nullptr_when_NewContextDefault)
{
    auto ctx = Fit::Context::NewContext();
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextApiTest, should_return_nullptr_when_NewContext_given_bad_allocator_func)
{
    auto ctx = Fit::Context::NewContext(nullptr, nullptr);
    EXPECT_THAT(nullptr, ::testing::Eq(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextApiTest, should_return_not_nullptr_when_NewContext_given_right_allocator_func)
{
    auto ctx = Fit::Context::NewContext(mallocFunctor, freeFunctor);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextApiTest, should_return_cnt_when_NewObj_given_right_allocator_func)
{
    auto ctx = Fit::Context::NewContext(mallocFunctor, freeFunctor);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    Fit::Context::NewObj<Fit::string>(ctx);
    ContextDestroy(ctx);

    EXPECT_THAT(g_cppMallocCnt, ::testing::Eq(2));
    EXPECT_THAT(g_cppFreeCnt, ::testing::Eq(2));

    g_cppMallocCnt = 0;
    g_cppFreeCnt = 0;
}

TEST_F(FitContextApiTest, should_return_empty_when_GetGenericableId_given_gid_not_set)
{
    Fit::string ret;

    ret = Fit::Context::GetGenericableId(ctx_);

    EXPECT_THAT(ret, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_empty_when_GetGenericableId_given_nullptr_ctx)
{
    Fit::string ret;

    ret = Fit::Context::GetGenericableId(nullptr);

    EXPECT_THAT(ret, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_empty_when_GetGenericableId_given_gid_setted)
{
    Fit::string expect{"test"};

    Fit::Context::SetGenericableId(ctx_, expect);

    auto ret = Fit::Context::GetGenericableId(ctx_);

    EXPECT_THAT(ret, ::testing::Eq(expect));
}

TEST_F(FitContextApiTest, should_return_false_when_PutRouteContext_given_null_context)
{
    bool expect {false};

    auto ret = Fit::Context::PutRouteContext(nullptr, "123", "123");

    EXPECT_THAT(expect, ::testing::Eq(ret));
}

TEST_F(FitContextApiTest, should_return_data_when_GetAllRouteContext_given_has_setted)
{
    bool expect {true};

    auto ret = Fit::Context::PutRouteContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));
    ret = Fit::Context::PutRouteContext(ctx_, "456", "456");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    auto kvSet = Fit::Context::GetAllRouteContext(ctx_);

    EXPECT_THAT(kvSet["123"], ::testing::Eq("123"));
    EXPECT_THAT(kvSet["456"], ::testing::Eq("456"));
}

TEST_F(FitContextApiTest, should_return_value_when_GetGlobalContext_given_has_setted)
{
    bool expect {true};

    auto ret = Fit::Context::Global::PutGlobalContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));
    EXPECT_THAT(Fit::Context::Global::HasGlobalContext(ctx_), ::testing::Eq(true));

    auto value = Fit::Context::Global::GetGlobalContext(ctx_, "123");

    EXPECT_THAT(value, ::testing::Eq("123"));
}

TEST_F(FitContextApiTest, should_return_empty_value_when_GetGlobalContext_given_not_set)
{
    auto value = Fit::Context::Global::GetGlobalContext(ctx_, "123");

    EXPECT_THAT(value, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_empty_value_when_GetGlobalContext_given_has_setted_and_remove)
{
    bool expect {true};

    auto ret = Fit::Context::Global::PutGlobalContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    ret = Fit::Context::Global::RemoveGlobalContext(ctx_, "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    auto value = Fit::Context::Global::GetGlobalContext(ctx_, "123");

    EXPECT_THAT(value, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_data_when_GetAllGlobalContext_given_has_setted)
{
    bool expect {true};

    auto ret = Fit::Context::Global::PutGlobalContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));
    ret = Fit::Context::Global::PutGlobalContext(ctx_, "456", "456");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    auto kvSet = Fit::Context::Global::GetAllGlobalContext(ctx_);

    EXPECT_THAT(kvSet["123"], ::testing::Eq("123"));
    EXPECT_THAT(kvSet["456"], ::testing::Eq("456"));
}

TEST_F(FitContextApiTest, should_return_data_when_GetAllGlobalContext_given_Restore)
{
    bool expect {true};

    auto ret = Fit::Context::Global::PutGlobalContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));
    ret = Fit::Context::Global::PutGlobalContext(ctx_, "456", "456");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    ret = Fit::Context::Global::RestoreGlobalContext(ctx_, {
        {"111", "111"},
        {"222", "222"}
    });
    EXPECT_THAT(expect, ::testing::Eq(ret));

    auto kvSet = Fit::Context::Global::GetAllGlobalContext(ctx_);

    EXPECT_THAT(kvSet["123"], ::testing::Eq(""));
    EXPECT_THAT(kvSet["456"], ::testing::Eq(""));
    EXPECT_THAT(kvSet["111"], ::testing::Eq("111"));
    EXPECT_THAT(kvSet["222"], ::testing::Eq("222"));
}

TEST_F(FitContextApiTest, should_return_FIT_OK_when_GlobalContextSerialize_given_data)
{
    Fit::string expect {"{\"pair\":[{\"key\":\"123\",\"value\":\"123\"},{\"key\":\"456\",\"value\":\"456\"}]}"};

    auto ret = Fit::Context::Global::PutGlobalContext(ctx_, "123", "123");
    EXPECT_THAT(true, ::testing::Eq(ret));
    ret = Fit::Context::Global::PutGlobalContext(ctx_, "456", "456");
    EXPECT_THAT(true, ::testing::Eq(ret));

    Fit::string result;
    auto code = Fit::Context::Global::GlobalContextSerialize(ctx_, result);

    EXPECT_THAT(code, ::testing::Eq(0));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(FitContextApiTest, should_return_FIT_OK_when_GlobalContextDeserialize_given_data)
{
    Fit::string input {"{\"pair\":[{\"key\":\"123\",\"value\":\"123\"},{\"key\":\"456\",\"value\":\"456\"}]}"};

    auto code = Fit::Context::Global::GlobalContextDeserialize(ctx_, input);
    EXPECT_THAT(code, ::testing::Eq(0));

    auto kvSet = Fit::Context::Global::GetAllGlobalContext(ctx_);
    EXPECT_THAT(kvSet["123"], ::testing::Eq("123"));
    EXPECT_THAT(kvSet["456"], ::testing::Eq("456"));
}

TEST_F(FitContextApiTest, should_return_NOT_EQUAL_when_GLOBAL_given_mult_thread)
{
    Fit::string key{"key"};
    Fit::string thread1ValueExpect {"111"};
    Fit::string thread2ValueExpect {"222"};

    Fit::Context::Global::PutGlobalContext(ctx_, key, thread2ValueExpect);

    std::thread th1([this, &key, &thread1ValueExpect] {
        Fit::Context::Global::PutGlobalContext(ctx_, key, thread1ValueExpect);
        auto result = Fit::Context::Global::GetGlobalContext(ctx_, key);
        EXPECT_THAT(result, ::testing::Eq(thread1ValueExpect));
    });

    auto result = Fit::Context::Global::GetGlobalContext(ctx_, key);
    EXPECT_THAT(result, ::testing::Eq(thread2ValueExpect));
    th1.join();
}

TEST_F(FitContextApiTest, should_return_false_when_PutGlobalContext_given_null_ctx)
{
    auto ret = Fit::Context::Global::PutGlobalContext(nullptr, "123", "123");
    EXPECT_THAT(ret, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_empty_when_GetAllGlobalContext_given_null_ctx)
{
    auto ret = Fit::Context::Global::GetAllGlobalContext(nullptr);
    EXPECT_THAT(ret.empty(), ::testing::Eq(true));
}

TEST_F(FitContextApiTest, should_return_false_when_RemoveGlobalContext_given_null_ctx)
{
    auto ret = Fit::Context::Global::RemoveGlobalContext(nullptr, "123");
    EXPECT_THAT(ret, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_empty_when_GetGlobalContext_given_null_ctx)
{
    auto ret = Fit::Context::Global::GetGlobalContext(nullptr, "123");
    EXPECT_THAT(ret, ::testing::IsEmpty());
}

TEST_F(FitContextApiTest, should_return_false_when_RestoreGlobalContext_given_null_ctx)
{
    auto ret = Fit::Context::Global::RestoreGlobalContext(nullptr, {});
    EXPECT_THAT(ret, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_false_when_HasGlobalContext_given_empty_context)
{
    auto result = Fit::Context::Global::HasGlobalContext(ctx_);
    EXPECT_THAT(result, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_false_when_HasGlobalContext_given_null_ctx)
{
    auto result = Fit::Context::Global::HasGlobalContext(nullptr);
    EXPECT_THAT(result, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_fail_when_GlobalContextSerialize_given_null_ctx)
{
    Fit::string buffer;
    auto ret = Fit::Context::Global::GlobalContextSerialize(nullptr, buffer);
    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_PARAM));
}

TEST_F(FitContextApiTest, should_return_fail_when_GlobalContextDeserialize_given_null_ctx)
{
    Fit::string buffer;
    auto ret = Fit::Context::Global::GlobalContextDeserialize(nullptr, buffer);
    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_PARAM));
}

TEST_F(FitContextApiTest, should_return_value_when_GetExceptionContext_given_has_setted)
{
    bool expect {true};

    auto ret = Fit::Context::Exception::PutExceptionContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));
    EXPECT_THAT(Fit::Context::Exception::HasExceptionContext(ctx_), ::testing::Eq(true));

    auto value = Fit::Context::Exception::GetExceptionContext(ctx_, "123");

    EXPECT_THAT(value, ::testing::Eq("123"));
}

TEST_F(FitContextApiTest, should_return_empty_value_when_GetExceptionContext_given_not_set)
{
    auto value = Fit::Context::Exception::GetExceptionContext(ctx_, "123");

    EXPECT_THAT(value, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_empty_value_when_GetExceptionContext_given_null_ctx)
{
    auto value = Fit::Context::Exception::GetExceptionContext(nullptr, "123");

    EXPECT_THAT(value, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_empty_value_when_GetExceptionContext_given_has_setted_and_remove)
{
    bool expect {true};

    auto ret = Fit::Context::Exception::PutExceptionContext(ctx_, "123", "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    ret = Fit::Context::Exception::RemoveExceptionContext(ctx_, "123");
    EXPECT_THAT(expect, ::testing::Eq(ret));

    auto value = Fit::Context::Exception::GetExceptionContext(ctx_, "123");

    EXPECT_THAT(value, ::testing::Eq(""));
}

TEST_F(FitContextApiTest, should_return_false_when_PutExceptionContext_given_null_ctx)
{
    auto ret = Fit::Context::Exception::PutExceptionContext(nullptr, "123", "123");
    EXPECT_THAT(ret, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_empty_when_GetAllExceptionContext_given_null_ctx)
{
    auto ret = Fit::Context::Exception::GetAllExceptionContext(nullptr);
    EXPECT_THAT(ret.empty(), ::testing::Eq(true));
}

TEST_F(FitContextApiTest, should_return_false_when_RemoveExceptionContext_given_null_ctx)
{
    auto ret = Fit::Context::Exception::RemoveExceptionContext(nullptr, "123");
    EXPECT_THAT(ret, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_FIT_OK_when_SerializeExceptionContext_given_data)
{
    Fit::string expect {R"({"123":"123","456":"456"})"};

    auto ret = Fit::Context::Exception::PutExceptionContext(ctx_, "123", "123");
    EXPECT_THAT(true, ::testing::Eq(ret));
    ret = Fit::Context::Exception::PutExceptionContext(ctx_, "456", "456");
    EXPECT_THAT(true, ::testing::Eq(ret));

    Fit::string result;
    auto code = Fit::Context::Exception::SerializeExceptionContext(ctx_, result);

    EXPECT_THAT(code, ::testing::Eq(0));
    EXPECT_THAT(result, ::testing::Eq(expect));
}

TEST_F(FitContextApiTest, should_return_FIT_OK_when_DeserializeExceptionContext_given_data)
{
    Fit::string input {R"({"123":"123","456":"456"})"};

    auto code = Fit::Context::Exception::DeserializeExceptionContext(ctx_, input);
    EXPECT_THAT(code, ::testing::Eq(0));

    auto kvSet = Fit::Context::Exception::GetAllExceptionContext(ctx_);
    EXPECT_THAT(kvSet["123"], ::testing::Eq("123"));
    EXPECT_THAT(kvSet["456"], ::testing::Eq("456"));
}

TEST_F(FitContextApiTest, should_return_not_same_when_GetExceptionContext_given_multi_thread)
{
    Fit::string key{"key"};
    Fit::string thread1ValueExpect {"111"};
    Fit::string thread2ValueExpect {"222"};

    Fit::Context::Exception::PutExceptionContext(ctx_, key, thread2ValueExpect);

    std::thread th1([this, &key, &thread1ValueExpect] {
        Fit::Context::Exception::PutExceptionContext(ctx_, key, thread1ValueExpect);
        auto result = Fit::Context::Exception::GetExceptionContext(ctx_, key);
        EXPECT_THAT(result, ::testing::Eq(thread1ValueExpect));
    });

    auto result = Fit::Context::Exception::GetExceptionContext(ctx_, key);
    EXPECT_THAT(result, ::testing::Eq(thread2ValueExpect));
    th1.join();
}

TEST_F(FitContextApiTest, should_return_false_when_HasExceptionContext_given_empty_context)
{
    auto result = Fit::Context::Exception::HasExceptionContext(ctx_);
    EXPECT_THAT(result, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_false_when_HasExceptionContext_given_null_ctx)
{
    auto result = Fit::Context::Exception::HasExceptionContext(nullptr);
    EXPECT_THAT(result, ::testing::Eq(false));
}

TEST_F(FitContextApiTest, should_return_fail_when_SerializeExceptionContext_given_null_ctx)
{
    Fit::string buffer;
    auto ret = Fit::Context::Exception::SerializeExceptionContext(nullptr, buffer);
    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_PARAM));
}

TEST_F(FitContextApiTest, should_return_fail_when_DeserializeExceptionContext_given_null_ctx)
{
    Fit::string buffer;
    auto ret = Fit::Context::Exception::DeserializeExceptionContext(nullptr, buffer);
    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_PARAM));
}

TEST_F(FitContextApiTest, should_return_nullptr_when_ContextGetTargetAddress_given_address_not_set)
{
    Fit::Context::TargetAddress* targetAddressPtr = Fit::Context::ContextGetTargetAddress(ctx_);
    EXPECT_THAT(targetAddressPtr, nullptr);
}

TEST_F(FitContextApiTest, should_return_nullptr_when_ContextGetTargetAddress_given_nullptr_ctx)
{
    Fit::Context::TargetAddress* targetAddressPtr = Fit::Context::ContextGetTargetAddress(nullptr);
    EXPECT_THAT(targetAddressPtr, nullptr);
}

TEST_F(FitContextApiTest, should_return_address_when_ContextGetTargetAddress_given_address_setted)
{
    Fit::Context::TargetAddress targetAddress = {"workerId", "host", 0, 3, {0}};
    int32_t result = Fit::Context::ContextSetTargetAddress(ctx_, &targetAddress);
    Fit::Context::TargetAddress* targetAddressPtr = Fit::Context::ContextGetTargetAddress(ctx_);
    EXPECT_EQ(result, FIT_OK);
    EXPECT_EQ(targetAddressPtr->workerId, "workerId");
    EXPECT_EQ(targetAddressPtr->host, "host");
    EXPECT_EQ(targetAddressPtr->port, 0);
    EXPECT_EQ(targetAddressPtr->protocol, 3);
    EXPECT_THAT(targetAddressPtr->formats, testing::ElementsAre(0));
}

TEST_F(FitContextApiTest, should_return_error_when_ContextSetTargetAddress_given_nullptr_ctx)
{
    Fit::Context::TargetAddress targetAddress = {"workerId", "host", 0, 3, {0}};
    int32_t result = Fit::Context::ContextSetTargetAddress(nullptr, &targetAddress);
    EXPECT_EQ(result, FIT_ERR_FAIL);
}

TEST_F(FitContextApiTest, should_return_ok_when_ContextSetTargetAddress_given_correct_address)
{
    Fit::Context::TargetAddress targetAddress = {"workerId", "host", 0, 3, {0}};
    int32_t result = Fit::Context::ContextSetTargetAddress(ctx_, &targetAddress);
    EXPECT_EQ(result, FIT_OK);
}