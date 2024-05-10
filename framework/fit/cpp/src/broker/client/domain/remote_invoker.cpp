/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for remote invoker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#include "remote_invoker.hpp"
#include <fit/fit_code_helper.h>
#include <fit/fit_log.h>
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/internal/util/protocol/fit_meta_package_builder.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/internal/util/protocol/tlv/tlv_tag_define.hpp>
#include <fit/internal/util/vector_utils.hpp>

#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_request_response_v4/1.0.0/cplusplus/requestResponseV4.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_mark_fitable_address_status/1.0.0/cplusplus/markFitableAddressStatus.hpp>

#include "fitable_invoker_factory.hpp"

using namespace Fit;
using namespace Fit::Framework::Annotation;
using namespace Fit::Framework::Formatter;
using namespace Fit::Util;

using RequestResponse = ::fit::hakuna::kernel::broker::client::requestResponseV4;

RemoteInvoker::RemoteInvoker(const FitableInvokerFactory* factory,
    FitableCoordinatePtr coordinate, FitableType fitableType, FitableEndpointPtr endpoint, FitConfigPtr config)
    : FitableInvokerBase(factory, move(coordinate), fitableType, move(config)), endpoint_(move(endpoint))
{
    serialization_ = BaseSerialization {
        GetCoordinate()->GetGenericableId(),
        endpoint_->GetFormats(),
        GetFitableType()
    };
}

const BaseSerialization& RemoteInvoker::GetSerialization() const
{
    return serialization_;
}

FitableEndpointPtr RemoteInvoker::GetEndpoint() const
{
    return endpoint_;
}

FitCode RemoteInvoker::Invoke(ContextObj context, vector<any>& in, vector<any>& out) const
{
    FitCode ret;
    bytes metadataBytes;
    ret = BuildMetadataBytes(context, metadataBytes);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to build metadata for remote invocation. [genericable=%s, fitable=%s, error=%x]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(), ret);
        return ret;
    }

    bytes requestBytes;
    ret = SerializeRequest(context, in, requestBytes);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to serialize request for remote invocation. [genericable=%s, error=%x]",
            GetCoordinate()->GetGenericableId().c_str(), ret);
        return ret;
    }

    return InvokeRemoteFitable(context, metadataBytes, requestBytes, out);
}
FitCode FillGlobalContext(ContextObj context, fit_meta_data& meta)
{
    string buffer;
    auto ret = Context::Global::GlobalContextSerialize(context, buffer);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to serialize global context. [error=%x]", ret);
        return ret;
    }
    meta.SetTagValue(TLV_GLOBAL_CONTEXT, buffer);
    return FIT_OK;
}
FitCode FillExceptionContext(ContextObj context, fit_meta_data& meta)
{
    string buffer;
    auto ret = Context::Exception::SerializeExceptionContext(context, buffer);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to serialize exception context. [error=%x]", ret);
        return ret;
    }
    meta.SetTagValue(TLV_EXCEPTION_CONTEXT, buffer);
    return FIT_OK;
}
FitCode FillTLV(ContextObj context, fit_meta_data& meta)
{
    auto ret = FillGlobalContext(context, meta);
    if (ret != FIT_OK) {
        return ret;
    }
    ret = FillExceptionContext(context, meta);
    if (ret != FIT_OK) {
        return ret;
    }
    return FIT_OK;
}
FitCode RemoteInvoker::BuildMetadataBytes(ContextObj context, bytes& result) const
{
    auto formatter = GetFactory()->GetFormatterService()->GetFormatter(GetSerialization());
    if (formatter == nullptr) {
        FIT_LOG_ERROR("Missing serialization plugin. [genericable:genericableVersion:fitableId:fitableVersion="
            "%s:%s:%s:%s, type=%d].",
            GetSerialization().genericId.c_str(), GetCoordinate()->GetGenericableVersion().c_str(),
            GetCoordinate()->GetFitableId().c_str(), GetCoordinate()->GetFitableVersion().c_str(),
            static_cast<int32_t>(GetSerialization().fitableType));
        return FIT_ERR_NOT_FOUND;
    }
    auto meta = fit_meta_data(fit_meta_defines::META_VERSION_HAS_RESPONSE_META, formatter->GetFormat(),
        fit_version {1, 0, 0}, GetCoordinate()->GetGenericableId(), GetCoordinate()->GetFitableId());
    auto tlvRet = FillTLV(context, meta);
    if (tlvRet != FIT_OK) {
        return tlvRet;
    }
    result = fit_meta_package_builder::build(meta);
    if (result.empty()) {
        FIT_LOG_ERROR("Failed to build request meta. (genericableId=%s, fitableId=%s).",
            GetSerialization().genericId.c_str(), GetCoordinate()->GetFitableId().c_str());
        return FIT_ERR_SERIALIZE;
    }
    return FIT_OK;
}

