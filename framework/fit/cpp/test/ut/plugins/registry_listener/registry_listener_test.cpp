/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 *
 * Description  : Test
 * Author       : 王攀博 00561424
 * Date         : 2024/03/01
 */

#include <fit/fit_log.h>
#include <registry_listener/include/sync/address_synchronizer.hpp>
#include <registry_listener/include/sync/active_address_synchronizer.hpp>
#include <registry_listener/include/sync/passive_address_synchronizer.hpp>
#include <registry_listener/include/sync/unavailable_endpoints_synchronizer.hpp>
#include <registry_listener/include/sync/address_synchronizer_composite.hpp>
#include <registry_listener/include/memory/memory_repo_factory.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <fit/internal/fit_system_property_utils.h>
#include <mock/mock_registry_listener_spi.hpp>
#include "gmock/gmock.h"
using namespace Fit;
using namespace Fit::Registry::Listener;
struct TestFitableStruct {
    using InType = ::Fit::Framework::ArgumentsIn<const int *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};

class TestFitable : public ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                                 TestFitableStruct::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "test_fitable_genericable_id";
    TestFitable() : ::Fit::Framework::ProxyClient<FitCode(TestFitableStruct::InType,
                                                          TestFitableStruct::OutType)>(GENERIC_ID) {}
    ~TestFitable() {}
};

class RegistryListenerTest : public ::testing::Test {
public:
    void SetUp() override
    {
        auto localAddress = FitSystemPropertyUtils::Address();

        isolationExpiration_ = 5;
        spi_ = std::make_shared<MockRegistryListenerSpi>();
        repoFactory_ = std::make_shared<MemoryRepoFactory>();
        registryListener_ = std::make_shared<RegistryListener>(spi_, std::move(repoFactory_), isolationExpiration_);
        synchronizerComposite_ = std::make_shared<Fit::Registry::Listener::AddressSynchronizerComposite>();
        synchronizerComposite_->Add(
            std::make_shared<Fit::Registry::Listener::UnavailableEndpointsSynchronizer>(registryListener_));
        synchronizerComposite_->Add(
            std::make_shared<Fit::Registry::Listener::ActiveAddressSynchronizer>(registryListener_, 5));
        synchronizerComposite_->Add(
            std::make_shared<Fit::Registry::Listener::PassiveAddressSynchronizer>(registryListener_));

        fitableInfo_.fitableId = "test_fitable_id";
        fitableInfo_.fitableVersion = "1.0.0";
        fitableInfo_.genericableId = TestFitable::GENERIC_ID;
        fitableInfo_.genericableVersion = "1.0.0";

        workerInfo_.id = localAddress.id;
        workerInfo_.environment = localAddress_.environment;
        workerInfo_.expire = 90;

        ::fit::hakuna::kernel::registry::shared::Address address;
        ::fit::hakuna::kernel::registry::shared::Endpoint endpoint;
        endpoint.port = localAddress_.port;
        endpoint.protocol = localAddress_.protocol;
        address.endpoints.emplace_back(endpoint);
        workerInfo_.addresses.emplace_back(address);

        applicationInfo_.name = "test_app_name";
        applicationInfo_.nameVersion = "test_app_version";
    }

    void TearDown() override
    {
    }
public:
    uint32_t isolationExpiration_;
    std::shared_ptr<MockRegistryListenerSpi> spi_;
    RepoFactoryPtr repoFactory_;
    RegistryListenerPtr registryListener_;
    std::shared_ptr<Fit::Registry::Listener::AddressSynchronizerComposite> synchronizerComposite_;

    FitableInfo fitableInfo_;
    WorkerInfo workerInfo_;
    fit::registry::Address localAddress_;
    ApplicationInfo applicationInfo_;
};

