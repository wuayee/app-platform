/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides endpoint for fitables.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#ifndef FIT_FITABLE_ENDPOINT_HPP
#define FIT_FITABLE_ENDPOINT_HPP

#include "fitable_coordinate.hpp"
#include "broker/client/application/gateway/fit_config.h"
#include "broker/client/application/gateway/fit_discovery.h"

#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

#include <functional>
#include <memory>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp>

namespace Fit {
class FitableEndpoint;
class FitableEndpointBuilder;

/**
 * 为服务实现的网络终结点提供共享指针。
 */
using FitableEndpointPtr = std::shared_ptr<FitableEndpoint>;

/**
 * 为服务的远程调用提供网络终结点信息。
 */
class FitableEndpoint {
public:
    struct Context {
        Framework::Worker worker;
        Framework::Application application;
    };
    virtual ~FitableEndpoint() = default;

    /**
     * 获取目标服务所在工作进程的唯一标识。
     *
     * @return 表示工作进程唯一标识的字符串。
     */
    virtual const ::Fit::string& GetWorkerId() const = 0;

    /**
     * 获取目标服务所在的环境标识。
     *
     * @return 表示环境标识的字符串。
     */
    virtual const ::Fit::string& GetEnvironment() const = 0;

    /**
     * 获取目标服务所在主机。
     *
     * @return 表示主机的字符串。
     */
    virtual const ::Fit::string& GetHost() const = 0;

    /**
     * 获取目标服务绑定的端口号。
     *
     * @return 表示端口号的16位无符号整数。
     */
    virtual uint16_t GetPort() const = 0;

    /**
     * 表示使用的传输协议。
     *
     * @return 表示传输协议的32位整数。
     */
    virtual int32_t GetProtocol() const = 0;

    /**
     * 获取目标服务所支持的序列化方式。
     *
     * @return 表示序列化方式的32位整数的集合。
     */
    virtual const ::Fit::vector<int32_t>& GetFormats() const = 0;

    /**
     * 获取一个值，该值指示当前网络终结点是否是一个本地终结点。
     *
     * @return 若是本地终结点，则为 true；否则为 false。
     */
    virtual bool IsLocal() const = 0;

    /**
     * @brief 查询所属的worker信息
     *
     * @return 所属的worker信息
     */
    virtual const Context& GetContext() const = 0;

    /**
     * 返回一个构建程序，用以创建服务实现的网络终结点的新实例。
     *
     * @return 表示用以构建网络终结点新实例的构建程序。
     */
    static FitableEndpointBuilder Custom();

    /**
     * 获取一个网络终结点，用以指示本地网络。
     *
     * @return 表示指向本地网络终结点的指针。
     */
    static FitableEndpointPtr GetLocalEndpoint();
};

/**
 * 为 FitableEndpoint 提供基类。
 */
class FitableEndpointBase : public FitableEndpoint {
public:
    bool IsLocal() const override;
};

/**
 * 为服务实现网络终结点提供构建程序。
 */
class FitableEndpointBuilder {
public:
    using Worker = fit::hakuna::kernel::registry::shared::Worker;
    using Application = fit::hakuna::kernel::registry::shared::Application;
    FitableEndpointBuilder() = default;
    ~FitableEndpointBuilder() = default;

    /**
     * 设置服务实现所在的工作进程的唯一标识。
     *
     * @param workerId 表示工作进程唯一标识的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetWorkerId(::Fit::string workerId);

    /**
     * 设置服务实现所在环境的标识。
     *
     * @param environment 表示环境标识的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetEnvironment(::Fit::string environment);

    /**
     * 设置服务实现所在的主机。
     *
     * @param host 表示主机的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetHost(::Fit::string host);

    /**
     * 设置服务实现使用的端口号。
     *
     * @param port 表示端口号的16位无符号整数。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetPort(uint16_t port);

    /**
     * 设置服务实现使用的传输协议。
     *
     * @param protocol 表示远程服务传输协议的32位整数。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetProtocol(int32_t protocol);

    /**
     * 设置服务实现支持的序列化方式。
     *
     * @param formats 表示序列化方式的32位整数的列表。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetFormats(::Fit::vector<int32_t> formats);

    /**
     * 设置服务实现关联的一些信息。
     *
     * @param Context 关联的信息。
     * @return 表示当前构建程序的引用。
     */
    FitableEndpointBuilder& SetContext(FitableEndpoint::Context context);

    /**
     * 构建服务实现的网络终结点新实例。
     *
     * @return 表示指向新创建的服务实现网络终结点的指针。
     */
    FitableEndpointPtr Build();
private:
    ::Fit::string workerId_ {};
    ::Fit::string environment_ {};
    ::Fit::string host_ {};
    uint16_t port_ {};
    int32_t protocol_ {};
    ::Fit::vector<int32_t> formats_ {};
    FitableEndpoint::Context context_;
};

/**
 * 为服务实现的网络终结点提供谓词。
 */
class FitableEndpointPredicate {
public:
    virtual ~FitableEndpointPredicate() = default;

