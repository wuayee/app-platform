/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2020-05-16
 */

#include <stl/memory.hpp>
#include <gmock/gmock-actions.h>
#include <src/broker/client/adapter/south/gateway/broker_fitable_discovery.h>
#include <fit/internal/fit_system_property_utils.h>
#include <fit/external/framework/proxy_client.hpp>
#include <mock/configuration_service_mock.hpp>
#include <mock/fitable_discovery_mock.hpp>
#include <mock/formatter_service_mock.h>
#include <mock/fitable_endpoint_mock.h>
#include <mock/registry_listener_api_mock.hpp>
#include <mock/broker_genericable_config_mock.hpp>
#include <fit/fit_log.h>
#include "broker_client_fit_config.h"
#include "gmock/gmock.h"

using namespace testing;
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

namespace Fit {
namespace Framework {
inline bool operator==(const Framework::ServiceAddress& l, const Framework::ServiceAddress& r)
{
    return l.serviceMeta.fitable.genericId == r.serviceMeta.fitable.genericId &&
           l.serviceMeta.fitable.genericVersion == r.serviceMeta.fitable.genericVersion &&
           l.serviceMeta.fitable.fitableId == r.serviceMeta.fitable.fitableId &&
           l.serviceMeta.fitable.fitableVersion == r.serviceMeta.fitable.fitableVersion &&
           l.address.host == r.address.host && l.address.port == r.address.port &&
           l.address.environment == r.address.environment &&
           l.address.protocol == r.address.protocol && l.address.formats == r.address.formats;
}
}
}

class BrokerFitableDiscoveryTest : public ::testing::Test {
public:
    void SetUp() override
    {
        fitableDiscoveryPtr_ = std::make_shared<FitableDiscoveryMock>();

        address_.address.workerId = "workerId";
        address_.address.host = "host";
        address_.address.port = 1;
        address_.address.protocol = 3;
        address_.address.environment = "env";
        address_.address.formats = {1, 2};
    }

    void TearDown() override
    {
    }
public:
    std::shared_ptr<FitableDiscoveryMock> fitableDiscoveryPtr_;
    Framework::ServiceAddress address_;
};

TEST_F(BrokerFitableDiscoveryTest, should_empty_when_get_local_fitable_given_null_discovery)
{
    // given
    Fitable id;
    Fit::BrokerFitableDiscovery brokerFitableDiscovery(nullptr, nullptr);
    // when
    Fit::Framework::Annotation::FitableDetailPtrList result = brokerFitableDiscovery.GetLocalFitable(id);
    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(BrokerFitableDiscoveryTest,
    should_return_registry_addresses_when_GetFitableAddresses_given_registry_fitable)
{
    Fitable fitable;
    fitable.genericId = "gid";
    fitable.fitableId = "fid";
    auto config = make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, IsRegistryFitable())
        .WillOnce(Return(true));
    auto registryListenerApiMockTmp = make_unique<RegistryListenerApiMock>();
    auto registryListenerApiMock = registryListenerApiMockTmp.get();
    vector<Framework::ServiceAddress> registryAddresses;
    registryAddresses.push_back(address_);
    EXPECT_CALL(*registryListenerApiMock, GetRegistryFitableAddresses(_))
        .WillOnce(DoAll(SetArgReferee<0>(registryAddresses), Return(FIT_OK)));

    BrokerFitableDiscovery brokerFitableDiscovery(nullptr, move(registryListenerApiMockTmp));
    auto result = brokerFitableDiscovery.GetFitableAddresses(*config, fitable);

    EXPECT_EQ(result, registryAddresses);
}

TEST_F(BrokerFitableDiscoveryTest,
    should_return_fitable_addresses_when_GetFitableAddresses_given_non_registry_fitable)
{
    Fitable fitable;
    fitable.genericId = "gid";
    fitable.fitableId = "fid";
    auto config = make_shared<BrokerGenericableConfigMock>();
    EXPECT_CALL(*config, IsRegistryFitable())
        .WillOnce(Return(false));
    auto registryListenerApiMockTmp = make_unique<RegistryListenerApiMock>();
    auto registryListenerApiMock = registryListenerApiMockTmp.get();
    vector<Framework::ServiceAddress> fitableAddresses;
    fitableAddresses.push_back(address_);
    EXPECT_CALL(*registryListenerApiMock, GetFitableAddresses(fitable, _))
        .WillOnce(DoAll(SetArgReferee<1>(fitableAddresses), Return(FIT_OK)));

    BrokerFitableDiscovery brokerFitableDiscovery(nullptr, move(registryListenerApiMockTmp));
    auto result = brokerFitableDiscovery.GetFitableAddresses(*config, fitable);

    EXPECT_EQ(result, fitableAddresses);
}