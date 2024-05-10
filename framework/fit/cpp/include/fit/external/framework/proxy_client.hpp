/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/16
 * Notes:       :
 */

#ifndef PROXYCLIENT_HPP
#define PROXYCLIENT_HPP

#include <fit/external/broker/broker_client_external.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/broker/multiplex_invoker.hpp>
#include <fit/stl/bits.hpp>
#include "proxy_define.hpp"
#include "function_proxy.hpp"

namespace Fit {
namespace Framework {
template<typename... Args>
Arguments PackArgs(Args &&... args)
{
    return {std::forward<Args>(args)...};
}

template<typename ...Args>
class ArgumentsIn {
};

template<typename ...Args>
class ArgumentsOut {
};

class ProxyClientBase {
public:
    explicit ProxyClientBase(const char *genericId)
        : genericId_(genericId),
          ctx_(Context::NewContext()),
          filterInvoker_(CreateMultiInvoker(genericId)) {}

    ProxyClientBase(const char *genericId, ContextObj ctx)
        : genericId_(genericId),
          ctx_(ctx),
          filterInvoker_(CreateMultiInvoker(genericId)),
          needDestroyCtx_(false) {}

    virtual ~ProxyClientBase()
    {
        if (needDestroyCtx_) {
            ContextDestroy(ctx_);
        }
    }
    ContextObj GetContext() const noexcept
    {
        return ctx_;
    }
    void SetAlias(const Fit::string &alias)
    {
        ContextSetAlias(ctx_, alias.data());
    }

    /**
     * 设置重试次数， 不设置默认不重试
     * @param ctx 上下文对象句柄
     * @param count 重试次数
     */
    void SetRetry(uint32_t count) noexcept
    {
        ContextSetRetry(ctx_, count);
    }

    /**
     * 设置超时时间， 不设置默认5000ms
     * @param ctx 上下文对象句柄
     * @param ms 超时时间，单位ms
     */
    void SetTimeout(uint32_t ms) noexcept
    {
        ContextSetTimeout(ctx_, ms);
    }

    FitCode SetFitableId(const char* fitableId) noexcept
    {
        return ContextSetFitableId(ctx_, fitableId);
    }

    /**
     * 设置指定调用地址，可以取消已设置的地址，不设置默认查找地址
     * @param ctx 上下文对象句柄
     * @param targetAddressPtr 指定调用地址，nullptr取消已设置地址
     */
    FitCode SetTargetAddress(const Fit::Context::TargetAddress* targetAddressPtr) noexcept
    {
        return ContextSetTargetAddress(ctx_, targetAddressPtr);
    }

    void ClearCall()
    {
        ContextFreeAll(ctx_);
    }

    const char *genericId_ {};
    ContextObj ctx_ {};
    MultiInvokerPtr filterInvoker_ {};
    bool needDestroyCtx_ {true};
};

template<typename ...Args>
class ProxyClient;

template<typename Ret, typename ...ArgsIn, typename ArgsOut>
class ProxyClient<Ret(ArgumentsIn<ArgsIn...>, ArgumentsOut<ArgsOut>)> : public ProxyClientBase {
public:
    using FilterCallCB = std::function<Ret(const CallBackInfo *, ArgsOut)>;

    explicit ProxyClient(const char *genericId)
        : ProxyClientBase(genericId) {}

    ProxyClient(const char *genericId, ContextObj ctx)
        : ProxyClientBase(genericId, ctx) {}

    ~ProxyClient() override = default;

    FitCode operator()(ArgsIn... in, ArgsOut out)
    {
        Arguments argsIn = PackArgs(std::forward<ArgsIn>(in)...);
        Arguments argsOut = PackArgs(std::forward<ArgsOut>(out));
        auto ret = GenericableInvoke(ctx_, genericId_, argsIn, argsOut);
        if (ret != FIT_ERR_SUCCESS) {
            return ret;
        }
        auto response = Fit::any_cast<ArgsOut>(argsOut[0]);
        if (response != nullptr) {
            *out = *response;
        }
        return ret;
    }

    ProxyClient &Route(MultiplexInvoker::RouteFilter filter)
    {
        filterInvoker_->Route(std::move(filter));
        return *this;
    }

    ProxyClient &Get(MultiplexInvoker::LBFilter filter)
    {
        filterInvoker_->Get(std::move(filter));
        return *this;
    }

    void Exec(ArgsIn &&...in, FilterCallCB cb)
    {
        auto func = [cb](const CallBackInfo &info, const Arguments &out) mutable {
            cb(&info, Fit::any_cast<ArgsOut>(out[0]));
        };

        Arguments argsIn = PackArgs(std::forward<ArgsIn>(in)...);
        using RealOut = typename std::remove_pointer<typename std::remove_pointer<ArgsOut>::type>::type;
        RealOut *tmpOut = Context::NewObj<RealOut>(ctx_);
        Arguments argsOut {&tmpOut};

        filterInvoker_->Exec(ctx_, argsIn, argsOut, func);
    }
};

template<typename Ret, typename ...ArgsIn>
class ProxyClient<Ret(ArgumentsIn<ArgsIn...>)> : public ProxyClientBase {
public:
    using FilterCallCB = std::function<Ret(const CallBackInfo *)>;