TEST_F(RegistryListenerTest, should_return_instance_when_get_address_given_param)
{
    // given
    TestFitable testFitable;

    Fit::vector<FitableInstance> fitableInstances;
    FitableInstance fitableInstance;
    fitableInstance.fitable = new FitableInfo(fitableInfo_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = new ApplicationInfo(applicationInfo_);
    applicationInstance.formats = {1};
    applicationInstance.workers.emplace_back(workerInfo_);
    fitableInstance.applicationInstances.emplace_back(applicationInstance);
    fitableInstances.emplace_back(fitableInstance);
    FitCode resultCode = FIT_ERR_FAIL;

    Fit::vector<FitableInstance> subscribeFitableInstances;
    FitableInstance subscribeFitableInstance;
    subscribeFitableInstance.fitable = new FitableInfo(fitableInfo_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance subscribeApplicationInstance;
    subscribeApplicationInstance.application = new ApplicationInfo(applicationInfo_);
    subscribeApplicationInstance.formats = {1};
    subscribeApplicationInstance.workers.emplace_back(workerInfo_);
    subscribeFitableInstance.applicationInstances.emplace_back(subscribeApplicationInstance);
    subscribeFitableInstances.emplace_back(subscribeFitableInstance);

    EXPECT_CALL(*spi_, QueryFitableInstances(testing::A<const vector<FitableInfo>&>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(::testing::Invoke([fitableInstances, resultCode](const vector<FitableInfo>&) {
            return FitableInstanceListGuard(fitableInstances, resultCode);
        }));
    EXPECT_CALL(*spi_, SubscribeFitables(testing::A<const vector<FitableInfo>&>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(::testing::Invoke([subscribeFitableInstances, resultCode](const vector<FitableInfo>&) {
            return FitableInstanceListGuard(subscribeFitableInstances, resultCode);
        }));
    // when
    FitableInstance* result = registryListener_->GetAddresses(testFitable.ctx_, fitableInfo_);
    // then
    EXPECT_EQ(result != nullptr, true);
}

TEST_F(RegistryListenerTest, should_return_nullptr_when_get_application_given_param)
{
    // given
    Fit::string appName = "test_app";
    Fit::string version = "test_version";
    bool createNew = false;

    // when
    ApplicationPtr appliaction = registryListener_->GetApplication(appName, version, {}, createNew);
    // then
    EXPECT_EQ(appliaction == nullptr, true);
}

TEST_F(RegistryListenerTest, should_return_application_when_get_application_given_param)
{
    // given
    Fit::string appName = "test_app";
    Fit::string version = "test_version";
    bool createNew = true;

    // when
    ApplicationPtr appliaction = registryListener_->GetApplication(appName, version, {}, createNew);
    // then
    EXPECT_EQ(appliaction != nullptr, true);
}

TEST_F(RegistryListenerTest, should_return_application_when_get_application_given_application_info)
{
    // given
    ApplicationInfo info;
    info.name = "test_app";
    info.nameVersion = "test_version";
    bool createNew = true;
    // when
    ApplicationPtr appliaction = registryListener_->GetApplication(info, createNew);
    // then
    EXPECT_EQ(appliaction != nullptr, true);
}

TEST_F(RegistryListenerTest, should_return_void_when_isolate_given_fitable_and_worker)
{
    // given
    // when
    registryListener_->Isolate(fitableInfo_, workerInfo_);
    // then
}

TEST_F(RegistryListenerTest, should_return_non_null_when_get_fitable_given_fitable_and_creatable)
{
    // given
    // when
    FitablePtr fitable = registryListener_->GetFitable(fitableInfo_, true);
    registryListener_->Isolate(fitableInfo_, workerInfo_);
    // then
    EXPECT_EQ(fitable != nullptr, true);
}

TEST_F(RegistryListenerTest, should_return_void_when_unsubscribe_fitables_given_fitables)
{
    // given
    // when
    FitablePtr fitable = registryListener_->GetFitable(fitableInfo_, true);
    registryListener_->UnsubscribeFitables({fitableInfo_});
    // then
    EXPECT_EQ(fitable != nullptr, true);
}

TEST_F(RegistryListenerTest, should_return_void_when_unsubscribe_fitables_given_not_save_fitables)
{
    // given
    Fit::vector<FitableInfo> fitableInfos;
    FitableInfo fitable;
    fitable.genericableId = "not_save_gid";
    fitable.genericableVersion = "1.0.0";
    fitable.fitableId = "not_save_fid";
    fitable.fitableVersion = "1.0.0";
    fitableInfos.push_back(fitable);

    // when
    registryListener_->UnsubscribeFitables(fitableInfos);
    // then
}

TEST_F(RegistryListenerTest, should_return_fitables_when_list_fitables_given_empty)
{
    // given
    // when
    FitablePtr fitable = registryListener_->GetFitable(fitableInfo_, true);
    vector<FitableInfo> fitables = registryListener_->ListFitables();
    // then
    EXPECT_EQ(fitable != nullptr, true);
    EXPECT_NE(fitables.empty(), true);
}