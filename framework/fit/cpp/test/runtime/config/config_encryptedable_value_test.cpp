/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : test for encryptedable value
 * Author       : songyongtan
 * Create       : 2023-08-18
 * Notes:       :
 */

#include "runtime/config/config_encryptedable_value.hpp"
#include "runtime/crypto/crypto_manager.hpp"

#include <gmock/gmock-actions.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace testing;
using namespace Fit;
using namespace Fit::Config;

namespace {
class ConfigEncryptedableValueTest : public Crypto {
public:
    MOCK_CONST_METHOD0(GetName, const char*());
    MOCK_CONST_METHOD3(Decrypt, FitCode(const char* data, uint32_t size, string& result));
};
}

TEST(FitConfigEncryptedableValueTest, should_return_decrypted_value_when_get_given_encrypted_value_with_exist_crypto)
{
    EncryptedableValue value("fit-encrypted(crypto(123))");
    string expectEncryptedContent = "123";
    string expectDecryptedContent = "456";
    // auto crypto = make_shared<TestCrypto>("crypto", expectDecryptedContent);
    auto crypto = make_shared<ConfigEncryptedableValueTest>();
    EXPECT_CALL(*crypto, GetName()).WillRepeatedly(Return("crypto"));
    EXPECT_CALL(*crypto, Decrypt(StrEq(expectEncryptedContent), Eq(expectEncryptedContent.size()), _))
        .WillOnce(DoAll(SetArgReferee<2>(expectDecryptedContent), Return(FIT_OK)));
    CryptoManager::Instance().Add(crypto);

    ASSERT_TRUE(value.IsString());
    ASSERT_EQ(value.GetType(), VALUE_TYPE_STRING);
    EXPECT_THAT(value.AsString(), Eq(expectDecryptedContent));
    CryptoManager::Instance().Remove(crypto);
}

TEST(FitConfigEncryptedableValueTest, should_return_default_value_when_get_with_default_given_not_exist_encrypted_type)
{
    EncryptedableValue value("fit-encrypted(crypto1(123))");
    string expectReuslt = "default value";
    EXPECT_THAT(value.AsString(expectReuslt), Eq(expectReuslt));
}

TEST(FitConfigEncryptedableValueTest, should_return_default_value_when_get_with_default_given_decrypt_error)
{
    EncryptedableValue value("fit-encrypted(crypto(123))");
    string expectEncryptedContent = "123";
    string expectReuslt = "456";
    auto crypto = make_shared<ConfigEncryptedableValueTest>();
    EXPECT_CALL(*crypto, GetName()).WillRepeatedly(Return("crypto"));
    EXPECT_CALL(*crypto, Decrypt(StrEq(expectEncryptedContent), Eq(expectEncryptedContent.size()), _))
        .WillRepeatedly(Return(FIT_ERR_FAIL));
    CryptoManager::Instance().Add(crypto);

    ASSERT_TRUE(value.IsString());
    ASSERT_EQ(value.GetType(), VALUE_TYPE_STRING);
    EXPECT_THAT(value.AsString(expectReuslt), Eq(expectReuslt));
    EXPECT_THAT(value.AsString(expectReuslt.c_str()), Eq(expectReuslt));
    CryptoManager::Instance().Remove(crypto);
}
