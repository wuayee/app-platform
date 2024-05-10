/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : test for default_registry_listener_api
 * Author       : songyongtan
 * Create       : 2022-07-25
 * Notes:       : nothing
 */

#include <gtest/gtest.h>

#include "broker/client/adapter/south/gateway/default_registry_listener_api.h"
#include "component/com_huawei_fit_hakuna_kernel_registry_shared_Address/1.0.0/cplusplus/Address.hpp"
#include "component/com_huawei_fit_hakuna_kernel_registry_shared_Endpoint/1.0.0/cplusplus/Endpoint.hpp"
#include "component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp"

using namespace testing;
using namespace Fit;
using ::fit::hakuna::kernel::registry::shared::Address;
using ::fit::hakuna::kernel::registry::shared::ApplicationInstance;
using ::fit::hakuna::kernel::registry::shared::Endpoint;
using ::fit::hakuna::kernel::registry::shared::Worker;

class DefaultRegistryListenerApiTest : public Test {
public:
    void SetUp()
    {
        endpoint1.port = 1111;
        endpoint1.protocol = 1;

        endpoint2.port = 2222;
        endpoint2.protocol = 2;

        address.host = "127.0.0.1";
        worker.id = "id";
    }

    static void CheckAddress(
        Framework::Address& frameworkerAddress, const Worker& worker, const Address& address, const Endpoint& endpoint)
    {
        EXPECT_EQ(frameworkerAddress.workerId, worker.id);
        EXPECT_EQ(frameworkerAddress.host, address.host);
        EXPECT_EQ(frameworkerAddress.port, endpoint.port);
        EXPECT_EQ(frameworkerAddress.protocol, endpoint.protocol);
    }

protected:
    Endpoint endpoint1;
    Endpoint endpoint2;
    Address address;
    Worker worker;
};

TEST_F(DefaultRegistryListenerApiTest, should_return_address_size_when_calculate_application_instance)
{
    vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    ApplicationInstance app;

    address.endpoints.push_back(endpoint1);
    address.endpoints.push_back(endpoint2);
    worker.addresses.push_back(address);
    app.workers.push_back(worker);
    app.workers.push_back(worker);
    applicationInstances.push_back(app);

    auto addressSize = DefaultRegistryListenerApi::CalculateApplicationInstanceAddressSize(applicationInstances);

    ASSERT_EQ(addressSize, 4);
}

TEST_F(DefaultRegistryListenerApiTest, should_return_flatted_addresses_when_get_address_from_application_instance)
{
    vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    vector<Framework::ServiceAddress> result;
    ApplicationInstance app;
    address.endpoints.push_back(endpoint1);
    address.endpoints.push_back(endpoint2);
    worker.addresses.push_back(address);
    app.workers.push_back(worker);
    app.workers.push_back(worker);
    applicationInstances.push_back(app);

    DefaultRegistryListenerApi::GetAddressesFromApplicationInstance({}, applicationInstances, result);

    ASSERT_EQ(result.size(), 4);
    CheckAddress(result[0].address, worker, address, endpoint1);
    CheckAddress(result[1].address, worker, address, endpoint2);
    CheckAddress(result[2].address, worker, address, endpoint1);
    CheckAddress(result[3].address, worker, address, endpoint2);
}