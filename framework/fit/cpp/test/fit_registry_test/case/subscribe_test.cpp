/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : 订阅接口和推送的测试，其中顺带验证了订阅信息的同步接口
 * Author       : songyongtan
 * Create       : 2022-07-12
 */

#include <algorithm>
#include <chrono>
#include <cstdint>
#include <thread>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_notify_fitables/1.0.0/cplusplus/notifyFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_subscribe_fitables/1.0.0/cplusplus/subscribeFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unsubscribe_fitables/1.0.0/cplusplus/unsubscribeFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>

#include "compare_helper.h"
#include "module_entry.h"
#include "fit_code.h"
#include "fit_log.h"
#include "new_interface_proxy.hpp"

using namespace ::testing;
using namespace ::RegistryTest;
using namespace NewInterfaceProxy;
using Fit::string;
using Fit::vector;

using fit::hakuna::kernel::registry::server::notifyFitables;
using fit::hakuna::kernel::registry::server::queryFitablesAddresses;
using fit::hakuna::kernel::registry::server::registerFitables;
using fit::hakuna::kernel::registry::server::subscribeFitables;
using fit::hakuna::kernel::registry::server::unsubscribeFitables;
using fit::hakuna::kernel::shared::Fitable;
using namespace fit::hakuna::kernel::registry::shared;

namespace {
constexpr const char* NOTIFY_FITABLES_FITABLE_ID = "testcase_callback";

class NotifyCheckInfo {
public:
    static bool getNotify;
    static Fitable notifyTargetFitable;
    static Application notifyTargetApp;
    static Worker notifyTargetWroker;
    static bool isApplicationInstancesEmpty;
};
bool NotifyCheckInfo::getNotify = false;
Fitable NotifyCheckInfo::notifyTargetFitable;
Application NotifyCheckInfo::notifyTargetApp;
Worker NotifyCheckInfo::notifyTargetWroker;
bool NotifyCheckInfo::isApplicationInstancesEmpty = true;
}

FitCode NotifyFitables(
    ContextObj ctx, const vector<::fit::hakuna::kernel::registry::shared::FitableInstance>* fitableInstances)
{
    NotifyCheckInfo::getNotify = true;
    FIT_LOG_DEBUG("Changes notification of fitable addresses received. [fitables=%lu]", fitableInstances->size());

    NotifyCheckInfo::getNotify = NotifyCheckInfo::getNotify && fitableInstances->size() == 1;
    NotifyCheckInfo::getNotify =
        NotifyCheckInfo::getNotify && Equals(*fitableInstances->begin()->fitable, NotifyCheckInfo::notifyTargetFitable);

    FIT_LOG_CORE("ApplicationInstances size is %lu, gid:fid %s:%s.",
        fitableInstances->begin()->applicationInstances.size(),
        fitableInstances->begin()->fitable->genericableId.c_str(),
        fitableInstances->begin()->fitable->fitableId.c_str());

    if (fitableInstances->begin()->applicationInstances.empty() == NotifyCheckInfo::isApplicationInstancesEmpty) {
        return FIT_OK;
    }
    NotifyCheckInfo::getNotify =
        NotifyCheckInfo::getNotify && fitableInstances->begin()->applicationInstances.size() == 1;
    NotifyCheckInfo::getNotify =
        NotifyCheckInfo::getNotify &&
        Equals(*fitableInstances->begin()->applicationInstances.begin()->application, NotifyCheckInfo::notifyTargetApp);
    NotifyCheckInfo::getNotify =
        NotifyCheckInfo::getNotify &&
        Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {NotifyCheckInfo::notifyTargetWroker});
    NotifyCheckInfo::getNotify =
        NotifyCheckInfo::getNotify && fitableInstances->begin()->applicationInstances.begin()->formats.size() == 1;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::NotifyFitables)
        .SetGenericId(fit::hakuna::kernel::registry::server::notifyFitables::GENERIC_ID)
        .SetFitableId(NOTIFY_FITABLES_FITABLE_ID);
}

