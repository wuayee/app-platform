/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/5
 * Notes:       :
 */

#include <benchmark/benchmark.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>

using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Annotation;

namespace {
int32_t Add(void *ctx, int32_t a, int32_t b)
{
    return a + b;
}

int32_t EmptyArg()
{
    return 0;
}

int32_t OneArg(void *ctx)
{
    return 0;
}

class Base {
public:
    Base() = default;
    virtual ~Base() = default;

    virtual int32_t Add(int32_t a, int32_t b) = 0;
};

class Derived : public Base {
public:
    Derived() = default;
    ~Derived() override = default;

    int32_t Add(int32_t a, int32_t b) override
    {
        return a + b;
    }
};
}

static void BM_VirtualFunctionCall(benchmark::State &state)
{
    Derived d;
    Base *b = &d;
    for (auto _ : state) {
        b->Add(1, 2);
    }
}

BENCHMARK(BM_VirtualFunctionCall);

static void BM_FunctionProxyCall_EmptyArg(benchmark::State &state)
{
    auto proxy = FitableFunctionWrapper<int32_t>(EmptyArg).GetProxy();
    Arguments args {};
    for (auto _ : state) {
        proxy(args);
    }
}

BENCHMARK(BM_FunctionProxyCall_EmptyArg);

static void BM_FunctionProxyCall_OneArg(benchmark::State &state)
{
    auto proxy = FitableFunctionWrapper<int32_t, void *>(OneArg).GetProxy();
    Arguments args {(void *)nullptr};
    for (auto _ : state) {
        proxy(args);
    }
}

BENCHMARK(BM_FunctionProxyCall_OneArg);

static void BM_FunctionProxyCall_ThreeArgs(benchmark::State &state)
{
    auto proxy = FitableFunctionWrapper<int32_t, void *, int32_t, int32_t>(Add).GetProxy();
    Arguments args {(void *)nullptr, 1, 2};
    for (auto _ : state) {
        proxy(args);
    }
}

BENCHMARK(BM_FunctionProxyCall_ThreeArgs);