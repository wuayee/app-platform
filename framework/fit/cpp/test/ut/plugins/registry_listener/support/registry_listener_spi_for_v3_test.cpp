/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 *
 * Description  : provide ut for registry listener spi v3
 * Author       : wangpanbo
 * Date         : 2023/09/13
 */
#include <registry_listener/include/support/registry_listener_spi_for_v3.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Endpoint/1.0.0/cplusplus/Endpoint.hpp>
#include <fit/internal/runtime/config/configuration_entities.h>
#include <mock/configuration_service_mock.hpp>
#include <mock/application_instance_spi_mock.hpp>
#include <fit/stl/memory.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
using namespace Fit::Registry::Listener;
using namespace Fit::Configuration;
using namespace fit::hakuna::kernel::registry::shared;
class RegistryListenerSpiForV3Test : public ::testing::Test {
public:
    void SetUp() override
    {
        configurationService_ = Fit::make_shared<ConfigurationServiceMock>();
        applicationInstanceSpi_ = Fit::make_shared<ApplicationInstanceSpiMock>();
        registryListenerSpiForV3_ = Fit::make_shared<RegistryListenerSpiForV3>(
            configurationService_, applicationInstanceSpi_);
        fitable_.fitableId = "test_fitable_id";
        fitable_.fitableVersion = "1.0.0";
        fitable_.genericableId = "test_genericable_id";
        fitable_.genericableVersion = "1.0.0";

        Fit::vector<Fit::string> tags {"test_tag1", "test_tag2"};
        Fit::vector<Fit::string> aliases {"test_alias1", "test_alias2"};
        Fit::map<Fit::string, Fit::string> extensions = {{"key1", "value1"}};
        genericableConf_.SetGenericId(fitable_.genericableId);
        genericableConf_.SetTags(tags);
        fitableConfig_.extensions = extensions;
        fitableConfig_.fitableId = fitable_.fitableId;
        fitableConfig_.aliases = aliases;
        ApplicationInfo application;
        application.name = "test_app_name";
        application.nameVersion = "test_app_name_version";
        fitableConfig_.applications.emplace_back(application);
        Fit::vector<int32_t> applicationsFormats {0, 1};
        fitableConfig_.applicationsFormats.emplace_back(applicationsFormats);
        genericableConf_.SetFitable(fitableConfig_);

        genericableConfNoFitableConfig_.SetGenericId(fitable_.genericableId);
        genericableConfNoFitableConfig_.SetTags(tags);

        Endpoint endpoint;
        endpoint.port = 666;
        endpoint.protocol = 1;
        Address address;
        address.host = "127.0.0.1";
        address.endpoints.emplace_back(endpoint);
        WorkerInfo worker;
        worker.addresses.emplace_back(address);
        worker.id = "127.0.0.1:8888";
        worker.expire = 6;
        worker.environment = "test_env";
        worker.extensions["key1"] = "value1";
        worker.extensions["key2"] = "value2";
        applicationInstance_.workers.emplace_back(worker);
        applicationInstance_.application = new ApplicationInfo;
        *applicationInstance_.application = application;
    }

