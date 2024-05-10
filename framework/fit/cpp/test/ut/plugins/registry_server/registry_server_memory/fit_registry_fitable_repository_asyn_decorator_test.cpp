/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <fit/internal/registry/repository/fit_registry_memory_repository.h>
#include <registry_server/registry_server_memory/fitable/fit_registry_fitable_repository_asyn_decorator.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace std;
using namespace ::testing;

class MockMySqlRepo : public FitRegistryServiceRepository {
public:
    MOCK_METHOD0(Start, bool());
    MOCK_METHOD0(Stop, bool());
    MOCK_METHOD1(Save, bool(const db_service_info_t &));
    MOCK_METHOD1(Save, bool(const db_service_set &));
    MOCK_METHOD1(Query, db_service_set(const fit_fitable_key_t &key));
    MOCK_METHOD2(Remove, bool(const Fit::fitable_id &fitable, const Fit::fit_address &address));
    MOCK_METHOD1(Remove, bool(const db_service_set &services));
    MOCK_METHOD0(GetAllServices, db_service_set());
    MOCK_METHOD1(GetServicesByGenericId, db_service_set(const string &generic_id));
    MOCK_METHOD1(DelServicesByAddress, int32_t(const Fit::fit_address &address));
    MOCK_METHOD1(Remove, db_service_set(const Fit::fit_address &address));
};

class FitRegistryFitableRepositoryAsynDecoratorTest : public ::testing::Test {
public:

    void SetUp() override
    {
        serviceInfoInput_.is_online = true;
        serviceInfoInput_.start_time = 123456;
        Fit::fit_address address;
        address.ip = "127.0.0.1";
        address.port = 8883;
        address.protocol = Fit::fit_protocol_type::GRPC;
        address.id = "127.0.0.1:8883";
        serviceInfoInput_.service.addresses.emplace_back(address);
        serviceInfoInput_.service.fitable.generic_id = "test_gid";
        serviceInfoInput_.service.fitable.fitable_id = "test_fid";
        serviceInfoInput_.service.fitable.generic_version = "test_version";

        serviceInfoInput2_.service.addresses.emplace_back(address);
        serviceInfoInput2_.service.fitable.generic_id = "test_gid2";
        serviceInfoInput2_.service.fitable.fitable_id = "test_fid2";
        serviceInfoInput2_.service.fitable.generic_version = "test_version2";
        nullRepoDecoratorPtr_ = FitRegistryRepositoryFactoryWithServiceRepository::Create(nullptr);
        mySqlRepo_ = std::make_shared<MockMySqlRepo>();
        decoratorPtr_ = FitRegistryRepositoryFactoryWithServiceRepository::Create(mySqlRepo_);
        nullRepoDecoratorPtr_->Start();
        decoratorPtr_->Start();
    }

