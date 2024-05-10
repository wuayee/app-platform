/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides invoker for fitables.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#ifndef FIT_FITABLE_INVOKER_HPP
#define FIT_FITABLE_INVOKER_HPP

#include "fitable_coordinate.hpp"

#include "broker/client/application/gateway/fit_config.h"
#include "trace/tracer.hpp"

#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/external/util/context/context_api.hpp>

namespace Fit {
class FitableInvokerFactory;

/**
 * 为服务实现提供调用程序。
 */
class FitableInvoker {
public:
    FitableInvoker() = default;
    virtual ~FitableInvoker() = default;

    FitableInvoker(const FitableInvoker&) = delete;
    FitableInvoker(FitableInvoker&&) = delete;
    FitableInvoker& operator=(const FitableInvoker&) = delete;
    FitableInvoker& operator=(FitableInvoker&&) = delete;

    virtual const ::Fit::FitableInvokerFactory* GetFactory() const = 0;

    /**
     * 获取服务实现的配置。
     *
     * @return 表示指向服务实现配置的指针。
     */
    virtual const FitConfigPtr& GetConfig() const = 0;

    /**
     * 获取服务实现的坐标。
     *
     * @return 表示服务实现的坐标的不可变引用。
     */
    virtual const ::Fit::FitableCoordinatePtr& GetCoordinate() const = 0;

    /**
     * 获取服务实现的类型。
     *
     * @return 表示服务实现的类型的不可变引用。
     */
    virtual ::Fit::Framework::Annotation::FitableType GetFitableType() const = 0;

    /**
     * 使用指定的输入和输出调用服务实现。
     *
     * @param context 表示服务实现执行时所关联的上下文。
     * @param in 表示服务实现的入参的列表。
     * @param out 表示服务实现的出参的列表。
     * @return 表示服务实现的执行结果。若为 FIT_OK，则表示执行成功；否则表示执行失败。
     */
    virtual FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const = 0;
};

/**
 * 为服务实现提供调用程序。
 */
class FitableInvokerBase : public virtual FitableInvoker {
public:
    explicit FitableInvokerBase(const ::Fit::FitableInvokerFactory* factory,
        ::Fit::FitableCoordinatePtr coordinate, ::Fit::Framework::Annotation::FitableType fitableType,
        FitConfigPtr config);
    ~FitableInvokerBase() override = default;
    const ::Fit::FitableInvokerFactory* GetFactory() const override;
    const ::Fit::FitConfigPtr& GetConfig() const final;
    const ::Fit::FitableCoordinatePtr& GetCoordinate() const final;
    ::Fit::Framework::Annotation::FitableType GetFitableType() const final;
private:
    const ::Fit::FitableInvokerFactory* factory_;
    ::Fit::FitableCoordinatePtr coordinate_;
    ::Fit::Framework::Annotation::FitableType fitableType_ {::Fit::Framework::Annotation::FitableType::MAIN};
    ::Fit::FitConfigPtr config_ {nullptr};
};
}

#endif // FIT_FITABLE_INVOKER_HPP
