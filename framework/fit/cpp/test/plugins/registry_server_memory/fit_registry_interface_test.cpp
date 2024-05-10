/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <utility>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_fitables/1.0.0/cplusplus/unregisterFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_subscribe_fitables/1.0.0/cplusplus/subscribeFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unsubscribe_fitables/1.0.0/cplusplus/unsubscribeFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_synchronize_fit_service/1.0.0/cplusplus/synchronizeFitService.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_running_fitables/1.0.0/cplusplus/query_running_fitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_sync_subscription_fit_service/1.0.0/cplusplus/syncSubscriptionFitService.hpp>
#include <genericable/com_huawei_fit_heartbeat_heartbeat_address_change/1.0.0/cplusplus/heartbeatAddressChange.hpp>
#include <genericable/com_huawei_fit_registry_get_registry_addresses/1.0.0/cplusplus/getRegistryAddresses.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_application_instances/1.0.0/cplusplus/register_application_instances.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_application_instances/1.0.0/cplusplus/query_application_instances.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_application_instances/1.0.0/cplusplus/unregister_application_instances.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_register_fitable_metas/1.0.0/cplusplus/register_fitable_metas.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_unregister_fitable_metas/1.0.0/cplusplus/unregister_fitable_metas.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_query_fitable_metas/1.0.0/cplusplus/query_fitable_metas.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using namespace fit::hakuna::kernel;
using namespace ::fit::hakuna::kernel::registry::shared;
class FitRegistryInterfaceTest : public ::testing::Test {
public:
    void SetUp() override
    {
        fitable_.genericableId = "test_interface_gid";
        fitable_.genericableVersion = "1.0.0";
        fitable_.fitableId = "test_interface_fid";
        fitable_.fitableVersion = "1.0.0";

        fitableSync_.genericableId = "sync_gid1";
        fitableSync_.genericableVersion = "1.0.0";
        fitableSync_.fitableId = "sync_fid1";
        fitableSync_.fitableVersion = "1.0.0";

        oldFitable_.genericId = "test_old_interface_gid";
        oldFitable_.genericVersion = "1.0.0";
        oldFitable_.fitId = "test_old_interface_fid";
        oldFitable_.fitVersion = "1.0.0";

        localAddress_ = FitSystemPropertyUtils::Address();

        ::fit::hakuna::kernel::registry::shared::Address addressNew;
        addressNew.host = localAddress_.host;
        ::fit::hakuna::kernel::registry::shared::Endpoint endPoint;
        endPoint.port = localAddress_.port;
        endPoint.protocol = localAddress_.protocol;
        addressNew.endpoints.emplace_back(endPoint);
        worker_.addresses.emplace_back(addressNew);
        worker_.environment = "debug";
        worker_.expire = 90;
        worker_.id = "not_local_worker_id";

        application_.name = "test_interface_name";
        application_.nameVersion = "test_interface_version";

        formats_.push_back(0);
        formats_.push_back(1);
        aliases_ = {"alias1", "alias2"};
        aliasesSync_ = {"alias3", "alias4"};
        tags_ = {"tags1", "tags2"};
        tagsSync_ = {"tags3", "tags4"};
        extensions_ = {{"key1", "value1"}, {"key2", "value2"}};
        extensionsSync_ = {{"key3", "value3"}, {"key4", "value4"}};

        fitableMeta_.fitable = &fitable_;
        fitableMeta_.formats = formats_;
        fitableMeta_.aliases = aliases_;
        fitableMeta_.tags = tags_;
        fitableMeta_.extensions = extensions_;
        fitableMeta_.environment = "test_env";
        fitableMeta_.application = &application_;
        fitableMetaSync_.fitable = &fitableSync_;
        fitableMetaSync_.formats = formats_;
        fitableMetaSync_.aliases = aliasesSync_;
        fitableMetaSync_.tags = tagsSync_;
        fitableMetaSync_.extensions = extensionsSync_;
        fitableMetaSync_.environment = "test_env";

        applicationInstance_.application = &application_;
        applicationInstance_.formats = formats_;
        applicationInstance_.workers.emplace_back(worker_);
        fitableInstance_.fitable = &fitable_;
        fitableInstance_.applicationInstances.emplace_back(applicationInstance_);
        serviceAddress_.fitableInstance = &fitableInstance_;
        serviceAddress_.operateType = 1;
    }