class SubscribeTest : public Test {
public:
    void SetUp()
    {
        fitable.genericableId = "g";
        fitable.genericableVersion = "1.0.0";
        fitable.fitableId = "f";

        worker.id = "for_registry_test";
        worker.environment = "registry_test";
        worker.expire = 15; // 15是过期时间

        address.host = ModuleEntry::Instance().GetConfig().GetLocalAddress().host;
        endpoint.port = ModuleEntry::Instance().GetConfig().GetLocalAddress().port;
        endpoint.protocol = ModuleEntry::Instance().GetConfig().GetLocalAddress().protocol;

        app.name = "registry.test";
        app.nameVersion = "version";

        subscribeCallbackFitableId = NOTIFY_FITABLES_FITABLE_ID;
        callbackFitable.genericableId = notifyFitables::GENERIC_ID;
        callbackFitable.genericableVersion = "1.0.0";
        callbackFitable.fitableId = subscribeCallbackFitableId;

        workerIdOfNodeA = ModuleEntry::Instance().GetConfig().GetTargetWorkerIdsOfRegistry()[0];
        workerIdOfNodeB = ModuleEntry::Instance().GetConfig().GetTargetWorkerIdsOfRegistry()[1];
        syncDelaySeconds = ModuleEntry::Instance().GetConfig().GetSyncDelaySeconds();

        // 构造第二个节点的服务
        fitable2.genericableId = "g_temp";
        fitable2.genericableVersion = "1.0.0";
        fitable2.fitableId = "f_temp";

        address2.host = "host_temp";

        endpoint2.port = 66;
        endpoint2.protocol = 6;
        address2.endpoints.emplace_back(endpoint2);

        app2.name = "registry_temp";
        app2.nameVersion = "version_temp";

        worker2.id = "for_registry_test_temp";
        worker2.environment = "registry_test";
        worker2.expire = 3; // 3是过期时间
        worker2.addresses.emplace_back(address2);
    }
    Fitable fitable {};
    Fitable callbackFitable {};
    Worker worker {};
    Address address {};
    Endpoint endpoint {};
    Application app {};

    // 构造第二个节点的服务
    Fitable fitable2 {};
    Worker worker2;
    Address address2;
    Endpoint endpoint2;
    Application app2;

    string subscribeCallbackFitableId {};
    string workerIdOfNodeA {};
    string workerIdOfNodeB {};
    uint32_t syncDelaySeconds {};
};

TEST_F(SubscribeTest, should_get_the_addresses_when_subscribe_given_register_fitable_with_same_node)
{
    FitableMeta fitableMeta;
    fitableMeta.formats = {0};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMeta.fitable = &callbackFitable;
    fitableMetas.push_back(fitableMeta);
    fitableMeta.fitable = &fitable;
    fitableMetas.push_back(fitableMeta);

    address.endpoints.emplace_back(endpoint);
    worker.addresses.emplace_back(address);

    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeA);

    NotifyCheckInfo::getNotify = false;
    NotifyCheckInfo::notifyTargetFitable = fitable;
    NotifyCheckInfo::notifyTargetApp = app;
    NotifyCheckInfo::notifyTargetWroker = worker;
    NotifyCheckInfo::isApplicationInstancesEmpty = false;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    subscribeFitables subscribeProxy;
    SubscribeFitables(subscribeProxy, &fitables,
                      &worker.id, &subscribeCallbackFitableId, fitableInstances, workerIdOfNodeA);

    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));

    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    registerFitables registerProxy;
    registerProxy(&fitableMetas, &worker, &app);

    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    unsubscribeFitables unsubscribeProxy;
    ASSERT_THAT(unsubscribeProxy(&fitables, &worker.id, &subscribeCallbackFitableId), Eq(FIT_OK));
    ASSERT_THAT(NotifyCheckInfo::getNotify, Eq(true));
}

TEST_F(SubscribeTest, should_get_notify_when_subscribe_and_wait_for_timeout_given_register_fitable_with_same_node)
{
    // 注册callback函数
    Fit::vector<FitableMeta> callbackFitableMetas;
    FitableMeta callbackFitableMeta;
    callbackFitableMeta.formats = {0};
    callbackFitableMeta.fitable = &callbackFitable;
    callbackFitableMetas.push_back(callbackFitableMeta);
    Worker workerCallback = worker;
    address.endpoints.emplace_back(endpoint);
    workerCallback.addresses.emplace_back(address);
    RegisterService(&callbackFitableMetas, &workerCallback, &app, workerIdOfNodeA);

    // 注册订阅函数
    FitableMeta fitableMeta;
    fitableMeta.formats = {0};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMeta.fitable = &fitable2;
    fitableMetas.push_back(fitableMeta);
    RegisterService(&fitableMetas, &worker2, &app2, workerIdOfNodeA);

    NotifyCheckInfo::getNotify = false;
    NotifyCheckInfo::notifyTargetFitable = fitable2;
    NotifyCheckInfo::notifyTargetApp = app2;
    NotifyCheckInfo::notifyTargetWroker = worker2;
    NotifyCheckInfo::isApplicationInstancesEmpty = true;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable2);
    vector<FitableInstance>* fitableInstances{};
    subscribeFitables subscribeProxy;
    SubscribeFitables(subscribeProxy, &fitables,
                      &worker.id, &subscribeCallbackFitableId, fitableInstances, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable2), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app2));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker2}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));

    std::this_thread::sleep_for(std::chrono::seconds(worker2.expire + 2));
    ASSERT_THAT(NotifyCheckInfo::getNotify, Eq(true));

    unsubscribeFitables unsubscribeProxy;
    ASSERT_THAT(unsubscribeProxy(&fitables, &worker2.id, &subscribeCallbackFitableId), Eq(FIT_OK));
}

