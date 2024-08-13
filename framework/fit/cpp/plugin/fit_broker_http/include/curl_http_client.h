/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供对的 http 客户端的默认实现
 * Author       : w00561424
 * Date:        : 2024/05/09
 */
#ifndef CURL_HTTP_CLIENT_H
#define CURL_HTTP_CLIENT_H
#include <http_client.hpp>
#include <curl/include/curl/curl.h>
#include <http_config.hpp>
namespace Fit {
class CurlHttpClient : public HttpClient {
public:
    CurlHttpClient(string contextPath, const HttpConfig* config, string hostAndPort);
    FitCode RequestResponse(const fit::hakuna::kernel::broker::client::RequestParam& req, Response& result) override;
    static void GlobalInit();
    static void GlobalUninit();
protected:
    virtual CURL* PreProcess(int64_t timeoutMs);
    virtual FitCode AfterProcess(CURL* curl);
private:
    FitCode Call(CURL* curl, Response& result);
private:
    const string contextPath_;
    const HttpConfig* config_ {};
    string hostAndPort_ {};
};
}
#endif