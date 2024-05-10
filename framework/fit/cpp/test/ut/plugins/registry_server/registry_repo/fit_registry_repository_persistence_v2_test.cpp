/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/11/30
*/
#include <chrono>
#include <memory>
#include <thread>
#include <fit/internal/registry/repository/fit_registry_repository.h>
#include <registry_server/registry_repo/fit_registry_repository_persistence_v2.h>
#include <fit/fit_log.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"
using namespace testing;
using namespace Fit;
class MockWorkerOperation : public FitWorkerTableOperation {
public:
    MOCK_METHOD0(Init, bool());
    MOCK_METHOD1(Save, int32_t(const Fit::RegistryInfo::Worker& worker));
    MOCK_METHOD1(Delete, int32_t(const Fit::string& workerId));
    MOCK_METHOD2(Delete, int32_t(const Fit::string& workerId, const Fit::RegistryInfo::Application& application));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::Worker>(const Fit::RegistryInfo::Application& application));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::Worker>(const Fit::string& workerId));
    MOCK_METHOD0(QueryAll, Fit::vector<Fit::RegistryInfo::Worker>());
};

class MockAddressOperation : public FitAddressTableOperation {
public:
    MOCK_METHOD0(Init, bool());
    MOCK_METHOD1(Save, int32_t(const Fit::RegistryInfo::Address& address));
    MOCK_METHOD1(Save, int32_t(const Fit::vector<Fit::RegistryInfo::Address>& address));
    MOCK_METHOD1(Delete, int32_t(const Fit::string& workerId));
    MOCK_METHOD2(Query, int32_t(const Fit::string& workerId, Fit::vector<Fit::RegistryInfo::Address>& addresses));
    MOCK_METHOD1(QueryAll, int32_t(Fit::vector<Fit::RegistryInfo::Address>& addresses));
};

class MockFitableOperation : public FitFitableTableOperation {
public:
    MOCK_METHOD0(Init, bool());
    MOCK_METHOD1(Save, int32_t(const Fit::RegistryInfo::FitableMeta& fitableMeta));
    MOCK_METHOD1(Delete, int32_t(const Fit::RegistryInfo::Application& application));
    MOCK_METHOD1(Delete, int32_t(const Fit::RegistryInfo::FitableMeta& fitableMeta));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::FitableMeta>(const Fit::RegistryInfo::Application& application));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::FitableMeta>(const Fit::RegistryInfo::FitableMeta& fitableMeta));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::FitableMeta>(const Fit::string& genericableId));
    MOCK_METHOD1(Query, Fit::vector<Fit::RegistryInfo::FitableMeta>(const Fit::RegistryInfo::Fitable& fitable));
    MOCK_METHOD0(QueryAll, Fit::vector<Fit::RegistryInfo::FitableMeta>());
};

class FitRegistryRepositoryPersistenceV2Test : public ::testing::Test {
public:

    void SetUp() override
    {
        applicationInput_.name = "test_available_name";
        applicationInput_.nameVersion = "test_available_name_version";

        addressV1Input_.ip = "127.0.0.1";
        addressV1Input_.port = 8883;
        addressV1Input_.protocol = Fit::fit_protocol_type::GRPC;
        addressV1Input_.id = "127.0.0.1:8883";
        addressV1Input_.environment = "debug";

        addressV1Input2_.ip = "127.0.0.1";
        addressV1Input2_.port = 8883;
        addressV1Input2_.protocol = Fit::fit_protocol_type::GRPC;
        addressV1Input2_.id = "127.0.0.1:8883";
        addressV1Input2_.environment = "prod";

        addressV1Input3_.ip = "127.0.0.1";
        addressV1Input3_.port = 8883;
        addressV1Input3_.protocol = Fit::fit_protocol_type::GRPC;
        addressV1Input3_.id = "127.0.0.1:8883";
        addressV1Input3_.environment = "debug";

        workerInput_.workerId = "127.0.0.1:8883";
        workerInput_.application = applicationInput_;
        workerInput_.expire = 60;
        workerInput_.environment = "debug";

        addressInput_.host = "127.0.0.1";
        addressInput_.port = 8883;
        addressInput_.protocol = Fit::fit_protocol_type::GRPC;
        addressInput_.workerId = "127.0.0.1:8883";

        fitableInput_.genericableId = "test_gid";
        fitableInput_.genericableVersion = "test_genericable_version";
        fitableInput_.fitableId = "test_fid";

        serviceInfoInput_.is_online = true;
        serviceInfoInput_.start_time = 123456;
        Fit::fit_address addressTemp = addressV1Input_;
        addressTemp.formats.push_back(Fit::fit_format_type::JSON);
        serviceInfoInput_.service.addresses.push_back(addressTemp);
        serviceInfoInput_.service.fitable.generic_id = "test_gid";
        serviceInfoInput_.service.fitable.fitable_id = "test_fid";
        serviceInfoInput_.service.fitable.generic_version = "test_genericable_version";
        serviceInfoInput_.service.application = applicationInput_;
        serviceInfoInput_.service.timeoutSeconds = 60;

        serviceInfoInput2_.is_online = true;
        serviceInfoInput2_.start_time = 123456;
        addressTemp = addressV1Input2_;
        addressTemp.formats.push_back(Fit::fit_format_type::JSON);
        serviceInfoInput2_.service.addresses.push_back(addressTemp);
        serviceInfoInput2_.service.fitable.generic_id = "test_gid2";
        serviceInfoInput2_.service.fitable.fitable_id = "test_fid2";
        serviceInfoInput2_.service.fitable.generic_version = "test_genericable_version2";
        serviceInfoInput2_.service.application = applicationInput_;
        serviceInfoInput2_.service.timeoutSeconds = 60;

        serviceInfoInput3_.is_online = true;
        serviceInfoInput3_.start_time = 123456;
        addressTemp = addressV1Input3_;
        addressTemp.formats.push_back(Fit::fit_format_type::JSON);
        serviceInfoInput3_.service.addresses.push_back(addressTemp);
        serviceInfoInput3_.service.fitable.generic_id = "test_gid2";
        serviceInfoInput3_.service.fitable.fitable_id = "test_fid2";
        serviceInfoInput3_.service.fitable.generic_version = "test_genericable_version2";
        serviceInfoInput3_.service.application = applicationInput_;
        serviceInfoInput3_.service.timeoutSeconds = 60;

        mockWorkerOperation_ = std::make_shared<MockWorkerOperation>();
        mockAddressOperation_ = std::make_shared<MockAddressOperation>();
        mockFitableOperation_ = std::make_shared<MockFitableOperation>();

        expectedQueryWorker_ = workerInput_;
        expectedQueryAddress_ =  addressInput_;
        expectedQueryFitable_ = fitableInput_;
        expectedQueryFitableMeta_.application = applicationInput_;
        expectedQueryFitableMeta_.fitable = expectedQueryFitable_;
        expectedQueryFitableMeta_.formats.push_back(Fit::fit_format_type::JSON);

        oldWorker_ = workerInput_;
        oldWorker_.application.nameVersion = "old_test_available_name_version";

        oldWorkerDiffExpire_ = workerInput_;
        oldWorkerDiffExpire_.expire = 80;

        oldAddress_ = addressInput_;
        oldAddress_.port = 8884;

        queryFitablekey_.fitable_id = fitableInput_.fitableId;
        queryFitablekey_.generic_id = fitableInput_.genericableId;
        queryFitablekey_.generic_version = fitableInput_.genericableVersion;
    }

    void TearDown() override
    {
    }
public:
    Fit::RegistryInfo::Application applicationInput_;
    Fit::fit_address addressV1Input_;
    Fit::fit_address addressV1Input2_;
    Fit::fit_address addressV1Input3_;
    db_service_info_t serviceInfoInput_;
    db_service_info_t serviceInfoInput2_;
    db_service_info_t serviceInfoInput3_;
    std::shared_ptr<MockWorkerOperation> mockWorkerOperation_ {nullptr};
    std::shared_ptr<MockAddressOperation> mockAddressOperation_ {nullptr};
    std::shared_ptr<MockFitableOperation> mockFitableOperation_ {nullptr};
    Fit::RegistryInfo::Worker expectedQueryWorker_;
    Fit::RegistryInfo::Address expectedQueryAddress_;
    Fit::RegistryInfo::Fitable expectedQueryFitable_;
    Fit::RegistryInfo::FitableMeta expectedQueryFitableMeta_;
    Fit::RegistryInfo::Worker workerInput_;
    Fit::RegistryInfo::Address addressInput_;
    Fit::RegistryInfo::Fitable fitableInput_;
    Fit::RegistryInfo::Worker oldWorker_;
    Fit::RegistryInfo::Address oldAddress_;
    Fit::RegistryInfo::Fitable oldFitable_;
    Fit::RegistryInfo::Worker oldWorkerDiffExpire_;
    fit_fitable_key_t queryFitablekey_;
};

