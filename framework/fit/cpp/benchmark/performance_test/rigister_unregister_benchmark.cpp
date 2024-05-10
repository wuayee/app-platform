/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : xiongxiaoping
 * Date         : 2021/08/02
 * Notes:       :
 */

#include <benchmark/benchmark.h>
#include <genericable/com_huawei_fit_registry_register_fit_service/1.0.0/cplusplus/registerFitService.hpp>
#include <genericable/com_huawei_fit_registry_unregister_fit_service/1.0.0/cplusplus/unregisterFitService.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include "echo/echo.hpp"
#include "echo/echo_impl.hpp"
#include "echo/echo_converter.hpp"

void Register(benchmark::State &state)
{
    fit::registry::registerFitService reg;
    fit::registry::unregisterFitService unreg;
    fit::registry::Address address = FitSystemPropertyUtils::Address();
    for (auto _ : state) {
        state.PauseTiming();
        std::vector<fit::registry::ServiceMeta> regFitables;
        std::vector<fit::registry::Fitable> unregFitables;
        for (int i = 0; i < state.range(0); ++i) {
            Fit::string gid = std::to_string(state.thread_index) +
                "_" + std::to_string(i) + "_" + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
            Fit::string fid = std::to_string(state.thread_index) +
                "_" + std::to_string(i) + "_" + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;

            fit::registry::ServiceMeta meta;
            meta.fitable = Fit::Context::NewObj<fit::registry::Fitable>(reg.ctx_);
            meta.fitable->genericId = gid;
            meta.fitable->genericVersion = "1.0.0";
            meta.fitable->fitId = fid;
            meta.fitable->fitVersion = "1.0.0";
            meta.serviceName = "";
            meta.pluginName = "";
            regFitables.push_back(meta);

            fit::registry::Fitable fitable;
            fitable.genericId = gid;
            fitable.genericVersion = "1.0.0";
            fitable.fitId = fid;
            fitable.fitVersion = "1.0.0";
            unregFitables.push_back(fitable);
        }
        state.ResumeTiming();

        bool *regResult = nullptr;
        auto regRet = reg(&regFitables, &address, &regResult);
        if (regRet != FIT_OK) {
            printf("register failed: ret = %X.\n", regRet);
        }

        state.PauseTiming();
        bool *unregResult = nullptr;
        auto unregRet = unreg(&unregFitables, &address, &unregResult);
        if (unregRet != FIT_OK) {
            printf("unregister failed: ret = %X.\n", unregRet);
        }
        state.ResumeTiming();
    }
}

void Unregister(benchmark::State &state)
{
    fit::registry::registerFitService reg;
    fit::registry::unregisterFitService unreg;
    fit::registry::Address address = FitSystemPropertyUtils::Address();
    for (auto _ : state) {
        state.PauseTiming();
        std::vector<fit::registry::ServiceMeta> regFitables;
        std::vector<fit::registry::Fitable> unregFitables;
        for (int i = 0; i < state.range(0); ++i) {
            Fit::string gid = std::to_string(state.thread_index) +
                "_" + std::to_string(i) + "_" + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
            Fit::string fid = std::to_string(state.thread_index) +
                "_" + std::to_string(i) + "_" + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;

            fit::registry::ServiceMeta meta;
            meta.fitable = Fit::Context::NewObj<fit::registry::Fitable>(reg.ctx_);
            meta.fitable->genericId = gid;
            meta.fitable->genericVersion = "1.0.0";
            meta.fitable->fitId = fid;
            meta.fitable->fitVersion = "1.0.0";
            meta.serviceName = "";
            meta.pluginName = "";
            regFitables.push_back(meta);

            fit::registry::Fitable fitable;
            fitable.genericId = gid;
            fitable.genericVersion = "1.0.0";
            fitable.fitId = fid;
            fitable.fitVersion = "1.0.0";
            unregFitables.push_back(fitable);
        }
        state.ResumeTiming();

        state.PauseTiming();
        bool *regResult = nullptr;
        auto regRet = reg(&regFitables, &address, &regResult);
        if (regRet != FIT_OK) {
            printf("register failed: ret = %X.\n", regRet);
        }
        state.ResumeTiming();

        bool *unregResult = nullptr;
        auto unregRet = unreg(&unregFitables, &address, &unregResult);
        if (unregRet != FIT_OK) {
            printf("unregister failed: ret = %X.\n", unregRet);
        }
    }
}

BENCHMARK(Register)
    ->Unit(benchmark::kMillisecond)
    ->RangeMultiplier(100)->Range(1, 100)
    ->Threads(1);

BENCHMARK(Unregister)
    ->Unit(benchmark::kMillisecond)
    ->RangeMultiplier(100)->Range(1, 100)
    ->Threads(1);
