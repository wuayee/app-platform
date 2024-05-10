/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 *
 * Description  : Test
 * Author       : 王攀博 00561424
 * Date         : 2024/03/01
 */

#include <fit/fit_log.h>
#include <registry_listener/include/registry_listener.hpp>
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

class UnavailableEndpointSynchronizerTest : public ::testing::Test {
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
        endpoint.port = 666;
        endpoint.protocol = 2;
        address.endpoints.emplace_back(endpoint);
        address.host = "127.0.0.1";
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

TEST_F(UnavailableEndpointSynchronizerTest, should_return_void_when_start_given_empty)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    ApplicationPtr applicationPtr = registryListener_->GetApplication(workerInfo_.id, "", {}, true);
    ApplicationFitablePtr applicationFitable = fitablePtr->GetApplications()->Get(applicationPtr, true);
    applicationFitable->SetFormats({1});
    WorkerPtr workerPtr =
        applicationPtr->GetWorkers()->Get(workerInfo_.id, workerInfo_.environment, workerInfo_.extensions, true);
    workerPtr->GetEndpoints()->Get(workerInfo_.addresses.front().host,
        workerInfo_.addresses.front().endpoints.front().port, workerInfo_.addresses.front().endpoints.front().protocol,
        true);
    registryListener_->Isolate(fitableInfo_, workerInfo_);
    // when
    UnavailableEndpointsSynchronizer unavailableEndpointsSynchronizer(registryListener_);
    unavailableEndpointsSynchronizer.Start();
    unavailableEndpointsSynchronizer.Stop();
    // then
    EXPECT_EQ(fitablePtr != nullptr, true);
}
