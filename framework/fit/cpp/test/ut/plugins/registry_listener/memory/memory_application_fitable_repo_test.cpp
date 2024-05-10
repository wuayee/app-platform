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

class MemoryApplicationFitableRepoTest : public ::testing::Test {
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

TEST_F(MemoryApplicationFitableRepoTest, should_return_registry_listener_when_get_listener_given_empty)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    ApplicationPtr applicationPtr =
        registryListener_->GetApplication(applicationInfo_.name, applicationInfo_.nameVersion, {}, true);
    ApplicationFitableRepoPtr fitableApplicationRepoPtr = applicationPtr->GetFitables();
    // when
    RegistryListenerPtr  registryListenerPtr = fitableApplicationRepoPtr->GetRegistryListener();
    // then
    EXPECT_EQ(registryListenerPtr != nullptr, true);
}

TEST_F(MemoryApplicationFitableRepoTest, should_return_application_fitable_when_get_given_fitable)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    ApplicationPtr applicationPtr =
        registryListener_->GetApplication(applicationInfo_.name, applicationInfo_.nameVersion, {}, true);
    ApplicationFitableRepoPtr fitableApplicationRepoPtr = applicationPtr->GetFitables();
    // when
    ApplicationFitablePtr applicationFitablePtr = fitableApplicationRepoPtr->Get(fitablePtr, true);
    // then
    EXPECT_EQ(applicationFitablePtr != nullptr, true);
}

TEST_F(MemoryApplicationFitableRepoTest, should_return_application_when_get_and_remove_application_given_fitable)
{
    // given
    FitablePtr fitablePtr = registryListener_->GetFitable(fitableInfo_, true);
    ApplicationPtr applicationPtr =
        registryListener_->GetApplication(applicationInfo_.name, applicationInfo_.nameVersion, {}, true);
    ApplicationFitableRepoPtr fitableApplicationRepoPtr = applicationPtr->GetFitables();
    // when
    ApplicationFitablePtr getApplicationFitablePtrBefore = fitableApplicationRepoPtr->Get(fitablePtr, true);
    ApplicationFitablePtr removeApplicationFitablePtr = fitableApplicationRepoPtr->Remove(fitablePtr);
    ApplicationFitablePtr getApplicationFitablePtr = fitableApplicationRepoPtr->Get(fitablePtr, false);
    // then
    ASSERT_EQ(getApplicationFitablePtrBefore != nullptr, true);
    EXPECT_EQ(getApplicationFitablePtrBefore->GetRegistryListener() != nullptr, true);
    ASSERT_EQ(getApplicationFitablePtrBefore->GetFitable() != nullptr, true);
    EXPECT_EQ(getApplicationFitablePtrBefore->GetFitable()->Compare(fitablePtr), 0);
    EXPECT_EQ(removeApplicationFitablePtr->Compare(getApplicationFitablePtrBefore), 0);
    EXPECT_EQ(removeApplicationFitablePtr != nullptr, true);
    EXPECT_EQ(getApplicationFitablePtr == nullptr, true);
}