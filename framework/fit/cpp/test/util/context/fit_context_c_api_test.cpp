/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/29 14:29
 */

#include <fit/external/util/context/context_c_api.h>
#include <fit/fit_code.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

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

class FitContextCApiTest : public ::testing::Test {
public:
    void SetUp() override
    {
        ctx_ = NewContextWithMemFunc(TestMalloc, TestFree);
        ContextKV kv;
        RestoreGlobalContext(ctx_, &kv, 0);
        EXPECT_THAT(nullptr, ::testing::Ne(ctx_));
    }

    void TearDown() override
    {
        ContextDestroy(ctx_);
    }
    ContextObj ctx_;
};

TEST_F(FitContextCApiTest, should_return_not_nullptr_when_NewContextDefault)
{
    auto ctx = NewContextDefault();
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextCApiTest, should_return_nullptr_when_NewContextWithMemFunc_given_bad_allocator_func)
{
    auto ctx = NewContextWithMemFunc(nullptr, nullptr);
    EXPECT_THAT(nullptr, ::testing::Eq(ctx));
    ContextDestroy(ctx);
}

TEST_F(FitContextCApiTest,
    should_return_cnt_when_ContextMalloc_given_right_allocator_func_and_auto_free_with_ctx_destory)
{
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    auto data = ContextMalloc(ctx, 10);
    auto data1 = ContextMalloc(ctx, 10);
    ContextDestroy(ctx);

    // malloc data 2, malloc entry 2
    EXPECT_THAT(g_allocCnt, ::testing::Eq(4));
    EXPECT_THAT(g_freeCnt, ::testing::Eq(4));

    g_allocCnt = 0;
    g_freeCnt = 0;
    ContextDestroy(ctx);
}

TEST_F(FitContextCApiTest, should_return_cnt_when_ContextMallocNoManage_given_right_allocator_func)
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

TEST_F(FitContextCApiTest, should_return_cnt_when_ContextMalloc_given_right_allocator_func_and_context_free)
{
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    auto data = ContextMalloc(ctx, 10);
    auto data1 = ContextMalloc(ctx, 10);

    ContextFreeHasManaged(ctx, data);
    ContextFreeHasManaged(ctx, data1);

    // malloc data 2, malloc entry 2
    EXPECT_THAT(g_allocCnt, ::testing::Eq(4));
    EXPECT_THAT(g_freeCnt, ::testing::Eq(4));

    g_allocCnt = 0;
    g_freeCnt = 0;
    ContextDestroy(ctx);
}

TEST_F(FitContextCApiTest, should_return_cnt_0_when_ContextFreeHasManaged_given_right_allocator_func_and_null_data)
{
    auto ctx = NewContextWithMemFunc(TestMalloc, TestFree);
    EXPECT_THAT(nullptr, ::testing::Ne(ctx));

    ContextFreeHasManaged(ctx, nullptr);

    EXPECT_THAT(g_freeCnt, ::testing::Eq(0));

    g_allocCnt = 0;
    g_freeCnt = 0;
    ContextDestroy(ctx);
}

