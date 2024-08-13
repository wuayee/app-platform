/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供 secure access client
 * Author       : w00561424
 * Date:        : 2024/07/27
 */
#ifndef SECURE_ACCESS_CLIENT_H
#define SECURE_ACCESS_CLIENT_H
#include <component/com_huawei_fit_secure_access_token_info/1.0.0/cplusplus/TokenInfo.hpp>
#include <fit/stl/mutex.hpp>
namespace Fit {
class SecureAccessClient {
public:
    int32_t GetToken(Fit::string& token);
    int32_t UpdateToken(Fit::string& token);
private:
    int32_t ApplyToken();
    int32_t RefreshToken(const Fit::string& freshToken);
private:
    Fit::mutex mutex_ {};
    ::fit::secure::access::TokenInfo tokenInfo_ {};
};
}
#endif