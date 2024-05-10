/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#ifndef FITABLE_REGISTRAR_HPP
#define FITABLE_REGISTRAR_HPP

#include <type_traits>
#include <functional>
#include <fit/stl/vector.hpp>
#include <fit/external/util/registration.hpp>
#include <fit/external/util/context/context_api.hpp>
#include "fitable_detail.hpp"
#include "fitable_collector.hpp"

namespace Fit {
namespace Framework {
namespace Annotation {
class __attribute__ ((visibility ("hidden"))) FitablePluginCollector {
public:
    ~FitablePluginCollector()
    {
        FitableCollector::UnRegister(annotations_);
    }

    static void Register(const FitableDetailList& annotations)
    {
        auto newItems = Instance().AddItems(annotations);
        FitableCollector::Register(newItems);
    }

    static FitablePluginCollector& Instance()
    {
        static FitablePluginCollector instance;
        return instance;
    }

    FitableDetailPtrList AddItems(const FitableDetailList& annotations)
    {
        FitableDetailPtrList newItems;
        newItems.reserve(annotations.size());
        for (auto& annotation : annotations) {
            newItems.push_back(std::make_shared<FitableDetail>(annotation));
        }

        annotations_.reserve(annotations_.size() + newItems.size());
        annotations_.insert(annotations_.end(), newItems.begin(), newItems.end());

        return newItems;
    }

private:
    FitableDetailPtrList annotations_{};
};

class FitableFunctionProxyDefaultReturn {
public:
    FitCode operator()(const FunctionWrapperError& err, Arguments& args);
};

template<typename Ret, typename... Args>
using FitableFunctionWrapper = FunctionWrapper<typename std::enable_if<std::is_same<Ret, FitCode>::value, Ret>::type(
    Args...), FitableFunctionProxyDefaultReturn>;

template<typename...>
class FitableRegistrar;

template<typename Ret, typename ...Args>
class FitableRegistrar<Ret(Args...)> : public FitableDetail {
public:
    using FuncType = std::function<Ret(Args...)>;

    explicit FitableRegistrar(FuncType func)
        : FitableDetail(FitableFunctionWrapper<Ret, Args...>(func).GetProxy()) {}

    ~FitableRegistrar()
    {
        FitablePluginCollector::Register(FitableDetailList{std::move(*this)});
    }
};

// support for c style function
template<typename Ret, typename... Args>
FitableRegistrar<Ret(Args...)> Fitable(Ret(func)(Args...))
{
    return FitableRegistrar<Ret(Args...)>(func);
}

// support for lambda
template<typename Ret, typename... Args>
FitableRegistrar<Ret(Args...)> Fitable(std::function<Ret(Args...)> func)
{
    return FitableRegistrar<Ret(Args...)>(func);
}
}
}
}
#endif // FITABLE_REGISTRAR_HPP
