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

class MemoryWorkerRepoTest : public ::testing::Test {
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
        workerInfo_.extensions["key1"] = "value1";
        workerInfo_.extensions["key2"] = "value2";

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

TEST_F(MemoryWorkerRepoTest, should_return_registry_listener_when_get_listener_given_empty)
{
    // given
    ApplicationPtr applicationPtr =
        registryListener_->GetApplication(applicationInfo_.name, applicationInfo_.nameVersion, {}, true);
    WorkerRepoPtr workerRepoPtr = applicationPtr->GetWorkers();
    // when
    RegistryListenerPtr registryListenerPtr = workerRepoPtr->GetRegistryListener();
    // then
    EXPECT_EQ(registryListenerPtr != nullptr, true);
}

TEST_F(MemoryWorkerRepoTest, should_return_worker_and_null_when_create_remove_and_get_given_param)
{
    // given
    ApplicationPtr applicationPtr =
        registryListener_->GetApplication(applicationInfo_.name, applicationInfo_.nameVersion, {}, true);
    WorkerRepoPtr workerRepoPtr = applicationPtr->GetWorkers();
    // when
    WorkerPtr workerPtrBefore =
        workerRepoPtr->Get(workerInfo_.id, workerInfo_.environment, workerInfo_.extensions, true);
    workerPtrBefore->Remove();
    WorkerPtr workerPtrAfter =
        workerRepoPtr->Get(workerInfo_.id, workerInfo_.environment, workerInfo_.extensions, false);
    // then
    ASSERT_EQ(workerPtrBefore != nullptr, true);
    EXPECT_EQ(workerPtrBefore->GetId(), workerInfo_.id);
    EXPECT_EQ(workerPtrBefore->GetEnvironment(), workerInfo_.environment);
    EXPECT_EQ(workerPtrBefore->GetExtensions(), workerInfo_.extensions);
    EXPECT_EQ(workerPtrAfter == nullptr, true);
}
