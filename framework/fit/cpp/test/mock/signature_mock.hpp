/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/24
 */
#ifndef SIGNATURE_MOCK_HPP
#define SIGNATURE_MOCK_HPP
#include <gmock/gmock.h>
#include <secure_access/include/signature/signature.h>
namespace Fit {
class SignatureMock : public Signature {
public:
    MOCK_METHOD2(Sign, string(const string& ak, const string& timestamp));
    MOCK_METHOD3(Verify, bool(const string& ak, const string& timestamp, const string& signature));
};
}
#endif