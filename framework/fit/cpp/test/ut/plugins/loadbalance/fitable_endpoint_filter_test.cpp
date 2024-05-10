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
#include <loadbalance/include/fitable_endpoint_filter.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <mock/load_balance_spi_mock.hpp>
#include <gmock/gmock.h>

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

class FitableEndpointFilterTest : public ::testing::Test {
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
};

TEST_F(FitableEndpointFilterTest, should_return_empty_application_when_filter_given_mock_diff_protocol)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    
    std::shared_ptr<LoadBalanceSpiMock> loadBalanceSpiPtr = std::make_shared<LoadBalanceSpiMock>();
    LoadBalanceSpiMock mock;
    Fit::vector<int32_t> protocols {0};
    EXPECT_CALL(*loadBalanceSpiPtr, GetProtocols())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(protocols));

    TestFitable testFitable;

    // when
    std::unique_ptr<FitableEndpointFilter> fitableEndpointFilter = Fit::LoadBalance::FitableEndpointFilter::Create(
        loadBalanceSpiPtr, testFitable.ctx_, applicationInstances);
    ASSERT_EQ(fitableEndpointFilter != nullptr, true);
    FitCode ret = fitableEndpointFilter->Filter();
    Fit::vector<fit::hakuna::kernel::registry::shared::ApplicationInstance>* result =
        fitableEndpointFilter->GetResult();

    // then
    EXPECT_EQ(ret == FIT_OK, true);
    ASSERT_EQ(result != nullptr, true);
    EXPECT_EQ(result->empty(), true);
}

TEST_F(FitableEndpointFilterTest, should_return_empty_application_when_filter_given_mock_diff_environment)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    
    std::shared_ptr<LoadBalanceSpiMock> loadBalanceSpiPtr = std::make_shared<LoadBalanceSpiMock>();
    Fit::vector<int32_t> protocols {3};
    EXPECT_CALL(*loadBalanceSpiPtr, GetProtocols())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(protocols));

    Fit::vector<Fit::string> environments {"prod"};
    EXPECT_CALL(*loadBalanceSpiPtr, GetEnvironmentChain())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(environments));

    TestFitable testFitable;

    // when
    std::unique_ptr<FitableEndpointFilter> fitableEndpointFilter = Fit::LoadBalance::FitableEndpointFilter::Create(
        loadBalanceSpiPtr, testFitable.ctx_, applicationInstances);
    ASSERT_EQ(fitableEndpointFilter != nullptr, true);
    FitCode ret = fitableEndpointFilter->Filter();
    Fit::vector<fit::hakuna::kernel::registry::shared::ApplicationInstance>* result =
        fitableEndpointFilter->GetResult();

    // then
    EXPECT_EQ(ret == FIT_OK, true);
    ASSERT_EQ(result != nullptr, true);
    EXPECT_EQ(result->empty(), true);
}


TEST_F(FitableEndpointFilterTest,
    should_return_empty_application_when_filter_given_mock_same_protocol_and_environment)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    
    std::shared_ptr<LoadBalanceSpiMock> loadBalanceSpiPtr = std::make_shared<LoadBalanceSpiMock>();
    Fit::vector<int32_t> protocols {3};
    EXPECT_CALL(*loadBalanceSpiPtr, GetProtocols())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(protocols));

    Fit::vector<Fit::string> environments {"debug"};
    EXPECT_CALL(*loadBalanceSpiPtr, GetEnvironmentChain())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(environments));

    TestFitable testFitable;

    // when
    std::unique_ptr<FitableEndpointFilter> fitableEndpointFilter = Fit::LoadBalance::FitableEndpointFilter::Create(
        loadBalanceSpiPtr, testFitable.ctx_, applicationInstances);
    ASSERT_EQ(fitableEndpointFilter != nullptr, true);
    FitCode ret = fitableEndpointFilter->Filter();
    Fit::vector<fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancesPtr =
        fitableEndpointFilter->GetResult();

    // then
    EXPECT_EQ(ret == FIT_OK, true);
    ASSERT_EQ(applicationInstancesPtr != nullptr, true);
    EXPECT_EQ(applicationInstancesPtr->size(), 1);

    EXPECT_EQ(applicationInstance.application->name, applicationInstancesPtr->front().application->name);
    EXPECT_EQ(applicationInstance.application->nameVersion, applicationInstancesPtr->front().application->nameVersion);
    ASSERT_EQ(applicationInstance.formats.size(), applicationInstancesPtr->front().formats.size());
    ASSERT_EQ(applicationInstancesPtr->front().formats.size(), 1);
    ASSERT_EQ(applicationInstancesPtr->front().formats.front(), 0);
    ASSERT_EQ(applicationInstance.workers.size(), applicationInstancesPtr->front().workers.size());
    EXPECT_EQ(applicationInstance.workers.front().id, applicationInstancesPtr->front().workers.front().id);
    EXPECT_EQ(applicationInstance.workers.front().expire, applicationInstancesPtr->front().workers.front().expire);
    EXPECT_EQ(applicationInstance.workers.front().environment,
              applicationInstancesPtr->front().workers.front().environment);
    ASSERT_EQ(applicationInstance.workers.front().addresses.size(),
              applicationInstancesPtr->front().workers.front().addresses.size());
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().host,
              applicationInstancesPtr->front().workers.front().addresses.front().host);
    ASSERT_EQ(applicationInstance.workers.front().addresses.front().endpoints.size(),
              applicationInstancesPtr->front().workers.front().addresses.front().endpoints.size());
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().port,
              applicationInstancesPtr->front().workers.front().addresses.front().endpoints.front().port);
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().protocol,
              applicationInstancesPtr->front().workers.front().addresses.front().endpoints.front().protocol);
}