TEST_F(FitContextCApiTest, should_return_BAD_ALLOC_when_ContextStringResize_given_null_ctx)
{
    FitCode expectCode {FIT_ERR_CTX_BAD_ALLOC};
    Fit_String str;

    auto ret = ContextStringResize(nullptr, 100, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextStringResize_given_size_0)
{
    FitCode expectCode {FIT_OK};
    Fit_String str;

    auto ret = ContextStringResize(ctx_, 0, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextStringResize_given_size_100)
{
    FitCode expectCode {FIT_OK};
    Fit_String str;

    auto ret = ContextStringResize(ctx_, 100, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Ne(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(100));
}

TEST_F(FitContextCApiTest, should_return_BAD_ALLOC_when_ContextStringAssign_given_null_ctx)
{
    FitCode expectCode {FIT_ERR_CTX_BAD_ALLOC};
    Fit_String str;

    auto ret = ContextStringAssign(nullptr, "123", &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextStringAssign_given_null_fromString)
{
    FitCode expectCode {FIT_OK};
    Fit_String str;

    auto ret = ContextStringAssign(ctx_, nullptr, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextStringAssign_given_not_null_fromString)
{
    FitCode expectCode {FIT_OK};
    Fit_String str;
    const char *fromString {"test"};

    auto ret = ContextStringAssign(ctx_, fromString, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Ne(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(strlen(fromString)));
}

TEST_F(FitContextCApiTest, should_return_BAD_ALLOC_when_ContextBytesResize_given_null_ctx)
{
    FitCode expectCode {FIT_ERR_CTX_BAD_ALLOC};
    Fit_Bytes str;

    auto ret = ContextBytesResize(nullptr, 100, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextBytesResize_given_size_0)
{
    FitCode expectCode {FIT_OK};
    Fit_Bytes str;

    auto ret = ContextBytesResize(ctx_, 0, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextBytesResize_given_size_100)
{
    FitCode expectCode {FIT_OK};
    Fit_Bytes str;

    auto ret = ContextBytesResize(ctx_, 100, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Ne(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(100));
}

TEST_F(FitContextCApiTest, should_return_BAD_ALLOC_when_ContextBytesAssign_given_null_ctx)
{
    FitCode expectCode {FIT_ERR_CTX_BAD_ALLOC};
    Fit_Bytes str;

    auto ret = ContextBytesAssign(nullptr, "123", 3, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_FIT_OK_when_ContextBytesAssign_given_null_fromBytes)
{
    FitCode expectCode {FIT_OK};
    Fit_Bytes str;

    auto ret = ContextBytesAssign(ctx_, nullptr, 1, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest, should_return_error_when_ContextBytesAssign_given_size_0)
{
    FitCode expectCode {FIT_ERR_FAIL};
    Fit_Bytes str;

    auto ret = ContextBytesAssign(ctx_, "123", 0, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Eq(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(0));
}

TEST_F(FitContextCApiTest,
    should_return_FIT_OK_when_ContextBytesAssign_given_not_null_fromBytes_and_szie_more_than_0)
{
    FitCode expectCode {FIT_OK};
    Fit_Bytes str;
    const char *fromBytes {"test"};
    size_t size {4};

    auto ret = ContextBytesAssign(ctx_, fromBytes, size, &str);

    EXPECT_THAT(ret, ::testing::Eq(expectCode));
    EXPECT_THAT(str.data, ::testing::Ne(nullptr));
    EXPECT_THAT(str.size, ::testing::Eq(size));
}

TEST_F(FitContextCApiTest, should_return_false_when_PutGlobalContext_given_key_is_nullptr)
{
    bool expected {false};
    Fit_String value;

    ContextStringAssign(ctx_, "123", &value);

    auto ret = PutGlobalContext(ctx_, nullptr, &value);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_false_when_PutGlobalContext_given_key_data_is_nullptr)
{
    bool expected {false};
    Fit_String key;
    Fit_StringInit(key);
    Fit_String value;

    ContextStringAssign(ctx_, "123", &value);

    auto ret = PutGlobalContext(ctx_, &key, &value);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_false_when_PutGlobalContext_given_value_is_nullptr)
{
    bool expected {false};
    Fit_String key;

    ContextStringAssign(ctx_, "123", &key);

    auto ret = PutGlobalContext(ctx_, &key, nullptr);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_false_when_PutGlobalContext_given_value_data_is_nullptr)
{
    bool expected {false};
    Fit_String key;
    Fit_StringInit(key);
    Fit_String value;
    Fit_StringInit(value);

    ContextStringAssign(ctx_, "123", &key);

    auto ret = PutGlobalContext(ctx_, &key, &value);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_false_when_PutGlobalContext_given_ctx_is_nullptr)
{
    bool expected {false};
    Fit_String key;
    Fit_String value;
    Fit_StringInit(value);

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);

    auto ret = PutGlobalContext(nullptr, &key, &value);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_true_when_PutGlobalContext_given_all_param_is_right)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);

    auto ret = PutGlobalContext(ctx_, &key, &value);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_true_when_GetGlobalContext_given_has_set)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;
    Fit_String *expectValue {nullptr};

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);
    auto ret = PutGlobalContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    ret = GetGlobalContext(ctx_, &key, &expectValue);
    EXPECT_THAT(ret, ::testing::Eq(expected));
    EXPECT_THAT(expectValue->size, ::testing::Eq(3));
}

TEST_F(FitContextCApiTest, should_return_true_false_when_GetGlobalContext_given_has_set_and_remove)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;
    Fit_String *expectValue {nullptr};

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);
    auto ret = PutGlobalContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    ret = RemoveGlobalContext(ctx_, &key);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    ret = GetGlobalContext(ctx_, &key, &expectValue);
    EXPECT_THAT(ret, ::testing::Eq(false));
}

TEST_F(FitContextCApiTest, should_return_all_kv_when_GetAllGlobalContext_given_has_set)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;
    ContextKV *expectValue {nullptr};
    uint32_t size {0};

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);
    auto ret = PutGlobalContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    ContextStringAssign(ctx_, "456", &key);
    ContextStringAssign(ctx_, "456", &value);
    ret = PutGlobalContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    GetAllGlobalContext(ctx_, &expectValue, &size);
    EXPECT_THAT(size, ::testing::Eq(2));
    EXPECT_THAT(expectValue, ::testing::Ne(nullptr));
    EXPECT_THAT(std::string(expectValue[0].key.data, expectValue[0].key.size), ::testing::Eq("123"));
    EXPECT_THAT(std::string(expectValue[0].value.data, expectValue[0].value.size), ::testing::Eq("123"));
    EXPECT_THAT(std::string(expectValue[1].key.data, expectValue[1].key.size), ::testing::Eq("456"));
    EXPECT_THAT(std::string(expectValue[1].value.data, expectValue[1].value.size), ::testing::Eq("456"));
}

TEST_F(FitContextCApiTest, should_return_true_when_RestoreGlobalContext_given_has_set)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;
    ContextKV *expectValue {nullptr};
    uint32_t size {0};

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);
    auto ret = PutGlobalContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    ContextKV newKv[2];
    ContextStringAssign(ctx_, "111", &newKv[0].key);
    ContextStringAssign(ctx_, "111", &newKv[0].value);
    ContextStringAssign(ctx_, "222", &newKv[1].key);
    ContextStringAssign(ctx_, "222", &newKv[1].value);

    ret = RestoreGlobalContext(ctx_, newKv, 2);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    GetAllGlobalContext(ctx_, &expectValue, &size);
    EXPECT_THAT(size, ::testing::Eq(2));
    EXPECT_THAT(expectValue, ::testing::Ne(nullptr));
    EXPECT_THAT(std::string(expectValue[0].key.data, expectValue[0].key.size), ::testing::Eq("111"));
    EXPECT_THAT(std::string(expectValue[0].value.data, expectValue[0].value.size), ::testing::Eq("111"));
    EXPECT_THAT(std::string(expectValue[1].key.data, expectValue[1].key.size), ::testing::Eq("222"));
    EXPECT_THAT(std::string(expectValue[1].value.data, expectValue[1].value.size), ::testing::Eq("222"));
}

TEST_F(FitContextCApiTest, should_return_true_when_PutOneRouteContext_given_right_data)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);

    auto ret = PutOneRouteContext(ctx_, &key, &value);

    EXPECT_THAT(ret, ::testing::Eq(expected));
}

