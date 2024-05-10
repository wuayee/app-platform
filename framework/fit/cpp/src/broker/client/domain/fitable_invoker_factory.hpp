/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides factory for fitable invokers.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#ifndef FIT_FITABLE_INVOKER_FACTORY_HPP
#define FIT_FITABLE_INVOKER_FACTORY_HPP

#include "fitable_endpoint.hpp"
#include "fitable_invoker.hpp"
#include "broker/client/application/gateway/fit_discovery.h"

#include <fit/internal/framework/formatter_service.hpp>

namespace Fit {
class FitableInvokerFactoryBuilder;

/**
 * 为服务实现的调用程序提供工厂。
 * <p>设计原则：服务调用程序在使用时每次创建新的实例，避免使用缓存。
 * <ul>
 * <li>缓存本身消耗内存，服务实现的唯一标识有四个字段，若使用树形结构的缓存，作为键将额外消耗较多内存。</li>
 * <li>对服务实现的唯一标识进行比较时，最多需要进行4个字符串的比较，服务实现数量较多时会产生较多的额外计算。</li>
 * </ul></p>
 */
class FitableInvokerFactory {
public:
    FitableInvokerFactory() = default;
    virtual ~FitableInvokerFactory() = default;

    FitableInvokerFactory(const FitableInvokerFactory&) = delete;
    FitableInvokerFactory(FitableInvokerFactory&&) = delete;
    FitableInvokerFactory& operator=(const FitableInvokerFactory&) = delete;
    FitableInvokerFactory& operator=(FitableInvokerFactory&&) = delete;

    /**
     * 获取当前工作进程的唯一标识。
     *
     * @return 表示指向当前工作进程的唯一标识的引用。
     */
    virtual const ::Fit::string& GetCurrentWorkerId() const = 0;

    /**
     * 获取服务发现程序。
     *
     * @return 表示指向服务发现程序的共享指针。
     */
    virtual const ::Fit::BrokerFitableDiscoveryPtr& GetFitableDiscovery() const = 0;

    /**
     * 获取序列化服务。
     *
     * @return 表示指向序列化服务的共享指针。
     */
    virtual const ::Fit::Framework::Formatter::FormatterServicePtr& GetFormatterService() const = 0;

    /**
     * 获取指定服务实现的原始调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetRawInvoker(
        ::Fit::FitableCoordinatePtr coordinate, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的原始调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param fitableType 表示服务实现的类型。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetRawInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的原始调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param endpointSupplier 表示服务实现的网络终结点的提供程序。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetRawInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的原始调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param fitableType 表示服务实现的类型。
     * @param endpointSupplier 表示服务实现的网络终结点的提供程序。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetRawInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType,
        std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetInvoker(
        ::Fit::FitableCoordinatePtr coordinate, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param fitableType 表示服务实现的类型。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param endpointSupplier 表示服务实现的网络终结点的提供程序。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        ::std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const = 0;

    /**
     * 获取指定服务实现的调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param fitableType 表示服务实现的类型。
     * @param endpointSupplier 表示服务实现的网络终结点的提供程序。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType,
        ::std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, FitConfigPtr config) const = 0;

    /**
     * 获取一个用以调用本地服务实现的调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetLocalInvoker(
        ::Fit::FitableCoordinatePtr coordinate, FitConfigPtr config) const = 0;

    /**
     * 获取一个用以调用本地服务实现的调用程序。
     *
     * @param coordinate 表示指向服务实现的坐标的指针。
     * @param fitableType 表示服务实现的类型。
     * @return 表示指向该服务实现的调用程序的指针。
     */
    virtual std::unique_ptr<FitableInvoker> GetLocalInvoker(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType, FitConfigPtr config) const = 0;

    /**
     * 返回一个构建程序，用以自定义服务调用程序工厂的新实例。
     *
     * @return 表示服务调用程序工厂的构建程序的新实例。
     */
    static FitableInvokerFactoryBuilder Custom();
};

using FitableInvokerFactoryPtr = std::shared_ptr<FitableInvokerFactory>;

/**
 * 为服务实现调用程序提供构建程序。
 */
class FitableInvokerFactoryBuilder {
public:
    /**
     * 设置本地工作进程的唯一标识。
     *
     * @param workerId 表示工作进程唯一标识的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableInvokerFactoryBuilder& SetCurrentWorkerId(::Fit::string workerId);

    /**
     * 设置所使用的服务实现发现程序。
     *
     * @param fitableDiscovery 表示指向服务发现程序的指针。
     * @return 表示当前构建程序的引用。
     */
    FitableInvokerFactoryBuilder& SetFitableDiscovery(::Fit::BrokerFitableDiscoveryPtr fitableDiscovery);

    /**
     * 设置序列化服务。
     *
     * @param formatterService 表示指向序列化服务的指针。
     * @return 表示当前构建程序的引用。
     */
    FitableInvokerFactoryBuilder& SetFormatterService(
        ::Fit::Framework::Formatter::FormatterServicePtr formatterService);

    /**
     * 构建服务实现调用程序工厂的新实例。
     *
     * @return 表示指向服务实现调用程序的指针。
     */
    FitableInvokerFactoryPtr Build();
private:
    ::Fit::string workerId_ {};
    ::Fit::BrokerFitableDiscoveryPtr fitableDiscovery_ {nullptr};
    ::Fit::Framework::Formatter::FormatterServicePtr formatterService_ {nullptr};
};
}

#endif // FIT_FITABLE_INVOKER_FACTORY_HPP
