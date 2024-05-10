/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <fit/internal/registry/registry_util.h>
#include <fit/internal/registry/fit_registry_entities.h>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;

class RegistryUtilTest : public ::testing::Test {
public:
    void SetUp() override
    {
        // fitable的memory适配层
        fitable_.fitableId = "test_fitable_id";
        fitable_.fitableVersion = "test_fitable_version";
        fitable_.genericableId = "test_genericable_id";
        fitable_.genericableVersion = "genericable_version";

        app_.name = "test_app";
        app_.nameVersion = "test_app_version";
        fitableMeta_.fitable = fitable_;
        fitableMeta_.application = app_;
        fitableMeta_.formats = {Fit::fit_format_type::JSON, Fit::fit_format_type::PROTOBUF};

        workerId_ = "127.0.0.1:8008";
        address_.host = "127.0.0.1";
        address_.port = 8008;
        address_.protocol = Fit::fit_protocol_type::GRPC;
        address_.workerId = workerId_;

        worker_.application = app_;
        worker_.environment = "test_env";
        worker_.workerId = workerId_;
    }

    void TearDown() override
    {
    }
public:
    Fit::RegistryInfo::Fitable fitable_;
    Fit::RegistryInfo::Application app_;
    Fit::RegistryInfo::FitableMeta fitableMeta_;
    Fit::RegistryInfo::Address address_;
    Fit::string workerId_;
    Fit::RegistryInfo::Worker worker_;
};

// workerId下的address为空，则不构建worker - app的关系；
TEST_F(RegistryUtilTest,
    should_return_empty_service_when_convert_to_service_given_empty_address_worker_and_fitable)
{
    // given
    // when
    db_service_set serviceSet = RegistryUtil::ConvertToServiceSet({worker_}, {}, {fitableMeta_});

    // then
    EXPECT_EQ(serviceSet.empty(), true);
}

// worker下 app为空，为异常情况，则不构建worker - app的关系；
TEST_F(RegistryUtilTest,
    should_return_empty_service_when_convert_to_service_given_emptyAppworker_address_and_fitable)
{
    // given
    Fit::RegistryInfo::Worker emptyAppWorker = worker_;
    emptyAppWorker.application = Fit::RegistryInfo::Application();

    // when
    db_service_set serviceSet = RegistryUtil::ConvertToServiceSet({emptyAppWorker}, {address_}, {fitableMeta_});

    // then
    EXPECT_EQ(serviceSet.empty(), true);
}

// app下worker信息为空，则不构建fitable和worker之间的关系
TEST_F(RegistryUtilTest, should_return_empty_service_when_convert_to_service_given_emptyWorker_address_and_fitable)
{
    // given
    // when
    db_service_set serviceSet = RegistryUtil::ConvertToServiceSet({}, {address_}, {fitableMeta_});

    // then
    EXPECT_EQ(serviceSet.empty(), true);
}

TEST_F(RegistryUtilTest, should_return_service_when_convert_to_service_given_worker_address_and_fitable)
{
    // given
    // when
    db_service_set serviceSet = RegistryUtil::ConvertToServiceSet({worker_}, {address_}, {fitableMeta_});

    // then
    ASSERT_EQ(serviceSet.empty(), false);
    db_service_info_t service = serviceSet.front();
    ASSERT_EQ(service.service.addresses.size(), 1);
    EXPECT_EQ(service.service.addresses.front().ip, address_.host);
    EXPECT_EQ(service.service.addresses.front().port, address_.port);
    EXPECT_EQ((service.service.addresses.front().protocol == address_.protocol), true);
    EXPECT_EQ(service.service.addresses.front().id, address_.workerId);
    EXPECT_EQ(service.service.application.name, app_.name);
    EXPECT_EQ(service.service.application.nameVersion, app_.nameVersion);
    EXPECT_EQ(service.service.fitable.fitable_id, fitable_.fitableId);
    EXPECT_EQ(service.service.fitable.fitable_version, fitable_.fitableVersion);
    EXPECT_EQ(service.service.fitable.generic_id, fitable_.genericableId);
    EXPECT_EQ(service.service.fitable.generic_version, fitable_.genericableVersion);
}