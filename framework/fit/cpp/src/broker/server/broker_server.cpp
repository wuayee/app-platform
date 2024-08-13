/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/4/30 17:04
 * Notes        :
 */

#include <fit/internal/broker/broker_server.h>
#include <fit/fit_log.h>
#include <fit/internal/broker/broker_client_inner.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/internal/util/protocol/tlv/tlv_tag_define.hpp>
#include <genericable/com_huawei_fit_broker_server_start_server/1.0.0/cplusplus/startServer.hpp>
#include <genericable/com_huawei_fit_broker_server_stop_server/1.0.0/cplusplus/stopServer.hpp>
#include <genericable/com_huawei_fit_secure_access_is_authorized/1.0.0/cplusplus/isAuthorized.hpp>
#include <genericable/com_huawei_fit_secure_access_apply_token/1.0.0/cplusplus/applyToken.hpp>
#include <genericable/com_huawei_fit_secure_access_refresh_token/1.0.0/cplusplus/refreshToken.hpp>
#include "broker_server_config.h"

namespace Fit {
BrokerServer &BrokerServer::Instance()
{
    static BrokerServer instance;
    return instance;
}

Fit::vector<fit::registry::Address> BrokerServer::StartServer(Framework::Formatter::FormatterServicePtr formatter,
    Fit::Framework::FitableDiscoveryPtr fitableDiscoveryPtr)
{
    Fit::vector<fit::registry::Address> resultAddresses;
    formatter_ = std::move(formatter);
    fitableDiscoveryPtr_ = std::move(fitableDiscoveryPtr);

    for (const auto &item : fitableDiscoveryPtr_->GetLocalFitableByGenericId(
        fit::broker::server::startServer::GENERIC_ID)) {
        fit::registry::Fitable fitable;
        fitable.genericId = item->GetGenericId();
        fitable.genericVersion = item->GetGenericVersion();
        fitable.fitId = item->GetFitableId();
        fitable.fitVersion = item->GetFitableVersion();
        Fit::Framework::Arguments in;
        ::fit::registry::Address *result {};
        Fit::Framework::Arguments out = Framework::PackArgs(&result);
        auto *ctx = NewContextDefault();
        if (Fit::GetBrokerClient()->LocalInvoke(ctx, fitable, in, out,
            Fit::Framework::Annotation::FitableType::MAIN) == FIT_OK && result != nullptr) {
            FIT_LOG_INFO("Start server(%s) success.", item->GetFitableId().c_str());
            resultAddresses.push_back(*result);
        } else {
            FIT_LOG_ERROR("Failed to start server(%s).", item->GetFitableId().c_str());
        }
        ContextDestroy(ctx);
    }

    return resultAddresses;
}

FitCode BrokerServer::StopServer()
{
    formatter_ = nullptr;
    if (fitableDiscoveryPtr_ == nullptr) {
        return FIT_OK;
    }

    for (const auto &item : fitableDiscoveryPtr_->GetLocalFitableByGenericId(
        fit::broker::server::stopServer::GENERIC_ID)) {
        fit::registry::Fitable fitable;
        fitable.genericId = item->GetGenericId();
        fitable.genericVersion = item->GetGenericVersion();
        fitable.fitId = item->GetFitableId();
        fitable.fitVersion = item->GetFitableVersion();
        Fit::Framework::Arguments in;
        Fit::Framework::Arguments out;
        auto *ctx = NewContextDefault();
        if (Fit::GetBrokerClient()->LocalInvoke(ctx, fitable, in, out,
            Fit::Framework::Annotation::FitableType::MAIN) == FIT_OK) {
            FIT_LOG_INFO("Stop server(%s) success.", item->GetFitableId().c_str());
        }
        ContextDestroy(ctx);
    }

    return FIT_OK;
}

FitCode BrokerServer::GetFitableType(
    const fit_meta_data &meta,
    Fit::Framework::Annotation::FitableType &fitableType)
{
    if (fitableDiscoveryPtr_ != nullptr) {
        Framework::Fitable fitable;
        fitable.genericId = meta.get_generic_id();
        fitable.genericVersion = meta.get_generic_version().to_string();
        fitable.fitableId = meta.get_fit_id();
        // compatible with python broker config: register fitables、heartbeat
        if (fitable.genericId == "85bdce64cf724589b87cb6b6a950999d") {
            fitable.fitableId = "dedaa28cfb2742819a9b0271bc34f72a";
        } else if (fitable.genericId == "e12fd1c57fd84f50a673d93d13074082") {
            fitable.fitableId = "DBC9E2F7C0E443F1AC986BBC3D58C27B";
        }

        auto fitableDetailList = fitableDiscoveryPtr_->GetLocalFitable(fitable);
        if (fitableDetailList.empty()) {
            FIT_LOG_ERROR("Cannot find the local fitable. (fitable=%s:%s).", fitable.genericId.c_str(),
                fitable.fitableId.c_str());
            return FIT_ERR_NOT_FOUND;
        }
        auto fitableDetailPtr = *fitableDetailList.begin();
        if (fitableDetailPtr != nullptr) {
            fitableType = fitableDetailPtr->GetType();
        }
    }
    return FIT_OK;
}

FitCode BrokerServer::RequestResponse(ContextObj ctx,
    const fit::hakuna::kernel::broker::shared::MetaData &metadata,
    const Fit::string &data,
    ::fit::hakuna::kernel::broker::shared::FitResponse &rsp)
{
    if (formatter_ == nullptr) {
        rsp.metadata = fit_response_meta_data(fit_meta_defines::META_VERSION_HAS_RESPONSE_META, 0, 0,
            FIT_ERR_NET_INTERNAL_FAULT, "Internal error").to_bytes();
        return FIT_OK;
    }

    fit_meta_data meta = fit_meta_data::from(metadata);
    auto globalContext = meta.GetValueByTag(TLV_GLOBAL_CONTEXT);
    if (!globalContext.empty()) {
        FIT_LOG_DEBUG("DeSerialize global context result %s.", globalContext.c_str());
        Context::Global::GlobalContextDeserialize(ctx, globalContext);
    }
    auto exceptionContext = meta.GetValueByTag(TLV_EXCEPTION_CONTEXT);
    if (!exceptionContext.empty()) {
        FIT_LOG_DEBUG("Deserialize exception context result %s.", exceptionContext.c_str());
        Context::Exception::DeserializeExceptionContext(ctx, exceptionContext);
    }

    FIT_LOG_DEBUG("BrokerServer received: genericId = %s, fitableId = %s, format = %d.",
        meta.get_generic_id().c_str(), meta.get_fit_id().c_str(), meta.get_payload_format());

    Fit::Framework::Annotation::FitableType fitableType {Fit::Framework::Annotation::FitableType::MAIN};
    auto ret = GetFitableType(meta, fitableType);
    if (ret != FIT_OK) {
        return ret;
    }

    return RequestResponse(ctx, meta, data, fitableType, rsp);
}

void FillResponseMetadata(ContextObj ctx, fit_response_meta_data& metadata)
{
    if (Context::Exception::HasExceptionContext(ctx)) {
        Fit::string exceptionContext;
        Context::Exception::SerializeExceptionContext(ctx, exceptionContext);
        FIT_LOG_DEBUG("Set exception context to TLV. [length=%lu]", exceptionContext.length());
        metadata.SetTagValue(TLV_EXCEPTION_CONTEXT, exceptionContext);
    }
}

int32_t BrokerServer::IsAuthorized(const Fit::string& accessToken, const fit::registry::Fitable& fitableIn)
{
    if (fitableIn.genericId == ::fit::secure::access::ApplyToken::GENERIC_ID ||
        fitableIn.genericId == ::fit::secure::access::RefreshToken::GENERIC_ID) {
        return FIT_OK;
    }
    ::fit::hakuna::kernel::shared::Fitable fitable;
    fit::secure::access::Permission permission;
    permission.fitable = &fitable;
    permission.fitable->fitableId = fitableIn.fitId;
    permission.fitable->fitableVersion = "1.0.0";
    permission.fitable->genericableId = fitableIn.genericId;
    permission.fitable->genericableVersion = fitableIn.genericVersion;

    fit::secure::access::IsAuthorized isAuthorizedExec;
    int32_t ret = isAuthorizedExec(&accessToken, &permission);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Checkout auth error, %d.", ret);
    }
    return ret;
}

