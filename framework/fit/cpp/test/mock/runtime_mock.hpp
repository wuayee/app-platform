/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/8/11
 * Notes:       :
 */

#ifndef RUNTIME_MOCK_HPP
#define RUNTIME_MOCK_HPP

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/internal/framework/annotation/fitable_collector_inner.hpp>
#include <mock/configuration_repo_mock.hpp>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/broker/broker_client.h>

namespace Fit {
class RuntimeContainerMock : public Runtime {
public:
    ~RuntimeContainerMock() override = default;
public:
    bool Start() override { return true; }
    bool Stop() override { return true; }

    void AddElement(unique_ptr<RuntimeElement> ele) override
    {
        elements_.emplace_back(move(ele));
    }

    bool GetElementAnyOf(const std::function<bool(RuntimeElement*)>& condition) override
    {
        for (auto& e : elements_) {
            if (condition(e.get())) {
                return true;
            }
        }
        return false;
    }

private:
    vector<unique_ptr<RuntimeElement>> elements_;
};
class RuntimeMock {
public:
    RuntimeMock();
    ~RuntimeMock();

    void SetUp();

    void TearDown();

    template<typename F>
    RuntimeMock &RegisterFitable(F &&f, const char *genericId, const char *fitableId)
    {
        return RegisterFitable(Fitable(std::forward<F>(f)), genericId, fitableId);
    }

    RuntimeMock &RegisterFitable(
        ::Fit::Framework::Annotation::FitableFunctionProxyType func, const char *genericId, const char *fitableId);

    RuntimeMock &ClearFitable();

    RuntimeMock &SetGenericableConfig(GenericableConfiguration config);

    GenericableConfiguration &GetGenericableConfig(const char *genericId);

protected:
    template<typename Ret, typename... Args>
    ::Fit::Framework::FunctionProxyType<FitCode> Fitable(Ret(func)(Args...))
    {
        return ::Fit::Framework::Annotation::FitableFunctionWrapper<Ret, Args...>(func).GetProxy();
    }

    template<typename Ret, typename... Args>
    ::Fit::Framework::FunctionProxyType<FitCode> Fitable(std::function<Ret(Args...)> func)
    {
        return ::Fit::Framework::Annotation::FitableFunctionWrapper<Ret, Args...>(func).GetProxy();
    }

private:
    class Impl;
    std::unique_ptr<Impl> impl_;
};
}

#endif // RUNTIME_MOCK_HPP
