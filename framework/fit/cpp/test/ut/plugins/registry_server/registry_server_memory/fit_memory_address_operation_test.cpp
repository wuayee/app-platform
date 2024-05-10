/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <registry_server/registry_server_memory/fitable/fit_memory_address_operation.h>
#include <fit/fit_log.h>
#include <fit/stl/string.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;
using namespace Fit::Registry;

class FitMemoryAddressOperationTest : public ::testing::Test {
public:

    void SetUp() override
    {
        address_ = std::make_shared<Fit::RegistryInfo::Address>();
        address_->host = "127.0.0.1";
        address_->port = 8080;
        address_->protocol = Fit::fit_protocol_type::GRPC;
        address_->workerId = "127.0.0.1:8080";
    }

    void TearDown() override
    {
    }
public:
    std::shared_ptr<Fit::RegistryInfo::Address> address_;
};

TEST_F(FitMemoryAddressOperationTest, should_return_success_when_save_addresses_given_addresses)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    addresses.push_back(address_);
    int32_t expectedRet = FIT_ERR_SUCCESS;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t ret = fitMemoryAddressOperation.Save(addresses);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitMemoryAddressOperationTest, should_return_fatal_error_when_save_addresses_given_empty)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    int32_t expectedRet = FIT_ERR_FAIL;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t ret = fitMemoryAddressOperation.Save(addresses);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitMemoryAddressOperationTest, should_return_fatal_error_when_save_addresses_given_nullptr)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    addresses.emplace_back(address_);
    addresses.front()->workerId = "";
    int32_t expectedRet = FIT_ERR_SUCCESS;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t ret = fitMemoryAddressOperation.Save(addresses);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitMemoryAddressOperationTest, should_return_fatal_error_when_save_addresses_given_worker_id_empty)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    addresses.emplace_back(nullptr);
    int32_t expectedRet = FIT_ERR_SUCCESS;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t ret = fitMemoryAddressOperation.Save(addresses);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitMemoryAddressOperationTest, should_return_nullptr_when_query_given_address)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::Address> expectedRet = nullptr;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    std::shared_ptr<Fit::RegistryInfo::Address> ret = fitMemoryAddressOperation.Query(*address_);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitMemoryAddressOperationTest, should_return_empty_address_when_query_given_invalid_worker_id)
{
    // given
    Fit::string invalid_worker_id = "invalid_worker_id";

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    FitMemoryAddressOperation::AddressPtrSet ret = fitMemoryAddressOperation.Query(invalid_worker_id);

    // then
    EXPECT_EQ(ret.empty(), true);
}

TEST_F(FitMemoryAddressOperationTest, should_return_address_when_save_and_query_given_address)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    addresses.push_back(address_);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    std::shared_ptr<Fit::RegistryInfo::Address> expectedQueryRet = address_;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t actualSaveRet = fitMemoryAddressOperation.Save(addresses);
    std::shared_ptr<Fit::RegistryInfo::Address> queryRet = fitMemoryAddressOperation.Query(*address_);

    // then
    EXPECT_EQ(actualSaveRet, expectedSaveRet);
    ASSERT_NE(queryRet, nullptr);
    EXPECT_EQ(queryRet->host, expectedQueryRet->host);
    EXPECT_EQ(queryRet->port, expectedQueryRet->port);
    EXPECT_EQ(queryRet->protocol, expectedQueryRet->protocol);
    EXPECT_EQ(queryRet->workerId, expectedQueryRet->workerId);
}

TEST_F(FitMemoryAddressOperationTest, should_return_nullptr_when_save_and_query_given_diff_port)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    addresses.push_back(address_);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    Fit::RegistryInfo::Address addressIn = *address_;
    addressIn.port = 6666;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t actualSaveRet = fitMemoryAddressOperation.Save(addresses);
    std::shared_ptr<Fit::RegistryInfo::Address> queryRet = fitMemoryAddressOperation.Query(addressIn);

    // then
    EXPECT_EQ(actualSaveRet, expectedSaveRet);
    EXPECT_EQ(queryRet, nullptr);
}

TEST_F(FitMemoryAddressOperationTest, should_return_nullptr_when_save_remove_and_query_given_address)
{
    // given
    FitMemoryAddressOperation::AddressPtrSet addresses;
    addresses.push_back(address_);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    int32_t expectedRemoveRet = FIT_ERR_SUCCESS;
    std::shared_ptr<Fit::RegistryInfo::Address> expectedQueryRet = nullptr;

    // when
    FitMemoryAddressOperation fitMemoryAddressOperation;
    int32_t actualSaveRet = fitMemoryAddressOperation.Save(addresses);
    int32_t actualRemoveRet = fitMemoryAddressOperation.Remove(address_->workerId);
    std::shared_ptr<Fit::RegistryInfo::Address> queryRet = fitMemoryAddressOperation.Query(*address_);

    // then
    EXPECT_EQ(actualSaveRet, expectedSaveRet);
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(queryRet, expectedQueryRet);
}