TEST_F(SubscribeTest, should_get_the_addresses_when_subscribe_from_A_given_registered_fitable_to_B)
{
    FitableMeta fitableMeta;
    fitableMeta.formats = {0};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMeta.fitable = &callbackFitable;
    fitableMetas.push_back(fitableMeta);
    fitableMeta.fitable = &fitable;
    fitableMetas.push_back(fitableMeta);

    address.endpoints.emplace_back(endpoint);
    worker.addresses.emplace_back(address);

    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeB);
    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));

    NotifyCheckInfo::getNotify = false;
    NotifyCheckInfo::notifyTargetFitable = fitable;
    NotifyCheckInfo::notifyTargetApp = app;
    NotifyCheckInfo::notifyTargetWroker = worker;
    NotifyCheckInfo::isApplicationInstancesEmpty = false;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable);
    vector<FitableInstance>* fitableInstances{};
    subscribeFitables subscribeProxy;
    SubscribeFitables(subscribeProxy, &fitables,
                      &worker.id, &subscribeCallbackFitableId, fitableInstances, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));

    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    RegisterService(&fitableMetas, &worker, &app, workerIdOfNodeB);
    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));
    ASSERT_THAT(NotifyCheckInfo::getNotify, Eq(true));

    unsubscribeFitables unsubscribeProxy;
    ASSERT_THAT(unsubscribeProxy(&fitables, &worker.id, &subscribeCallbackFitableId), Eq(FIT_OK));
}


TEST_F(SubscribeTest, should_get_notify_when_subscribe_in_B_and_wait_for_timeout_given_register_fitable_in_A)
{
    // 注册callback函数
    Fit::vector<FitableMeta> callbackFitableMetas;
    FitableMeta callbackFitableMeta;
    callbackFitableMeta.formats = {0};
    callbackFitableMeta.fitable = &callbackFitable;
    callbackFitableMetas.push_back(callbackFitableMeta);
    Worker workerCallback = worker;
    address.endpoints.emplace_back(endpoint);
    workerCallback.addresses.emplace_back(address);
    RegisterService(&callbackFitableMetas, &workerCallback, &app, workerIdOfNodeB);

    // 注册订阅函数
    FitableMeta fitableMeta;
    fitableMeta.formats = {0};
    Fit::vector<FitableMeta> fitableMetas;
    fitableMeta.fitable = &fitable2;
    fitableMetas.push_back(fitableMeta);
    RegisterService(&fitableMetas, &worker2, &app2, workerIdOfNodeB);
    std::this_thread::sleep_for(std::chrono::seconds(syncDelaySeconds));

    NotifyCheckInfo::getNotify = false;
    NotifyCheckInfo::notifyTargetFitable = fitable2;
    NotifyCheckInfo::notifyTargetApp = app2;
    NotifyCheckInfo::notifyTargetWroker = worker2;
    NotifyCheckInfo::isApplicationInstancesEmpty = true;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable2);
    vector<FitableInstance>* fitableInstances{};
    subscribeFitables subscribeProxy;
    SubscribeFitables(subscribeProxy, &fitables,
                      &worker.id, &subscribeCallbackFitableId, fitableInstances, workerIdOfNodeA);
    ASSERT_THAT(fitableInstances, NotNull());
    ASSERT_THAT(fitableInstances->size(), Eq(1));
    ASSERT_THAT(Equals(*fitableInstances->begin()->fitable, fitable2), true);
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.size(), Eq(1));
    ASSERT_TRUE(Equals(*fitableInstances->begin()->applicationInstances.begin()->application, app2));
    ASSERT_TRUE(Equals(fitableInstances->begin()->applicationInstances.begin()->workers, {worker2}));
    ASSERT_THAT(fitableInstances->begin()->applicationInstances.begin()->formats.size(), Eq(1));

    std::this_thread::sleep_for(std::chrono::seconds(worker2.expire + 2));
    ASSERT_THAT(NotifyCheckInfo::getNotify, Eq(true));

    unsubscribeFitables unsubscribeProxy;
    ASSERT_THAT(unsubscribeProxy(&fitables, &worker2.id, &subscribeCallbackFitableId), Eq(FIT_OK));
}