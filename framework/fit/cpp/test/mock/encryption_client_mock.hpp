/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/08/03
 * Notes:       :
 */

#ifndef CONFIG_VALUE_MOCK_HPP
#define CONFIG_VALUE_MOCK_HPP

#include <gmock/gmock.h>
#include <fit/internal/runtime/crypto/encryption_client.h>

class EncryptionClientMock : public Fit::EncryptionClient {
public:
    MOCK_METHOD0(Init, int32_t());
    MOCK_METHOD2(Encrypt, int32_t(const Fit::string&, Fit::string&));
    MOCK_METHOD2(Decrypt, int32_t(const Fit::string&, Fit::string&));
};
#endif // CONFIG_VALUE_MOCK_HPP
