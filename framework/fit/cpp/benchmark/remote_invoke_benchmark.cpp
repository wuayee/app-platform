/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/15
 * Notes:       :
 */

#include <benchmark/benchmark.h>
#include <fit/external/runtime/fit_runtime.h>
#include <fit/fit_log.h>
#include <mutex>
#include <fit/internal/broker/broker_client_inner.h>
#include <fit/internal/fit_scope_guard.h>

#include "prepared_data/generiacable/add.hpp"

void BM_RemoteInvoke(benchmark::State &state)
{
    if (state.thread_index == 0) {
        static std::once_flag of;
        std::call_once(of, []() {
            FitLogSetOutput(FitLogOutputType::file);
            Fit::GetBrokerClient() = nullptr;
            FitCode launcherRet = FitRuntimeStart("worker_config_add_client.json");
            if (launcherRet != FIT_ERR_SUCCESS) {
                FIT_LOG_ERROR("Start runtime error! ret:%x.", launcherRet);
                return;
            }
            ::Fit::Benchmark::Add proxy;
            int32_t a {1};
            int32_t b {2};
            int32_t *result {};
            proxy(&a, &b, &result);
        });
    }

    int32_t a {1};
    int32_t b {2};
    int32_t *result {};
    for (auto _ : state) {
        ::Fit::Benchmark::Add()(&a, &b, &result);
    }
}

BENCHMARK(BM_RemoteInvoke);
BENCHMARK(BM_RemoteInvoke)->Threads(10);