    void TearDown() override
    {
    }

    void CheckFitable(const shared::Fitable& l, ::fit::hakuna::kernel::shared::Fitable r)
    {
        EXPECT_EQ(l.genericableId, r.genericableId);
        EXPECT_EQ(l.genericableVersion, r.genericableVersion);
        EXPECT_EQ(l.fitableId, r.fitableId);
        EXPECT_EQ(l.fitableVersion, r.fitableVersion);
    }
    void CheckFitableMeta(FitableMeta& fitableMeta)
    {
        CheckFitable(*fitableMeta.fitable, *fitableMeta_.fitable);
        EXPECT_EQ(fitableMeta.formats.size(), fitableMeta_.formats.size());
        EXPECT_EQ(fitableMeta.formats[0], fitableMeta_.formats[0]);
        EXPECT_EQ(fitableMeta.formats[1], fitableMeta_.formats[1]);
        EXPECT_EQ(fitableMeta.aliases[0], fitableMeta_.aliases[0]);
        EXPECT_EQ(fitableMeta.aliases[1], fitableMeta_.aliases[1]);
        EXPECT_EQ(fitableMeta.tags[0], fitableMeta_.tags[0]);
        EXPECT_EQ(fitableMeta.tags[1], fitableMeta_.tags[1]);
        EXPECT_EQ(fitableMeta.extensions["key1"], fitableMeta_.extensions["key1"]);
        EXPECT_EQ(fitableMeta.extensions["key2"], fitableMeta_.extensions["key2"]);
        EXPECT_EQ(fitableMeta.application->name, fitableMeta_.application->name);
        EXPECT_EQ(fitableMeta.application->nameVersion, fitableMeta_.application->nameVersion);
    }
    void CheckApplicationInstance(
        const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& applicationInstance)
    {
        EXPECT_EQ(applicationInstance.formats.size(), formats_.size());
        CheckApplicationInstanceV3(applicationInstance);
    }
    void CheckApplicationInstanceV3(
        const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& applicationInstance)
    {
        EXPECT_EQ(applicationInstance.application->name, application_.name);
        EXPECT_EQ(applicationInstance.application->nameVersion, application_.nameVersion);
        EXPECT_EQ(applicationInstance.workers.front().id, worker_.id);
        EXPECT_EQ(applicationInstance.workers.front().environment, worker_.environment);
        EXPECT_EQ(applicationInstance.workers.front().addresses.front().host, worker_.addresses.front().host);
        EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().port,
            worker_.addresses.front().endpoints.front().port);
        EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().protocol,
            worker_.addresses.front().endpoints.front().protocol);
    }
    void CheckFitableInstance(const ::fit::hakuna::kernel::registry::shared::FitableInstance& fitableInstance)
    {
        CheckFitable(*fitableInstance.fitable, fitable_);
        CheckApplicationInstance(fitableInstance.applicationInstances.front());
    }