    /**
     * 测试指定的服务实现的网络终结点是否符合条件。
     *
     * @param endpoint 表示待测试的服务实现的网络终结点。
     * @return 若符合条件，则为 true；否则为 false。
     */
    virtual bool Test(const FitableEndpoint& endpoint) const = 0;

    /**
     * 使用指定的断言方法创建服务实现网络终结点的谓词。
     *
     * @param func 表示用以断言服务实现网络终结点的函数。
     * @return 表示指向新创建的断言的指针。
     */
    static std::unique_ptr<FitableEndpointPredicate> Create(std::function<bool(const FitableEndpoint&)> func);

    /**
     * 使用逻辑与的语义合并两个断言程序。
     *
     * @param predicate1 表示待合并的第一个断言程序。
     * @param predicate2 表示待合并的第二个断言程序。
     * @return 表示合并后的断言程序。
     */
    static std::unique_ptr<FitableEndpointPredicate> Combine(
        std::unique_ptr<FitableEndpointPredicate> predicate1,
        std::unique_ptr<FitableEndpointPredicate> predicate2);
};

/**
 * 为服务实现的网络终结点提供供应程序。
 */
class FitableEndpointSupplier {
public:
    FitableEndpointSupplier() = default;
    virtual ~FitableEndpointSupplier() = default;

    FitableEndpointSupplier(const FitableEndpointSupplier&) = delete;
    FitableEndpointSupplier(FitableEndpointSupplier&&) = delete;
    FitableEndpointSupplier& operator=(const FitableEndpointSupplier&) = delete;
    FitableEndpointSupplier& operator=(FitableEndpointSupplier&&) = delete;

    /**
     * 获取服务实现的网络终结点。
     *
     * @return 若存在可用的网络终结点，则为指向网络终结点的指针；否则为 nullptr。
     */
    virtual FitableEndpointPtr Get() const = 0;

    /**
     * 创建一个网络端点提供程序，用以直接使用指定的网络端点。
     *
     * @param endpoint 表示指向待使用的网络端点的指针。
     * @return 表示指向新创建的网络端点提供程序的的指针。
     */
    static std::unique_ptr<FitableEndpointSupplier> CreateDirectSupplier(FitableEndpointPtr endpoint);

    /**
     * 创建一个网络端点提供程序，用以通过 LoadBalance 获取地址。
     *
     * @param coordinate 表示指向待调用的服务实现的坐标的指针。
     * @param config 表示指向服务实现的配置的指针。
     * @return 表示指向新创建的网络端点提供程序的的指针。
     */
    static std::unique_ptr<FitableEndpointSupplier> CreateLoadBalanceSupplier(FitableCoordinatePtr coordinate,
        FitConfigPtr config, BrokerFitableDiscoveryPtr discovery);

    /**
     * 创建一个网络端点提供程序，用以通过 LoadBalance 获取地址。
     *
     * @param coordinate 表示指向待调用的服务实现的坐标的指针。
     * @param config 表示指向服务实现的配置的指针。
     * @param predicate 表示用以筛选目标地址的断言。
     * @return 表示指向新创建的网络端点提供程序的的指针。
     */
    static std::unique_ptr<FitableEndpointSupplier> CreateLoadBalanceSupplier(FitableCoordinatePtr coordinate,
        FitConfigPtr config, BrokerFitableDiscoveryPtr discovery, std::unique_ptr<FitableEndpointPredicate> predicate);

    /**
     * 创建一个网络终结点提供程序，用以获取本地服务的地址。
     *
     * @param coordinate 表示指向待调用的服务实现的坐标的指针。
     * @param discovery 表示指向本地服务发现程序的指针。
     * @return 表示指向新创建的网络终结点提供程序的指针。
     */
    static std::unique_ptr<FitableEndpointSupplier> CreateLocalSupplier(FitableCoordinatePtr coordinate,
        ::Fit::BrokerFitableDiscoveryPtr discovery);

    /**
     * 组合两个网络终结点提供程序。
     *
     * @param supplier1 表示待组合的第一个网络终结点的指针。
     * @param supplier2 表示待组合的第二个网络终结点的指针。
     * @return 若 supplier1 为 nullptr，则为 supplier2；若 supplier2 为 nullptr，则为 supplier1；否则为组合后的提供程序。
     */
    static std::unique_ptr<FitableEndpointSupplier> Combine(
        std::unique_ptr<FitableEndpointSupplier> supplier1,
        std::unique_ptr<FitableEndpointSupplier> supplier2);
};
}

#endif // FIT_FITABLE_ENDPOINT_HPP
