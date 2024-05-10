/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/14
 * Notes:       :
 */
#include <memory>
#include <fit_code.h>
#include <fit/fit_log.h>
#include <plugin/fit_crypto_kms/src/get_static_token.h>
#include <fit/stl/memory.hpp>
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class GetStaticTokenTest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
public:
};

TEST_F(GetStaticTokenTest, should_return_ptr_when_create_given_path_empty)
{
    // given
    // when
    Fit::shared_ptr<KmsToken> kmsClientPtr = KmsToken::Create();
    // then
    EXPECT_NE(kmsClientPtr, nullptr);
}

TEST_F(GetStaticTokenTest, should_return_empty_when_get_static_token_given_empty)
{
    // given
    // when
    Fit::shared_ptr<KmsTokenImpl> kmsClientPtr = Fit::make_shared<KmsTokenImpl>();
    Fit::string staticToken = kmsClientPtr->GetStaticToken();
    // then
    EXPECT_EQ(staticToken, "");
}