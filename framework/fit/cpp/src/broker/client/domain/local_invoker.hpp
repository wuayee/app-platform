/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */

#ifndef FIT_LOCAL_INVOKE_HPP
#define FIT_LOCAL_INVOKE_HPP

#include <fit/internal/fit_scope_guard.h>
#include <fit/stl/vector.hpp>
#include <fit/stl/any.hpp>
#include <fit/external/util/context/context_api.hpp>
#include "decorator/fitable_invoker_trace_decorator.hpp"
#include "trace/tracer.hpp"
#include "broker/client/application/gateway/fit_discovery.h"
#include "fitable_coordinate.hpp"

namespace Fit {
/**
 * 为服务实现调用程序提供基于本地调用的实现。
 */
class LocalInvoker : public FitableInvokerBase, public virtual TraceAbleFitableInvoker {
public:
    explicit LocalInvoker(const ::Fit::FitableInvokerFactory* factory,
        ::Fit::FitableCoordinatePtr coordinate, ::Fit::Framework::Annotation::FitableType fitableType,
        FitConfigPtr config);
    ~LocalInvoker() override = default;
    ::Fit::TraceContextPtr CreateTraceContext(ContextObj context) const override;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
};

/**
 * 为服务实现的本地执行程序提供构建程序。
 */
class LocalInvokerBuilder final {
public:
    LocalInvokerBuilder() = default;
    ~LocalInvokerBuilder() = default;

    /**
     * 设置正在创建服务实例的工厂。
     *
     * @param factory 表示指向创建当前服务实例的工厂的指针。
     * @return 表示当前构建程序的引用。
     */
    LocalInvokerBuilder& SetFactory(const ::Fit::FitableInvokerFactory* factory);

    /**
     * 设置服务实现的坐标。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @return 表示当前构建程序的引用。
     */
    LocalInvokerBuilder& SetCoordinate(::Fit::FitableCoordinatePtr coordinate);

    /**
     * 设置服务实现的类型。
     *
     * @param fitableType 表示服务实现的枚举值。
     * @return 表示当前构建程序的引用。
     */
    LocalInvokerBuilder& SetFitableType(::Fit::Framework::Annotation::FitableType fitableType);
    LocalInvokerBuilder& SetFitConfig(FitConfigPtr config);

    /**
     * 构建服务实现的本地执行程序。
     *
     * @return 表示指向新构建的本地服务执行程序的指针。
     */
    std::unique_ptr<FitableInvoker> Build();
private:
    const ::Fit::FitableInvokerFactory* factory_ {nullptr};
    ::Fit::FitableCoordinatePtr coordinate_ {nullptr};
    ::Fit::Framework::Annotation::FitableType fitableType_ {};
    FitConfigPtr config_ {};
};
}
#endif // FIT_LOCAL_INVOKE_HPP