    void TearDown() override
    {
    }
    void CheckFitableInstance(const FitableInstance& fitableInstance)
    {
        EXPECT_EQ(fitableInstance.fitable->fitableId, fitable_.fitableId);
        EXPECT_EQ(fitableInstance.fitable->genericableId, fitable_.genericableId);
        EXPECT_EQ(fitableInstance.fitable->genericableVersion, fitable_.genericableVersion);
        EXPECT_EQ(fitableInstance.aliases[0], fitableConfig_.aliases[0]);
        EXPECT_EQ(fitableInstance.aliases[1], fitableConfig_.aliases[1]);
        EXPECT_EQ(fitableInstance.tags[0], genericableConf_.GetTags()[0]);
        EXPECT_EQ(fitableInstance.tags[1], genericableConf_.GetTags()[1]);
        auto actualExtensions = fitableInstance.extensions;
        auto extensions = fitableConfig_.extensions;
        EXPECT_EQ(actualExtensions["key1"], extensions["key1"]);
        EXPECT_EQ(fitableInstance.applicationInstances.size(), 1);

        ApplicationInstance applicationInstance = fitableInstance.applicationInstances.front();
        EXPECT_EQ(applicationInstance.application->name, fitableConfig_.applications[0].name);
        EXPECT_EQ(applicationInstance.application->nameVersion, fitableConfig_.applications[0].nameVersion);
        EXPECT_EQ(applicationInstance.workers.size(), 1);

        WorkerInfo actualWorker = applicationInstance.workers.front();
        WorkerInfo expectedWorker = applicationInstance_.workers.front();
        EXPECT_EQ(actualWorker.id, expectedWorker.id);
        EXPECT_EQ(actualWorker.expire, expectedWorker.expire);
        EXPECT_EQ(actualWorker.environment, expectedWorker.environment);
        EXPECT_EQ(actualWorker.extensions, expectedWorker.extensions);
        EXPECT_EQ(actualWorker.addresses.size(), expectedWorker.addresses.size());

        Address actualAddress = actualWorker.addresses.front();
        Address expectedAddress = expectedWorker.addresses.front();
        EXPECT_EQ(actualAddress.host, expectedAddress.host);
        EXPECT_EQ(actualAddress.endpoints.size(), expectedAddress.endpoints.size());

        Endpoint actualEndpoint = actualAddress.endpoints.front();
        Endpoint expectedEndpoint = expectedAddress.endpoints.front();
        EXPECT_EQ(actualEndpoint.port, expectedEndpoint.port);
        EXPECT_EQ(actualEndpoint.protocol, expectedEndpoint.protocol);
    }
public:
    Fit::shared_ptr<ConfigurationServiceMock> configurationService_ {};
    Fit::shared_ptr<ApplicationInstanceSpiMock> applicationInstanceSpi_ {};
    Fit::shared_ptr<RegistryListenerSpiForV3> registryListenerSpiForV3_ {};
    FitableInfo fitable_ {};
    GenericableConfiguration genericableConf_ {};
    GenericableConfiguration genericableConfNoFitableConfig_ {};
    FitableConfiguration fitableConfig_ {};
    ApplicationInstance applicationInstance_ {};
};

TEST_F(RegistryListenerSpiForV3Test, should_return_empty_fitable_instance_when_query_given_fitables)
{
    // given
    Fit::vector<FitableInfo> fitables {fitable_};
    EXPECT_CALL(*configurationService_, GetGenericableConfigPtr(::testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(nullptr));

    // when
    FitableInstanceListGuard queryResult = registryListenerSpiForV3_->QueryFitableInstances(fitables);

    // then
    EXPECT_EQ(queryResult.GetResultCode(), FIT_OK);
    EXPECT_EQ(queryResult.Get().empty(), true);
}

TEST_F(RegistryListenerSpiForV3Test, should_return_empty_fitable_instance_when_query_given_mock_no_fitable_conf)
{
    // given
    Fit::vector<FitableInfo> fitables {fitable_};
    EXPECT_CALL(*configurationService_, GetGenericableConfigPtr(::testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(Fit::make_shared<GenericableConfiguration>(genericableConfNoFitableConfig_)));

    // when
    FitableInstanceListGuard queryResult = registryListenerSpiForV3_->QueryFitableInstances(fitables);

    // then
    EXPECT_EQ(queryResult.GetResultCode(), FIT_OK);
    EXPECT_EQ(queryResult.Get().empty(), true);
}

TEST_F(RegistryListenerSpiForV3Test, should_return_fitable_instance_when_query_given_mock_fitable_conf)
{
    // given
    Fit::vector<FitableInfo> fitables {fitable_};
    EXPECT_CALL(*configurationService_, GetGenericableConfigPtr(::testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(Fit::make_shared<GenericableConfiguration>(genericableConf_)));
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_};
    EXPECT_CALL(*applicationInstanceSpi_, Query(::testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(applicationInstances));

    // when
    FitableInstanceListGuard queryResult = registryListenerSpiForV3_->QueryFitableInstances(fitables);

    // then
    EXPECT_EQ(queryResult.GetResultCode(), FIT_OK);
    EXPECT_EQ(queryResult.Get().empty(), false);
    FitableInstance fitableInstance = queryResult.Get().front();
    CheckFitableInstance(fitableInstance);
}

TEST_F(RegistryListenerSpiForV3Test, should_return_ok_when_call_all_mock_method_given_fitables)
{
    // given
    Fit::vector<FitableInfo> fitables {fitable_};
    // when
    FitableInstanceListGuard subscribeResult = registryListenerSpiForV3_->SubscribeFitables(fitables);
    FitCode unsubscribeRet = registryListenerSpiForV3_->UnsubscribeFitables(fitables);
    registryListenerSpiForV3_->SubscribeFitablesChanged(nullptr);
    registryListenerSpiForV3_->UnsubscribeFitablesChanged(nullptr);
    // then
    EXPECT_EQ(subscribeResult.GetResultCode(), FIT_OK);
    EXPECT_EQ(subscribeResult.Get().empty(), true);
    EXPECT_EQ(unsubscribeRet, FIT_OK);
}