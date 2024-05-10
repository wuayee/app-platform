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
#include <loadbalance/include/support/default_load_balance_spi.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <mock/load_balance_spi_mock.hpp>
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

class DefaultLoadBalanceSpiTest : public ::testing::Test {
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

TEST_F(DefaultLoadBalanceSpiTest, should_return_empty_when_get_param_form_spi_given_empty)
{
    // given
    std::shared_ptr<LoadBalanceSpi> loadBalanceSpi = std::make_shared<DefaultLoadBalanceSpi>();

    // when
    ::Fit::string workerId = loadBalanceSpi->GetWorkerId();
    ::Fit::string environment = loadBalanceSpi->GetEnvironment();
    Fit::vector<int32_t> protocols = loadBalanceSpi->GetProtocols();
    Fit::vector<::Fit::string> environmentChain = loadBalanceSpi->GetEnvironmentChain();

    // then
    EXPECT_EQ(workerId.empty(), true);
    EXPECT_EQ(environment.empty(), true);
    EXPECT_EQ(protocols.empty(), true);
    EXPECT_EQ(environmentChain.empty(), true);
}