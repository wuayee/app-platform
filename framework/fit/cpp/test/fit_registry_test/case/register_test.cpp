/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 测试注册接口，其中也顺带验证了服务同步接口和查询接口
 * Author       : songyongtan
 * Create       : 2022-07-08
 */

#include <algorithm>
#include <chrono>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

#include <cstdint>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <thread>

#include "compare_helper.h"
#include "fit_code.h"
#include "module_entry.h"
#include "new_interface_proxy.hpp"

using namespace ::testing;
using namespace ::RegistryTest;
using Fit::string;
using Fit::vector;

using fit::hakuna::kernel::registry::server::queryFitablesAddresses;
using fit::hakuna::kernel::registry::server::registerFitables;
using fit::hakuna::kernel::shared::Fitable;
using namespace fit::hakuna::kernel::registry::shared;
using namespace NewInterfaceProxy;


class RegisterTest : public Test {
public:
    void SetUp()
    {
        fitable.genericableId = "g_new";
        fitable.genericableVersion = "1.0.0";
        fitable.fitableId = "f_new";
        fitable2.genericableId = "g_new2";
        fitable2.genericableVersion = "1.0.0";
        fitable2.fitableId = "f_new2";

        worker.id = "new_worker_id";
        worker.environment = "new_env";
        worker.expire = 3; // 3是过期时间

        address.host = "new_host";

        endpoint.port = 1;
        endpoint.protocol = 3; // 3是grpc通信协议

        endpoints.emplace_back(endpoint);
        Endpoint endpointTemp;
        endpointTemp.port = 2;
        endpointTemp.protocol = 4;
        endpoints.emplace_back(endpointTemp);
        endpointTemp.port = 3;
        endpointTemp.protocol = 5;
        endpoints.emplace_back(endpointTemp);

        app.name = "registry.test";
        app.nameVersion = "version";
        app2.name = "registry.test2";
        app2.nameVersion = "version2";

        workerIdOfNodeA = ModuleEntry::Instance().GetConfig().GetTargetWorkerIdsOfRegistry()[0];
        workerIdOfNodeB = ModuleEntry::Instance().GetConfig().GetTargetWorkerIdsOfRegistry()[1];
        syncDelaySeconds = ModuleEntry::Instance().GetConfig().GetSyncDelaySeconds();
    }
    Fitable fitable {};
    Fitable fitable2 {};
    Worker worker {};
    Address address {};
    Endpoint endpoint {};
    Fit::vector<Endpoint> endpoints {};
    Application app {};
    Application app2 {};
    string workerIdOfNodeA {};
    string workerIdOfNodeB {};
    uint32_t syncDelaySeconds {};
};

TEST_F(RegisterTest, should_get_the_addresses_when_query_given_register_fitable_with_same_node)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    Address addressTemp = address;
    for (const auto it : endpoints) {
        addressTemp.endpoints.emplace_back(it);
    }
    worker.addresses.emplace_back(addressTemp);
    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeA);

    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));
}

TEST_F(RegisterTest,
    should_get_the_addresses_when_query_given_register_twice_given_different_address_with_same_node)
{
    Fit::vector<Worker> workers;
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    worker.addresses.clear();
    Address addressTemp = address;
    for (const auto it : endpoints) {
        addressTemp.endpoints.emplace_back(it);
    }
    worker.addresses.emplace_back(addressTemp);
    workers.emplace_back(worker);

    worker.addresses.clear();
    address.endpoints.emplace_back(endpoint);
    worker.addresses = { address };
    workers.emplace_back(worker);
    for (auto& it : workers) {
        RegisterService(&fitableMetas, &it, &app, workerIdOfNodeA);

        Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
        fitables.push_back(fitable);
        vector<FitableInstance>* fitableInstances{};
        queryFitablesAddresses queryProxy;
        QueryService(queryProxy, &fitables, &it.id, fitableInstances, workerIdOfNodeA);

        ASSERT_THAT(fitableInstances, NotNull());
        ASSERT_THAT(fitableInstances->size(), Eq(1));
        ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
        ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
        ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
        ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {it}));
        ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));
    }
}

TEST_F(RegisterTest, should_get_the_addresses_when_query_from_B_given_register_fitable_to_A)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    Address addressTemp = address;
    for (const auto it : endpoints) {
        addressTemp.endpoints.emplace_back(it);
    }
    worker.addresses.emplace_back(addressTemp);
    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeB);

    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));
}

