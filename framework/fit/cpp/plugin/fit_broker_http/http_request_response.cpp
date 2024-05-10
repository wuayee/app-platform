/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fit http request response
 * Author       : songyongtan
 * Create       : 2023-07-29
 * Notes:       :
 */

#include <cpp-httplib/httplib.h>
#include <fit/stl/algorithm.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_request_response_v4/1.0.0/cplusplus/requestResponseV4.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/util/base64.h>
#include <fit/fit_log.h>

#include <fit/internal/network/network_define.h>
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/internal/util/protocol/fit_meta_package_builder.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/internal/util/protocol/tlv/tlv_tag_define.hpp>
#include <fit/internal/util/protocol/fit_meta_package_parser.h>

#include "http_manager.hpp"
#include "http_util.hpp"

using namespace Fit;
using ::fit::hakuna::kernel::broker::client::RequestContext;
using ::fit::hakuna::kernel::broker::shared::FitResponse;

namespace {
struct RequestRef {
    ContextObj ctx;
    const ::fit::hakuna::shared::Address* address;
    const bytes* metadata;
    const bytes* data;
    const RequestContext* requestContext;
};
template<typename F>
FitCode HttpRequestResponseTemplate(F&& getClient, const RequestRef& req, FitResponse** result)
{
    auto response = Context::NewObj<FitResponse>(req.ctx);
    if (response == nullptr) {
        FIT_LOG_ERROR("Bad alloc, target(%s:%d:%s).", req.address->host.c_str(), req.address->port,
            req.address->workerId.c_str());
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    auto contextPath = find_or(req.requestContext->worker.extensions, WORKER_CONTEXT_PATH_KEY, "");
    HttpClient::Response resRef {&response->metadata, &response->data};
    auto ret = getClient(*req.address, move(contextPath))->
        RequestResponse({req.metadata, req.data}, req.requestContext->timeout, resRef);
    *result = response;

    return ret;
}
/**
 * 调用Fit请求，同步接收一个返回值。
 *
 * @param address 表示请求的地址。
 * @param metadata 表示请求的元数据。
 * @param data 表示请求的业务数据。
 * @param requestContext 表示请求的上下文。
 * @return 表示返回值。
 */
FitCode RequestResponseV4(ContextObj ctx, const ::fit::hakuna::shared::Address* address, const bytes* metadata,
    const bytes* data, const RequestContext* requestContext, FitResponse** result)
{
    return HttpRequestResponseTemplate(
        [](const ::fit::hakuna::shared::Address& address, string contextPath) {
            return HttpManager::Instance().GetClient(address.host, address.port, move(contextPath));
        },
        {ctx, address, metadata, data, requestContext}, result);
}

FitCode HttpsRequestResponseV4(ContextObj ctx, const ::fit::hakuna::shared::Address* address, const bytes* metadata,
    const bytes* data, const RequestContext* requestContext, FitResponse** result)
{
    return HttpRequestResponseTemplate(
        [](const ::fit::hakuna::shared::Address& address, string contextPath) {
            return HttpManager::Instance().GetHttpsClient(address.host, address.port, move(contextPath));
        },
        {ctx, address, metadata, data, requestContext}, result);
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(RequestResponseV4)
        .SetGenericId(fit::hakuna::kernel::broker::client::requestResponseV4::GENERIC_ID)
        .SetFitableId("http");
    ::Fit::Framework::Annotation::Fitable(HttpsRequestResponseV4)
        .SetGenericId(fit::hakuna::kernel::broker::client::requestResponseV4::GENERIC_ID)
        .SetFitableId("https");
}
}