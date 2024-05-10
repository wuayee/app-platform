/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date: 2022/04/02
*/
#include <fit/stl/vector.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_load_balance_v2/1.0.0/cplusplus/loadBalanceV2.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_filter_v3/1.0.0/cplusplus/filterV3.hpp>
#include "gmock/gmock.h"
class LoadBalanceInterfaceTest : public ::testing::Test {
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

        fitable_.fitableId = "test_fitable";
        fitable_.fitableVersion = "1.0.0";
        fitable_.genericableId = "test_genericable_id";
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

TEST_F(LoadBalanceInterfaceTest, should_return_ok_when_filter_given_application_instance)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};

    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *filtered {nullptr};
    ::fit::hakuna::kernel::loadbalance::filterV3 filter;
    // when
    FitCode ret = filter(&fitable_, &applicationInstances, &filtered);
    // then
    EXPECT_EQ(ret == FIT_OK, true);
}

TEST_F(LoadBalanceInterfaceTest, should_return_error_when_filter_given_fitable_nullptr)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};

    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *filtered {nullptr};
    ::fit::hakuna::kernel::loadbalance::filterV3 filter;
    // when
    FitCode ret = filter(nullptr, &applicationInstances, &filtered);
    // then
    EXPECT_EQ(ret == FIT_ERR_FAIL, true);
}


TEST_F(LoadBalanceInterfaceTest, should_return_error_when_filter_given_instance_nullptr)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};

    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *filtered {nullptr};
    ::fit::hakuna::kernel::loadbalance::filterV3 filter;
    // when
    FitCode ret = filter(&fitable_, nullptr, &filtered);
    // then
    EXPECT_EQ(ret == FIT_ERR_FAIL, true);
}

TEST_F(LoadBalanceInterfaceTest, should_return_ok_when_loadbalance_given_application_instance)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};

    fit::hakuna::kernel::loadbalance::loadBalanceV2 loadbalance;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance* applicationInstanceRet {nullptr};
    // when
    FitCode ret = loadbalance(&fitable_, nullptr, &applicationInstances, &applicationInstanceRet);
    // then
    EXPECT_EQ(ret == FIT_OK, true);
}

TEST_F(LoadBalanceInterfaceTest, should_return_error_when_loadbalance_given_fitable_nullptr)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};

    fit::hakuna::kernel::loadbalance::loadBalanceV2 loadbalance;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance* applicationInstanceRet {nullptr};
    // when
    FitCode ret = loadbalance(nullptr, nullptr, &applicationInstances, &applicationInstanceRet);
    // then
    EXPECT_EQ(ret == FIT_ERR_FAIL, true);
}

TEST_F(LoadBalanceInterfaceTest, should_return_error_when_loadbalance_given_target_instance_nullptr)
{
    // given
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = &application_;
    applicationInstance.formats = formats_;
    applicationInstance.workers.emplace_back(worker_);
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> \
        applicationInstances {applicationInstance};

    fit::hakuna::kernel::loadbalance::loadBalanceV2 loadbalance;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance* applicationInstanceRet {nullptr};
    // when
    FitCode ret = loadbalance(&fitable_, nullptr, nullptr, &applicationInstanceRet);
    // then
    EXPECT_EQ(ret == FIT_ERR_FAIL, true);
}
