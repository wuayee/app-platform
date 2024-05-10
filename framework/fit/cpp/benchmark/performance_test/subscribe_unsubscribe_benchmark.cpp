/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : x00559153
 * Date         : 2021/8/5 10:00
 * Notes        :
 */

#include <benchmark/benchmark.h>

#include <genericable/com_huawei_fit_registry_register_fit_service/1.0.0/cplusplus/registerFitService.hpp>
#include <genericable/com_huawei_fit_registry_unregister_fit_service/1.0.0/cplusplus/unregisterFitService.hpp>
#include <genericable/com_huawei_fit_registry_subscribe_fit_service/1.0.0/cplusplus/subscribeFitService.hpp>
#include <genericable/com_huawei_fit_registry_unsubscribe_fit_service/1.0.0/cplusplus/unsubscribeFitService.hpp>
#include <genericable/com_huawei_fit_registry_notify_fit_service/1.0.0/cplusplus/notifyFitService.hpp>

#include <fit/internal/fit_system_property_utils.h>

#include "echo/echo.hpp"
#include "echo/echo_impl.hpp"
#include "echo/echo_converter.hpp"

#include <fit/stl/mutex.hpp>
#include <condition_variable>

namespace {
int64_t fitableCount = 0;
Fit::mutex mtx;
std::condition_variable cv;
}

int32_t NotifyFitServiceImpl(ContextObj ctx, const Fit::vector<fit::registry::ServiceAddress> *addrList, bool **ret)
{
    if (addrList == nullptr) {
        return FIT_ERR_PARAM;
    }

    {
        std::lock_guard<Fit::mutex> lg(mtx);
        fitableCount += addrList->size();
    }
    cv.notify_one();

    *ret = Fit::Context::NewObj<bool>(ctx);
    if (*ret == nullptr) {
        return FIT_ERR_FAIL;
    }
    **ret = true;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    Fit::Framework::Annotation::Fitable(NotifyFitServiceImpl)
        .SetGenericId(fit::registry::notifyFitService::GENERIC_ID)
        .SetFitableId("ec7364a5788a4df3a18b5f9e71184573");
};

class SubscribeUnsubscribeFixture : public benchmark::Fixture {
public:
    void SetUp(const benchmark::State &state)
    {
    }

    void TearDown(const benchmark::State &state)
    {
    }
};

BENCHMARK_DEFINE_F(SubscribeUnsubscribeFixture, SubscribeUnsubscribe)(benchmark::State &state)
{
    fit::registry::subscribeFitService subscribeFitService;
    fit::registry::Address address = FitSystemPropertyUtils::Address();
    Fit::string callbackFitableId = "ec7364a5788a4df3a18b5f9e71184573";
    fit::registry::unsubscribeFitService unsubscribeFitService;
    Fit::vector<fit::registry::Fitable> unsubscribeFitables;
    for (auto _ : state) {
        state.PauseTiming();
        Fit::vector<fit::registry::Fitable> subscribeFitables;
        for (int i = 0; i < state.range(0); ++i) {
            fit::registry::Fitable fitable;
            fitable.genericId = std::to_string(i) + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
            fitable.genericVersion = "1.0.0";
            fitable.fitId = std::to_string(i) + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;
            fitable.fitVersion = "1.0.0";
            subscribeFitables.push_back(fitable);
        }
        state.ResumeTiming();
        Fit::vector<fit::registry::ServiceAddress> *serviceAddressList = nullptr;
        auto ret = subscribeFitService(&subscribeFitables, &address, &callbackFitableId, &serviceAddressList);
        if (ret != FIT_OK) {
            printf("subscribeFitService failed: ret = %X.\n", ret);
        }

        state.PauseTiming();
        for (int i = 0; i < state.range(0); ++i) {
            fit::registry::Fitable fitable;
            fitable.genericId = std::to_string(i) + Fit::Benchmark::Echo<0>::GENERIC_ID_TAIL;
            fitable.genericVersion = "1.0.0";
            fitable.fitId = std::to_string(i) + Fit::Benchmark::Echo<0>::FITABLE_ID_TAIL;
            fitable.fitVersion = "1.0.0";
            unsubscribeFitables.push_back(fitable);
        }
        state.ResumeTiming();
        bool *unsubscribeResult = nullptr;
        ret = unsubscribeFitService(&unsubscribeFitables, &address, &unsubscribeResult);
        if (ret != FIT_OK) {
            printf("unsubscribeFitService failed: ret = %X.\n", ret);
        }
    }
}

BENCHMARK_REGISTER_F(SubscribeUnsubscribeFixture, SubscribeUnsubscribe)->Arg(1);