void CheckService(fit_service_instance_t left, fit_service_instance_t right)
{
    EXPECT_EQ(left.fitable.generic_id, right.fitable.generic_id);
    EXPECT_EQ(left.fitable.generic_version, right.fitable.generic_version);
    EXPECT_EQ(left.fitable.fitable_id, right.fitable.fitable_id);
    ASSERT_EQ(left.addresses.size(), right.addresses.size());
    EXPECT_EQ(left.addresses.front().ip, right.addresses.front().ip);
    EXPECT_EQ(left.addresses.front().port, right.addresses.front().port);
    EXPECT_EQ(left.addresses.front().protocol, right.addresses.front().protocol);
    EXPECT_EQ(left.addresses.front().formats.size(), right.addresses.front().formats.size());
    EXPECT_EQ(left.addresses.front().environment, right.addresses.front().environment);
    EXPECT_EQ(left.addresses.front().id, right.addresses.front().id);
    EXPECT_EQ(left.timeoutSeconds, right.timeoutSeconds);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_false_when_start_fit_service_given_table_null)
{
    // given
    bool expectedRet = false;

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(nullptr, nullptr, nullptr);
    bool ret = fitableRepo->Start();

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_false_when_save_fit_service_given_table_null)
{
    // given
    bool expectedRet = false;

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(nullptr, nullptr, nullptr);
    bool ret = fitableRepo->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_false_when_save_fit_service_given_mocker_table_return_fasle)
{
    // given
    bool expectedSaveServiceRet = false;
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(false));
    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(false));
    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(false));

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_false_when_save_fit_service_given_address_empty)
{
    // given
    bool expectedSaveServiceRet = false;
    db_service_info_t serviceInfoInput = serviceInfoInput_;
    serviceInfoInput.service.addresses.clear();

    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(serviceInfoInput);

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

// 全是新的场景
TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_true_when_save_fit_service_given_mocker_table_available_value)
{
    // given
    bool expectedSaveServiceRet = true;
    int32_t expectedSaveWorkerRet = FIT_ERR_SUCCESS;

    int32_t expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveFitableRet = FIT_ERR_SUCCESS;
    Fit::RegistryInfo::FitableMeta fitableMetaIn;
    Fit::vector<Fit::RegistryInfo::Address> queryAddressResult;
    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>()));
    EXPECT_CALL(*mockWorkerOperation_, Save(testing::A<const Fit::RegistryInfo::Worker&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveWorkerRet));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<1>(queryAddressResult),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Save(testing::A<const Fit::vector<Fit::RegistryInfo::Address>&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>()));
    EXPECT_CALL(*mockFitableOperation_, Save(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveFitableRet));

    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

// 有老数据场景，清理老数据
TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_true_when_clear_old_data_and_save_fit_service_given_mocker_table_available_value)
{
    // given
    bool expectedSaveServiceRet = true;
    int32_t expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveWorkerRet = FIT_ERR_SUCCESS;

    int32_t expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteAddressRet = FIT_ERR_SUCCESS;

    int32_t expectedSaveFitableRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteFitableRet = FIT_ERR_SUCCESS;
    Fit::RegistryInfo::FitableMeta fitableMetaIn;
    Fit::vector<Fit::RegistryInfo::Address> queryAddressResult;
    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorker_}));
    EXPECT_CALL(*mockWorkerOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteWorkerRet));
    EXPECT_CALL(*mockWorkerOperation_, Save(testing::A<const Fit::RegistryInfo::Worker&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveWorkerRet));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{oldAddress_}),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Delete(oldAddress_.workerId))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));
    EXPECT_CALL(*mockAddressOperation_, Save(testing::A<const Fit::vector<Fit::RegistryInfo::Address>&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Delete(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteFitableRet));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>()));
    EXPECT_CALL(*mockFitableOperation_, Save(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveFitableRet));

    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

