/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides shell for fitable invokers.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/06
 */

#ifndef FIT_FITABLE_INVOKER_SHELL_HPP
#define FIT_FITABLE_INVOKER_SHELL_HPP

#include "remote_invoker.hpp"

#include "fitable_invoker_factory.hpp"
#include "broker/client/application/gateway/fit_discovery.h"

#include <fit/internal/framework/formatter_service.hpp>

namespace Fit {
/**
 * 为服务实现的调用程序提供外壳程序。
 * <p>外壳程序根据实际情况决定调用本地或远程的服务实现。</p>
 */
class FitableInvokerShell : public FitableInvokerBase {
public:
    /**
     * 创建服务实现调用程序的外壳程序的新实例。
     *
     * @param factory 表示指向所属的工厂的指针。
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param fitableType 表示服务实现的类型。
     * @param endpointSupplier 表示服务实现的网络终结点的提供程序。当为 nullptr 并将进行远程调用时，将使用负载均衡方式选择网络终结点。
     */
    explicit FitableInvokerShell(const ::Fit::FitableInvokerFactory* factory,
        ::Fit::FitableCoordinatePtr coordinate, ::Fit::Framework::Annotation::FitableType fitableType,
        std::unique_ptr<FitableEndpointSupplier> endpointSupplier, FitConfigPtr config);
    ~FitableInvokerShell() override = default;
    FitCode Invoke(ContextObj context, ::Fit::Framework::Arguments& in,
        ::Fit::Framework::Arguments& out) const override;
private:
    std::unique_ptr<FitableInvoker> CreateLocalInvoker() const;
    std::unique_ptr<FitableInvoker> CreateRemoteInvoker(FitableEndpointPtr endpoint) const;
    std::unique_ptr<FitableEndpointSupplier> CreateDefaultEndpointSupplier() const;
    std::unique_ptr<FitableEndpointSupplier> endpointSupplier_ {nullptr};
};
}

#endif // FIT_FITABLE_INVOKER_SHELL_HPP