TEST_F(FitContextCApiTest, should_return_data_when_GetAllRouteContext_given_has_set)
{
    bool expected {true};
    Fit_String key;
    Fit_String value;
    ContextKV *expectValue {nullptr};
    uint32_t size {0};

    ContextStringAssign(ctx_, "123", &key);
    ContextStringAssign(ctx_, "123", &value);
    auto ret = PutOneRouteContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    ContextStringAssign(ctx_, "456", &key);
    ContextStringAssign(ctx_, "456", &value);
    ret = PutOneRouteContext(ctx_, &key, &value);
    EXPECT_THAT(ret, ::testing::Eq(expected));

    GetAllRouteContext(ctx_, &expectValue, &size);
    EXPECT_THAT(size, ::testing::Eq(2));
    EXPECT_THAT(expectValue, ::testing::Ne(nullptr));
    EXPECT_THAT(std::string(expectValue[0].key.data, expectValue[0].key.size), ::testing::Eq("123"));
    EXPECT_THAT(std::string(expectValue[0].value.data, expectValue[0].value.size), ::testing::Eq("123"));
    EXPECT_THAT(std::string(expectValue[1].key.data, expectValue[1].key.size), ::testing::Eq("456"));
    EXPECT_THAT(std::string(expectValue[1].value.data, expectValue[1].value.size), ::testing::Eq("456"));
}