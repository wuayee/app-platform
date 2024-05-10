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
#include <genericable/com_huawei_fit_registry_query_fit_service_address_list/1.0.0/cplusplus/queryFitServiceAddressList.hpp>

#include <fit/internal/fit_system_property_utils.h>

#include "echo/echo.hpp"
#include "echo/echo_impl.hpp"
#include "echo/echo_converter.hpp"

void Query(benchmark::State &state)
{
    fit::registry::queryFitServiceAddressList queryFitServiceAddressList;
    fit::registry::Address address = FitSystemPropertyUtils::Address();

    Fit::vector<fit::registry::ServiceAddress>* serviceAddressSets = nullptr;
    Fit::vector<fit::registry::Fitable> fitables;
    for (int i = 0; i < state.range(0); ++i) {
        fit::registry::Fitable fitable;
        fitable.genericId = std::to_string(i) + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
        fitable.genericVersion = "1.0.0";
        fitable.fitId = std::to_string(i) + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;
        fitable.fitVersion = "1.0.0";
        fitables.push_back(fitable);
    }
    for (auto _ : state) {
        auto ret = queryFitServiceAddressList(&fitables, &address, &serviceAddressSets);
        if (ret != FIT_OK) {
            printf("queryFitServiceAddressList failed: ret = %X.\n", ret);
        }
    }
}

BENCHMARK(Query)
    ->Unit(benchmark::kMillisecond)
    ->RangeMultiplier(10)->Range(1, 100)
    ->Threads(1)->Threads(10)->Threads(20);
