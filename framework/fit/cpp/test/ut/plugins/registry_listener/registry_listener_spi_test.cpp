/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 *
 * Description  : Test
 * Author       : 王攀博 00561424
 * Date         : 2024/03/01
 */

#include <fit/fit_log.h>
#include <registry_listener/include/support/registry_listener_spi.hpp>
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

class RegistryListenerSPITest : public ::testing::Test {
public:
    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
public:
};

TEST_F(RegistryListenerSPITest, should_return_instance_when_get_address_given_param)
{
    // given
    vector<FitableInstance> fitableInstances;
    FitableInstance fitableInstance;
    fitableInstance.fitable = new ::fit::hakuna::kernel::shared::Fitable();
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = new ::fit::hakuna::kernel::registry::shared::Application();
    fitableInstance.applicationInstances.emplace_back(applicationInstance);
    fitableInstances.emplace_back(fitableInstance);
    FitCode resultCode = FIT_OK;
    FitableInstanceListGuard fitableInstanceListGuard(fitableInstances, resultCode);
    // when
    auto getResult = fitableInstanceListGuard.GetResultCode();
    Fit::vector<FitableInstance> fitableInstancesResult = fitableInstanceListGuard.Get();
    // then
    EXPECT_EQ(getResult, FIT_OK);
    EXPECT_EQ(fitableInstancesResult.empty(), false);
}

TEST_F(RegistryListenerSPITest, should_get_moved_instance_when_get_given_rvalue)
{
    // given
    vector<FitableInstance> fitableInstances;
    FitableInstance fitableInstance;
    fitableInstance.fitable = new ::fit::hakuna::kernel::shared::Fitable();
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = new ::fit::hakuna::kernel::registry::shared::Application();
    fitableInstance.applicationInstances.emplace_back(applicationInstance);
    fitableInstances.emplace_back(fitableInstance);
    FitCode resultCode = FIT_OK;
    FitableInstanceListGuard temp(fitableInstances, resultCode);
    // when
    FitableInstanceListGuard fitableInstanceListGuard = std::move(temp);
    auto getResult = fitableInstanceListGuard.GetResultCode();
    Fit::vector<FitableInstance> fitableInstancesResult = fitableInstanceListGuard.Get();
    // then
    EXPECT_EQ(getResult, FIT_OK);
    EXPECT_EQ(fitableInstancesResult.empty(), false);
}

TEST_F(RegistryListenerSPITest, should_get_moved_instance_when_get_given_rvalue_self)
{
    // given
    vector<FitableInstance> fitableInstances;
    FitableInstance fitableInstance;
    fitableInstance.fitable = new ::fit::hakuna::kernel::shared::Fitable();
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.application = new ::fit::hakuna::kernel::registry::shared::Application();
    fitableInstance.applicationInstances.emplace_back(applicationInstance);
    fitableInstances.emplace_back(fitableInstance);
    FitCode resultCode = FIT_OK;
    FitableInstanceListGuard fitableInstanceListGuard(fitableInstances, resultCode);
    // when
    fitableInstanceListGuard = std::move(fitableInstanceListGuard);
    auto getResult = fitableInstanceListGuard.GetResultCode();
    Fit::vector<FitableInstance> fitableInstancesResult = fitableInstanceListGuard.Get();
    // then
    EXPECT_EQ(getResult, FIT_OK);
    EXPECT_EQ(fitableInstancesResult.empty(), false);
}

TEST_F(RegistryListenerSPITest, should_return_callback_when_create_given_function_callback)
{
    // given
    std::function<FitCode(const vector<FitableInstance>&)> action = [](const vector<FitableInstance>&) -> FitCode {
        return FIT_OK;
    };
    Fit::vector<FitableInstance> instances;
    // when
    auto callbackAction = FitablesChangedCallback::Create(action);
    int32_t notifyResult = callbackAction->Notify(instances);
    // then
    EXPECT_EQ(callbackAction != nullptr, true);
    EXPECT_EQ(notifyResult, FIT_OK);
}
