/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : libpq registry server table scene subscribe test
 * Author       : x00649642
 * Date         : 2023/12/05
 */
#include "fit/stl/memory.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include "registry_server_repository_pg/src/connection_pool.hpp"
#include "registry_server_repository_pg/src/table_service/heartbeat_server/table_scene_subscribe.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_connection_mock.hpp"
#include "ut/plugins/registry_server_repository_pg/mock/sql_exec_result_mock.hpp"

using namespace Fit::Pg;
using SceneSubscriber = fit_scene_subscriber;
using SubscribeBeatInfo = Fit::Heartbeat::SubscribeBeatInfo;

MATCHER(IsTupleSameString, "")
{
    return std::string(std::get<0>(arg)).compare(std::string(std::get<1>(arg))) == 0;
}

namespace {
constexpr const char* SCENE_TYPE = "scene_type";
constexpr const char* ID = "subscriber_id";
constexpr const char* CALLBACK_FITID = "subscriber_callback_fitid";

Fit::vector<Fit::string> RESULT_STR{SCENE_TYPE, ID, CALLBACK_FITID};
}  // namespace

class TableSceneSubscribeTest : public ::testing::Test {
public:
    void SetUp() override
    {
        testing::internal::CaptureStdout();
        table = Fit::make_unique<TableSceneSubscribe>(&ConnectionPool::Instance());
        dummySubscriberBeatInfo.sceneType = SCENE_TYPE;
        dummySubscriberBeatInfo.id = ID;
        dummySubscriberBeatInfo.callbackFitId = CALLBACK_FITID;
        dummySceneSubscriber = Fit::make_unique<SceneSubscriber>(dummySubscriberBeatInfo);
    }

    void TearDown() override
    {
        ConnectionPool::Instance().TearDown();
        ConnectionPool::Instance().SetStatus(ConnectionPoolStatus::INIT);
        table.reset();
        dummySceneSubscriber.reset();
        const ::testing::TestInfo* const test_info = ::testing::UnitTest::GetInstance()->current_test_info();
        auto capture = testing::internal::GetCapturedStdout();
        if (test_info->result()->Failed()) {
            std::cout << std::endl
                      << "Captured output from failed test" << std::endl
                      << test_info->test_suite_name() << "." << test_info->name() << std::endl
                      << capture << std::endl;
        }
    }

public:
    Fit::unique_ptr<TableSceneSubscribe> table;
    SubscribeBeatInfo dummySubscriberBeatInfo;
    Fit::unique_ptr<SceneSubscriber> dummySceneSubscriber;
};

TEST_F(TableSceneSubscribeTest, should_success_when_save_1_entity_success)
{
    constexpr const char* sqlCmdStr =
        "insert into registry_scene_subscribe(scene_type,subscriber_id,subscriber_callback_fitid) "
        "values ($1::varchar,$2::varchar,$3::varchar) on conflict on constraint "
        "registry_scene_subscribe_index do nothing";
    Fit::vector<const char*> sqlParams{SCENE_TYPE, ID, CALLBACK_FITID};
    auto connectionCreator = [sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->add(*dummySceneSubscriber), FIT_ERR_SUCCESS);
}

TEST_F(TableSceneSubscribeTest, should_success_when_delete_by_scene_subscriber_success)
{
    constexpr const char* sqlCmdStr =
        "delete from registry_scene_subscribe where subscriber_id=$1::varchar and scene_type=$2::varchar";
    Fit::vector<const char*> sqlParams{ID, SCENE_TYPE};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountAffected()).Times(AtLeast(1)).WillRepeatedly(Return(1));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->remove(*dummySceneSubscriber), FIT_ERR_SUCCESS);
}

TEST_F(TableSceneSubscribeTest, should_success_when_delete_by_id_success)
{
    constexpr const char* sqlCmdStr = "delete from registry_scene_subscribe where subscriber_id=$1::varchar";
    Fit::vector<const char*> sqlParams{ID};
    auto connectionCreator = [this, sqlCmdStr, sqlParams](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(AtLeast(1)).WillRepeatedly(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountAffected()).Times(AtLeast(1)).WillRepeatedly(Return(1));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    ASSERT_EQ(table->remove(ID), FIT_ERR_SUCCESS);
}

TEST_F(TableSceneSubscribeTest, should_success_return_multiple_rows_when_query_by_scene_type_success)
{
    constexpr const char* sqlCmdStr = "select scene_type, subscriber_id, subscriber_callback_fitid from "
                                      "registry_scene_subscribe where scene_type=$1::varchar";
    Fit::vector<const char*> sqlParams{SCENE_TYPE};
    Fit::vector<SceneSubscriber> resultCollector;
    const size_t resultCount = 3;
    auto connectionCreator = [this, sqlCmdStr, sqlParams, resultCount](const char*) {
        auto connectionMock = Fit::make_unique<::testing::StrictMock<SqlConnectionMock>>();
        auto sqlExecResultMock = Fit::make_unique<::testing::StrictMock<SqlExecResultMock>>();

        using namespace ::testing;
        EXPECT_CALL(*sqlExecResultMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*sqlExecResultMock, CountRow()).Times(AtLeast(1)).WillRepeatedly(Return(resultCount));
        EXPECT_CALL(*sqlExecResultMock, CountCol()).Times(AtLeast(1)).WillRepeatedly(Return(RESULT_STR.size()));
        EXPECT_CALL(*sqlExecResultMock, GetResultRow(_)).Times(resultCount).WillRepeatedly(Return(RESULT_STR));

        EXPECT_CALL(*connectionMock, IsOk()).Times(1).WillOnce(Return(true));
        EXPECT_CALL(*connectionMock, ExecParam(StrCaseEq(sqlCmdStr), Pointwise(IsTupleSameString(), sqlParams)))
            .Times(1)
            .WillOnce(Return(ByMove(Fit::move(sqlExecResultMock))));
        return connectionMock;
    };
    ConnectionPool::Instance().SetUp("", 1, 1, connectionCreator);

    resultCollector = table->query(SCENE_TYPE);
    ASSERT_EQ(resultCollector.size(), resultCount);
    for (const auto& result : resultCollector) {
        EXPECT_EQ(result.get_subscribe_info().sceneType, dummySceneSubscriber->get_subscribe_info().sceneType);
        EXPECT_EQ(result.get_subscribe_info().id, dummySceneSubscriber->get_subscribe_info().id);
        EXPECT_EQ(result.get_subscribe_info().callbackFitId, dummySceneSubscriber->get_subscribe_info().callbackFitId);
    }
}
