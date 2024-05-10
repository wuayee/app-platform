/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : Test
 * Author       : l00558918
 * Date         : 2021/7/20 20:41
 */

#include <src/broker/client/application/use_case/broker_client_impl.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/framework/proxy_client.hpp>
#include <src/broker/client/domain/fitable_endpoint.cpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/formatter_service_mock.h>
#include <mock/fitable_endpoint_mock.h>
#include <mock/fitable_invoker_factory_mock.h>
#include <mock/fitable_invoker_mock.hpp>
#include <mock/fitable_discovery_mock.hpp>
#include <mock/broker_genericable_config_mock.hpp>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "broker_client_fit_config.h"
#include "gmock/gmock.h"

using namespace Fit;
using namespace Fit::Framework;
using namespace Fit::Framework::Formatter;
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

class BrokerClientImplTest : public ::testing::Test {
public:
    void SetUp() override
    {
        fitableDiscoveryPtr_ = std::make_shared<FitableDiscoveryMock>();
        configurationServicePtr_ = std::make_shared<ConfigurationServiceMock>();
        formatterService_ = std::make_shared<FormatterServiceMock>();

        localAddress_ = FitSystemPropertyUtils::Address();

        service_.serviceMeta.fitable.fitableId = "test_broker_client_fitable_id";
        service_.serviceMeta.fitable.fitableVersion = "1.0.0";
        service_.serviceMeta.fitable.genericId = "test_broker_client_genericable_id";
        service_.serviceMeta.fitable.genericVersion = "1.0.0";
        service_.address.host = localAddress_.host;
        service_.address.port = localAddress_.port;
        service_.address.workerId = localAddress_.id;
        service_.address.environment = localAddress_.environment;
        service_.address.formats = formats_;
        service_.address.protocol = localAddress_.protocol;
        service_.address.workerId = localAddress_.id;

        fitable_.fitId = "test_broker_client_fitable_id";
        fitable_.fitVersion = "1.0.0";
        fitable_.genericId = "test_broker_client_genericable_id";
        fitable_.genericVersion = "1.0.0";
    }

    void TearDown() override
    {
    }
public:
    std::shared_ptr<FitableDiscoveryMock> fitableDiscoveryPtr_;
    std::shared_ptr<ConfigurationServiceMock> configurationServicePtr_;
    std::shared_ptr<FormatterServiceMock> formatterService_;
    Framework::ParamJsonFormatter::ParamJsonFormatterPtr paramJsonFormatterService_ {nullptr};
    fit::registry::Address localAddress_;
    Fit::Framework::ServiceAddress service_;
    Fit::vector<int32_t> formats_ {0};
    fit::registry::Fitable fitable_;
};

