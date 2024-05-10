/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for tracer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/30
 */

#include "tracer.hpp"

#include <fit/fit_log.h>
#include <fit/internal/fit_time_utils.h>
#include <fit/stl/mutex.hpp>
#include <fit/stl/memory.hpp>

#include <genericable/com_huawei_fit_sdk_system_get_local_addresses/1.0.0/cplusplus/getLocalAddresses.hpp>
#include <genericable/com_huawei_fit_tracer_add_fitable_trace/1.0.0/cplusplus/addFitableTrace.hpp>
#include "trace_id.h"
#include "span.h"

using namespace Fit;
using ::Fit::Framework::Annotation::FitableType;

namespace {
::fit::registry::Address GetLocalAddress()
{
    static ::fit::registry::Address localAddress {};
    if (localAddress.host.empty()) {
        ::fit::sdk::system::getLocalAddresses getLocalAddresses;
        ::Fit::vector<::fit::registry::Address>* value {nullptr};
        auto ret = getLocalAddresses(&value);
        if (ret != FIT_OK || value == nullptr) {
            FIT_LOG_ERROR("Failed to fetch local address. [error=%x]", ret);
        } else if (value->empty()) {
            FIT_LOG_WARN("No local address found.");
        } else {
            localAddress = (*value)[0];
        }
    }
    return localAddress;
}

string GetAppName()
{
    static string appName {};
    return appName;
}

string GetTraceId(ContextObj context, const string& ip)
{
    if (Span::Util::GetOrInitSpanId(context) == "1" && TraceId::GetId(context).empty()) {
        return TraceId::CreateId(context, ip);
    }
    return TraceId::GetId(context);
}

class DefaultTracer : public Tracer {
public:
    DefaultTracer() = default;
    ~DefaultTracer() override = default;
    bool IsEnabled() const override;
    void SetEnabled(bool enabled) override;
    bool IsLocalTraceEnabled() const override;
    void SetLocalTraceEnabled(bool localTraceEnabled) override;
    bool IsGlobalTraceEnabled() const override;
    void SetGlobalTraceEnabled(bool globalTraceEnabled) override;
private:
    bool enabled_ {false};
    bool localTraceEnabled_ {false};
    bool globalTraceEnabled_ {false};
};

class DefaultTraceContext : public TraceContext {
public:
    explicit DefaultTraceContext(ContextObj context, const FitableCoordinatePtr& coordinate, CallType callType,
        TrustStage trustStage, string targetHost, uint16_t targetPort);
    ~DefaultTraceContext() override = default;
    void OnFitableInvoking() override;
    void OnFitableInvoked(string result) override;
private:
    void AddTrace();
    ContextObj context_ {nullptr};
    ::fit::tracer::FitableTrace fitableTrace_ {};
    ::fit::tracer::BaseFitTrace baseTrace_ {};
};
}

namespace Fit {
TrustStage GetTrustStage(::Fit::Framework::Annotation::FitableType fitableType)
{
    switch (fitableType) {
        case FitableType::VALIDATE: return TrustStage::VALIDATION;
        case FitableType::BEFORE: return TrustStage::BEFORE;
        case FitableType::AFTER: return TrustStage::AFTER;
        case FitableType::ERROR: return TrustStage::ERROR;
        default: return TrustStage::PROCESS;
    }
}
}

DefaultTraceContext::DefaultTraceContext(ContextObj context, const FitableCoordinatePtr& coordinate, CallType callType,
    TrustStage trustStage, string targetHost, uint16_t targetPort) : context_(context)
{
    ::fit::registry::Address localAddress = GetLocalAddress();

    fitableTrace_.genericId = coordinate->GetGenericableId();
    fitableTrace_.genericVersion = coordinate->GetGenericableVersion();
    fitableTrace_.fitId = coordinate->GetFitableId();
    fitableTrace_.serviceName = GetAppName();
    fitableTrace_.callType = (uint32_t)callType;
    fitableTrace_.fromFitId = ::Fit::Context::Global::GetGlobalContext(context_, "FIT_TRACE_FITABLE_ID_PRE");
    fitableTrace_.trustStage = (uint32_t)trustStage;
    fitableTrace_.targetHost = std::move(targetHost);
    fitableTrace_.targetPort = targetPort;

    baseTrace_.traceId = GetTraceId(context_, localAddress.host);
    baseTrace_.span = Span::Util::GetOrInitSpanId(context_);
    baseTrace_.traceType = 0; // RPC
    baseTrace_.host = localAddress.host;
    baseTrace_.port = static_cast<uint32_t>(localAddress.port);
    baseTrace_.workerId = localAddress.id;
    baseTrace_.flowType = (uint32_t)FlowType::NORMAL;

    fitableTrace_.baseFitTrace = &baseTrace_;
}

