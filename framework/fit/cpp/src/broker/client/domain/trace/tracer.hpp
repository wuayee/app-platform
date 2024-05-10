/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides tracer for fitable invocation.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/30
 */

#ifndef FIT_TRACER_HPP
#define FIT_TRACER_HPP

#include <cstdint>
#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/external/util/context/context_api.hpp>
#include "../fitable_coordinate.hpp"

namespace Fit {
/**
 * 表示调用类型。
 */
enum class CallType {
    /**
     * 表示本地调用。
     */
    LOCAL = 1,

    /**
     * 表示远程调用。
     */
    REMOTE = 2
};

/**
 * 表示可信阶段。
 */
enum class TrustStage {
    /**
     * 表示正在进行校验。
     */
    VALIDATION = 0,

    /**
     * 表示在服务主体调用前的阶段。
     */
    BEFORE = 1,

    /**
     * 表示正在执行服务主体。
     */
    PROCESS = 2,

    /**
     * 表示正在执行降级方案。
     */
    DEGRADATION = 3,

    /**
     * 表示在服务主体调用后的阶段。
     */
    AFTER = 4,

    /**
     * 表示正在异常处理阶段。
     */
    ERROR = 5
};

/**
 * 表示调用的阶段。
 */
enum class Stage {
    /**
     * 表示输入阶段。即正准备执行服务实例。
     */
    IN = 0,

    /**
     * 表示输出阶段。即已完成服务实例的执行。
     */
    OUT = 1
};

/**
 * 表示流量类型。
 */
enum class FlowType {
    /**
     * 表示常规调用。
     */
    NORMAL = 0,

    /**
     * 表示测试场景。
     */
    TEST = 1,

    /**
     * 表示打桩场景。
     */
    MOCK = -1
};

TrustStage GetTrustStage(::Fit::Framework::Annotation::FitableType fitableType);

class TraceContext;
using TraceContextPtr = std::shared_ptr<TraceContext>;

/**
 * 为调用跟踪的上下文提供构建程序。
 */
class TraceContextBuilder {
public:
    /**
     * 设置服务调用的上下文信息。
     *
     * @param context 表示服务调用的上下文信息。
     * @return 表示当前构建程序的引用。
     */
    TraceContextBuilder& SetContext(ContextObj context);

    /**
     * 设置正在调用的服务的坐标。
     *
     * @param coordinate 表示指向服务坐标的共享指针。
     * @return 表示当前构建程序的引用。
     */
    TraceContextBuilder& SetFitableCoordinate(FitableCoordinatePtr coordinate);

    /**
     * 设置调用类型。
     *
     * @param callType 表示调用类型的枚举值。
     * @return 表示当前构建程序的引用。
     */
    TraceContextBuilder& SetCallType(CallType callType);

    /**
     * 设置可信的阶段。
     *
     * @param trustStage 表示可信阶段的枚举值。
     * @return 表示当前构建程序的引用。
     */
    TraceContextBuilder& SetTrustStage(TrustStage trustStage);

    /**
     * 获取调用到的目标主机。
     *
     * @param targetHost 表示目标主机的字符串。
     * @return 表示当前构建程序的引用。
     */
    TraceContextBuilder& SetTargetHost(string targetHost);

    /**
     * 获取调用到的目标端口号。
     *
     * @param targetPort 表示目标端口号的16位无符号整数。
     * @return 表示当前构建程序的引用。
     */
    TraceContextBuilder& SetTargetPort(uint16_t targetPort);

    /**
     * 构建服务调用跟踪上下文信息的新实例。
     *
     * @return 表示指向新构建的上下文的共享指针。
     */
    TraceContextPtr Build();
private:
    ContextObj context_ {};
    FitableCoordinatePtr coordinate_ {};
    CallType callType_ {};
    TrustStage trustStage_ {};
    string targetHost_ {};
    uint16_t targetPort_ {};
};

/**
 * 为调用跟踪提供上下文信息。
 */
class TraceContext {
public:
    TraceContext() = default;
    virtual ~TraceContext() = default;

    /**
     * 当服务被调用时执行。
     */
    virtual void OnFitableInvoking() = 0;

    /**
     * 当服务被调用后执行。
     *
     * @param result 表示服务的执行结果。
     */
    virtual void OnFitableInvoked(string result) = 0;

    /**
     * 返回一个调用跟踪上下文的构建程序，用以构建上下文的新实例。
     *
     * @return 表示用以构建调用跟踪上下文的构建程序。
     */
    static TraceContextBuilder Custom();
};

/**
 * 为服务实现提供调用跟踪能力。
 */
class Tracer {
public:
    virtual ~Tracer() = default;

    /**
     * 获取一个值，该值指示调用跟踪功能是否被启用。
     *
     * @return 若调用跟踪功能被启用，则为 true；否则为 false。
     */
    virtual bool IsEnabled() const = 0;

    /**
     * 设置一个值，该值指示调用跟踪功能是否被启用。
     *
     * @param enabled 若为 true，则启用调用跟踪功能；否则禁用调用跟踪功能。
     */
    virtual void SetEnabled(bool enabled) = 0;

    /**
     * 获取一个值，该值指示本地调用跟踪功能是否被启用。
     *
     * @return 若本地调用跟踪功能被启用，则为 true；否则为 false。
     */
    virtual bool IsLocalTraceEnabled() const = 0;

    /**
     * 设置一个值，该值指示本地调用跟踪功能是否被启用。
     *
     * @param localTraceEnabled 若为 true，则启用本地调用跟踪；否则禁用本地调用跟踪。
     */
    virtual void SetLocalTraceEnabled(bool localTraceEnabled) = 0;

    /**
     * 获取一个值，该值指示全局调用跟踪功能是否被启用。
     *
     * @return 若全局调用跟踪功能被启用，则为 true；否则为 false。
     */
    virtual bool IsGlobalTraceEnabled() const = 0;

    /**
     * 设置一个值，该值指示是否启用全局调用跟踪功能。
     *
     * @param globalTraceEnabled 若为 true，则启用全局调用跟踪功能；否则禁用全局调用跟踪功能。
     */
    virtual void SetGlobalTraceEnabled(bool globalTraceEnabled) = 0;

    /**
     * 设置调用跟踪组件的唯一实例。
     *
     * @return 表示指向调用跟踪组件的唯一实例的指针。
     */
    static Tracer* GetInstance();
};
}

#endif // FIT_TRACER_HPP