FitCode BrokerServer::RequestResponse(ContextObj ctx,
    const fit_meta_data &meta,
    const Fit::string &data,
    const Fit::Framework::Annotation::FitableType &fitableType,
    ::fit::hakuna::kernel::broker::shared::FitResponse &rsp)
{
    Framework::Arguments in;
    Fit::Framework::Formatter::BaseSerialization baseSerialization = {
        .genericId = meta.get_generic_id(),
        .formats = {meta.get_payload_format()},
        .fitableType = fitableType
    };
    auto ret = formatter_->DeserializeRequest(ctx, baseSerialization, data, in);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("BrokerServer DeserializeRequest failed: genericId = %s, fitableId = %s, format = %d.",
            meta.get_generic_id().c_str(), meta.get_fit_id().c_str(), meta.get_payload_format());
        rsp.metadata = fit_response_meta_data(fit_meta_defines::META_VERSION_HAS_RESPONSE_META,
            meta.get_payload_format(), 1,
            ret, "DeserializeRequest fail").to_bytes();
        return FIT_OK;
    }

    Framework::Arguments out = formatter_->CreateArgOut(ctx, baseSerialization);

    fit::registry::Fitable fitable;
    fitable.genericId = meta.get_generic_id();
    fitable.genericVersion = meta.get_generic_version().to_string();
    fitable.fitId = meta.get_fit_id();
    // compatible with python broker config: register fitables、heartbeat
    if (fitable.genericId == "85bdce64cf724589b87cb6b6a950999d") {
        fitable.fitId = "dedaa28cfb2742819a9b0271bc34f72a";
    } else if (fitable.genericId == "e12fd1c57fd84f50a673d93d13074082") {
        fitable.fitId = "DBC9E2F7C0E443F1AC986BBC3D58C27B";
    }
    fitable.fitVersion = Fit::to_string(meta.get_version());

    // 认证鉴权可配置
    if (Fit::BrokerServerConfig::Instance()->IsEnableAccessToken()) {
        ret = IsAuthorized(meta.get_access_token(), fitable);
        if (ret != FIT_OK) {
            FIT_LOG_ERROR("Not authorized gid:fid (%s:%s).", fitable.genericId.c_str(), fitable.fitId.c_str());
            rsp.metadata = fit_response_meta_data(fit_meta_defines::META_VERSION_HAS_RESPONSE_META,
                meta.get_payload_format(), 1, ret, "Unauthorized").to_bytes();
            return FIT_OK;
        }
    }

    FIT_LOG_DEBUG("Authorized gid:fid (%s:%s).", fitable.genericId.c_str(), fitable.fitId.c_str());
    ret = GetBrokerClient()->LocalInvoke(ctx, fitable, in, out, fitableType);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("BrokerServer LocalInvoke failed: genericId = %s, fitableId = %s, format = %d, ret = %x.",
            meta.get_generic_id().c_str(), meta.get_fit_id().c_str(), meta.get_payload_format(), ret);
        fit_response_meta_data metadata(fit_meta_defines::META_VERSION_HAS_RESPONSE_META,
            meta.get_payload_format(), 0,
            ret, "Local invoke fail");
        FillResponseMetadata(ctx, metadata);
        rsp.metadata = metadata.to_bytes();
        return FIT_OK;
    }

    // serialize response
    Framework::Formatter::Response response {ret, "", out};
    fit_response_meta_data metadata(fit_meta_defines::META_VERSION_HAS_RESPONSE_META, meta.get_payload_format(),
        0, FIT_OK, "Ok");
    FillResponseMetadata(ctx, metadata);

    rsp.metadata = metadata.to_bytes();
    rsp.data = formatter_->SerializeResponse(ctx, baseSerialization, response);
    return FIT_OK;
}
} // LCOV_EXCL_LINE