public:
    ::fit::hakuna::kernel::registry::shared::FitableMeta fitableMeta_;
    ::fit::hakuna::kernel::shared::Fitable fitable_;
    Fit::vector<int32_t> formats_;
    Fit::vector<Fit::string> aliases_;
    Fit::vector<Fit::string> tags_;
    Fit::map<Fit::string, Fit::string> extensions_;
    ::fit::hakuna::kernel::registry::shared::FitableMeta fitableMetaSync_;
    ::fit::hakuna::kernel::shared::Fitable fitableSync_;
    Fit::vector<Fit::string> aliasesSync_;
    Fit::vector<Fit::string> tagsSync_;
    Fit::map<Fit::string, Fit::string> extensionsSync_;
    fit::registry::Address localAddress_;
    ::fit::hakuna::kernel::registry::shared::Worker worker_;
    ::fit::hakuna::kernel::registry::shared::Application application_;

    fit::registry::Fitable oldFitable_;
    Fit::string callbackId_ = "test_callback_id";

    ::fit::hakuna::kernel::registry::server::SyncSeviceAddress serviceAddress_;
    ::fit::hakuna::kernel::registry::shared::FitableInstance fitableInstance_;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance_;
    int32_t expire_ { 90 };
};

TEST_F(FitRegistryInterfaceTest, should_return_error_when_register_fitable_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::registerFitables registerFitablesObj;
    // when
    auto ret = registerFitablesObj(nullptr, nullptr, nullptr);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_register_fitable_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::registerFitables registerFitablesObj;
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitableMetas {};

    ::fit::hakuna::kernel::registry::shared::FitableMeta fitableMeta;
    fitableMeta.fitable = &fitable_;
    fitableMeta.formats = formats_;
    fitableMetas.push_back(fitableMeta);
    // when
    auto ret = registerFitablesObj(&fitableMetas, &worker_, &application_);
    // then
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_unregister_fitable_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::unregisterFitables unRegisterFitablesObj;
    // when
    auto ret = unRegisterFitablesObj(nullptr, nullptr);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_unregister_fitable_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::unregisterFitables unRegisterFitablesObj;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable_);
    // when
    auto ret = unRegisterFitablesObj(&fitables, &worker_.id);
    // then
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_subscribe_fitable_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::subscribeFitables subscribeFitablesObj;

    // when
    auto ret = subscribeFitablesObj(nullptr, nullptr, nullptr, nullptr);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_fitable_when_subscribe_fitable_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::registerFitables registerFitablesObj;
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitableMetas {};
    ::fit::hakuna::kernel::registry::shared::FitableMeta fitableMeta;
    fitableMeta.fitable = &fitableSync_;
    fitableMeta.formats = formats_;
    fitableMetas.push_back(fitableMeta);
    fitableMeta.fitable = &fitable_;
    fitableMetas.push_back(fitableMeta);

    fit::hakuna::kernel::registry::server::subscribeFitables subscribeFitablesObj;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableInstance> *result;

    // when
    auto registerRet = registerFitablesObj(&fitableMetas, &worker_, &application_);
    auto subscribeRet = subscribeFitablesObj(&fitables, &worker_.id, &callbackId_, &result);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(subscribeRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    ASSERT_EQ((*result).size(), 1);
    CheckFitableInstance((*result).front());
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_unsubscribe_fitable_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::unsubscribeFitables unsubscribeFitablesObj;

    // when
    auto ret = unsubscribeFitablesObj(nullptr, nullptr, nullptr);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_unsubscribe_fitable_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::unsubscribeFitables unsubscribeFitablesObj;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable_);

    // when
    auto ret = unsubscribeFitablesObj(&fitables, &worker_.id, &callbackId_);
    // then
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}