TEST_F(RegisterTest, should_get_the_addresses_when_query_from_B_given_register_twice_given_different_address_in_A)
{
    Fit::vector<Worker> workers;
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    worker.addresses.clear();
    Address addressTemp = address;
    for (const auto it : endpoints) {
        addressTemp.endpoints.emplace_back(it);
    }
    worker.addresses.emplace_back(addressTemp);
    workers.emplace_back(worker);

    worker.addresses.clear();
    address.endpoints.emplace_back(endpoint);
    worker.addresses = { address };
    workers.emplace_back(worker);
    for (auto& it : workers) {
        RegisterService(&fitableMetas, &it, &app, workerIdOfNodeA);

        std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
        Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
        fitables.push_back(fitable);
        vector<FitableInstance>* fitableInstances{};
        queryFitablesAddresses queryProxy;
        QueryService(queryProxy, &fitables, &it.id, fitableInstances, workerIdOfNodeB);

        ASSERT_THAT(fitableInstances, NotNull());
        ASSERT_THAT(fitableInstances->size(), Eq(1));
        ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
        ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
        ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
        ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {it}));
        ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));
    }
}

TEST_F(RegisterTest, should_get_empty_addresses_when_query_given_register_fitable_after_timeout_with_same_node)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    address.endpoints.emplace_back(endpoint);
    worker.addresses.emplace_back(address);

    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    std::this_thread::sleep_for(std::chrono::seconds(worker.expire + 2)); // 2 等待超时2s后再查询验证

    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeA);

    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_TRUE(fitableInstances->begin()->applicationInstances.empty());
}

TEST_F(RegisterTest, should_get_empty_the_addresses_when_query_from_B_given_register_fitable_to_A_after_timeout)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    address.endpoints.emplace_back(endpoint);
    worker.addresses.emplace_back(address);

    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);
    std::this_thread::sleep_for(std::chrono::seconds(worker.expire + 2)); // 2 等待超时2s后再查询验证

    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeB);

    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_TRUE(fitableInstances->begin()->applicationInstances.empty());
}

TEST_F(RegisterTest,
    should_get_the_new_workerId_addresses_when_query_from_A_given_register_same_workerId_and_diff_app_fitable_to_A)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    Address addressTemp = address;
    for (const auto it : endpoints) {
        addressTemp.endpoints.emplace_back(it);
    }
    worker.addresses.emplace_back(addressTemp);
    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));

    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));

    RegisterService(&fitableMetas, &worker, &app2, workerIdOfNodeA);
    vector<FitableInstance>* fitableInstances2{};
    queryFitablesAddresses queryProxy2;
    QueryService(queryProxy2, &fitables, &worker.id, fitableInstances2, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances2, NotNull());
    ASSERT_THAT(fitableInstances2->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances2->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances2->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances2->begin()->applicationInstances.begin()->application, app2));
    ASSERT_TRUE(Equals(fitableInstances2->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances2->begin()->applicationInstances.begin()->formats.size(), Eq(1));
}


TEST_F(RegisterTest,
    should_get_the_new_workerId_addresses_when_query_from_A_given_register_same_workerId_and_diff_app_and_diff_fitable)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    Address addressTemp = address;
    for (const auto it : endpoints) {
        addressTemp.endpoints.emplace_back(it);
    }
    worker.addresses.emplace_back(addressTemp);
    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));

    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));

    FitableMeta fitableMeta2;
    fitableMeta2.fitable = &fitable2;
    fitableMeta2.formats = {1};
    Fit::vector<FitableMeta> fitableMetas2;
    fitableMetas2.push_back(fitableMeta2);
    RegisterService(&fitableMetas2, &worker, &app2, workerIdOfNodeA);
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables2;
    fitables2.push_back(fitable2);
    vector<FitableInstance>* fitableInstances2{};
    queryFitablesAddresses queryProxy2;
    QueryService(queryProxy2, &fitables2, &worker.id, fitableInstances2, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances2, NotNull());
    ASSERT_THAT(fitableInstances2->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances2->begin()->fitable, fitable2), true);
    ASSERT_THAT(fitableInstances2->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances2->begin()->applicationInstances.begin()->application, app2));
    ASSERT_TRUE(Equals(fitableInstances2->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances2->begin()->applicationInstances.begin()->formats.size(), Eq(1));
}