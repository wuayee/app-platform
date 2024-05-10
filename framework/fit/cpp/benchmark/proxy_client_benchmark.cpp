/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/7/5
 * Notes:       :
 */

#include <benchmark/benchmark.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <framework/fitable_discovery_default_impl.hpp>
#include <broker/client/adapter/south/gateway/broker_fitable_discovery.h>
#include <fit/internal/broker/broker_client_inner.h>
#include <runtime/config/configuration_service_for_config_file.h>
#include <runtime/config/configuration_repo_impl.h>

#include <genericable/com_huawei_fit_sdk_system_get_local_addresses/1.0.0/cplusplus/getLocalAddresses.hpp>
#include <genericable/com_huawei_fit_tracer_add_fitable_trace/1.0.0/cplusplus/addFitableTrace.hpp>

#include <domain/trace/tracer.hpp>
#include "prepared_data/fitable_helper.hpp"
#include "prepared_data/generiacable/add.hpp"

#include "fit_config.h"

using namespace ::Fit;
using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Annotation;
using namespace ::Fit::Configuration;

namespace {
FitCode AddImpl(void *ctx, const int32_t *a, const int32_t *b, int32_t **result)
{
    *result = Fit::Context::NewObj<int32_t>(ctx);
    **result = *a + *b;

    return FIT_OK;
}

int32_t GetLocalAddresses(void *ctx, Fit::vector<fit::registry::Address> **addresses)
{
    if (ctx == nullptr) {
        FIT_LOG_ERROR("%s", "Param is nullptr.");
        return FIT_ERR_FAIL;
    }

    *addresses = Fit::Context::NewObj<Fit::vector<fit::registry::Address>>(ctx);
    if (*addresses == nullptr) {
        FIT_LOG_ERROR("%s", "New result failed.");
        return FIT_ERR_FAIL;
    }
    return FIT_OK;
}

FitCode AddFitableTrace(ContextObj ctx,
    const ::fit::tracer::FitableTrace *trace,
    bool **result)
{
    return 0;
}

void PrepareBrokerClient(FitableDiscoveryPtr &fitableDiscovery, bool withTrace)
{
    GetBrokerClient() = nullptr;
    auto repo = Fit::Framework::Formatter::CreateFormatterRepo();
    auto formatterService = CreateFormatterService(repo);
    auto paramJsonFormatterService =
        Fit::Framework::ParamJsonFormatter::CreateParamJsonFormatterService(repo);

    auto configurationRepo = std::make_shared<Fit::Configuration::ConfigurationRepoImpl>();
    auto configurationService = std::make_shared<Fit::Configuration::ConfigurationServiceImpl>(
        configurationRepo, nullptr, nullptr);
    Fit::InitBrokerInstance(fitableDiscovery, configurationService);
    Fit::BrokerClientPtr brokerClientPtr =
        Fit::CreateBrokerClient(fitableDiscovery,
            configurationService,
            formatterService,
            paramJsonFormatterService,
            "", "");
    auto genericableConfiguration = std::make_shared<GenericableConfiguration>();
    genericableConfiguration->SetGenericId(::Fit::Benchmark::Add::GENERIC_ID);
    genericableConfiguration->SetDefaultFitableId("9cc24bc763e54c4b89dc7a100a530a37");
    if (!withTrace) {
        TagList tags {"traceIgnored", "localOnly"};
        genericableConfiguration->SetTags(tags);
    }
    configurationRepo->Set(genericableConfiguration);

    auto genericableConfigurationTrace = std::make_shared<GenericableConfiguration>();
    genericableConfigurationTrace->SetGenericId(::fit::tracer::addFitableTrace::GENERIC_ID);
    genericableConfigurationTrace->SetDefaultFitableId("c06c1bc0438b48958c16af3ab5be4889");
    TagList tracerTags {"traceIgnored", "trustIgnored", "localOnly"};
    genericableConfigurationTrace->SetTags(tracerTags);
    configurationRepo->Set(genericableConfigurationTrace);

    auto genericableConfigurationGetLocal = std::make_shared<GenericableConfiguration>();
    genericableConfigurationGetLocal->SetGenericId(::fit::sdk::system::getLocalAddresses::GENERIC_ID);
    genericableConfigurationGetLocal->SetDefaultFitableId("fit_sdk_system_get_local_addresses");
    TagList addressTags {"traceIgnored", "localOnly"};
    genericableConfigurationGetLocal->SetTags(addressTags);
    configurationRepo->Set(genericableConfigurationGetLocal);

    Fit::Tracer::GetInstance()->SetGlobalTraceEnabled(true);
    Fit::Tracer::GetInstance()->SetLocalTraceEnabled(true);
}

void PrepareEnv(benchmark::State &state, bool withTrace)
{
    auto fitableDiscovery = CreateFitableDiscovery();
    fitableDiscovery->ClearAllLocalFitables();
    PrepareBrokerClient(fitableDiscovery, withTrace);

    auto fitableCount = state.range(0);
    ::Fit::Benchmark::PrepareRedundantFitables(fitableDiscovery, fitableCount);

    Fit::string targetGenericId = ::Fit::Benchmark::Add::GENERIC_ID;
    Fit::string targetFitableId = "9cc24bc763e54c4b89dc7a100a530a37";

    fitableDiscovery->RegisterLocalFitable(FitableDetailPtrList {
        ::Fit::Benchmark::BuildFitableDetail(
            FitableFunctionWrapper<int32_t, void *, const int32_t *, const int32_t *, int32_t **>(AddImpl).GetProxy(),
            targetGenericId.c_str(), targetFitableId.c_str())});

    fitableDiscovery->RegisterLocalFitable(FitableDetailPtrList {
        ::Fit::Benchmark::BuildFitableDetail(
            FitableFunctionWrapper<int32_t, void *, Fit::vector<fit::registry::Address> **>(
                GetLocalAddresses).GetProxy(),
            "50b44bc61b674a73882e88fafda7c2f7", "fit_sdk_system_get_local_addresses")});

    fitableDiscovery->RegisterLocalFitable(FitableDetailPtrList {
        ::Fit::Benchmark::BuildFitableDetail(
            FitableFunctionWrapper<int32_t, void *, const ::fit::tracer::FitableTrace *, bool **>(
                AddFitableTrace).GetProxy(),
            "395fcc31872c42c7964ffa7b902dbad9", "c06c1bc0438b48958c16af3ab5be4889")});
}
}