    explicit ProxyClient(const char *genericId)
        : ProxyClientBase(genericId) {}

    ProxyClient(const char *genericId, ContextObj ctx)
        : ProxyClientBase(genericId, ctx) {}

    ~ProxyClient() override = default;

    FitCode operator()(ArgsIn... in)
    {
        Arguments argsIn = PackArgs(std::forward<ArgsIn>(in)...);
        Arguments argsOut {};
        return GenericableInvoke(ctx_, genericId_, argsIn, argsOut);
    }

    ProxyClient &Route(MultiplexInvoker::RouteFilter filter)
    {
        filterInvoker_->Route(std::move(filter));
        return *this;
    }

    ProxyClient &Get(MultiplexInvoker::LBFilter filter)
    {
        filterInvoker_->Get(std::move(filter));
        return *this;
    }

    void Exec(ArgsIn &&...in, FilterCallCB cb)
    {
        auto func = [cb](const CallBackInfo &info, const Arguments &out) mutable {
            cb(&info);
        };

        Arguments argsIn = PackArgs(std::forward<ArgsIn>(in)...);
        Arguments argsOut {};
        filterInvoker_->Exec(ctx_, argsIn, argsOut, func);
    }
};

template<typename Ret, typename ArgsOut>
class ProxyClient<Ret(ArgumentsOut<ArgsOut>)> : public ProxyClientBase {
public:
    using FilterCallCB = std::function<Ret(const CallBackInfo *, ArgsOut)>;

    explicit ProxyClient(const char *genericId)
        : ProxyClientBase(genericId) {}

    ProxyClient(const char *genericId, ContextObj ctx)
        : ProxyClientBase(genericId, ctx) {}

    ~ProxyClient() override = default;

    FitCode operator()(ArgsOut out)
    {
        Arguments argsIn {};
        Arguments argsOut = PackArgs(std::forward<ArgsOut>(out));
        auto ret = GenericableInvoke(ctx_, genericId_, argsIn, argsOut);
        if (ret != FIT_ERR_SUCCESS) {
            return ret;
        }
        auto response = Fit::any_cast<ArgsOut>(argsOut[0]);
        if (response != nullptr) {
            *out = *response;
        }
        return ret;
    }

    ProxyClient &Route(MultiplexInvoker::RouteFilter filter)
    {
        filterInvoker_->Route(std::move(filter));
        return *this;
    }

    ProxyClient &Get(MultiplexInvoker::LBFilter filter)
    {
        filterInvoker_->Get(std::move(filter));
        return *this;
    }

    void Exec(FilterCallCB cb)
    {
        auto func = [cb](const CallBackInfo &info, const Arguments &out) mutable {
            cb(&info, Fit::any_cast<ArgsOut>(out[0]));
        };

        Arguments argsIn {};
        using RealOut = typename std::remove_pointer<typename std::remove_pointer<ArgsOut>::type>::type;
        RealOut *tmpOut = Context::NewObj<RealOut>(ctx_);
        Arguments argsOut {&tmpOut};
        filterInvoker_->Exec(ctx_, argsIn, argsOut, func);
    }
};

template<typename Ret>
class ProxyClient<Ret()> : public ProxyClientBase {
public:
    using FilterCallCB = std::function<Ret(const CallBackInfo *)>;

    explicit ProxyClient(const char *genericId)
        : ProxyClientBase(genericId) {}

    ProxyClient(const char *genericId, ContextObj ctx)
        : ProxyClientBase(genericId, ctx) {}

    ~ProxyClient() override = default;

    FitCode operator()()
    {
        Arguments argsIn {};
        Arguments argsOut {};
        return GenericableInvoke(ctx_, genericId_, argsIn, argsOut);
    }

    ProxyClient &Route(MultiplexInvoker::RouteFilter filter)
    {
        filterInvoker_->Route(std::move(filter));
        return *this;
    }

    ProxyClient &Get(MultiplexInvoker::LBFilter filter)
    {
        filterInvoker_->Get(std::move(filter));
        return *this;
    }

    void Exec(FilterCallCB cb)
    {
        auto func = [cb](const CallBackInfo &info, const Arguments &out) mutable {
            cb(&info);
        };

        Arguments argsIn {};
        Arguments argsOut {};
        filterInvoker_->Exec(ctx_, argsIn, argsOut, func);
    }
};
}
}
#endif // PROXYCLIENT_HPP