FitCode RemoteInvoker::SerializeRequest(ContextObj context, const vector<any>& in, bytes& result) const
{
    string serialized;
    auto ret = GetFactory()->GetFormatterService()->SerializeRequest(context, GetSerialization(), in, serialized);
    result = serialized;
    return ret;
}

FitCode RemoteInvoker::InvokeRemoteFitable(ContextObj context, const bytes& metadata, const bytes& request,
    vector<any>& out) const
{
    ::fit::hakuna::shared::Address targetAddress;
    targetAddress.workerId = GetEndpoint()->GetWorkerId();
    targetAddress.port = GetEndpoint()->GetPort();
    targetAddress.host = GetEndpoint()->GetHost();

    ::fit::hakuna::kernel::broker::client::RequestContext requestContext;
    requestContext.timeout = ContextGetTimeout(context);
    auto& appRef = GetEndpoint()->GetContext().application;
    auto& workerRef = GetEndpoint()->GetContext().worker;
    requestContext.application.name = appRef.name;
    requestContext.application.nameVersion = appRef.version;
    requestContext.application.extensions = appRef.extensions;
    requestContext.worker.id = workerRef.id;
    requestContext.worker.extensions = workerRef.extensions;

    ::fit::hakuna::kernel::broker::shared::FitResponse* result {nullptr};
    RequestResponse proxy;
    proxy.SetAlias("protocol=" + Fit::to_string(GetEndpoint()->GetProtocol()));
    auto ret = proxy(&targetAddress, &metadata, &request, &requestContext, &result);
    if (ret == FIT_OK) {
        return ParseResult(context, result, out);
    } else if (IsNetErrorCode(ret)) {
        FIT_LOG_ERROR("Network fault occurs when invoke remote fitable. "
                      "[genericable=%s, fitable=%s, worker=%s, host=%s, port=%d]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(),
            targetAddress.workerId.c_str(), targetAddress.host.c_str(), targetAddress.port);
        DisableAddress();
        return ret;
    } else {
        FIT_LOG_ERROR("Failed to invoke remote fitable. [genericable=%s, fitable=%s, error=%x]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(), ret);
        return ret;
    }
}

FitCode RemoteInvoker::ParseResult(ContextObj context, ::fit::hakuna::kernel::broker::shared::FitResponse* fitResponse,
    vector<any>& out) const
{
    if (!fitResponse) {
        FIT_LOG_ERROR("The response returned from remote fitable is nullptr. [genericable=%s, fitable=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str());
        return FIT_ERR_NET_NO_RESPONSE;
    }
    fit_response_meta_data metadata;
    if (!metadata.from_bytes(fitResponse->metadata)) {
        FIT_LOG_ERROR("Failed to read metadata from response returned by remote fitable. [genericable=%s, fitable=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str());
        return FIT_ERR_NET_INVALID_RESPONSE_METADATA;
    }
    if (metadata.get_code() != FIT_OK) {
        FIT_LOG_ERROR("Remote fitable invoked failed. "
            "[genericable=%s, fitable=%s, host=%s, port=%d, error=%x, message=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(),
            GetEndpoint()->GetHost().c_str(), GetEndpoint()->GetPort(), metadata.get_code(),
            metadata.get_message().c_str());
        return (FitCode)metadata.get_code();
    }
    if (GetFitableType() == FitableType::MAIN) {
        Response response = GetFactory()->GetFormatterService()->DeserializeResponse(
            context, GetSerialization(), fitResponse->data);
        if (response.code == FIT_OK) {
            out = std::move(response.args);
        } else {
            FIT_LOG_ERROR("Failed to deserialize response for remote invocation. "
                "[genericable=%s, fitable=%s, error=%x, message=%s]",
                GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(), response.code,
                response.msg.c_str());
            return response.code;
        }
    }
    return FIT_OK;
}

TraceContextPtr RemoteInvoker::CreateTraceContext(ContextObj context) const
{
    if (!GetConfig()->TraceIgnore() && Tracer::GetInstance()->IsGlobalTraceEnabled()) {
        return TraceContext::Custom()
            .SetContext(context)
            .SetFitableCoordinate(GetCoordinate())
            .SetCallType(CallType::REMOTE)
            .SetTrustStage(GetTrustStage(GetFitableType()))
            .SetTargetHost(GetEndpoint()->GetHost())
            .SetTargetPort(GetEndpoint()->GetPort())
            .Build();
    } else {
        return nullptr;
    }
}

FitCode RemoteInvoker::DisableAddress() const
{
    ::fit::hakuna::kernel::registry::listener::markFitableAddressStatus markFitableAddressStatus;
    ::fit::hakuna::kernel::shared::Fitable fitable {};
    fitable.genericableId = GetCoordinate()->GetGenericableId();
    fitable.genericableVersion = GetCoordinate()->GetGenericableVersion();
    fitable.fitableId = GetCoordinate()->GetFitableId();
    fitable.fitableVersion = GetCoordinate()->GetFitableVersion();
    ::fit::hakuna::kernel::registry::shared::Worker worker {};
    worker.id = GetEndpoint()->GetWorkerId();
    worker.environment = GetEndpoint()->GetEnvironment();
    ::fit::hakuna::kernel::registry::shared::Address address {};
    address.host = GetEndpoint()->GetHost();
    ::fit::hakuna::kernel::registry::shared::Endpoint endpoint {};
    endpoint.port = GetEndpoint()->GetPort();
    endpoint.protocol = GetEndpoint()->GetProtocol();
    address.endpoints.push_back(endpoint);
    worker.addresses.push_back(address);
    bool valid = false;
    auto ret = markFitableAddressStatus(&fitable, &worker, &valid);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to mark address status. [error=%x]", ret);
        ret = FIT_ERR_FAIL;
    }
    return ret;
}

RemoteInvokerBuilder& RemoteInvokerBuilder::SetFactory(const FitableInvokerFactory* factory)
{
    factory_ = factory;
    return *this;
}

RemoteInvokerBuilder& RemoteInvokerBuilder::SetCoordinate(FitableCoordinatePtr coordinate)
{
    coordinate_ = std::move(coordinate);
    return *this;
}

RemoteInvokerBuilder& RemoteInvokerBuilder::SetFitableType(FitableType fitableType)
{
    fitableType_ = fitableType;
    return *this;
}

RemoteInvokerBuilder& RemoteInvokerBuilder::SetEndpoint(FitableEndpointPtr endpoint)
{
    endpoint_ = std::move(endpoint);
    return *this;
}

RemoteInvokerBuilder& RemoteInvokerBuilder::SetFitConfig(FitConfigPtr config)
{
    config_ = move(config);
    return *this;
}

std::unique_ptr<FitableInvoker> RemoteInvokerBuilder::Build()
{
    auto invoker = make_unique<RemoteInvoker>(factory_, move(coordinate_), fitableType_, move(endpoint_),
        move(config_));
    return make_unique<FitableInvokerTraceDecorator>(move(invoker));
}