TEST_F(FitRegistryInterfaceTest, should_return_error_when_query_fitable_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::queryFitablesAddresses queryFitablesAddressesObj;

    // when
    auto ret = queryFitablesAddressesObj(nullptr, nullptr, nullptr);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_fitable_instances_when_query_fitable_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::registerFitables registerFitablesObj;
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitableMetas {};
    ::fit::hakuna::kernel::registry::shared::FitableMeta fitableMeta;
    fitableMeta.fitable = &fitableSync_;
    fitableMeta.formats = formats_;
    fitableMetas.push_back(fitableMeta);
    fitableMeta.fitable = &fitable_;
    fitableMetas.push_back(fitableMeta);

    fit::hakuna::kernel::registry::server::queryFitablesAddresses queryFitablesAddressesObj;
    Fit::vector<::fit::hakuna::kernel::shared::Fitable> fitables;
    fitables.push_back(fitable_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableInstance> *result;

    // when
    auto registerRet = registerFitablesObj(&fitableMetas, &worker_, &application_);
    auto queryFitablesRet = queryFitablesAddressesObj(&fitables, &worker_.id, &result);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(queryFitablesRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    ASSERT_EQ((*result).size(), 1);
    CheckFitableInstance((*result).front());
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_sync_service_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::synchronizeFitService synchronizeFitServiceObj;
    // when
    auto synchronizeFitServiceRet = synchronizeFitServiceObj(nullptr, nullptr);
    // then
    EXPECT_EQ(synchronizeFitServiceRet, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_sync_save_service_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::synchronizeFitService synchronizeFitServiceObj;
    Fit::vector<::fit::hakuna::kernel::registry::server::SyncSeviceAddress> syncSeviceAddress;
    syncSeviceAddress.emplace_back(serviceAddress_);
    int32_t *result = nullptr;
    // when
    auto synchronizeFitServiceRet = synchronizeFitServiceObj(&syncSeviceAddress, &result);

    // then
    EXPECT_EQ(synchronizeFitServiceRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    EXPECT_EQ(*result, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_sync_remove_service_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::synchronizeFitService synchronizeFitServiceObj;
    Fit::vector<::fit::hakuna::kernel::registry::server::SyncSeviceAddress> syncSeviceAddress;
    ::fit::hakuna::kernel::registry::server::SyncSeviceAddress serviceAddress;
    syncSeviceAddress.emplace_back(serviceAddress_);
    int32_t *result = nullptr;
    // when
    auto synchronizeFitServiceRet = synchronizeFitServiceObj(&syncSeviceAddress, &result);

    // then
    EXPECT_EQ(synchronizeFitServiceRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    EXPECT_EQ(*result, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_sync_subscription_given_param_null)
{
    // given
    fit::hakuna::kernel::registry::server::syncSubscriptionFitService syncSubscriptionFitServiceObj;
    // when
    auto syncSubscriptionFitServiceRet = syncSubscriptionFitServiceObj(nullptr, nullptr);
    // then
    EXPECT_EQ(syncSubscriptionFitServiceRet, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_sync_subscription_save_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::syncSubscriptionFitService syncSubscriptionFitServiceObj;
    Fit::vector<::fit::hakuna::kernel::registry::server::SyncSubscriptionService> syncSubscriptionServices;
    ::fit::hakuna::kernel::registry::server::SyncSubscriptionService syncSubscriptionService;
    syncSubscriptionService.callbackFitId = "test_interface_callback_fid";
    syncSubscriptionService.fitable = &oldFitable_;
    syncSubscriptionService.listenerAddress = &localAddress_;
    syncSubscriptionService.operateType = 0;
    syncSubscriptionServices.emplace_back(syncSubscriptionService);
    int32_t *result = nullptr;

    // when
    auto syncSubscriptionFitServiceRet = syncSubscriptionFitServiceObj(&syncSubscriptionServices, &result);

    // then
    EXPECT_EQ(syncSubscriptionFitServiceRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    EXPECT_EQ(*result, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_sync_subscription_remove_given_param)
{
    // given
    fit::hakuna::kernel::registry::server::syncSubscriptionFitService syncSubscriptionFitServiceObj;
    Fit::vector<::fit::hakuna::kernel::registry::server::SyncSubscriptionService> syncSubscriptionServices;
    ::fit::hakuna::kernel::registry::server::SyncSubscriptionService syncSubscriptionService;
    syncSubscriptionService.callbackFitId = "test_interface_callback_fid";
    syncSubscriptionService.fitable = &oldFitable_;
    syncSubscriptionService.listenerAddress = &localAddress_;
    syncSubscriptionService.operateType = 1;
    syncSubscriptionServices.emplace_back(syncSubscriptionService);
    int32_t *result = nullptr;

    // when
    auto syncSubscriptionFitServiceRet = syncSubscriptionFitServiceObj(&syncSubscriptionServices, &result);

    // then
    EXPECT_EQ(syncSubscriptionFitServiceRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    EXPECT_EQ(*result, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_heart_beat_change_given_param_null)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(nullptr, nullptr);
    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_heart_beat_change_given_empty_event_list)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);
    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_heart_beat_change_given_online_registry_server)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "RUN_STATE_ONLINE";
    heartbeatEvent.sceneType = "fit_registry_server";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_heart_beat_change_given_online_registry)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "RUN_STATE_ONLINE";
    heartbeatEvent.sceneType = "fit_registry";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_heart_beat_change_given_offline_registry_server)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "RUN_STATE_OFFLINE";
    heartbeatEvent.sceneType = "fit_registry_server";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_heart_beat_change_given_offline_registry)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "RUN_STATE_OFFLINE";
    heartbeatEvent.sceneType = "fit_registry";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_heart_beat_change_given_master_registry_server)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "ROLE_MASTER";
    heartbeatEvent.sceneType = "fit_registry_server";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_heart_beat_change_given_slave_registry_server)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "ROLE_SLAVE";
    heartbeatEvent.sceneType = "fit_registry_server";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest,
    should_return_success_when_heart_beat_change_given_slave_registry_server_and_invalid_address)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "ROLE_SLAVE";
    heartbeatEvent.sceneType = "fit_registry_server";
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest,
    should_return_success_when_heart_beat_change_given_slave_registry_server_and_invalid_scene_type)
{
    // given
    fit::heartbeat::heartbeatAddressChange heartbeatAddressChangeObj;
    Fit::vector<fit::heartbeat::HeartbeatEvent> eventList;
    fit::heartbeat::HeartbeatEvent heartbeatEvent;
    heartbeatEvent.eventType = "ROLE_SLAVE";
    heartbeatEvent.address = &localAddress_;
    eventList.emplace_back(heartbeatEvent);

    bool *result = nullptr;
    // when
    auto heartbeatAddressChangeRet = heartbeatAddressChangeObj(&eventList, &result);

    // then
    EXPECT_EQ(heartbeatAddressChangeRet, FIT_OK);
}

TEST_F(FitRegistryInterfaceTest, should_return_running_fitables_when_QueryRunningFitables_given_exist_generic_id)
{
    // given
    ::Fit::vector<registry::shared::FitableMeta>
        fitableMetas {fitableMetaSync_, fitableMeta_};

    auto worker1 = worker_;
    auto worker2 = worker_;
    worker1.id = "worker1";
    worker1.environment = "worker1Env";
    worker2.id = "worker2";
    worker2.environment = "worker2Env";
    Fit::vector<Fit::string> expectEnvironments {worker1.environment, worker2.environment};

    registry::server::QueryRunningFitablesParam param;
    param.SetGenericableId(fitable_.genericableId);
    Fit::vector<registry::server::QueryRunningFitablesParam> queryRunningFitablesParams;
    queryRunningFitablesParams.push_back(param);
    param.SetGenericableId(fitableSync_.genericableId);
    queryRunningFitablesParams.push_back(param);

    // when
    registry::server::registerFitables registerFitables;
    auto registerRet = registerFitables(&fitableMetas, &worker1, &application_);
    registerRet |= registerFitables(&fitableMetas, &worker2, &application_);

    Fit::vector<registry::server::RunningFitable> *result {};
    registry::server::queryRunningFitables queryRunningFitables;
    auto queryFitablesRet = queryRunningFitables(&queryRunningFitablesParams, &result);

    // then
    ASSERT_EQ(registerRet, FIT_ERR_SUCCESS);
    ASSERT_EQ(queryFitablesRet, FIT_ERR_SUCCESS);
    ASSERT_NE(result, nullptr);
    ASSERT_EQ(result->size(), 2);
    auto runningFitableEq = [](const registry::server::RunningFitable& l,
        const registry::shared::FitableMeta& rFitableMeta,
        const Fit::vector<Fit::string>& rEnvironments) {
        EXPECT_EQ(l.meta->fitable->genericableId, rFitableMeta.fitable->genericableId);
        EXPECT_EQ(l.meta->fitable->genericableVersion, rFitableMeta.fitable->genericableVersion);
        EXPECT_EQ(l.meta->fitable->fitableId, rFitableMeta.fitable->fitableId);
        EXPECT_EQ(l.meta->fitable->fitableVersion, rFitableMeta.fitable->fitableVersion);
        EXPECT_EQ(l.meta->aliases[0], rFitableMeta.aliases[0]);
        EXPECT_EQ(l.meta->aliases[1], rFitableMeta.aliases[1]);
        EXPECT_EQ(l.meta->tags[0], rFitableMeta.tags[0]);
        EXPECT_EQ(l.meta->tags[1], rFitableMeta.tags[1]);
        for (const auto& it : l.meta->extensions) {
            Fit::string key = it.first;
            Fit::string value = it.second;
            Fit::string value2 = rFitableMeta.extensions.find(key)->second;
            EXPECT_EQ(value, value2);
        }
        EXPECT_EQ(l.environments, rEnvironments);
    };
    runningFitableEq((*result)[0], fitableMeta_, expectEnvironments);
    runningFitableEq((*result)[1], fitableMetaSync_, expectEnvironments);

    // clear
    registry::server::unregisterFitables unregisterFitables;
    Fit::vector<shared::Fitable> fitables;
    fitables.push_back(fitableSync_);
    fitables.push_back(fitable_);
    unregisterFitables(&fitables, &worker1.id);
    unregisterFitables(&fitables, &worker2.id);
}

TEST_F(FitRegistryInterfaceTest, should_return_ok_when_get_registry_address)
{
    // given
    fit::registry::getRegistryAddresses getRegistryAddressesInvoke;
    FitableInstance* fitableInstance {nullptr};

    // when
    auto ret = getRegistryAddressesInvoke(&fitableInstance);

    // then
    ASSERT_EQ(ret, FIT_OK);
    ASSERT_NE(fitableInstance, nullptr);
    ASSERT_EQ(fitableInstance->applicationInstances.size(), 1);
    ASSERT_EQ(fitableInstance->applicationInstances.front().workers.size(), 1);
    auto& appInstance = fitableInstance->applicationInstances.front();
    auto& worker = appInstance.workers.front();
    EXPECT_EQ(worker.environment.empty(), false);
    EXPECT_EQ(worker.id.empty(), false);
    ASSERT_EQ(worker.extensions["key1"], "value1");
    auto& address = fitableInstance->applicationInstances.front().workers.front().addresses.front();
    EXPECT_EQ(address.host.empty(), false);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_register_application_instance_given_null)
{
    // given
    registry::server::registerApplicationInstances registerApplicationInstancesInvoke;
    Fit::vector<ApplicationInstance> *applicationInstances {nullptr};
    // when
    auto ret = registerApplicationInstancesInvoke(nullptr);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest,
    should_return_success_when_register_application_instance_given_application_instances)
{
    // given
    registry::server::registerApplicationInstances registerApplicationInstancesInvoke;
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_};
    // when
    auto ret = registerApplicationInstancesInvoke(&applicationInstances);
    // then
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest,
    should_return_application_instance_when_register_and_query_given_application_instances)
{
    // given
    registry::server::registerApplicationInstances registerApplicationInstancesInvoke;
    registry::server::queryApplicationInstances queryApplicationInstancesInvoke;
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_};

    Fit::vector<Application> applications {application_};
    Fit::vector<ApplicationInstance> *result {nullptr};
    // when
    auto registerRet = registerApplicationInstancesInvoke(&applicationInstances);
    auto queryRet = queryApplicationInstancesInvoke(&applications, &result);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(queryRet, FIT_ERR_SUCCESS);
    CheckApplicationInstanceV3(result->front());
}

