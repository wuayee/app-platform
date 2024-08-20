/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/28
 */
#include <secure_access/include/signature/hmac_signature.h>
#include <mock/auth_key_repo_mock.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace testing;
class HmacSignatureTest : public ::testing::Test {
public:
    void SetUp() override
    {
        authKeyRepo_ = make_shared<AuthKeyRepoMock>();
        hmacSignature_ = new HmacSignature(authKeyRepo_);
        ak_ = "test_ak1";
        sk_ = "test_sk1";
        timestamp_ = "test_timestamp";
        authKey_.ak = ak_;
        authKey_.sk = sk_;
        authKey_.role = "provider";
        expectedSignature_ = "45387cf6b1a6e70e10f62895c02079b20c8a182b9a9752f57a6d11131a3a6461";
    }

    void TearDown() override
    {
        delete hmacSignature_;
        hmacSignature_ = nullptr;
    }
public:
    Fit::shared_ptr<AuthKeyRepoMock> authKeyRepo_ {};
    HmacSignature* hmacSignature_ {};
    Fit::string ak_ {};
    Fit::string sk_ {};
    string timestamp_ {};
    AuthKey authKey_ {};
    string expectedSignature_ {};
};

TEST_F(HmacSignatureTest, should_return_signature_when_sign_given_param)
{
    // given
    vector<AuthKey> authKeys {authKey_};
    EXPECT_CALL(*authKeyRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(authKeys));

    // when
    string signature = hmacSignature_->Sign(ak_, timestamp_);

    // then
    EXPECT_EQ(signature, expectedSignature_);
}

TEST_F(HmacSignatureTest, should_return_empty_when_sign_given_null_repo)
{
    // given
    HmacSignature hmacSignature(nullptr);

    // when
    string signature = hmacSignature.Sign(ak_, timestamp_);

    // then
    EXPECT_EQ(signature.empty(), true);
}


TEST_F(HmacSignatureTest, should_return_empty_when_sign_given_mock_query_empty)
{
    // given
    vector<AuthKey> authKeys {};
    EXPECT_CALL(*authKeyRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(authKeys));

    // when
    string signature = hmacSignature_->Sign(ak_, timestamp_);

    // then
    EXPECT_EQ(signature.empty(), true);
}

TEST_F(HmacSignatureTest, should_return_true_when_verify_given_param)
{
    // given
    vector<AuthKey> authKeys {authKey_};
    EXPECT_CALL(*authKeyRepo_, Query(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(authKeys));

    // when
    bool result = hmacSignature_->Verify(ak_, timestamp_, expectedSignature_);

    // then
    EXPECT_EQ(result, true);
}