static void BM_ProxyClient_LocalInvoke(benchmark::State &state)
{
    if (state.thread_index == 0) {
        FitLogSetOutput(FitLogOutputType::file);
        PrepareEnv(state, false);
    }
    ::Fit::Benchmark::Add proxy;
    int32_t a {1};
    int32_t b {2};
    int32_t *result {};
    for (auto _ : state) {
        proxy(&a, &b, &result);
    }
}

BENCHMARK(BM_ProxyClient_LocalInvoke)->Arg(1)->Arg(64)->Arg(512)->Arg(1 << 10)->Arg(8 << 10);
BENCHMARK(BM_ProxyClient_LocalInvoke)->Arg(1)->Arg(64)->Arg(512)->Arg(1 << 10)->Arg(8 << 10)->Threads(100);

static void BM_ProxyClient_LocalInvokeWithTrace(benchmark::State &state)
{
    if (state.thread_index == 0) {
        FitLogSetOutput(FitLogOutputType::file);
        PrepareEnv(state, true);
    }
    ::Fit::Benchmark::Add proxy;
    int32_t a {1};
    int32_t b {2};
    int32_t *result {};
    for (auto _ : state) {
        proxy(&a, &b, &result);
    }
}

BENCHMARK(BM_ProxyClient_LocalInvokeWithTrace)->Arg(1)->Arg(64)->Arg(512)->Arg(1 << 10)->Arg(8 << 10);
BENCHMARK(BM_ProxyClient_LocalInvokeWithTrace)->Arg(1)->Arg(64)->Arg(512)->Arg(1 << 10)->Arg(8 << 10)->Threads(100);
