/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供对的 https 客户端的实现
 * Author       : w00561424
 * Date:        : 2024/05/09
 */
#ifndef CURL_HTTPS_CLIENT_H
#define CURL_HTTPS_CLIENT_H
#include <include/curl_http_client.h>
namespace Fit {
class CurlHttpsClient : public CurlHttpClient {
public:
    CurlHttpsClient(string contextPath, const HttpConfig* config, string hostAndPort);
    FitCode RequestResponse(const fit::hakuna::kernel::broker::client::RequestParam& req, Response& result) override;
private:
    CURL* PreProcess(int64_t timeoutMs) override;
    FitCode AfterProcess(CURL* curl) override;
    void OpenSsl(CURL* curl);
private:
    const HttpConfig* config_ {};
};
}
#endif