// 更新超时时间
// 有老数据场景，清理老数据
TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_true_when_update_expire_and_save_fit_service_given_mocker_table_available_value)
{
    // given
    bool expectedSaveServiceRet = true;
    int32_t expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveWorkerRet = FIT_ERR_SUCCESS;

    int32_t expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteAddressRet = FIT_ERR_SUCCESS;

    int32_t expectedSaveFitableRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteFitableRet = FIT_ERR_SUCCESS;
    Fit::RegistryInfo::FitableMeta fitableMetaIn;
    Fit::vector<Fit::RegistryInfo::Address> queryAddressResult;
    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorkerDiffExpire_}));
    EXPECT_CALL(*mockWorkerOperation_, Save(testing::A<const Fit::RegistryInfo::Worker&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveWorkerRet));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{oldAddress_}),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Delete(oldAddress_.workerId))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));
    EXPECT_CALL(*mockAddressOperation_, Save(testing::A<const Fit::vector<Fit::RegistryInfo::Address>&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>()));
    EXPECT_CALL(*mockFitableOperation_, Save(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveFitableRet));

    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(serviceInfoInput_);

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_false_when_save_fit_service_set_given_mocker_table_init_false)
{
    // given
    bool expectedSaveServiceRet = false;
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(false));
    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(false));
    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(false));

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(db_service_set {serviceInfoInput_});

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_true_when_save_fit_service_set_given_mocker_table_init_and_query)
{
    // given
    bool expectedSaveServiceRet = true;
    int32_t expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveWorkerRet = FIT_ERR_SUCCESS;

    int32_t expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteAddressRet = FIT_ERR_SUCCESS;

    int32_t expectedSaveFitableRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteFitableRet = FIT_ERR_SUCCESS;
    Fit::RegistryInfo::FitableMeta fitableMetaIn;
    Fit::vector<Fit::RegistryInfo::Address> queryAddressResult;
    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorkerDiffExpire_}));
    EXPECT_CALL(*mockWorkerOperation_, Save(testing::A<const Fit::RegistryInfo::Worker&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveWorkerRet));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{oldAddress_}),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Delete(oldAddress_.workerId))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));
    EXPECT_CALL(*mockAddressOperation_, Save(testing::A<const Fit::vector<Fit::RegistryInfo::Address>&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>()));
    EXPECT_CALL(*mockFitableOperation_, Save(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedSaveFitableRet));

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(db_service_set {serviceInfoInput_});

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_true_when_save_fit_service_set_given_three_address)
{
    // given
    bool expectedSaveServiceRet = true;
    int32_t expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveWorkerRet = FIT_ERR_SUCCESS;

    int32_t expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedSaveAddressRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteAddressRet = FIT_ERR_SUCCESS;

    int32_t expectedSaveFitableRet = FIT_ERR_SUCCESS;
    int32_t expectedDeleteFitableRet = FIT_ERR_SUCCESS;
    Fit::RegistryInfo::FitableMeta fitableMetaIn;
    Fit::vector<Fit::RegistryInfo::Address> queryAddressResult;

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(addressV1Input_.id))
        .Times(AtLeast(1))
        .WillRepeatedly(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorkerDiffExpire_}));
    EXPECT_CALL(*mockWorkerOperation_, Save(testing::A<const Fit::RegistryInfo::Worker&>()))
        .Times(AtLeast(1))
        .WillRepeatedly(testing::Return(expectedSaveWorkerRet));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(testing::_, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{oldAddress_}),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Delete(testing::_))
        .Times(AtLeast(1))
        .WillRepeatedly(testing::Return(expectedDeleteAddressRet));
    EXPECT_CALL(*mockAddressOperation_, Save(testing::A<const Fit::vector<Fit::RegistryInfo::Address>&>()))
        .Times(AtLeast(1))
        .WillRepeatedly(testing::Return(expectedSaveAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>()));
    EXPECT_CALL(*mockFitableOperation_, Save(testing::A<const Fit::RegistryInfo::FitableMeta&>()))
        .Times(AtLeast(1))
        .WillRepeatedly(testing::Return(expectedSaveFitableRet));

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool ret = fitableRepo->Save(db_service_set {serviceInfoInput_, serviceInfoInput2_, serviceInfoInput3_});

    // then
    EXPECT_EQ(ret, expectedSaveServiceRet);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_service_set_when_query_service_by_key_given_mocker_table_query_result)
{
    // given
    int expectedQueryAddressRet = FIT_ERR_SUCCESS;
    db_service_set expectedQueryResult {serviceInfoInput_};

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{expectedQueryWorker_}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(
            testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{expectedQueryAddress_}),
            testing::Return(expectedQueryAddressRet)));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::Fitable&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>{expectedQueryFitableMeta_}));
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    db_service_set result = fitableRepo->Query(queryFitablekey_);

    // then
    ASSERT_EQ(result.size(), expectedQueryResult.size());
    CheckService(result.front().service, expectedQueryResult.front().service);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_true_when_remove_given_fitable_and_address)
{
    // given
    int expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int expectedDeleteAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteFitableRet = FIT_ERR_SUCCESS;

    bool expectedRemoveResult = true;

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorker_}));
    EXPECT_CALL(*mockWorkerOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteWorkerRet));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Delete(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteFitableRet));
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool result = fitableRepo->Remove(serviceInfoInput_.service.fitable, serviceInfoInput_.service.addresses.front());

    // then
    EXPECT_EQ(result, expectedRemoveResult);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_true_when_remove_service_set_given_services)
{
    // given
    int expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int expectedDeleteAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteFitableRet = FIT_ERR_SUCCESS;
    db_service_set removeServiceIn {serviceInfoInput_};
    bool expectedRemoveResult = true;

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorker_}));
    EXPECT_CALL(*mockWorkerOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteWorkerRet));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Delete(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteFitableRet));
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    bool result = fitableRepo->Remove(removeServiceIn);

    // then
    EXPECT_EQ(result, expectedRemoveResult);
}