TEST_F(BrokerClientImplTest, should_return_not_found_when_service_invoker_given_no_converter)
{
    // given
    BrokerClient brokerClient(fitableDiscoveryPtr_,
        configurationServicePtr_,
        formatterService_,
        paramJsonFormatterService_,
        localAddress_.environment,
        localAddress_.id);
    const int a = 20;
    bool* ret {};
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    EXPECT_CALL(*configurationServicePtr_, GetGenericableConfigPtr(testing::A<const Fit::string &>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(genericConfigGetter));

    // when
    int32_t result = brokerClient.ServiceInvoker(service_, testFitable.ctx_, in, out);
    // then
    EXPECT_EQ(result, FIT_ERR_NOT_FOUND);
}

TEST_F(BrokerClientImplTest, should_error_when_genericable_invoker_given_param)
{
    // given
    BrokerClient brokerClient(fitableDiscoveryPtr_,
        configurationServicePtr_,
        formatterService_,
        paramJsonFormatterService_,
        localAddress_.environment,
        localAddress_.id);
    const int a = 20;
    bool* ret = new bool();
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    EXPECT_CALL(*configurationServicePtr_, GetGenericableConfigPtr(testing::A<const Fit::string &>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(genericConfigGetter));

    // when
    int32_t result = brokerClient.GenericableInvoke(testFitable.ctx_, TestFitable::GENERIC_ID, in, out);
    // then
    EXPECT_EQ(result, FIT_ERR_ROUTE);
}

TEST_F(BrokerClientImplTest, should_error_when_local_invoker_given_param)
{
    // given
    BrokerClient brokerClient(fitableDiscoveryPtr_,
        configurationServicePtr_,
        formatterService_,
        paramJsonFormatterService_,
        localAddress_.environment,
        localAddress_.id);
    const int a = 20;
    bool* ret = new bool();
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;

    // when
    int32_t result = brokerClient.LocalInvoke(testFitable.ctx_, fitable_, in, out,
                                              Fit::Framework::Annotation::FitableType::MAIN);
    // then
    EXPECT_EQ(result, FIT_ERR_FAIL);
}

TEST_F(BrokerClientImplTest, should_use_appointed_endpoint_when_GenericableInvoke_given_address)
{
    // given
    std::shared_ptr<FitableInvokerFactoryMock> fitableInvokerFactoryMockPtr = make_shared<FitableInvokerFactoryMock>();
    BrokerClient brokerClient(fitableDiscoveryPtr_,
        configurationServicePtr_,
        formatterService_,
        paramJsonFormatterService_,
        localAddress_.environment,
        localAddress_.id,
        fitableInvokerFactoryMockPtr);

    const int a = 20;
    bool* ret;
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;
    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    config->SetDefaultFitableId("023f2a130c51480ab88f0a91daaf67e5");
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    EXPECT_CALL(*configurationServicePtr_, GetGenericableConfigPtr(testing::A<const Fit::string &>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(genericConfigGetter));

    Fit::Context::TargetAddress targetAddress = {"workerId", "host", 0, 3, {1}};
    Fit::Context::ContextSetTargetAddress(testFitable.ctx_, &targetAddress);

    Fit::vector<int32_t> formats {1};
    EXPECT_CALL(*formatterService_, GetFormats(testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(formats));

    // when
    std::unique_ptr<FitableInvokerMock> invokerMockPtr = make_unique<FitableInvokerMock>();
    EXPECT_CALL(*fitableInvokerFactoryMockPtr, GetInvoker(testing::An<::Fit::FitableCoordinatePtr>(),
        testing::An<::std::unique_ptr<::Fit::FitableEndpointSupplier>>(), testing::An<FitConfigPtr>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Invoke([&targetAddress, &invokerMockPtr, &config, &formats]
            (::Fit::FitableCoordinatePtr coordinate, ::std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier,
                FitConfigPtr c) {
            EXPECT_STREQ(typeid(*endpointSupplier).name(), "N12_GLOBAL__N_122DirectEndpointSupplierE");
            EXPECT_EQ(targetAddress.workerId, endpointSupplier->Get()->GetWorkerId());
            EXPECT_EQ(targetAddress.host, endpointSupplier->Get()->GetHost());
            EXPECT_EQ(targetAddress.port, endpointSupplier->Get()->GetPort());
            EXPECT_EQ(targetAddress.protocol, endpointSupplier->Get()->GetProtocol());
            EXPECT_EQ(targetAddress.formats, formats);
            return std::move(invokerMockPtr);
        }));
    EXPECT_CALL(*invokerMockPtr, Invoke(testing::_, testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    int32_t result = brokerClient.GenericableInvoke(testFitable.ctx_, TestFitable::GENERIC_ID, in, out);
    // then
    EXPECT_THAT(result, testing::Eq(FIT_OK));
}

TEST_F(BrokerClientImplTest, should_use_created_endpoint_when_GenericableInvoke_given_address_not_setted)
{
    // given
    std::shared_ptr<FitableInvokerFactoryMock> fitableInvokerFactoryMockPtr = make_shared<FitableInvokerFactoryMock>();
    BrokerClient brokerClient(fitableDiscoveryPtr_,
        configurationServicePtr_,
        formatterService_,
        paramJsonFormatterService_,
        localAddress_.environment,
        localAddress_.id,
        fitableInvokerFactoryMockPtr);

    const int a = 20;
    bool* ret;
    ::Fit::vector<::Fit::any> in {&a};
    ::Fit::vector<::Fit::any> out {&ret};
    TestFitable testFitable;
    Fit::Configuration::GenericConfigPtr config = std::make_shared<Fit::Configuration::GenericableConfiguration>();
    config->SetDefaultFitableId("023f2a130c51480ab88f0a91daaf67e5");
    Fit::Configuration::GenericConfigPtr genericConfigGetter = config;
    EXPECT_CALL(*configurationServicePtr_, GetGenericableConfigPtr(testing::A<const Fit::string &>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(genericConfigGetter));
    // when
    std::unique_ptr<FitableInvokerMock> invokerMockPtr = make_unique<FitableInvokerMock>();
    std::shared_ptr<Fit::IBrokerFitableDiscovery> brokerFitableDiscoveryPtr;
    EXPECT_CALL(*fitableInvokerFactoryMockPtr, GetFitableDiscovery())
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::ReturnRef(brokerFitableDiscoveryPtr));
    EXPECT_CALL(*fitableInvokerFactoryMockPtr, GetInvoker(testing::An<::Fit::FitableCoordinatePtr>(),
        testing::An<::std::unique_ptr<::Fit::FitableEndpointSupplier>>(), testing::An<FitConfigPtr>()))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Invoke([&invokerMockPtr](::Fit::FitableCoordinatePtr coordinate,
            ::std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, FitConfigPtr c) {
            EXPECT_STREQ(typeid(*endpointSupplier).name(), "N12_GLOBAL__N_132FitableEndpointSupplierCompositeE");
            return std::move(invokerMockPtr);
        }));
    EXPECT_CALL(*invokerMockPtr, Invoke(testing::_, testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillRepeatedly(testing::Return(FIT_OK));
    int32_t result = brokerClient.GenericableInvoke(testFitable.ctx_, TestFitable::GENERIC_ID, in, out);
    // then
    EXPECT_THAT(result, testing::Eq(FIT_OK));
}