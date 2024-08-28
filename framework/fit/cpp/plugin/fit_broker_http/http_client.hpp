/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http client
 * Author       : songyongtan
 * Create       : 2023-08-01
 * Notes:       :
 */
#ifndef FIT_HTTP_CLIENT_HPP
#define FIT_HTTP_CLIENT_HPP
#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_request_response_v5/1.0.0/cplusplus/requestResponseV5.hpp>
namespace Fit {
class HttpClient {
public:
    struct Request {
        const bytes* metadata;
        const bytes* data;
    };
    struct Response {
        int32_t code;
        Fit::string message;
        bytes* metadata;
        bytes* data;
    };
    virtual FitCode RequestResponse(const fit::hakuna::kernel::broker::client::RequestParam& req, Response& result) = 0;
};
using HttpClientPtr = Fit::shared_ptr<HttpClient>;
}
#endif