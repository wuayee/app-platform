/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides fitable implementation for generic invoke.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/11
 */

#include <fit/external/framework/formatter/formatter_collector.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/internal/broker/broker_client_inner.h>
#include <genericable/com_huawei_fit_hakuna_kernel_broker_client_generic_invoke_v3/1.0.0/cplusplus/genericInvokeV3.hpp>

using ::Fit::Framework::Annotation::FitableType;
using ::Fit::Framework::Arguments;
using ::Fit::Framework::Formatter::BaseSerialization;
using ::Fit::Framework::Formatter::Response;
using ::Fit::string;

using ::fit::hakuna::kernel::broker::client::GenericInvokeOptions;

namespace {
class __attribute__((visibility("hidden"))) ContextRecorder {
public:
    virtual ~ContextRecorder() = default;
};
class __attribute__((visibility("hidden"))) AliasContextRecorder : public ContextRecorder {
public:
    explicit AliasContextRecorder(ContextObj context, const string& alias);
    ~AliasContextRecorder() override;
private:
    ContextObj context_ {nullptr};
    FitablePolicy originPolicy_ {};
    string originAlias_ {};
};
class __attribute__((visibility("hidden"))) TimeoutContextRecorder : public ContextRecorder {
public:
    explicit TimeoutContextRecorder(ContextObj context, uint32_t timeout);
    ~TimeoutContextRecorder() override;
private:
    ContextObj context_ {nullptr};
    uint32_t originTimeout_ {};
};
class __attribute__((visibility("hidden"))) WorkerContextRecorder : public ContextRecorder {
public:
    explicit WorkerContextRecorder(ContextObj context, const string& targetWorkerId);
    ~WorkerContextRecorder() override;
private:
    ContextObj context_ {nullptr};
    string originTargetWorkerId_ {};
};
}

AliasContextRecorder::AliasContextRecorder(ContextObj context, const string& alias) : context_(context)
{
    originPolicy_ = ContextGetPolicy(context_);
    originAlias_ = ContextGetAlias(context_);
    ContextSetAlias(context_, alias.c_str());
    if (alias.empty()) {
        ContextSetPolicy(context_, FitablePolicy::POLICY_DEFAULT);
    } else {
        ContextSetPolicy(context_, FitablePolicy::POLICY_ALIAS);
    }
}

AliasContextRecorder::~AliasContextRecorder()
{
    ContextSetPolicy(context_, originPolicy_);
    ContextSetAlias(context_, originAlias_.c_str());
}

TimeoutContextRecorder::TimeoutContextRecorder(ContextObj context, uint32_t timeout) : context_(context)
{
    originTimeout_ = ContextGetTimeout(context_);
    ContextSetTimeout(context_, timeout);
}

TimeoutContextRecorder::~TimeoutContextRecorder()
{
    ContextSetTimeout(context_, originTimeout_);
}

WorkerContextRecorder::WorkerContextRecorder(ContextObj context, const string& targetWorkerId) : context_(context)
{
    originTargetWorkerId_ = ContextGetTargetWorker(context_);
    ContextSetTargetWorker(context_, targetWorkerId.c_str());
}

WorkerContextRecorder::~WorkerContextRecorder()
{
    ContextSetTargetWorker(context_, originTargetWorkerId_.c_str());
}

namespace Fit {
namespace Broker {
namespace Client {
/**
 * 泛化调用。
 *
 * @param genericableId 表示待调用的泛化服务的唯一标识的字符串。
 * @param requestJson 表示待调用泛化服务的输入参数的JSON表现形式的字符串。
 * @param options 表示泛化调用的可选参数配置。
 * @return 表示执行结果的JSON表现形式的字符串。
 */
FitCode GenericInvoke(ContextObj ctx,
    const string* genericableId,
    const string* requestJson,
    const GenericInvokeOptions* options,
    string** result)
{
    BaseSerialization serialization;
    serialization.genericId = *genericableId;
    serialization.fitableType = FitableType::MAIN;
    serialization.formats = { Fit::Framework::Formatter::ProtocolType::PROTOCOL_TYPE_JSON };

    Arguments in {};
    auto ret = GetBrokerClient()->GetFormatterService()->DeserializeRequest(ctx, serialization, *requestJson, in);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to deserialize request for generic invocation. [genericable=%s, request=%s, error=%x]",
            genericableId->c_str(), requestJson->c_str(), ret);
        return ret;
    }
    Arguments out {};
    {
        AliasContextRecorder recorder {ctx, options->HasAlias() ? options->GetAlias() : ""};
        TimeoutContextRecorder timeout {ctx, (uint32_t)options->GetTimeout()};
        WorkerContextRecorder worker {ctx, options->GetWorkerId()};
        ret = GetBrokerClient()->GenericableInvoke(ctx, *genericableId, in, out);
    }
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to invoke genericable. [genericable=%s, ret=%x]", genericableId->c_str(), ret);
        return ret;
    }
    Response response {};
    response.args = out;
    auto* responseJson = Context::NewObj<string>(ctx);
    *responseJson = GetBrokerClient()->GetFormatterService()->SerializeResponse(ctx, serialization, response);
    *result = responseJson;
    return FIT_OK;
}
} // namespace Client
} // namespace Broker
} // Fit

namespace {
FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::Fit::Broker::Client::GenericInvoke)
        .SetGenericId(::fit::hakuna::kernel::broker::client::genericInvokeV3::GENERIC_ID)
        .SetFitableId("c9d2e96403f34ae780bf00106fa952f6");
}
} // LCOV_EXCL_LINE