/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       :
 * Create       : 2022-09-02
 */
#ifndef NEW_INTERFACE_PROXY_HPP
#define NEW_INTERFACE_PROXY_HPP

#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_fitables/1.0.0/cplusplus/unregisterFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_subscribe_fitables/1.0.0/cplusplus/subscribeFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unsubscribe_fitables/1.0.0/cplusplus/unsubscribeFitables.hpp>

#include <gmock/gmock.h>
#include <gtest/gtest.h>

using namespace ::testing;

using fit::hakuna::kernel::registry::server::queryFitablesAddresses;
using fit::hakuna::kernel::registry::server::registerFitables;
using fit::hakuna::kernel::registry::server::unregisterFitables;
using fit::hakuna::kernel::registry::server::subscribeFitables;
using fit::hakuna::kernel::registry::server::unsubscribeFitables;
using fit::hakuna::kernel::shared::Fitable;
using namespace fit::hakuna::kernel::registry::shared;
using Fit::string;
using Fit::vector;

namespace NewInterfaceProxy {
static void RegisterService(Fit::vector<FitableMeta>* fitableMetas,
                            Worker* worker, Application* app, const Fit::string& dstRegistryWorkerId)
{
    registerFitables proxy;
    auto ret = FIT_ERR_FAIL;
    proxy.Route([](const Fit::RouteFilterParam& param) { return true; })
        .Get([=](const Fit::LBFilterParam& param) { return param.workerId == dstRegistryWorkerId; })
        .Exec(fitableMetas, worker, app, [&ret](const Fit::CallBackInfo* cb) -> FitCode {
            ret = cb->code;
            return cb->code;
        });
    ASSERT_THAT(ret, Eq(FIT_OK));
}

void static UnRegisterService(Fit::vector<::fit::hakuna::kernel::shared::Fitable>* fitables, Fit::string* workerId,
    const Fit::string& dstRegistryWorkerId)
{
    unregisterFitables proxy;
    auto ret = FIT_ERR_FAIL;
    proxy.Route([](const Fit::RouteFilterParam& param) { return true; })
        .Get([dstRegistryWorkerId](const Fit::LBFilterParam& param) { return param.workerId == dstRegistryWorkerId; })
        .Exec(fitables, workerId, [&ret](const Fit::CallBackInfo* cb) -> FitCode {
            ret = cb->code;
            return cb->code;
        });

    ASSERT_THAT(ret, Eq(FIT_OK));
}

void static QueryService(queryFitablesAddresses& queryProxy,
    Fit::vector<::fit::hakuna::kernel::shared::Fitable>* fitables, Fit::string* workerId,
    vector<FitableInstance>*& fitableInstances, const Fit::string& dstRegistryWorkerId)
{
    auto queryRet = FIT_ERR_FAIL;
    queryProxy.Route([](const Fit::RouteFilterParam& param) { return true; })
        .Get([dstRegistryWorkerId](const Fit::LBFilterParam& param) { return param.workerId == dstRegistryWorkerId; })
        .Exec(fitables, workerId,
        [&queryRet, &fitableInstances](const Fit::CallBackInfo* cb, vector<FitableInstance>** result) -> FitCode {
            queryRet = cb->code;
            fitableInstances = *result;
            return cb->code;
        });

    ASSERT_THAT(queryRet, Eq(FIT_OK));
}

void static SubscribeFitables(subscribeFitables& proxy,
    Fit::vector<::fit::hakuna::kernel::shared::Fitable>* fitables, Fit::string* workerId,
    Fit::string* callbackFitableId, vector<FitableInstance>*& fitableInstances,
    const Fit::string& dstRegistryWorkerId)
{
    auto subscribeRet = FIT_ERR_FAIL;
    proxy.Route([](const Fit::RouteFilterParam& param) { return true; })
        .Get([dstRegistryWorkerId](const Fit::LBFilterParam& param) { return param.workerId == dstRegistryWorkerId; })
        .Exec(fitables, workerId, callbackFitableId,
        [&subscribeRet, &fitableInstances](const Fit::CallBackInfo* cb,
                                            vector<FitableInstance>** result) -> FitCode {
            subscribeRet = cb->code;
            fitableInstances = *result;
            return cb->code;
        });

    ASSERT_THAT(subscribeRet, Eq(FIT_OK));
}

void static UnSubscribeFitables(Fit::vector<::fit::hakuna::kernel::shared::Fitable>* fitables, Fit::string* workerId,
    Fit::string* callbackFitableId, const Fit::string& dstRegistryWorkerId)
{
    auto unsubscribeRet = FIT_ERR_FAIL;
    unsubscribeFitables unsubscribeProxy;
    unsubscribeProxy.Route([](const Fit::RouteFilterParam& param) { return true; })
        .Get([dstRegistryWorkerId](const Fit::LBFilterParam& param) { return param.workerId == dstRegistryWorkerId; })
        .Exec(fitables, workerId, callbackFitableId,
        [&unsubscribeRet](const Fit::CallBackInfo* cb) -> FitCode {
            unsubscribeRet = cb->code;
            return cb->code;
        });

    ASSERT_THAT(unsubscribeRet, Eq(FIT_OK));
}
}
#endif