void DefaultTraceContext::OnFitableInvoking()
{
    baseTrace_.resultCode = "";
    baseTrace_.stage = (uint32_t)Stage::IN;
    baseTrace_.traceTimestamp = Fit::TimeUtil::GetCurrentLocalTimestampMs();
    AddTrace();
    // 原始实现仅在本地调用时保存到全局上下文，Java侧没有实现，先统一处理
    Span::Util::SaveSpanIdIntoGlobalContext(context_, Span::Util::IncreaseDepth(baseTrace_.span));
}

void DefaultTraceContext::OnFitableInvoked(string result)
{
    baseTrace_.resultCode = std::move(result);
    baseTrace_.stage = (uint32_t)Stage::OUT;
    baseTrace_.timeCost = Fit::TimeUtil::GetCurrentLocalTimestampMs() - baseTrace_.traceTimestamp;

    AddTrace();

    Span::Util::SaveSpanIdIntoGlobalContext(context_, Span::Util::IncreaseWidth(baseTrace_.span));
    TraceId::SaveIdIntoGlobalContext(context_, baseTrace_.traceId);
    ::Fit::Context::Global::PutGlobalContext(context_, "FIT_TRACE_FITABLE_ID_PRE", fitableTrace_.fromFitId);
}

void DefaultTraceContext::AddTrace()
{
    ::fit::tracer::addFitableTrace trace;
    bool* result {nullptr};
    auto ret = trace(&fitableTrace_, &result);
    if (ret != FIT_OK) {
        FIT_LOG_WARN("Failed to add trace. [error=%x]", ret);
    }
}

bool DefaultTracer::IsEnabled() const
{
    return enabled_;
}

void DefaultTracer::SetEnabled(bool enabled)
{
    enabled_ = enabled;
}

bool DefaultTracer::IsLocalTraceEnabled() const
{
    return IsGlobalTraceEnabled() && localTraceEnabled_;
}

void DefaultTracer::SetLocalTraceEnabled(bool localTraceEnabled)
{
    localTraceEnabled_ = localTraceEnabled;
}

bool DefaultTracer::IsGlobalTraceEnabled() const
{
    return IsEnabled() && globalTraceEnabled_;
}

void DefaultTracer::SetGlobalTraceEnabled(bool globalTraceEnabled)
{
    globalTraceEnabled_ = globalTraceEnabled;
}

Tracer* Tracer::GetInstance()
{
    static std::unique_ptr<Tracer> instance = make_unique<DefaultTracer>();
    return instance.get();
}

TraceContextBuilder& TraceContextBuilder::SetContext(ContextObj context)
{
    context_ = context;
    return *this;
}

TraceContextBuilder& TraceContextBuilder::SetFitableCoordinate(FitableCoordinatePtr coordinate)
{
    coordinate_ = std::move(coordinate);
    return *this;
}

TraceContextBuilder& TraceContextBuilder::SetCallType(CallType callType)
{
    callType_ = callType;
    return *this;
}

TraceContextBuilder& TraceContextBuilder::SetTrustStage(TrustStage trustStage)
{
    trustStage_ = trustStage;
    return *this;
}

TraceContextBuilder& TraceContextBuilder::SetTargetHost(string targetHost)
{
    targetHost_ = std::move(targetHost);
    return *this;
}

TraceContextBuilder& TraceContextBuilder::SetTargetPort(uint16_t targetPort)
{
    targetPort_ = targetPort;
    return *this;
}

TraceContextPtr TraceContextBuilder::Build()
{
    return std::make_shared<DefaultTraceContext>(context_, coordinate_, callType_, trustStage_,
        std::move(targetHost_), targetPort_);
}

TraceContextBuilder TraceContext::Custom()
{
    return {};
}
