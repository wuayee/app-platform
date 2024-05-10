/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/02
 * Notes:       :
 */
#include <memory>
#include <fit_code.h>
#include <fit/fit_log.h>
#include <plugin/fit_crypto_kms/src/kms_client.h>
#include "gmock/gmock.h"
using namespace Fit;

class KMSClientTest : public ::testing::Test {
public:
    void SetUp() override
    {
        Fit::string appId = "com.huawei.dsiv.fit.lab";
        Fit::string staticToken = "MFV5ZUhHbGRpeVRIWmMzWGx1bGZubEdMNnIxVmx \
                                   RYVV6MDAtdnUzbWRNQU1ZanJYR1dDd2poTUFtV1 \
                                   dUaUdhc1Q2OExCUzJYVlhrbERIZEtTR2doX0E=";
        Fit::string authIp = "oauth2.huawei.com";
        Fit::string hisIp = "his.huawei.com";
        kMSClient_ = KMSClient::Create(appId, staticToken, authIp, hisIp);
    }

    void TearDown() override
    {
    }
public:
    std::shared_ptr<KMSClient> kMSClient_ {nullptr};
};

TEST_F(KMSClientTest, should_return_ok_when_init_given_empty)
{
    // given
    // when
    int32_t ret = kMSClient_->Init();
    // then
    EXPECT_EQ(ret, FIT_OK);
}

TEST_F(KMSClientTest, should_return_ok_when_encrypt_and_decrypt_given_test_pass_word)
{
    // given
    Fit::string plaintext = "test_plain_text";
    // when
    kMSClient_->Init();
    Fit::string actualCiphertextBlob {};
    int32_t encryptRet = kMSClient_->Encrypt(plaintext, actualCiphertextBlob);
    Fit::string actualPlaintext {};
    int32_t decryptRet = kMSClient_->Decrypt(actualCiphertextBlob, actualPlaintext);
    // then
    EXPECT_EQ(encryptRet, FIT_OK);
    EXPECT_EQ(decryptRet, FIT_OK);
    EXPECT_EQ(actualPlaintext, plaintext);
}