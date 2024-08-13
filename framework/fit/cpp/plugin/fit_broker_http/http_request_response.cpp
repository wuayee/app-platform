/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http request response
 * Author       : songyongtan
 * Create       : 2023-07-29
 * Notes:       :
 */

#include <cpp-httplib/httplib.h>
#include <fit/stl/algorithm.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_request_response_v5/1.0.0/cplusplus/requestResponseV5.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/util/base64.h>
#include <fit/fit_log.h>

#include <fit/internal/network/network_define.h>
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/internal/util/protocol/tlv/tlv_tag_define.hpp>

#include "http_manager.hpp"
#include "http_util.hpp"

using namespace Fit;
using ::fit::hakuna::kernel::broker::client::RequestParam;
using ::fit::hakuna::kernel::broker::shared::FitResponseV2;

namespace {
bool ParseMetaDataFromBytes(FitResponseV2* result, const Fit::bytes metadataBytes)
{
    fit_response_meta_data metadata;
    if (!metadata.from_bytes(metadataBytes)) {
        FIT_LOG_DEBUG("Not enable to read metadata from response returned by remote fitable.");
        return false;
    }
    result->code = metadata.get_code();
    result->message = metadata.get_message();
    return true;
}

template<typename F>
FitCode HttpRequestResponseTemplateV5(F&& getClient, ContextObj ctx, const RequestParam* req, FitResponseV2** result)
{
    auto response = Context::NewObj<FitResponseV2>(ctx);
    if (response == nullptr) {
        FIT_LOG_ERROR("Bad alloc, target(%s:%d:%s).", req->address.host.c_str(), req->address.port,
            req->address.workerId.c_str());
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    auto contextPath = find_or(req->worker.extensions, WORKER_CONTEXT_PATH_KEY, "");
    Fit::bytes metadataBytes {};
    HttpClient::Response resRef;
    resRef.metadata = &metadataBytes;
    resRef.data = &response->data;
    auto ret = getClient(req->address, move(contextPath))->RequestResponse(*req, resRef);
    if (!ParseMetaDataFromBytes(response, metadataBytes)) {
        response->code = resRef.code;
        response->message = resRef.message;
    }
    FIT_LOG_DEBUG("Code:message (%d:%s).", response->code, response->message.c_str());
    *result = response;
    return response->code;
}

FitCode RequestResponseV5(ContextObj ctx, const RequestParam* requestParam, FitResponseV2** result)
{
    return HttpRequestResponseTemplateV5(
        [](const ::fit::hakuna::shared::Address& address, string contextPath) {
            return HttpManager::Instance().GetClient(address.host, address.port, move(contextPath));
        },
        ctx, requestParam, result);
}

FitCode HttpsRequestResponseV5(ContextObj ctx, const RequestParam* requestParam, FitResponseV2** result)
{
    return HttpRequestResponseTemplateV5(
        [](const ::fit::hakuna::shared::Address& address, string contextPath) {
            return HttpManager::Instance().GetHttpsClient(address.host, address.port, move(contextPath));
        },
        ctx, requestParam, result);
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(RequestResponseV5)
        .SetGenericId(fit::hakuna::kernel::broker::client::requestResponseV5::GENERIC_ID)
        .SetFitableId("http");
    ::Fit::Framework::Annotation::Fitable(HttpsRequestResponseV5)
        .SetGenericId(fit::hakuna::kernel::broker::client::requestResponseV5::GENERIC_ID)
        .SetFitableId("https");
}
}