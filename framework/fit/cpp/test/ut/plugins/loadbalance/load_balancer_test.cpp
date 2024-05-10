/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides unit test for config center.
 * Author       : w00561424
 * Date         : 2022/03/31
 */
#include <memory>
#include <fit/stl/vector.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <loadbalance/include/load_balancer.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include "gmock/gmock.h"
using namespace Fit::LoadBalance;
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

class LoadBalanceTest : public ::testing::Test {
public:
    void SetUp() override
    {
        endPoint_.port = 8080;
        endPoint_.protocol = 3;

        address_.host = "127.0.0.1";
        address_.endpoints.emplace_back(endPoint_);

        worker_.addresses.emplace_back(address_);
        worker_.environment = "debug";
        worker_.expire = 90;
        worker_.id = "127.0.0.1:8080";

        application_.name = "test_fitable_endpoint_name";
        application_.nameVersion = "test_fitable_endpoint_version";

        formats_.push_back(0);
        fitable_.fitableId = "test_fitable_id";
        fitable_.fitableVersion = "1.0.0";
        fitable_.genericableId = TestFitable::GENERIC_ID;
        fitable_.genericableVersion = "1.0.0";
    }

    void TearDown() override
    {
    }
public:
    Fit::vector<int32_t> formats_;
    ::fit::hakuna::kernel::registry::shared::Endpoint endPoint_;
    ::fit::hakuna::kernel::registry::shared::Worker worker_;
    ::fit::hakuna::kernel::registry::shared::Address address_;
    ::fit::hakuna::kernel::registry::shared::Application application_;
    ::fit::hakuna::kernel::shared::Fitable fitable_;
};

TEST_F(LoadBalanceTest, should_return_application_when_loadbalance_given_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);

    TestFitable testFitable;
    // when
    std::unique_ptr<LoadBalancer> loadBalancer = Fit::LoadBalance::LoadBalancer::Create(
        testFitable.ctx_, fitable_, applicationInstances);
    ASSERT_EQ(loadBalancer != nullptr, true);
    FitCode ret = loadBalancer->LoadBalance();
    fit::hakuna::kernel::registry::shared::ApplicationInstance* applicationInstancePtr = loadBalancer->GetResult();

    // then
    EXPECT_EQ(ret == FIT_OK, true);
    ASSERT_EQ(applicationInstancePtr != nullptr, true);
    EXPECT_EQ(applicationInstance.application->name, applicationInstancePtr->application->name);
    EXPECT_EQ(applicationInstance.application->nameVersion, applicationInstancePtr->application->nameVersion);
    ASSERT_EQ(applicationInstance.formats.size(), applicationInstancePtr->formats.size());
    ASSERT_EQ(applicationInstancePtr->formats.size(), 1);
    ASSERT_EQ(applicationInstancePtr->formats.front(), 0);
    ASSERT_EQ(applicationInstance.workers.size(), applicationInstancePtr->workers.size());
    EXPECT_EQ(applicationInstance.workers.front().id, applicationInstancePtr->workers.front().id);
    EXPECT_EQ(applicationInstance.workers.front().expire, applicationInstancePtr->workers.front().expire);
    EXPECT_EQ(applicationInstance.workers.front().environment, applicationInstancePtr->workers.front().environment);
    ASSERT_EQ(applicationInstance.workers.front().addresses.size(),
              applicationInstancePtr->workers.front().addresses.size());
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().host,
              applicationInstancePtr->workers.front().addresses.front().host);
    ASSERT_EQ(applicationInstance.workers.front().addresses.front().endpoints.size(),
              applicationInstancePtr->workers.front().addresses.front().endpoints.size());
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().port,
              applicationInstancePtr->workers.front().addresses.front().endpoints.front().port);
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().protocol,
              applicationInstancePtr->workers.front().addresses.front().endpoints.front().protocol);
}

TEST_F(LoadBalanceTest, should_return_null_application_when_loadbalance_given_empty_application_instances)
{
    // given
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    TestFitable testFitable;
    // when
    std::unique_ptr<LoadBalancer> loadBalancer = Fit::LoadBalance::LoadBalancer::Create(
        testFitable.ctx_, fitable_, applicationInstances);
    ASSERT_EQ(loadBalancer != nullptr, true);
    FitCode ret = loadBalancer->LoadBalance();
    fit::hakuna::kernel::registry::shared::ApplicationInstance* applicationInstancePtr = loadBalancer->GetResult();

    // then
    EXPECT_EQ(ret == FIT_ERR_NOT_FOUND, true);
    ASSERT_EQ(applicationInstancePtr == nullptr, true);
}