    void TearDown() override
    {
        nullRepoDecoratorPtr_->Stop();
        decoratorPtr_->Stop();
    }
public:
    db_service_info_t serviceInfoInput_;
    db_service_info_t serviceInfoInput2_;
    FitRegistryRepositoryDecoratorPtr nullRepoDecoratorPtr_;
    std::shared_ptr<MockMySqlRepo> mySqlRepo_;
    FitRegistryRepositoryDecoratorPtr decoratorPtr_;
};

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest, should_return_true_when_save_fit_service_given_fit_service)
{
    // given
    bool expectedRet = true;

    // when
    bool ret = nullRepoDecoratorPtr_->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest,
    should_return_true_when_save_fit_service_given_two_same_fit_service)
{
    // given
    bool expectedRet = true;

    // when
    bool ret = nullRepoDecoratorPtr_->Save(serviceInfoInput_);
    bool ret2 = nullRepoDecoratorPtr_->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(ret2, expectedRet);
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest,
    should_return_true_when_save_fit_service_set_given_fit_service)
{
    // given
    bool expectedRet = true;
    db_service_set serviceSet;
    serviceSet.push_back(serviceInfoInput_);

    // when
    bool ret = nullRepoDecoratorPtr_->Save(serviceSet);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest,
    should_return_true_when_save_service_and_query_given_fit_service_set_and_key)
{
    // given
    bool expectedRet = true;

    db_service_set serviceSet;
    serviceSet.push_back(serviceInfoInput_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    // when

    EXPECT_CALL(*mySqlRepo_, Query(key))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(expectedSet));
    EXPECT_CALL(*mySqlRepo_, Save(testing::A<const db_service_set&>()))
        .WillRepeatedly(testing::Return(true));

    bool ret = decoratorPtr_->Save(serviceSet);
    decoratorPtr_->Save(serviceSet);
    decoratorPtr_->Save(serviceSet);
    decoratorPtr_->Save(serviceSet);
    decoratorPtr_->Save(serviceSet);
    db_service_set actualServiceSet = decoratorPtr_->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->is_online, expectedSet.begin()->is_online);
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest,
    should_return_true_when_delete_and_query_given_fitable_and_address)
{
    // given
    bool expectedRet = true;

    db_service_set serviceSet;
    serviceSet.push_back(serviceInfoInput_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    Fit::fitable_id fitableRemove;
    fitableRemove.generic_id = "test_gid";
    fitableRemove.generic_version = "test_version";
    fitableRemove.fitable_id = "test_fid";
    Fit::fit_address addressRemove;
    addressRemove.ip = "127.0.0.1";
    addressRemove.port = 8883;
    addressRemove.protocol = Fit::fit_protocol_type::GRPC;
    db_service_set expectedSetAfterRemove;
    bool expectedRetRemove = true;

    // when
    EXPECT_CALL(*mySqlRepo_, Save(testing::A<const db_service_set&>()))
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mySqlRepo_, Query(key))
        .Times(testing::AtLeast(2))
        .WillOnce(testing::Return(expectedSet))
        .WillOnce(testing::Return(expectedSetAfterRemove));

    bool ret = decoratorPtr_->Save(serviceSet);

    db_service_set actualServiceSet = decoratorPtr_->Query(key);
    bool actualRemoveRet = decoratorPtr_->Remove(fitableRemove, addressRemove);
    db_service_set actualServiceSetAfterRemove = decoratorPtr_->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->is_online, expectedSet.begin()->is_online);
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().id,
              expectedSet.begin()->service.addresses.front().id);
    EXPECT_EQ(actualRemoveRet, expectedRetRemove);
    EXPECT_EQ(actualServiceSetAfterRemove.size(), expectedSetAfterRemove.size());
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest, should_return_service_set_when_get_all_services_given_empty)
{
    // given
    // when
    EXPECT_CALL(*mySqlRepo_, GetAllServices())
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(db_service_set {}));
    db_service_set actualServiceSetGetAllService = decoratorPtr_->GetAllServices();

    // then
    EXPECT_TRUE(actualServiceSetGetAllService.empty());
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest, should_return_service_set_when_Remove_given_address)
{
    // given
    bool expectedRetSave = true;
    db_service_set serviceSet;
    serviceSet.push_back(serviceInfoInput_);
    serviceSet.push_back(serviceInfoInput2_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";

    Fit::fit_address address = serviceInfoInput_.service.addresses.front();
    db_service_set expectedRetRemove;
    // when
    bool retSave = nullRepoDecoratorPtr_->Save(serviceSet);
    db_service_set actualRetRemove = nullRepoDecoratorPtr_->Remove(address);

    // then
    EXPECT_EQ(retSave, expectedRetSave);
    EXPECT_EQ(actualRetRemove.size(), expectedRetRemove.size());
}

TEST_F(FitRegistryFitableRepositoryAsynDecoratorTest,
    should_return_empty_when_delete_services_and_query_given_services)
{
    // given
    bool expectedRet = true;

    db_service_set serviceSet;
    serviceSet.push_back(serviceInfoInput_);

    fit_fitable_key_t key;
    key.generic_id = "test_gid";
    key.generic_version = "test_version";
    key.fitable_id = "test_fid";
    db_service_set expectedSet = serviceSet;

    db_service_set removeServiceSetInput {serviceInfoInput_};
    db_service_set expectedSetAfterRemove;

    // when
    EXPECT_CALL(*mySqlRepo_, Save(testing::A<const db_service_set&>()))
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mySqlRepo_, Query(key))
        .Times(testing::AtLeast(2))
        .WillOnce(testing::Return(expectedSet))
        .WillOnce(testing::Return(expectedSetAfterRemove));

    bool ret = decoratorPtr_->Save(serviceSet);
    db_service_set actualServiceSet = decoratorPtr_->Query(key);
    decoratorPtr_->Remove(removeServiceSetInput);
    db_service_set actualServiceSetAfterRemove = decoratorPtr_->Query(key);

    // then
    EXPECT_EQ(ret, expectedRet);
    EXPECT_EQ(actualServiceSet.size(), expectedSet.size());
    EXPECT_EQ(actualServiceSet.begin()->is_online, expectedSet.begin()->is_online);
    EXPECT_EQ(actualServiceSet.begin()->start_time, expectedSet.begin()->start_time);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_id, expectedSet.begin()->service.fitable.generic_id);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.generic_version,
              expectedSet.begin()->service.fitable.generic_version);
    EXPECT_EQ(actualServiceSet.begin()->service.fitable.fitable_id, expectedSet.begin()->service.fitable.fitable_id);
    ASSERT_EQ(actualServiceSet.begin()->service.addresses.size(), expectedSet.begin()->service.addresses.size());
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().ip,
              expectedSet.begin()->service.addresses.front().ip);
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().port,
              expectedSet.begin()->service.addresses.front().port);
    EXPECT_EQ(int(actualServiceSet.begin()->service.addresses.front().protocol),
              int(expectedSet.begin()->service.addresses.front().protocol));
    EXPECT_EQ(actualServiceSet.begin()->service.addresses.front().id,
              expectedSet.begin()->service.addresses.front().id);
    EXPECT_EQ(actualServiceSetAfterRemove.size(), expectedSetAfterRemove.size());
}