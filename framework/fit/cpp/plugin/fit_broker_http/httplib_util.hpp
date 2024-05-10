/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http lib util
 * Author       : songyongtan
 * Create       : 2023-08-03
 * Notes:       :
 */
#ifndef HTTP_LIB_UTIL_HPP
#define HTTP_LIB_UTIL_HPP
#include <cpp-httplib/httplib.h>
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/internal/network/network_define.h>

namespace Fit {
struct HttpResult {
    uint32_t status;
    string msg;
};
class HttplibUtil {
public:
    static HttpResult GetRequest(const httplib::Request& req, Network::Request& innerReq);
    static string BuildExceptionResponse(int32_t status, const string& msg, const httplib::Request& req);
    static httplib::Headers BuildRequestHeaders(const fit_meta_data& meta);
    static bytes GetResponseMetaBytes(const httplib::Response& res);
    static string GetHttpAddress(string host, int32_t port);
    static string GetHttpsAddress(string host, int32_t port);
    static string GetFileFirstLine(const Fit::string& fullFilePath);
};
}
#endif