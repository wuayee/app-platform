/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/12
 * Notes:       :
 */

#include <benchmark/benchmark.h>
#include <framework/fitable_discovery_default_impl.hpp>
#include <fit/fit_log.h>

#include "prepared_data/fitable_helper.hpp"

using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Annotation;

static void BM_FitableDiscovery_GetLocalFitable(benchmark::State& state)
{
    FitLogSetOutput(FitLogOutputType::file);
    static FitableDiscoveryDefaultImpl fitableDiscovery;
    if (state.thread_index == 0) {
        auto fitableCount = state.range(0);
        ::Fit::Benchmark::PrepareRedundantFitables(
            std::shared_ptr<FitableDiscovery>(&fitableDiscovery, [](FitableDiscovery *) {}), fitableCount);
    }

    fit::registry::Fitable fitable;
    fitable.genericId = "3588cbacbe5c43cca66f4180408f2177";
    fitable.genericVersion = "1.0.0";
    fitable.fitId = "43c69400369f4234925de05e5118fdb5";
    fitable.fitVersion = "1.0.0";
    for (auto _ : state) {
        fitableDiscovery.GetLocalFitable(fitable);
    }

    if (state.thread_index == 0) {
        fitableDiscovery.ClearAllLocalFitables();
    }
}
BENCHMARK(BM_FitableDiscovery_GetLocalFitable)->Arg(1)->Arg(64)->Arg(512)->Arg(1<<10)->Arg(8<<10);
BENCHMARK(BM_FitableDiscovery_GetLocalFitable)->Arg(1)->Arg(64)->Arg(512)->Arg(1<<10)->Arg(8<<10)->Threads(100);

