/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/8/12 11:37
 * Notes        :
 */
#include <benchmark/benchmark.h>
#include <functional>

#include <fit/external/runtime/fit_runtime.h>
#include <fit/fit_log.h>
#include <fit/internal/fit_scope_guard.h>
#include <thread>

#include "prepare_data.h"

int main(int argc, char *argv[])
{
    printf("Begin, config file = %s\n", argv[1]);
    FitLogSetOutput(FitLogOutputType::file);
    FitCode ret = FitRuntimeStart(argv[1]);
    if (ret != FIT_OK) {
        printf("FitRuntimeStart failed: errorCode - %X.\n", ret);
        return -1;
    }
    Fit::scope_guard runtimeScopeGuard([] {
        FitCode ret = FitRuntimeStop();
        if (ret != FIT_OK) {
            printf("FitRuntimeStop failed: errorCode - %X.\n", ret);
        }
    });

    std::this_thread::sleep_for(std::chrono::seconds(10));
    int preCount = std::stoi(argv[2]);
    PrepareData prepareData(preCount);

    benchmark::Initialize(&argc, argv);
    benchmark::RunSpecifiedBenchmarks();
    benchmark::Shutdown();
    printf("End\n");
}
