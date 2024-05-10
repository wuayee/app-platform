/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 *
 * Description  : Test
 * Author       : 王攀博 00561424
 * Date         : 2024/03/01
 */

#include <fit/fit_log.h>
#include <registry_listener/include/registry_listener.hpp>
#include <registry_listener/include/memory/memory_application_fitable_repo.hpp>
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

class MemoryFitableUnavailableEndpointRepoTest : public ::testing::Test {
public:
    void SetUp() override
    {
        isolationExpiration_ = 5;
        spi_ = std::make_shared<MockRegistryListenerSpi>();
        repoFactory_ = std::make_shared<MemoryRepoFactory>();
        registryListener_ = std::make_shared<RegistryListener>(spi_, std::move(repoFactory_), isolationExpiration_);

        fitableInfo_.fitableId = "test_fitable_id";
        fitableInfo_.fitableVersion = "1.0.0";
        fitableInfo_.genericableId = TestFitable::GENERIC_ID;
        fitableInfo_.genericableVersion = "1.0.0";

        localAddress_ = FitSystemPropertyUtils::Address();
        workerInfo_.id = localAddress_.id;
        workerInfo_.environment = localAddress_.environment;
        workerInfo_.expire = 90;

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

    FitableInfo fitableInfo_;
    WorkerInfo workerInfo_;
    fit::registry::Address localAddress_;
    ApplicationInfo applicationInfo_;
};

TEST_F(MemoryFitableUnavailableEndpointRepoTest, should_return_registry_listener_when_get_listener_given_empty)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    FitableUnavailableEndpointRepoPtr fitableUnavailableEndpointRepoPtr = fitablePtr->GetUnavailableEndpoints();
    // when
    RegistryListenerPtr  registryListenerPtr = fitableUnavailableEndpointRepoPtr->GetRegistryListener();
    // then
    EXPECT_EQ(registryListenerPtr != nullptr, true);
}

TEST_F(MemoryFitableUnavailableEndpointRepoTest, should_return_endpoint_when_add_given_param)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    FitableUnavailableEndpointRepoPtr fitableUnavailableEndpointRepoPtr = fitablePtr->GetUnavailableEndpoints();
    uint32_t expiration = 1;
    // when
    FitableUnavailableEndpointPtr fitableUnavailableEndpointPtrBefore  = fitableUnavailableEndpointRepoPtr->Add(
        localAddress_.host, localAddress_.port, expiration);
    bool containResultBefore = fitableUnavailableEndpointRepoPtr->Contains(
        localAddress_.host, localAddress_.port);
    FitableUnavailableEndpointPtr removeFitableUnavailableEndpointPtr  = fitableUnavailableEndpointRepoPtr->Remove(
        localAddress_.host, localAddress_.port);
    bool containResultAfter = fitableUnavailableEndpointRepoPtr->Contains(
        localAddress_.host, localAddress_.port);
    // then
    ASSERT_EQ(fitableUnavailableEndpointPtrBefore != nullptr, true);
    ASSERT_EQ(fitableUnavailableEndpointPtrBefore->GetFitable() != nullptr, true);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetFitable()->GetId(), fitableInfo_.fitableId);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetFitable()->GetVersion(), fitableInfo_.fitableVersion);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetHost(), localAddress_.host);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetPort(), localAddress_.port);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetExpiration(), expiration);
    ASSERT_EQ(fitableUnavailableEndpointPtrBefore->GetRegistryListener() != nullptr, true);
    EXPECT_EQ(containResultBefore, true);
    ASSERT_EQ(removeFitableUnavailableEndpointPtr->GetFitable() != nullptr, true);
    EXPECT_EQ(removeFitableUnavailableEndpointPtr->GetFitable()->GetId(), fitableInfo_.fitableId);
    EXPECT_EQ(removeFitableUnavailableEndpointPtr->GetFitable()->GetVersion(), fitableInfo_.fitableVersion);
    EXPECT_EQ(removeFitableUnavailableEndpointPtr->GetHost(), localAddress_.host);
    EXPECT_EQ(removeFitableUnavailableEndpointPtr->GetPort(), localAddress_.port);
    EXPECT_EQ(removeFitableUnavailableEndpointPtr->GetExpiration(), expiration);
    EXPECT_EQ(removeFitableUnavailableEndpointPtr->Compare(fitableUnavailableEndpointPtrBefore), 0);
    EXPECT_EQ(containResultAfter, false);
}

TEST_F(MemoryFitableUnavailableEndpointRepoTest, should_return_endpoint_when_add_and_remove_itself_given_param)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    FitableUnavailableEndpointRepoPtr fitableUnavailableEndpointRepoPtr = fitablePtr->GetUnavailableEndpoints();
    uint32_t expiration = 1;
    // when
    FitableUnavailableEndpointPtr fitableUnavailableEndpointPtrBefore  = fitableUnavailableEndpointRepoPtr->Add(
        localAddress_.host, localAddress_.port, expiration);
    bool containResultBefore = fitableUnavailableEndpointRepoPtr->Contains(
        localAddress_.host, localAddress_.port);

    fitableUnavailableEndpointPtrBefore->SetExpiration(2);
    uint32_t expirationBefore = fitableUnavailableEndpointPtrBefore->GetExpiration();
    bool isExpire = fitableUnavailableEndpointPtrBefore->TryExpire();
    uint32_t expirationAfter = fitableUnavailableEndpointPtrBefore->GetExpiration();
    fitableUnavailableEndpointPtrBefore->Remove();

    bool containResultAfter = fitableUnavailableEndpointRepoPtr->Contains(
        localAddress_.host, localAddress_.port);
    // then
    ASSERT_EQ(fitableUnavailableEndpointPtrBefore != nullptr, true);
    ASSERT_EQ(fitableUnavailableEndpointPtrBefore->GetFitable() != nullptr, true);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetFitable()->GetId(), fitableInfo_.fitableId);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetFitable()->GetVersion(), fitableInfo_.fitableVersion);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetHost(), localAddress_.host);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetPort(), localAddress_.port);
    EXPECT_EQ(fitableUnavailableEndpointPtrBefore->GetExpiration(), expiration);
    EXPECT_EQ(containResultBefore, true);
    
    EXPECT_EQ(expirationBefore, 2);
    EXPECT_EQ(isExpire, false);
    EXPECT_EQ(expirationAfter, 1);

    EXPECT_EQ(containResultAfter, false);
}