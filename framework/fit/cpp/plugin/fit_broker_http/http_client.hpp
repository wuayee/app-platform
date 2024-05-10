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
#include <fit/fit_log.h>
#include <fit/stl/memory.hpp>
#include <cpp-httplib/httplib.h>
#include <fit/external/util/base64.h>
#include <fit/fit_log.h>
#include <fit/internal/util/protocol/fit_meta_package_parser.h>
#include "http_config.hpp"
#include "http_util.hpp"
#include "httplib_util.hpp"


namespace Fit {
constexpr uint32_t MS_PER_SECOND = 1000;
constexpr uint32_t US_PER_MS = 1000;

inline void ConvertTimeout(int64_t ms, time_t& second, time_t& us)
{
    second = ms / MS_PER_SECOND;
    us = (ms % MS_PER_SECOND) * US_PER_MS;
}

class HttpClient {
public:
    struct Request {
        const bytes* metadata;
        const bytes* data;
    };
    struct Response {
        bytes* metadata;
        bytes* data;
    };
    virtual FitCode RequestResponse(const Request& req, int64_t timeoutMs, Response& result) = 0;
};

using HttpClientPtr = Fit::shared_ptr<HttpClient>;

template<typename T>
class HttpClientTemp : public HttpClient {
public:
    HttpClientTemp(string contextPath, const HttpConfig* config, T client, string hostAndPort)
        : contextPath_(move(contextPath)), config_(config), client_(std::move(client)),
        hostAndPort_(std::move(hostAndPort))
    {
    }

    FitCode RequestResponse(const Request& req, int64_t timeoutMs, Response& result) override
    {
        if (client_ == nullptr) {
            FIT_LOG_ERROR("Client is null");
            return FIT_ERR_FAIL;
        }
        fit_meta_data meta;
        if (!fit_meta_package_parser(*req.metadata).parse_to(meta)) {
            FIT_LOG_ERROR("Failed to parse metadata.");
            return FIT_ERR_PARAM;
        }
        time_t second {};
        time_t us {};
        ConvertTimeout(timeoutMs, second, us);
        client_->set_read_timeout(second, us);
        httplib::Headers headers = HttplibUtil::BuildRequestHeaders(meta);
        auto path = config_->GetClientPath(contextPath_, meta.get_generic_id(), meta.get_fit_id());
        auto res = client_->Post(path.c_str(),
            headers, std::string(req.data->data(), req.data->size()), HTTP_CONTENT_TYPE_JSON);
        if (!res) {
            FIT_LOG_WARN("Request has fault. (code=%u, address=%s, path=%s).", (uint32_t)res.error(),
                hostAndPort_.c_str(), path.c_str());
            return FIT_ERR_NET_SEND_FAIL;
        }
        if (res->status != HTTP_STATUS_OK) {
            FIT_LOG_WARN("Response has fault. (status=%d, address=%s, path=%s, payload=%s).",
                res->status, hostAndPort_.c_str(), path.c_str(), res->body.c_str());
            return FIT_ERR_FAIL;
        }
        *result.metadata = HttplibUtil::GetResponseMetaBytes(*res);
        *result.data = bytes(res->body.data(), res->body.size());
        FIT_LOG_DEBUG("Request: address=%s, path=%s, bodySize=%lu, resBodySize=%lu.",
            hostAndPort_.c_str(), path.c_str(), req.data->size(), res->body.size());

        return FIT_OK;
    }
private:
    const string contextPath_;
    const HttpConfig* config_ {};
    T client_ {};
    string hostAndPort_ {};
};

}

#endif