// 正常删除
TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_true_when_remove_service_by_address_given_address)
{
    // given
    int expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteFitableRet = FIT_ERR_SUCCESS;

    Fit::fit_address addressInput = serviceInfoInput_.service.addresses.front();

    Fit::vector<Fit::RegistryInfo::FitableMeta> expectedQueryFitableMetasRet {expectedQueryFitableMeta_};

    db_service_set expectedRemoveResult {serviceInfoInput_};

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorker_}));
    EXPECT_CALL(*mockWorkerOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteWorkerRet));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(
            testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{expectedQueryAddress_}),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedQueryFitableMetasRet));
    EXPECT_CALL(*mockFitableOperation_, Delete(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteFitableRet));
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    db_service_set result = fitableRepo->Remove(addressInput);

    // then
    ASSERT_EQ(result.size(), expectedRemoveResult.size());
    CheckService(result.front().service, expectedRemoveResult.front().service);
}


TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_true_when_remove_given_mock_query_worker_by_id_empty)
{
    // given
    int expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int expectedDeleteAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteFitableRet = FIT_ERR_SUCCESS;
    Fit::fit_address addressInput = serviceInfoInput_.service.addresses.front();

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    db_service_set result = fitableRepo->Remove(addressInput);

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_true_when_remove_service_by_address_given_mocker_query_fitable_empty)
{
    // given
    int expectedDeleteWorkerRet = FIT_ERR_SUCCESS;
    int expectedQueryAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteAddressRet = FIT_ERR_SUCCESS;
    int expectedDeleteFitableRet = FIT_ERR_SUCCESS;

    Fit::fit_address addressInput = serviceInfoInput_.service.addresses.front();

    Fit::vector<Fit::RegistryInfo::FitableMeta> expectedQueryFitableMetasRet {expectedQueryFitableMeta_};

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{oldWorker_}));
    EXPECT_CALL(*mockWorkerOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteWorkerRet));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(
            testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{expectedQueryAddress_}),
            testing::Return(expectedQueryAddressRet)));
    EXPECT_CALL(*mockAddressOperation_, Delete(serviceInfoInput_.service.addresses.front().id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedDeleteAddressRet));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta> {}));
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    db_service_set result = fitableRepo->Remove(addressInput);

    // then
    ASSERT_EQ(result.empty(), true);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_service_set_when_query_all_given_empty)
{
    // given
    int expectedQueryAllAddressRet = FIT_ERR_SUCCESS;
    Fit::vector<Fit::RegistryInfo::FitableMeta> expectedQueryFitableMetasRet {expectedQueryFitableMeta_};
    db_service_set expectedQueryAllResult {serviceInfoInput_};

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, QueryAll())
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker>{expectedQueryWorker_}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, QueryAll(testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(
            testing::SetArgReferee<0>(Fit::vector<Fit::RegistryInfo::Address> { expectedQueryAddress_ }),
            testing::Return(expectedQueryAllAddressRet)));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, QueryAll())
        .Times(AtLeast(1))
        .WillOnce(testing::Return(expectedQueryFitableMetasRet));

    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    db_service_set result = fitableRepo->GetAllServices();

    // then
    ASSERT_EQ(result.size(), expectedQueryAllResult.size());
    CheckService(result.front().service, expectedQueryAllResult.front().service);
}


TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_service_set_when_query_service_given_genericable_id)
{
    // given
    int expectedQueryAddressRet = FIT_ERR_SUCCESS;
    db_service_set expectedQueryServiceByGenericableIdResult {serviceInfoInput_};

    // when
    EXPECT_CALL(*mockWorkerOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockWorkerOperation_, Query(testing::A<const Fit::RegistryInfo::Application&>()))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::Worker> {expectedQueryWorker_}));

    EXPECT_CALL(*mockAddressOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockAddressOperation_, Query(serviceInfoInput_.service.addresses.front().id, testing::_))
        .Times(AtLeast(1))
        .WillOnce(testing::DoAll(
            testing::SetArgReferee<1>(Fit::vector<Fit::RegistryInfo::Address>{expectedQueryAddress_}),
            testing::Return(expectedQueryAddressRet)));

    EXPECT_CALL(*mockFitableOperation_, Init())
        .WillRepeatedly(testing::Return(true));
    EXPECT_CALL(*mockFitableOperation_, Query(serviceInfoInput_.service.fitable.generic_id))
        .Times(AtLeast(1))
        .WillOnce(testing::Return(Fit::vector<Fit::RegistryInfo::FitableMeta>{expectedQueryFitableMeta_}));

    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        mockWorkerOperation_, mockAddressOperation_, mockFitableOperation_);
    db_service_set result = fitableRepo->GetServicesByGenericId(serviceInfoInput_.service.fitable.generic_id);

    // then
    ASSERT_EQ(result.size(), expectedQueryServiceByGenericableIdResult.size());
    CheckService(result.front().service, expectedQueryServiceByGenericableIdResult.front().service);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_empty_when_query_given_null_table_and_key)
{
    // given
    fit_fitable_key_t key;

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    db_service_set result = fitableRepo->Query(key);

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_empty_when_query_service_given_null_table)
{
    // given
    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    db_service_set result = fitableRepo->GetServicesByGenericId(serviceInfoInput_.service.fitable.generic_id);

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_false_when_remove_given_services)
{
    // given
    db_service_set services;

    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    bool result = fitableRepo->Remove(services);

    // then
    EXPECT_EQ(result, false);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_false_when_remove_given_null_table)
{
    // given
    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    bool result = fitableRepo->Remove(serviceInfoInput_.service.fitable, serviceInfoInput_.service.addresses.front());

    // then
    EXPECT_EQ(result, false);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_empty_when_get_all_services_given_null_table)
{
    // given
    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    db_service_set result = fitableRepo->GetAllServices();

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test,
    should_return_empty_when_get_services_by_generic_id_given_generic_id)
{
    // given
    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    db_service_set result = fitableRepo->GetServicesByGenericId(serviceInfoInput_.service.fitable.generic_id);

    // then
    EXPECT_EQ(result.empty(), true);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_error_when_delete_services_given_address)
{
    // given
    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    int32_t result = fitableRepo->DelServicesByAddress(serviceInfoInput_.service.addresses.front());

    // then
    EXPECT_EQ(result, REGISTRY_ERROR);
}

TEST_F(FitRegistryRepositoryPersistenceV2Test, should_return_empty_when_remove_given_address)
{
    // given
    // when
    FitRegistryServiceRepositoryPtr fitableRepo =
        std::make_shared<FitRegistryRepositoryPersistenceV2>(
        nullptr, nullptr, nullptr);
    db_service_set result = fitableRepo->Remove(serviceInfoInput_.service.addresses.front());

    // then
    EXPECT_EQ(result.empty(), true);
}