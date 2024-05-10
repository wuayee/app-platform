/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides unit test for config center.
 * Author       : w00561424
 * Date         : 2022/03/31
 */

#include <fit/stl/vector.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <loadbalance/include/fitable_endpoint.hpp>
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

class FitableEndpointTest : public ::testing::Test {
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

TEST_F(FitableEndpointTest, should_return_fitable_endpoints_when_flat_given_application_instance)
{
    // given
    
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstance);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 1);
    EXPECT_EQ(fitableEndpoints.front().GetEndpoint()->port, endPoint_.port);
    EXPECT_EQ(fitableEndpoints.front().GetEndpoint()->protocol, endPoint_.protocol);
    EXPECT_EQ(fitableEndpoints.front().GetAddress()->host, address_.host);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->id, worker_.id);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->expire, worker_.expire);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->environment, worker_.environment);
    EXPECT_EQ(fitableEndpoints.front().GetApplication()->name, application_.name);
    EXPECT_EQ(fitableEndpoints.front().GetApplication()->nameVersion, application_.nameVersion);
    ASSERT_EQ(fitableEndpoints.front().GetApplicationInstance()->formats.size(), 1);
    EXPECT_EQ(fitableEndpoints.front().GetApplicationInstance()->formats[0], 0);
    ASSERT_EQ(fitableEndpoints.front().GetApplicationInstance()->workers.size(), 1);
}

TEST_F(FitableEndpointTest, should_return_fitable_endpoint_empty_when_flat_given_empty_application_instance)
{
    // given
    
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstance);
    // then
    EXPECT_EQ(fitableEndpoints.empty(), true);
}

TEST_F(FitableEndpointTest, should_return_empty_fitable_endpoint_when_flat_given_empty_application_instances)
{
    // given
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    EXPECT_EQ(fitableEndpoints.empty(), true);
}


TEST_F(FitableEndpointTest, should_return_fitable_endpoints_when_flat_given_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 1);
    EXPECT_EQ(fitableEndpoints.front().GetEndpoint()->port, endPoint_.port);
    EXPECT_EQ(fitableEndpoints.front().GetEndpoint()->protocol, endPoint_.protocol);
    EXPECT_EQ(fitableEndpoints.front().GetAddress()->host, address_.host);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->id, worker_.id);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->expire, worker_.expire);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->environment, worker_.environment);
    EXPECT_EQ(fitableEndpoints.front().GetApplication()->name, application_.name);
    EXPECT_EQ(fitableEndpoints.front().GetApplication()->nameVersion, application_.nameVersion);
    ASSERT_EQ(fitableEndpoints.front().GetApplicationInstance()->formats.size(), 1);
    EXPECT_EQ(fitableEndpoints.front().GetApplicationInstance()->formats[0], 0);
    ASSERT_EQ(fitableEndpoints.front().GetApplicationInstance()->workers.size(), 1);
}

TEST_F(FitableEndpointTest, should_return_one_fitable_endpoint_when_flat_given_two_same_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 1);
    EXPECT_EQ(fitableEndpoints.front().GetEndpoint()->port, endPoint_.port);
    EXPECT_EQ(fitableEndpoints.front().GetEndpoint()->protocol, endPoint_.protocol);
    EXPECT_EQ(fitableEndpoints.front().GetAddress()->host, address_.host);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->id, worker_.id);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->expire, worker_.expire);
    EXPECT_EQ(fitableEndpoints.front().GetWorker()->environment, worker_.environment);
    EXPECT_EQ(fitableEndpoints.front().GetApplication()->name, application_.name);
    EXPECT_EQ(fitableEndpoints.front().GetApplication()->nameVersion, application_.nameVersion);
    ASSERT_EQ(fitableEndpoints.front().GetApplicationInstance()->formats.size(), 1);
    EXPECT_EQ(fitableEndpoints.front().GetApplicationInstance()->formats[0], 0);
    ASSERT_EQ(fitableEndpoints.front().GetApplicationInstance()->workers.size(), 1);
}

TEST_F(FitableEndpointTest, should_return_two_fitable_endpoint_when_flat_given_diff_id_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().id = "127.0.0.1:8888";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);
    
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 2);
}

TEST_F(FitableEndpointTest,
    should_return_two_fitable_endpoint_when_flat_given_diff_environment_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().environment = "prod";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);
    
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 2);
}

TEST_F(FitableEndpointTest, should_return_two_fitable_endpoint_when_flat_given_diff_host_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().addresses.front().host = "126.0.0.1";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);
    
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 2);
}

TEST_F(FitableEndpointTest, should_return_two_fitable_endpoint_when_flat_given_diff_port_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().addresses.front().endpoints.front().port = 8888;
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);
    
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 2);
}

TEST_F(FitableEndpointTest, should_return_two_fitable_endpoint_when_flat_given_diff_protocol_application_instances)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().addresses.front().endpoints.front().protocol = 0;
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);
    
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    // then
    ASSERT_EQ(fitableEndpoints.size(), 2);
}

