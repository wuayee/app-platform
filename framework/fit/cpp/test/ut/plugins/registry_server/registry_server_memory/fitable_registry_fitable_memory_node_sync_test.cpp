/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/10
*/
#include "gtest/gtest.h"
#include "gmock/gmock.h"
#define private public
#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <registry_server/registry_server_memory/fitable/fitable_registry_fitable_memory_node_sync.h>
#include <registry_server/core/fit_registry_conf.h>
#include <mock/runtime_mock.hpp>
#include <fit/fit_log.h>
class FitableRegistryFitableMemoryNodeSyncTest : public ::testing::Test {
public:

    void SetUp() override
    {
    }

    void TearDown() override
    {
    }
};
TEST_F(FitableRegistryFitableMemoryNodeSyncTest, should_return_success_when_add_given_service_set)
{
    // given
    int32_t expectedRet = REGISTRY_SUCCESS;

    db_service_set serviceSet;
    db_service_info_t serviceInfo;
    serviceInfo.is_online = true;
    serviceInfo.start_time = 123456;
    Fit::fit_address address;
    address.ip = "127.0.0.1";
    address.port = 8883;
    address.protocol = Fit::fit_protocol_type::GRPC;
    address.id = "127.0.0.1:8883";
    serviceInfo.service.addresses.emplace_back(address);

    serviceInfo.service.fitable.generic_id = "test_gid";
    serviceInfo.service.fitable.fitable_id = "test_fid";
    serviceInfo.service.fitable.generic_version = "test_version";
    serviceSet.push_back(serviceInfo);

    // when
    FitableRegistryFitableNodeSyncPtr syncRepo = FitableRegistryFitableNodeSyncPtrFacotry::Create();
    int32_t ret = syncRepo->Add(serviceSet);
    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitableRegistryFitableMemoryNodeSyncTest, should_return_success_when_remove_given_service_set)
{
    // given
    int32_t expectedRetAdd = REGISTRY_SUCCESS;
    int32_t expectedRetRemove = REGISTRY_SUCCESS;

    db_service_set serviceSet;
    db_service_info_t serviceInfo;
    serviceInfo.is_online = true;
    serviceInfo.start_time = 123456;
    Fit::fit_address address;
    address.ip = "127.0.0.1";
    address.port = 8883;
    address.protocol = Fit::fit_protocol_type::GRPC;
    address.id = "127.0.0.1:8883";
    serviceInfo.service.addresses.emplace_back(address);

    serviceInfo.service.fitable.generic_id = "test_gid";
    serviceInfo.service.fitable.fitable_id = "test_fid";
    serviceInfo.service.fitable.generic_version = "test_version";
    serviceSet.push_back(serviceInfo);

    // when
    FitableRegistryFitableNodeSyncPtr syncRepo = FitableRegistryFitableNodeSyncPtrFacotry::Create();
    int32_t retAdd = syncRepo->Add(serviceSet);
    int32_t retRemove = syncRepo->Remove(serviceSet);

    // then
    EXPECT_EQ(retAdd, expectedRetAdd);
    EXPECT_EQ(retRemove, expectedRetRemove);
}

TEST_F(FitableRegistryFitableMemoryNodeSyncTest, should_return_id_when_get_address_by_id_given_address)
{
    // given
    uint64_t expectedRet = 0;

    // when
    uint64_t ret = Fit::Registry::FitableRegistryFitableMemoryNodeSync().GetIdByAddress("127.0.0.1:8881");

    bool right = (ret <= Fit::Registry::GetRegistryFitableNodeSyncThreadNum());
    // then
    EXPECT_EQ(right, true);
}

TEST_F(FitableRegistryFitableMemoryNodeSyncTest, should_return_0_when_get_address_by_id_given_thread_num_0)
{
    // given
    uint32_t threadNum = Fit::Registry::GetRegistryFitableNodeSyncThreadNum();
    Fit::Registry::SetRegistryFitableNodeSyncThreadNum(0);
    // when
    uint64_t ret = Fit::Registry::FitableRegistryFitableMemoryNodeSync().GetIdByAddress("127.0.0.1:8881");
    // then
    EXPECT_EQ(ret, 0);
    Fit::Registry::SetRegistryFitableNodeSyncThreadNum(threadNum);
}