TEST_F(FitRegistryInterfaceTest,
    should_return_application_instance_when_register_unregister_and_query_given_application_instances)
{
    // given
    registry::server::registerApplicationInstances registerApplicationInstancesInvoke;
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_};

    registry::server::queryApplicationInstances queryApplicationInstancesInvoke;
    Fit::vector<Application> applicationsQuery {application_};
    Fit::vector<ApplicationInstance> *result {nullptr};

    registry::server::unregisterApplicationInstances unregisterApplicationInstancesInvoke;
    Fit::vector<Application> applicationsUnregister {application_};
    Fit::string workerId = worker_.id;
    // when
    auto registerRet = registerApplicationInstancesInvoke(&applicationInstances);
    auto unregisterRet = unregisterApplicationInstancesInvoke(&applicationsUnregister, &workerId);
    auto queryRet = queryApplicationInstancesInvoke(&applicationsQuery, &result);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(unregisterRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(queryRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(result->empty(), true);
}

TEST_F(FitRegistryInterfaceTest, should_return_error_when_register_metas_given_fitable_metas)
{
    // given
    registry::shared::registerFitableMetas registerFitableMetasInvoke;
    Fit::vector<FitableMeta>* fitableMetas {nullptr};
    // when
    auto ret = registerFitableMetasInvoke(fitableMetas);
    // then
    EXPECT_EQ(ret, FIT_ERR_PARAM);
}

TEST_F(FitRegistryInterfaceTest, should_return_success_when_register_metas_given_fitable_metas)
{
    // given
    registry::shared::registerFitableMetas registerFitableMetasInvoke;
    Fit::vector<FitableMeta> fitableMetas {fitableMeta_};
    // when
    auto registerRet = registerFitableMetasInvoke(&fitableMetas);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
}

TEST_F(FitRegistryInterfaceTest, should_return_fitable_meta_when_register_and_query_metas_given_fitable_metas)
{
    // given
    registry::shared::registerFitableMetas registerFitableMetasInvoke;
    Fit::vector<FitableMeta> fitableMetas {fitableMeta_};

    registry::shared::queryFitableMetas queryFitableMetasInvoke;
    Fit::vector<Fit::string> genericableIds {fitable_.genericableId};
    Fit::string environment {fitableMeta_.environment};
    Fit::vector<FitableMeta> *result;

    // when
    auto registerRet = registerFitableMetasInvoke(&fitableMetas);
    auto queryRet = queryFitableMetasInvoke(&genericableIds, &environment, &result);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(queryRet, FIT_ERR_SUCCESS);
    CheckFitableMeta(result->front());
}

TEST_F(FitRegistryInterfaceTest,
    should_return_fitable_meta_when_register_unregister_and_query_metas_given_fitable_metas)
{
    // given
    registry::shared::registerFitableMetas registerFitableMetasInvoke;
    Fit::vector<FitableMeta> fitableMetas {fitableMeta_};

    registry::shared::queryFitableMetas queryFitableMetasInvoke;
    Fit::vector<Fit::string> genericableIds {fitable_.genericableId};
    Fit::string environment {fitableMeta_.environment};
    Fit::vector<FitableMeta> *result;

    registry::shared::unregisterFitableMetas unregisterFitableMetasInvoke;
    Fit::vector<Application> applications {application_};
    // when
    auto registerRet = registerFitableMetasInvoke(&fitableMetas);
    auto unregisterInvoke = unregisterFitableMetasInvoke(&applications, &environment);
    auto queryRet = queryFitableMetasInvoke(&genericableIds, &environment, &result);
    // then
    EXPECT_EQ(registerRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(unregisterInvoke, FIT_ERR_SUCCESS);
    EXPECT_EQ(queryRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(result->empty(), true);
}