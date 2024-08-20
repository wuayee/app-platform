/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for remote invoker.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#include "remote_invoker.hpp"
#include <atomic>
#include <fit/fit_code_helper.h>
#include <fit/fit_log.h>
#include <fit/internal/util/protocol/fit_meta_data.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/internal/util/protocol/tlv/tlv_tag_define.hpp>
#include <fit/internal/util/vector_utils.hpp>
#include <fit/external/util/context/context_base.h>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_request_response_v5/1.0.0/cplusplus/requestResponseV5.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_mark_fitable_address_status/1.0.0/cplusplus/markFitableAddressStatus.hpp>
#include <genericable/com_huawei_fit_secure_access_get_token/1.0.0/cplusplus/getToken.hpp>
#include "fitable_invoker_factory.hpp"

using namespace Fit::Framework::Annotation;
using namespace Fit::Framework::Formatter;
using namespace Fit::Util;

using RequestParam = ::fit::hakuna::kernel::broker::client::RequestParam;
using RequestResponseV5 = ::fit::hakuna::kernel::broker::client::requestResponseV5;
namespace {
static std::atomic<bool> g_isEnableAccessToken {false};
}

namespace Fit {
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
    RequestMetaData metaData;
    FitCode ret = BuildMetadataBytes(context, metaData);
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

    return InvokeRemoteFitable(context, metaData, requestBytes, out);
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

FitCode RemoteInvoker::BuildMetadataBytes(ContextObj context, RequestMetaData& metaData) const
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

    Fit::string token = ContextGetAccessToken(context);
    auto meta = fit_meta_data(fit_meta_defines::META_VERSION_HAS_RESPONSE_META, formatter->GetFormat(),
        fit_version {1, 0, 0}, GetCoordinate()->GetGenericableId(), GetCoordinate()->GetFitableId(), token);
    auto tlvRet = FillTLV(context, meta);
    if (tlvRet != FIT_OK) {
        return tlvRet;
    }

    metaData.version = meta.get_version();
    metaData.payloadFormat = meta.get_payload_format();
    metaData.genericableVersion = meta.get_generic_version().to_string();
    metaData.genericableId = meta.get_generic_id();
    metaData.fitableId = meta.get_fit_id();
    metaData.accessToken = meta.get_access_token();
    return FIT_OK;
}

FitCode RemoteInvoker::SerializeRequest(ContextObj context, const vector<any>& in, bytes& result) const
{
    string serialized;
    auto ret = GetFactory()->GetFormatterService()->SerializeRequest(context, GetSerialization(), in, serialized);
    result = serialized;
    return ret;
}

FitCode RemoteInvoker::InvokeRemoteFitable(ContextObj context, const RequestMetaData& metaData, const bytes& request,
    vector<any>& out) const
{
    ::fit::hakuna::shared::Address targetAddress;
    targetAddress.workerId = GetEndpoint()->GetWorkerId();
    targetAddress.port = GetEndpoint()->GetPort();
    targetAddress.host = GetEndpoint()->GetHost();

    auto& appRef = GetEndpoint()->GetContext().application;
    auto& workerRef = GetEndpoint()->GetContext().worker;

    RequestParam requestParam;
    requestParam.timeout = ContextGetTimeout(context);
    requestParam.address = targetAddress;
    requestParam.metaData = metaData;
    requestParam.data = request;
    requestParam.worker.id = workerRef.id;
    requestParam.worker.extensions = workerRef.extensions;
    requestParam.application.name = appRef.name;
    requestParam.application.nameVersion = appRef.version;
    requestParam.application.extensions = appRef.extensions;

    ::fit::hakuna::kernel::broker::shared::FitResponseV2* result {nullptr};
    RequestResponseV5 proxy;
    proxy.SetAlias("protocol=" + Fit::to_string(GetEndpoint()->GetProtocol()));
    auto ret = proxy(&requestParam, &result);
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

FitCode RemoteInvoker::ParseResult(ContextObj context,
    ::fit::hakuna::kernel::broker::shared::FitResponseV2* fitResponse, vector<any>& out) const
{
    if (!fitResponse) {
        FIT_LOG_ERROR("The response returned from remote fitable is nullptr. [genericable=%s, fitable=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str());
        return FIT_ERR_NET_NO_RESPONSE;
    }

    if (fitResponse->code != FIT_OK) {
        FIT_LOG_ERROR("Remote fitable invoked failed. "
            "[genericable=%s, fitable=%s, host=%s, port=%d, error=%x, message=%s]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableId().c_str(),
            GetEndpoint()->GetHost().c_str(), GetEndpoint()->GetPort(), fitResponse->code,
            fitResponse->message.c_str());
        return (FitCode)fitResponse->code;
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

AuthenticationForRemoteInvoker::AuthenticationForRemoteInvoker(
    const ::Fit::FitableInvokerFactory* factory, ::Fit::FitableCoordinatePtr coordinate,
    ::Fit::Framework::Annotation::FitableType fitableType, ::Fit::FitableEndpointPtr endpoint, FitConfigPtr config)
    : RemoteInvoker(factory, move(coordinate), fitableType, move(endpoint), move(config))
{
}

int32_t AuthenticationForRemoteInvoker::GetToken(bool isForceUpdate, Fit::string& token) const
{
    fit::secure::access::GetToken getTokenExec;
    Fit::string* tokenTemp = new Fit::string();
    int32_t ret = getTokenExec(&isForceUpdate, &tokenTemp);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Get token failed, %d.", ret);
        return ret;
    }

    token = *tokenTemp;
    return FIT_OK;
}

FitCode AuthenticationForRemoteInvoker::Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
    ::Fit::Framework::Arguments& out) const
{
    if (!AuthenticationForRemoteInvoker::GetIsEnableAccessToken() || !GetConfig()->IsRegistryFitable()) {
        return RemoteInvoker::Invoke(context, in, out);
    }

    string token {};
    FitCode ret = GetToken(false, token);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Get token failed.");
        return ret;
    }
    ContextSetAccessToken(context, token.c_str());
    ret = RemoteInvoker::Invoke(context, in, out);
    if (ret == FIT_ERR_AUTHENTICATION_INVALID_ACCESS_TOKEN) {
        FIT_LOG_WARN("Token is invalid error occurs. [genericableId=%s, genericableVersion=%s, "
            "fitableId=%s, fitableVersion=%s, errorCode=%d.]",
            GetCoordinate()->GetGenericableId().c_str(), GetCoordinate()->GetFitableVersion().c_str(),
            GetCoordinate()->GetFitableId().c_str(), GetCoordinate()->GetFitableVersion().c_str(), ret);
        ret = GetToken(true, token);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Get token failed.");
            return ret;
        }
        ContextSetAccessToken(context, token.c_str());
        ret = RemoteInvoker::Invoke(context, in, out);
    }
    return ret;
}

void AuthenticationForRemoteInvoker::SetIsEnableAccessToken(bool isEnableAccessTokenIn)
{
    g_isEnableAccessToken.store(isEnableAccessTokenIn);
}
bool AuthenticationForRemoteInvoker::GetIsEnableAccessToken()
{
    return g_isEnableAccessToken.load();
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
    auto invoker = make_unique<AuthenticationForRemoteInvoker>(
        factory_, move(coordinate_), fitableType_, move(endpoint_), move(config_));
    return make_unique<FitableInvokerTraceDecorator>(move(invoker));
}
}