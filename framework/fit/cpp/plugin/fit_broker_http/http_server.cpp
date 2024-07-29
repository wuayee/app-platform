/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http server
 * Author       : songyongtan
 * Create       : 2023-07-29
 * Notes:       :
 */

#include "http_server.hpp"
#include <fit/stl/memory.hpp>
#include <fit/external/util/string_utils.hpp>
#include <fit/external/util/base64.h>
#include <fit/fit_log.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>

#include "http_util.hpp"
#include "httplib_util.hpp"

using namespace fit_meta_defines;

namespace Fit {
namespace {
void SetHeaders(const Network::Response& handlerRes, httplib::Response& res)
{
    fit_response_meta_data metadata;
    if (!metadata.from_bytes(handlerRes.metadata)) {
        return;
    }
    res.set_header(HEADER_FIT_DATA_FORMAT, std::to_string(metadata.get_payload_format()));
    res.set_header(HEADER_FIT_CODE, std::to_string(metadata.get_code()));
    res.set_header(HEADER_FIT_MESSAGE, to_std_string(metadata.get_message()));
    string tlv;
    metadata.Serialize(tlv);
    res.set_header(HEADER_FIT_TLV, tlv);
    res.set_header(HEADER_FIT_META, to_std_string(Base64Encode(handlerRes.metadata)));
    res.set_content(handlerRes.payload.data(), handlerRes.payload.size(), HTTP_CONTENT_TYPE_JSON);
}
}

HttpServer::HttpServer(string host, int32_t port, const HttpConfig* config, unique_ptr<httplib::Server> svr)
    : host_(move(host)), port_(port), config_(config), svr_(std::move(svr))
{
}

HttpServer::~HttpServer()
{
    Stop();
}

const string& HttpServer::GetHost() const noexcept
{
    return host_;
}

int32_t HttpServer::GetPort() const noexcept
{
    return port_;
}

int32_t HttpServer::GetProtocol() const noexcept
{
    return config_->GetProtocol();
}

string HttpServer::GetPattern() const
{
    return config_->GetServerPath() + R"(/(.*))";
}

FitCode HttpServer::Start(Handler handler)
{
    if (!svr_) {
        FIT_LOG_ERROR("Http lib server is null.");
        return FIT_ERR_FAIL;
    }
    if (svr_->is_running()) {
        return FIT_ERR_EXIST;
    }
    svr_->Post(GetPattern().c_str(), [handler](const httplib::Request& req, httplib::Response& res) {
        FIT_LOG_DEBUG("Request: path=%s, bodySize=%lu.", req.path.c_str(), req.body.size());
        Network::Response handlerRes {};
        Network::Request handlerReq {};
        auto getRet = HttplibUtil::GetRequest(req, handlerReq);
        if (getRet.status != HTTP_STATUS_OK) {
            res.status = getRet.status;
            res.set_content(to_std_string(HttplibUtil::BuildExceptionResponse(getRet.status, getRet.msg, req)),
                HTTP_CONTENT_TYPE_JSON);
            return;
        }
        auto ret = handler(handlerReq, handlerRes);
        if (ret != FIT_OK) {
            res.status = HTTP_STATUS_INTERNAL_ERROR;
            res.set_content(to_std_string(HttplibUtil::BuildExceptionResponse(res.status, "Internal error", req)),
                HTTP_CONTENT_TYPE_JSON);
            return;
        }
        SetHeaders(handlerRes, res);
    });
    svr_->Get(config_->GetServerPath() + "/health", [this](const httplib::Request& req, httplib::Response& res) {
        res.status = HTTP_STATUS_OK;
        res.set_content(to_std_string(GetHost() + ":" + to_string(GetPort())), "text/plain");
    });

    if (!svr_->is_valid()) {
        FIT_LOG_ERROR("Server is invalid. (port=%d).", port_);
        return FIT_ERR_FAIL;
    }

    serverThread_ = std::thread([this]() {
        if (!svr_->listen("0.0.0.0", port_)) {
            FIT_LOG_ERROR("Failed to listen. (port=%d).", port_);
        }
    });

    return FIT_OK;
}

FitCode HttpServer::Stop()
{
    if (svr_) {
        svr_->stop();
    }
    if (serverThread_.joinable()) {
        serverThread_.join();
    }
    return FIT_OK;
}
}