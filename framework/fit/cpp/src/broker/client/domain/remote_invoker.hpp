/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */

#ifndef FIT_REMOTE_INVOKER_HPP
#define FIT_REMOTE_INVOKER_HPP
#include <memory>
#include <utility>
#include <fit/internal/framework/formatter_service.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <component/com_huawei_fit_hakuna_kernel_broker_shared_fit_response/1.0.0/cplusplus/fitResponse.hpp>
#include "decorator/fitable_invoker_trace_decorator.hpp"
#include "fitable_endpoint.hpp"
namespace Fit {
/**
 * 为服务实现提供远程调用程序。
 */
class RemoteInvoker : public FitableInvokerBase, public virtual TraceAbleFitableInvoker {
public:
    explicit RemoteInvoker(const ::Fit::FitableInvokerFactory* factory,
        ::Fit::FitableCoordinatePtr coordinate, ::Fit::Framework::Annotation::FitableType fitableType,
        ::Fit::FitableEndpointPtr endpoint, FitConfigPtr config);
    ~RemoteInvoker() override = default;
    ::Fit::TraceContextPtr CreateTraceContext(ContextObj context) const override;
    FitCode Invoke(ContextObj context, ::Fit::vector<::Fit::any>& in, ::Fit::vector<::Fit::any>& out) const override;

    /**
     * 获取待调用的远程服务的终结点信息。
     *
     * @return 表示网络终结点信息的指针。
     */
    ::Fit::FitableEndpointPtr GetEndpoint() const;
protected:
    /**
     * 获取序列化方式的基本信息。
     *
     * @return 表示序列化方式基本信息的引用。
     */
    const ::Fit::Framework::Formatter::BaseSerialization& GetSerialization() const;
private:
    FitCode DisableAddress() const;
    FitCode BuildMetadataBytes(ContextObj context, ::Fit::bytes& result) const;
    FitCode SerializeRequest(ContextObj context, const ::Fit::vector<::Fit::any>& in, ::Fit::bytes& result) const;
    FitCode InvokeRemoteFitable(ContextObj context, const ::Fit::bytes& metadata, const ::Fit::bytes& request,
        ::Fit::vector<::Fit::any>& out) const;
    FitCode ParseResult(ContextObj context, ::fit::hakuna::kernel::broker::shared::FitResponse* fitResponse,
        ::Fit::vector<::Fit::any>& out) const;
private:
    FitableEndpointPtr endpoint_;
    ::Fit::Framework::Formatter::BaseSerialization serialization_ {};
};

/**
 * 为服务实现的远程执行程序提供构建程序。
 */
class RemoteInvokerBuilder final {
public:
    RemoteInvokerBuilder() = default;
    ~RemoteInvokerBuilder() = default;

    /**
     * 设置正在创建服务实例的工厂。
     *
     * @param factory 表示指向创建当前服务实例的工厂的指针。
     * @return 表示当前构建程序的引用。
     */
    RemoteInvokerBuilder& SetFactory(const ::Fit::FitableInvokerFactory* factory);

    /**
     * 设置服务实现的坐标。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @return 表示当前构建程序的引用。
     */
    RemoteInvokerBuilder& SetCoordinate(::Fit::FitableCoordinatePtr coordinate);

    /**
     * 设置服务实现的类型。
     *
     * @param fitableType 表示服务实现的枚举值。
     * @return 表示当前构建程序的引用。
     */
    RemoteInvokerBuilder& SetFitableType(::Fit::Framework::Annotation::FitableType fitableType);

    /**
     * 设置服务实现的远程网络终结点。
     *
     * @param endpoint 表示指向网络终结点信息的指针。
     * @return 表示当前构建程序的引用。
     */
    RemoteInvokerBuilder& SetEndpoint(FitableEndpointPtr endpoint);
    RemoteInvokerBuilder& SetFitConfig(FitConfigPtr config);

    /**
     * 构建服务实现的远程执行程序。
     *
     * @return 表示指向新构建的远程服务执行程序的指针。
     */
    std::unique_ptr<FitableInvoker> Build();
private:
    const ::Fit::FitableInvokerFactory* factory_ {nullptr};
    ::Fit::FitableCoordinatePtr coordinate_ {nullptr};
    ::Fit::Framework::Annotation::FitableType fitableType_ {};
    FitableEndpointPtr endpoint_ {nullptr};
    FitConfigPtr config_ {nullptr};
};
}
#endif // FIT_REMOTE_INVOKER_HPP