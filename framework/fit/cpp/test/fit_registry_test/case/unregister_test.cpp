/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 注销接口的测试，顺带也验证了注册中心间同步服务删除的功能
 * Author       : songyongtan
 * Create       : 2022-07-11
 */

#include <algorithm>
#include <chrono>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

#include <cstdint>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_fitables/1.0.0/cplusplus/unregisterFitables.hpp>
#include <thread>

#include "compare_helper.h"
#include "module_entry.h"
#include "new_interface_proxy.hpp"

using namespace ::testing;
using namespace ::RegistryTest;
using Fit::string;
using Fit::vector;

using fit::hakuna::kernel::registry::server::queryFitablesAddresses;
using fit::hakuna::kernel::registry::server::registerFitables;
using fit::hakuna::kernel::registry::server::unregisterFitables;
using fit::hakuna::kernel::shared::Fitable;
using namespace fit::hakuna::kernel::registry::shared;
using namespace NewInterfaceProxy;

class UnregisterTest : public Test {
public:
    void SetUp()
    {
        fitable.genericableId = "g";
        fitable.genericableVersion = "1.0.0";
        fitable.fitableId = "f";

        worker.id = "for_registry_test";
        worker.environment = "registry_test";
        worker.expire = 3; // 3是过期时间

        address.host = "registry.test";

        endpoint.port = 1;
        endpoint.protocol = 3; // 3是grpc通信协议

        app.name = "registry.test";
        app.nameVersion = "version";

        workerIdOfNodeA = ModuleEntry::Instance().GetConfig().GetTargetWorkerIdsOfRegistry()[0];
        workerIdOfNodeB = ModuleEntry::Instance().GetConfig().GetTargetWorkerIdsOfRegistry()[1];
        syncDelaySeconds = ModuleEntry::Instance().GetConfig().GetSyncDelaySeconds();
    }
    Fitable fitable {};
    Worker worker {};
    Address address {};
    Endpoint endpoint {};
    Application app {};
    string workerIdOfNodeA {};
    string workerIdOfNodeB {};
    uint32_t syncDelaySeconds {};
};

TEST_F(UnregisterTest,
    should_get_empty_addresses_when_query_given_register_fitable_after_unregistered_with_same_node)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    address.endpoints.emplace_back(endpoint);
    worker.addresses.emplace_back(address);

    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    UnRegisterService(&fitables, &worker.id, workerIdOfNodeA);

    // sleep, waiting for registry clear it from db.
    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    auto queryRet = queryProxy(&fitables, &worker.id, &fitableInstances);

    ASSERT_THAT(queryRet, Eq(FIT_OK));
    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_TRUE(fitableInstances->begin()->applicationInstances.empty());
}

/**
 * 注册服务到A->从B注销服务->从A查询服务为空
 */
TEST_F(UnregisterTest, should_get_empty_addresses_when_query_from_A_given_unregister_fitable_to_B)
{
    FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable;
    fitableMeta.formats = {1};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMetas.push_back(fitableMeta);
    address.endpoints.emplace_back(endpoint);
    worker.addresses.emplace_back(address);

    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    // sleep, waiting for registry A synchronize it to B.
    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    UnRegisterService(&fitables, &worker.id, workerIdOfNodeB);

    // sleep, waiting for registry B clear it from db and registry synchronize to A.
    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));

    vector<FitableInstance>* fitableInstances{};
    queryFitablesAddresses queryProxy;
    QueryService(queryProxy, &fitables, &worker.id, fitableInstances, workerIdOfNodeA);

    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_TRUE(fitableInstances->begin()->applicationInstances.empty());
}