/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#ifndef FUNCTION_PROXY_HPP
#define FUNCTION_PROXY_HPP

#include <iostream>
#include <fit/stl/any.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/utility.hpp>
#include <fit/stl/except.hpp>
#include <functional>

#include <fit/fit_code.h>
#include "proxy_define.hpp"

namespace Fit {
namespace Framework {
template<typename T>
bool UnpackArgs(Arguments& packArgs, uint32_t index, T& arg)
{
    if (typeid(T) != packArgs[index].type()) {
        return false;
    }
    arg = Fit::any_cast<T>(packArgs[index]);

    return true;
}

template<typename T, typename... Args>
bool UnpackArgs(Arguments& packArgs, uint32_t index, T& arg, Args& ... args)
{
    if (typeid(T) != packArgs[index].type()) {
        return false;
    }
    arg = Fit::any_cast<T>(packArgs[index]);
    return UnpackArgs(packArgs, ++index, args...);
}

template<typename... Args>
bool UnpackArgs(Arguments& packArgs, Args& ... args)
{
    return UnpackArgs(packArgs, 0, args...);
}

__attribute__ ((visibility ("default"))) std::string ExceptionMessage(const char *desc, size_t expectArgCount,
    const char *functionSign, const Arguments &actualArgs);

template<typename Ret, typename F, typename Tp, std::size_t... Indexes>
Ret FunctionInvoke(F& func, Arguments& packArgs, Tp*, Fit::index_sequence<Indexes...>*)
{
    if (packArgs.size() != std::tuple_size<Tp>::value) {
        FIT_THROW_INVALID_ARGUMENT(ExceptionMessage("Not matched argument size.", std::tuple_size<Tp>::value,
            typeid(F).name(), packArgs).c_str());
        return {};
    }
    Tp unpackArgs {};
    if (UnpackArgs(packArgs, std::get<Indexes>(unpackArgs)...)) {
        return func(std::get<Indexes>(unpackArgs)...);
    }

    FIT_THROW_INVALID_ARGUMENT(ExceptionMessage("Not matched argument type.", std::tuple_size<Tp>::value,
        typeid(F).name(), packArgs).c_str());
    return {};
}

template<size_t N>
class Invoker {
public:
    template<typename Ret, typename F, typename Tp>
    Ret Invoke(F& f, Arguments& args, Tp*)
    {
        return FunctionInvoke<Ret>(f, args, (Tp*)nullptr,
            (Fit::make_index_sequence<std::tuple_size<Tp>::value>*)nullptr);
    }
};

template<>
class Invoker<0> {
public:
    template<typename Ret, typename F, typename Tp>
    Ret Invoke(F& f, Arguments& args, Tp*)
    {
        return f();
    }
};

static constexpr int32_t FUNCTION_WRAPPER_OK = 0;
static constexpr int32_t FUNCTION_WRAPPER_NO_FUNC = 1;
static constexpr int32_t FUNCTION_WRAPPER_INVALID_ARGUMENT = 2;
static constexpr int32_t FUNCTION_WRAPPER_CANNOT_INVOKE = 3;

struct FunctionWrapperError {
    int32_t code;
    Fit::string msg;
};

template<typename Ret>
using FunctionProxyType = std::function<Ret(Arguments&)>;

template<typename...>
class FunctionWrapper;
template<typename Ret, typename... Args, typename DefaultRet>
class FunctionWrapper<Ret(Args...), DefaultRet> {
public:
    using ReturnType = Ret;
    using FuncType = std::function<Ret(Args...)>;

    explicit FunctionWrapper(FuncType func)
    {
        FuncType func_(func);
        proxy_ = [func_](Arguments& args) -> ReturnType {
            if (!func_) {
                return DefaultRet()(FunctionWrapperError {FUNCTION_WRAPPER_NO_FUNC, "function is invalid"}, args);
            }
#if __cpp_exceptions
            try {
                return Invoker<sizeof...(Args)>().template Invoke<Ret>(func_, args, (std::tuple<Args...>*)nullptr);
            } catch (std::invalid_argument& e) {
                return DefaultRet()(FunctionWrapperError {FUNCTION_WRAPPER_INVALID_ARGUMENT, e.what()}, args);
            } catch (std::exception& e) {
                return DefaultRet()(FunctionWrapperError {FUNCTION_WRAPPER_CANNOT_INVOKE, e.what()}, args);
            }
#else
            return Invoker<sizeof...(Args)>().template Invoke<Ret>(func_, args, (std::tuple<Args...>*)nullptr);
#endif
        };
    }
    ~FunctionWrapper() = default;

    const FunctionProxyType<Ret>& GetProxy() const noexcept
    {
        return proxy_;
    }

private:
    FunctionProxyType<Ret> proxy_;
};
}
}
#endif // FUNCTION_PROXY_HPP