TEST_F(FitableEndpointTest, should_return_application_instance_when_create_application_instance_given_context)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstance);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 1);

    ::fit::hakuna::kernel::registry::shared::ApplicationInstance* applicationInstancePtr
        = fitableEndpoints.front().CreateApplicationInstance(testFitable.ctx_);
    
    // then
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

TEST_F(FitableEndpointTest, should_return_one_application_instance_when_aggregate_given_one_endpoints)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstance);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 1);

    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);
    
    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 1);

    EXPECT_EQ(applicationInstance.application->name, applicationInstancePtrs->front().application->name);
    EXPECT_EQ(applicationInstance.application->nameVersion, applicationInstancePtrs->front().application->nameVersion);
    ASSERT_EQ(applicationInstance.formats.size(), applicationInstancePtrs->front().formats.size());
    ASSERT_EQ(applicationInstancePtrs->front().formats.size(), 1);
    ASSERT_EQ(applicationInstancePtrs->front().formats.front(), 0);
    ASSERT_EQ(applicationInstance.workers.size(), applicationInstancePtrs->front().workers.size());
    EXPECT_EQ(applicationInstance.workers.front().id, applicationInstancePtrs->front().workers.front().id);
    EXPECT_EQ(applicationInstance.workers.front().expire, applicationInstancePtrs->front().workers.front().expire);
    EXPECT_EQ(applicationInstance.workers.front().environment,
              applicationInstancePtrs->front().workers.front().environment);
    ASSERT_EQ(applicationInstance.workers.front().addresses.size(),
              applicationInstancePtrs->front().workers.front().addresses.size());
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().host,
              applicationInstancePtrs->front().workers.front().addresses.front().host);
    ASSERT_EQ(applicationInstance.workers.front().addresses.front().endpoints.size(),
              applicationInstancePtrs->front().workers.front().addresses.front().endpoints.size());
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().port,
              applicationInstancePtrs->front().workers.front().addresses.front().endpoints.front().port);
    EXPECT_EQ(applicationInstance.workers.front().addresses.front().endpoints.front().protocol,
              applicationInstancePtrs->front().workers.front().addresses.front().endpoints.front().protocol);
}

TEST_F(FitableEndpointTest,
    should_return_two_application_instance_when_aggregate_given_two_diff_application_endpoints)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    ::fit::hakuna::kernel::registry::shared::Application application2;
    application2.name = "test_app_name2";
    application2.nameVersion = "test_app_name_version2";
    applicationInstance2.application =  &application2;
    ASSERT_NE(applicationInstance2.application->name, applicationInstance.application->name);
    applicationInstance2.workers.front().id = "127.0.0.1:8888";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);

    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);
    
    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 2);
}


TEST_F(FitableEndpointTest, should_return_two_application_instance_when_aggregate_given_one_null_application)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.application =  nullptr;
    applicationInstance2.workers.front().id = "127.0.0.1:8888";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint>
        fitableEndpoints = Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);

    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);
    
    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 2);
}

TEST_F(FitableEndpointTest,
    should_return_two_application_instance_when_aggregate_given_two_diff_application_version)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    ::fit::hakuna::kernel::registry::shared::Application application2 = application_;
    application2.nameVersion = "test_app_name_version2";
    applicationInstance2.application = &application2;
    applicationInstance2.workers.front().id = "127.0.0.1:8888";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);

    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 2);
}

TEST_F(FitableEndpointTest, should_return_one_application_instance_when_aggregate_given_two_diff_id)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().id = "127.0.0.1:8888";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);

    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 1);
}

TEST_F(FitableEndpointTest, should_return_one_application_instance_when_aggregate_given_two_diff_environment)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().environment = "prod";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);

    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 1);
}

TEST_F(FitableEndpointTest, should_return_one_application_instance_when_aggregate_given_two_diff_host)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().addresses.front().host = "126.0.0.1";
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);

    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 1);
}

TEST_F(FitableEndpointTest, should_return_one_application_instance_when_aggregate_given_two_diff_port)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().addresses.front().endpoints.front().port = 8888;
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);

    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 1);
}

TEST_F(FitableEndpointTest, should_return_one_application_instance_when_aggregate_given_two_diff_protocol)
{
    // given
    TestFitable testFitable;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance2 = applicationInstance;
    applicationInstance2.workers.front().addresses.front().endpoints.front().protocol = 0;
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    applicationInstances.push_back(applicationInstance);
    applicationInstances.push_back(applicationInstance2);

    // when
    Fit::vector<Fit::LoadBalance::FitableEndpoint> fitableEndpoints =
        Fit::LoadBalance::FitableEndpoint::Flat(applicationInstances);
    ASSERT_NE(fitableEndpoints.empty(), true);
    ASSERT_EQ(fitableEndpoints.size(), 2);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>* applicationInstancePtrs
        = Fit::LoadBalance::FitableEndpoint::Aggregate(testFitable.ctx_, fitableEndpoints);

    // then
    ASSERT_EQ(applicationInstancePtrs != nullptr, true);
    EXPECT_EQ(applicationInstancePtrs->size(), 1);
}